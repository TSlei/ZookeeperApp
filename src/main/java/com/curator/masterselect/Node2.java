package com.curator.masterselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;


public class Node2 {
	
	static String master_path = "/curator_master";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(10000) //60秒后过期,ZNode节点消失
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	private static LeaderSelector selector;
	
	public static void main(String[] args) {
		client.start();
		selector = new LeaderSelector(client, 
				master_path, 
				new LeaderSelectorListenerAdapter() {
					
					@Override
					public void takeLeadership(CuratorFramework client) throws Exception {
						System.out.println("node2成为master角色");
						client.setData().forPath(master_path, "node2".getBytes());
						System.in.read();
					}
				});
//		selector.autoRequeue();
		try {
			System.out.println(new String(client.getData().forPath(master_path)));
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		selector.start();
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
