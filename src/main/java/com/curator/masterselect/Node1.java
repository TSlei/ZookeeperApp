package com.curator.masterselect;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;


/**
 * 最主要的方法是takeLeadership，Curator会在竞争到Master后自动调用该方法，开发者可以在这个方法中实现自己的业务逻辑。
 * 需要注意的是，一旦执行完takeLeadership方法，Curator就会立即释放Master权利，然后重新开始新一轮的Master选举。
 * 
 * */
public class Node1 {
	
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
						System.out.println("node1成为master角色");
						client.setData().forPath(master_path, "node1".getBytes());
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
