package com.zookeeper.lock;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class DistributedLock {

	/**
	 * zookeeper节点的默认分隔符
	 */
	private final static String SEPARATOR = "/";

	/**
	 * Lock在zk中的根节点
	 */
	private final static String ROOT_LOCK_NODE = SEPARATOR + "Lock";// 锁的zk根节点

	/**
	 * Lock默认的EPHEMERAL节点的超时时间，单位毫秒
	 */
	private static final int DEFAULT_SESSION_TIMEOUT = 5000;

	/**
	 * 竞争者节点，每个想要尝试获得锁的节点都会获得一个竞争者节点
	 */
	private static final String COMPETITOR_NODE = "competitorNode";

	/**
	 * 统一的zooKeeper连接，在Init的时候初始化
	 */
	private static ZooKeeper client = null;

	/**
	 * 与zk连接成功后消除围栏
	 */
	private CountDownLatch latch = new CountDownLatch(1);

	private CountDownLatch getTheLocklatch = new CountDownLatch(1);

	private String lockName = null;

	private String rootPath = null;

	private String lockPath = null;

	private String competitorPath = null;

	private String thisCompetitorPath = null;

	private String waitCompetitorPath = null;

	/**
	 * 释放锁
	 */
	public void releaseLock() {
		if (client == null) {
			throw new RuntimeException("you can not release anyLock before you initial connectZookeeper");
		}
		try {
			client.delete(thisCompetitorPath, -1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 尝试获得锁，能获得就立马获得锁，如果不能获得就立马返回
	 */
	public boolean tryLock() {
		if (client == null) {
			throw new RuntimeException("you can not tryLock anyone before you initial connectZookeeper");
		}
		List<String> allCompetitorList = null;
		
		try {
			createComPrtitorNode();
			allCompetitorList = client.getChildren(lockPath, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.sort(allCompetitorList);
		
		int index = allCompetitorList.indexOf(thisCompetitorPath.substring((lockPath + SEPARATOR).length()));
		if (index == -1) {
			throw new RuntimeException("competitorPath not exit after create");
		} else if (index == 0) {// 如果发现自己就是最小节点,那么说明本人获得了锁
			return true;
		} else {// 说明自己不是最小节点
			return false;
		}
	}

	/**
	 * 尝试获得锁，一直阻塞，直到获得锁为止
	 */
	public void lock() {
		if (client == null) {
			throw new RuntimeException("you can not lock anyone before you connect to Zookeeper");
		}
		List<String> allCompetitorList = null;
		try {
			createComPrtitorNode();
			allCompetitorList = client.getChildren(lockPath, false);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		Collections.sort(allCompetitorList);
		int index = allCompetitorList.indexOf(thisCompetitorPath.substring((lockPath + SEPARATOR).length()));
		if (index == -1) {
			throw new RuntimeException("competitorPath not exit after create");
		} else if (index == 0) {// 如果发现自己就是最小节点,那么说明本人获得了锁
			return;
		} else {// 说明自己不是最小节点
			waitCompetitorPath = lockPath + SEPARATOR + allCompetitorList.get(index - 1);
			// 在waitPath上注册监听器, 当waitPath被删除时, zookeeper会回调监听器的process方法
			Stat waitNodeStat;
			try {
				waitNodeStat = client.exists(waitCompetitorPath, new Watcher() {
					@Override
					public void process(WatchedEvent event) {
						if (event.getType().equals(EventType.NodeDeleted)
								&& event.getPath().equals(waitCompetitorPath)) {
							getTheLocklatch.countDown();
						}
					}
				});
				if (waitNodeStat == null) {// 如果运行到此处发现前面一个节点已经不存在了。说明前面的进程已经释放了锁
					return;
				} else {
					getTheLocklatch.await();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 

		}

	}

	/**
	 * 尝试获得锁，如果有锁就返回，如果没有锁就等待，如果等待了一段时间后还没能获取到锁，那么就返回
	 */
	public boolean tryLock(int timeout) {
		return false;
	}

	/**
	 * 创建竞争者节点
	 * 
	 */
	private void createComPrtitorNode() throws KeeperException, InterruptedException {
		competitorPath = lockPath + SEPARATOR + COMPETITOR_NODE;
		thisCompetitorPath = client.create(competitorPath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
	}

	public void connectZooKeeper(String zkhosts, String lockName)
			throws KeeperException, InterruptedException, IOException {
		Stat rootStat = null;
		Stat lockStat = null;
		if (zkhosts == null) {
			throw new RuntimeException("zookeeper hosts can not be blank");
		}
		if (lockName == null) {
			throw new RuntimeException("lockName can not be blank");
		}
		if (client == null) {
			client = new ZooKeeper(zkhosts, DEFAULT_SESSION_TIMEOUT, new Watcher() {

				@Override
				public void process(WatchedEvent event) {
					if (event.getState().equals(KeeperState.SyncConnected)) {
						latch.countDown();
					}

				}
			});
		}
		latch.await();
		
		rootStat = client.exists(ROOT_LOCK_NODE, false);
		if (rootStat == null) {
			rootPath = client.create(ROOT_LOCK_NODE, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		} else {
			rootPath = ROOT_LOCK_NODE;
		}
		
		String lockNodePath = ROOT_LOCK_NODE + SEPARATOR + lockName;
		lockStat = client.exists(lockNodePath, false);
		if (lockStat != null) {// 说明此锁已经存在
			lockPath = lockNodePath;
		} else {
			// 创建相对应的锁节点
			// 这里可能会导致多个线程同事创建锁路径,然后出现路径已经存在异常
			// 可以尝试通过master选举功能，让Master去创建就好了
			lockPath = client.create(lockNodePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		}

		this.setLockName(lockName);
	}
	
	
	public static void createNode(){
		try {
			// EPHEMERAL_SEQUENTIAL模式会在"/test"目录名后面加上自增序列号
			client.create("/test", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public String getLockName() {
		return lockName;
	}

	public void setLockName(String lockName) {
		this.lockName = lockName;
	}
	
	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getLockPath() {
		return lockPath;
	}

	public void setLockPath(String lockPath) {
		this.lockPath = lockPath;
	}

}