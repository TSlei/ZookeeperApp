package com.curator.counter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

public class DistAtomicInt {
	
	static String path = "/curator_recipes_distatomic_path";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(60000) //60秒后过期,ZNode节点消失
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();
	
	
	public static void main(String[] args) {
		client.start();
		DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(client, path, 
				new RetryNTimes(3, 1000));
		
		AtomicValue<Integer> rc;
		try {
			rc = atomicInteger.add(8);
			System.out.println("result: " + rc.succeeded());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
