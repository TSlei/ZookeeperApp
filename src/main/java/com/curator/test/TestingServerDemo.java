package com.curator.test;

import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;

/**
 * TestingServer允许开发人员自定义Zookeeper服务器对外服务的端口和dataDir路径
 * 如果没有指定dataDir，那么Curator默认会在系统的临时目录java.io.tmpdir中创建一个临时目录存储
 * */
public class TestingServerDemo {
	
	private static TestingCluster cluster;

	public static void main(String[] args) throws Exception {
		cluster = new TestingCluster(3);
		cluster.start();
		Thread.sleep(2000);
		
		TestingZooKeeperServer leader = null;
		for(TestingZooKeeperServer zs : cluster.getServers()){
			System.out.print(zs.getInstanceSpec().getServerId() + "-");
			System.out.print(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
			if(zs.getQuorumPeer().getServerState().equals("leading")){
				leader = zs;
			}
		}
		leader.kill();
		System.out.println("--After leader kill : ");
		//leader服务器被kill之后进入了Leader选举
		for(TestingZooKeeperServer zs : cluster.getServers()){
			System.out.print(zs.getInstanceSpec().getServerId() + "-");
			System.out.print(zs.getQuorumPeer().getServerState() + "-");
			System.out.println(zs.getInstanceSpec().getDataDirectory().getAbsolutePath());
		}
		cluster.stop();
	}
}
