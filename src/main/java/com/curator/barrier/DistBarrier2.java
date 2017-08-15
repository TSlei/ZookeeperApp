package com.curator.barrier;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * 线程自发触发barrier释放模式
 * 
 * */
public class DistBarrier2 {
	
	static String barrier_path = "/curator_barrier_path";
	
	public static void main(String[] args) throws Exception {
		for(int i = 0; i < 5; i++){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						CuratorFramework client = CuratorFrameworkFactory.builder()
								.connectString("domain1.zookeeper:2181")
								.retryPolicy(new ExponentialBackoffRetry(1000, 3))
								.build();
						client.start();
						DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, barrier_path, 5);
						Thread.sleep(Math.round(Math.random() * 3000));
						System.out.println(Thread.currentThread().getName() + "号barrier设置");
						barrier.enter();
						System.out.println("启动...");
						Thread.sleep(Math.round(Math.random() * 3000));
						barrier.leave();
						System.out.println("退出...");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	}
}
