package com.zookeeper.masterselect;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;


/**
 * 
 * ZooKeeper将会保证客户端无法创建一个已经存在的ZNode。
 * 也就是说，如果同时有多个客户端请求创建同一个临时节点，那么最终一定只有一个客户端请求能够创建成功。
 * 利用这个特性，就能很容易地在分布式环境中进行Master选举了。成功创建该节点的客户端所在的机器就成为了Master。
 * 同时，其他没有成功创建该节点的客户端，都会在该节点上注册一个节点变更的Watcher，用于监控当前Master机器是否存活，
 * 一旦发现当前的Master挂了，那么其他客户端将会重新进行Master选举。这样就实现了Master的动态选举
 * 
 * */
public class Node1 implements Watcher{
	ZooKeeper zk;
	String hostPort;
	String znode;
	
	public Node1(String hostPort,String znode) throws Throwable{
		this.hostPort = hostPort;
		this.znode = znode;
		
		zk = new ZooKeeper(hostPort, 3000, this);
		try {
			//每个客户端都创建同一个节点，如果创建成功，则该客户端是master
			zk.create(znode, "node1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("master节点是：node1");
		} catch (KeeperException.NodeExistsException e) {
			//如果抛出节点存在的异常，则master已经存在，在该节点上添加watcher
			System.out.println("master节点是：" + new String(zk.getData(znode, false, null)));
			zk.exists(znode, true);
		}
		
	}

	@Override
	public void process(WatchedEvent event) {
			try {
				
				if (event.getType() == EventType.NodeDeleted) {
					try {
						zk.create(znode, "node1".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
						System.out.println("master节点是：node1");
					} catch (KeeperException.NodeExistsException e) {
						System.out.println("master节点是：" + new String(zk.getData(znode, false, null)));
						zk.exists(znode, true);
					}
				}
				
			} catch (KeeperException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}

	public static void main(String[] args) throws Throwable {
		new Node1("192.168.31.224:2181", "/master");
		System.in.read();
	}
}
