package com.curator.lock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;


public class Recipes_Lock {
	
	static String path = "/curator_recipes_lock_path";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(60000) //60秒后过期,ZNode节点消失
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();
	
	public static void main(String[] args) {
		
		client.start();
		
		final InterProcessMutex lock = new InterProcessMutex(client, path);
		
		final CountDownLatch downLatch = new CountDownLatch(1);
		
		for(int i = 0; i < 30; i++){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						downLatch.await();
						lock.acquire();
					} catch (Exception e) {
						e.printStackTrace();
					}
					SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
					String orderNo = sdf.format(new Date());
					System.out.println("生成的订单号是 ： " + orderNo);
					try {
						lock.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}).start();
		}
		downLatch.countDown();
	}
}
