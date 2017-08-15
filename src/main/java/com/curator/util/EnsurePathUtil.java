package com.curator.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.EnsurePath;

public class EnsurePathUtil {
	
	static String path = "/curator_ensure_path/c1";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(10000)
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();
	
	public static void main(String[] args) throws Exception {
		client.start();
		//没啥用
		client.usingNamespace("curator_ensure_path");
		
		EnsurePath ensurePath = new EnsurePath(path);
		ensurePath.ensure(client.getZookeeperClient());
		ensurePath.ensure(client.getZookeeperClient());
		
		//检查命名空间,如果不存在则创建
		EnsurePath ensurePath2 = client.newNamespaceAwareEnsurePath("/c4");
		ensurePath2.ensure(client.getZookeeperClient());
		
	}
}
