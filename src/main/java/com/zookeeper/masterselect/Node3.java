package com.zookeeper.masterselect;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Node3 implements Watcher{
	ZooKeeper zk;
	String hostPort;
	String znode;
	
	public Node3(String hostPort,String znode) throws Throwable{
		this.hostPort = hostPort;
		this.znode = znode;
		
		zk = new ZooKeeper(hostPort, 3000, this);
		try {
			//每个客户端都创建同一个节点，如果创建成功，则该客户端是master
			zk.create(znode, "node3".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			System.out.println("master节点是：node3");
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
						zk.create(znode, "node3".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
						System.out.println("master节点是：node3");
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
		new Node3("192.168.31.224:2181", "/master");
		System.in.read();
	}
}
