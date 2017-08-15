package com.zookeeper.lock;

public class LockTest {

	public static void main(String[] args) throws Exception {

		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
					try {
						DistributedLock lock = new DistributedLock();
						lock.connectZooKeeper("192.168.31.224:2181", "leo");
						lock.lock();
						System.out.println(Thread.currentThread().getName() + "在做事，做完就释放锁");
						Thread.sleep(1000);
						System.out.println(Thread.currentThread().getName() + "我做完事情了");
						lock.releaseLock();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}