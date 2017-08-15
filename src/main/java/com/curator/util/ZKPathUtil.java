package com.curator.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.curator.utils.ZKPaths.PathAndNode;
import org.apache.zookeeper.ZooKeeper;

public class ZKPathUtil {
	static String path = "/curator_zkpath";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(10000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		ZooKeeper zooKeeper = client.getZookeeperClient().getZooKeeper();
		
		//只是返回,并不会创建目录
		System.out.println(ZKPaths.fixForNamespace(path, "sub"));
		System.out.println(ZKPaths.makePath(path, "sub"));
		System.out.println(ZKPaths.getNodeFromPath("/curator_zkpath/sub1"));
		PathAndNode pNode = ZKPaths.getPathAndNode("/curator_zkpath/sub1");
		
		System.out.println("pNode.getNode() : " + pNode.getNode());
		System.out.println("pNode.getPath() : " + pNode.getPath());
		
		String dir1 = path + "/child1";
		String dir2 = path + "/child2";
		
		ZKPaths.mkdirs(zooKeeper, dir1);
		ZKPaths.mkdirs(zooKeeper, dir2);
		
		System.out.println(ZKPaths.getSortedChildren(zooKeeper, path));
		
		//删除path,包括path
		ZKPaths.deleteChildren(client.getZookeeperClient().getZooKeeper(), path, true);
	}
}
