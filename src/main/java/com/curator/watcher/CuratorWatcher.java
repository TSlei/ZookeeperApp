package com.curator.watcher;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;


public class CuratorWatcher {
	
	static String path = "/zk-book/nodecache";
	
	static CuratorFramework client = CuratorFrameworkFactory.builder()
			.connectString("192.168.31.224:2181")
			.sessionTimeoutMs(60000) //60秒后过期,ZNode节点消失
			.retryPolicy(new ExponentialBackoffRetry(1000, 3))
			.build();

	private static NodeCache nodeCache;

	private static PathChildrenCache pathChildrenCache;
	
	
	/**
	 * NodeCache不仅能监听数据节点的内容变更，也能监听指定节点是否存在。
	 * 如果原本节点不存在，那么在Cache创建节点后会触发NodeCacheListener
	 * 但是，如果该数据节点被删除，那么Cutor就无法触发NodeCacheListener
	 * 
	 * */
	public static void NodeCacheWatcher(){
		client.start();
		try {
			client.create()
				.creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL) //这个模式：ZNode在Timeout之后被删除
				.forPath(path, "i am data in the path".getBytes());
			nodeCache = new NodeCache(client, path, false);
			nodeCache.start(true);
			nodeCache.getListenable().addListener(new NodeCacheListener() {
				
				@Override
				public void nodeChanged() throws Exception {

					System.out.println("Node data update , new data: " + 
					new String(nodeCache.getCurrentData().getData()));
				}
			});
			client.setData().forPath(path, "data=123".getBytes());
			Thread.sleep(1000);
			client.setData().forPath(path, "data=789".getBytes());
			Thread.sleep(1000);
			client.delete().deletingChildrenIfNeeded().forPath(path);
			Thread.sleep(1000);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				nodeCache.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.close();
		}
	}
	
	
	/**
	 * PathChildrenCache可以用于对子节点进行事件的监听，一旦该节点新增/删除子节点，或者子节点数据发生变更，就会回调PathChildrenCacheListener
	 * 并根据对应的事件进行相关的处理，对节点本身的变更不会回调。和Zookeeper客户端产品一样，Curator也无法对耳机子节点进行事件监听。
	 * 
	 * */
	public static void PathChildrenCacheWatcher(){
		client.start();
		pathChildrenCache = new PathChildrenCache(client, path, true);
		try {
			pathChildrenCache.start(StartMode.POST_INITIALIZED_EVENT);
			pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
				
				@Override
				public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

					switch (event.getType()) {
					case CHILD_ADDED:
						System.out.println("CHILD_ADDED," + event.getData().getPath());
						break;
					case CHILD_UPDATED:
						System.out.println("CHILD_UPDATED," + event.getData().getPath());
						break;
					case CHILD_REMOVED:
						System.out.println("CHILD_REMOVED," + event.getData().getPath());
						break;
					default:
						break;
					}
				}
			});
			client.create().withMode(CreateMode.PERSISTENT).forPath(path);
			Thread.sleep(1000);
			
			client.create().withMode(CreateMode.PERSISTENT).forPath(path + "/c1");
			Thread.sleep(1000);
			client.delete().forPath(path + "/c1");
			Thread.sleep(1000);
			client.delete().forPath(path);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				pathChildrenCache.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			client.close();
		}
		
	}
	
	public static void main(String[] args) {
//		NodeCacheWatcher();
		PathChildrenCacheWatcher();
	}
}
