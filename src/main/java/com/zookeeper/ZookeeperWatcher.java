package com.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;


/**
 * ZooKeeper中一共由三种方法可以实现Watch，分别为getData、exists和getChildren
 * 
 * getData()方法:仅仅监控对应节点的一次数据变化，无论是数据修改还是删除!若要每次对应节点发生变化都被监测到,那么每次都得先调用getData()方法获取一遍数据！
 * getChildren()方法:仅仅监控对应节点直接子目录的一次变化，但是只会监控直接子节点的增减情况，不会监控数据变化情况！若要每次对应节点发生增减变化都被监测到,那么每次都得先调用getChildren()方法获取一遍节点的子节点列表！
 * exists()方法:仅仅监控对应节点的一次数据变化，无论是数据修改还是删除！若要每次对应节点发生变化都被监测到，那么每次都得先调用exists()方法获取一遍节点状态！
 * 
 * */
public class ZookeeperWatcher {

	private static final String address = "192.168.31.224:2181";
	
	private static final int sessionTimeout = 3000;
	
	private static ZooKeeper client = null;
			
	static {
		
		// 创建与ZooKeeper服务器的连接zk,第一次默认注册为EventType.None
		try {
			System.out.println("开始连接ZooKeeper...");
			
			client = new ZooKeeper(address, sessionTimeout, new Watcher() {
				// 监控所有被触发的事件
				public void process(WatchedEvent event) {
					if (event.getType() == null || "".equals(event.getType())) {
						return;
					}
					System.out.println(event.getType());
				}
			});
			
			System.out.println("ZooKeeper连接创建成功！");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void createZNode() throws Exception{
		// 创建根目录节点,"/tmp_root_path"
		// 节点内容为字符串"我是根目录/tmp_root_path"
		// 创建模式为CreateMode.PERSISTENT
		System.out.println("开始创建根目录节点/tmp_root_path...");
		client.create("/tmp_root_path", "我是根目录/tmp_root_path".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		System.out.println("根目录节点/tmp_root_path创建成功！");
	}
	
	public static void createZNode(int i) throws Exception{
		// 创建第i个子目录节点,"/tmp_root_path/childPath"+i
		// 节点内容为字符串"我是第i个子目录/tmp_root_path/childPath"+i
		// 创建模式为CreateMode.PERSISTENT
		System.out.println("开始创建第" + i + "个子目录节点/tmp_root_path/childPath" + i);
		client.create("/tmp_root_path/childPath"+ i, ("我是第一个子目录/tmp_root_path/childPath1"+i).getBytes(), Ids.OPEN_ACL_UNSAFE,
				CreateMode.PERSISTENT);
		System.out.println("第" + i + "个子目录节点/tmp_root_path/childPath" + i + "创建成功！");
	}
	
	//将watcher设置为true
	public static void getZNode(int i) throws Exception{
		// 获取第i个子目录节点,"/tmp_root_path/childPath"+i 节点数据
		System.out.println("开始获取第" + i + "个子目录节点/tmp_root_path/childPath" + i + "节点数据...");
		System.out.println(new String(client.getData("/tmp_root_path/childPath" + i, true, null)));
		System.out.println("第" + i + "个子目录节点/tmp_root_path/childPath" + i + "节点数据获取成功！");
	}
	
	public static void updateZNode(int i) throws Exception{
		System.out.println("开始修改第" + i + "个子目录节点/tmp_root_path/childPath" + i + "数据");
		client.setData("/tmp_root_path/childPath" + i, ("我是修改数据后的第一个子目录/tmp_root_path/childPath" + i).getBytes(), -1);
		System.out.println("修改第一个子目录节点/tmp_root_path/childPath" + i + "数据成功！");
	}
	
	public static void deleteZNode(String path, int i) throws Exception{
		System.out.println("开始删除第"+ i + "个子目录节点" + path + i);
		client.delete(path + i, -1);
		System.out.println("第一个子目录节点" + path+ i + "删除成功！");
	}
	
	public static void deleteZNode(String path) throws Exception{
		System.out.println("开始删除根目录节点/tmp_root_path");
		client.delete("/tmp_root_path", -1);
		System.out.println("根目录节点/tmp_root_path删除成功！");
	}
	
	
	public static void getChildren() throws Exception{
		System.out.println("开始获取根目录/tmp_root_path节点的子目录节点列...");  
		System.out.println(client.getChildren("/tmp_root_path", true));  
		System.out.println("根目录/tmp_root_path节点的子目录节点列获取成功！");
	}
	
	public static void getZNodeStatus() throws Exception{
        // 获取根目录节点状态  
        System.out.println("开始获取根目录节点状态...");  
        System.out.println(client.exists("/tmp_root_path", true));  
        System.out.println("根目录节点状态获取成功");  
	}

	public static void main(String[] args) {

		try {

			Thread.sleep(1000);
			
			createZNode();
			
			Thread.sleep(1000);

			// 创建第一个子目录节点
			createZNode(1);

			Thread.sleep(1000);
			
			// 创建第二个子目录节点
			createZNode(2);
			
			Thread.sleep(1000);

//			// 获取第二个子目录节点/tmp_root_path/childPath2节点数据
//			getZNode(2);
//			
//			Thread.sleep(1000);
//
//			// 修改第一个子目录节点/tmp_root_path/childPath1数据
//			updateZNode(1);
//			
//			Thread.sleep(1000);
//
//			// 修改第二个子目录节点/tmp_root_path/childPath2数据
//			updateZNode(2);
//
//			Thread.sleep(1000);
//
//			// 删除第一个子目录节点
//			deleteZNode("/tmp_root_path/childPath", 1);
//
//			Thread.sleep(1000);
//
//			// 删除第二个子目录节点
//			deleteZNode("/tmp_root_path/childPath", 2);
//			
//			Thread.sleep(1000);
//
//			// 删除根目录节点
//			deleteZNode("/tmp_root_path");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 关闭连接
			if (client != null) {
					try {
						client.close();
						System.out.println("释放ZooKeeper连接成功！");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}
		}

	}
}
