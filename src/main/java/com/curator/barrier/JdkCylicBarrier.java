package com.curator.barrier;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * jdk自带barrier,并非分布式实现
 * 
 * */
public class JdkCylicBarrier {
	public static CyclicBarrier barrier = new CyclicBarrier(3);
	
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(3);
		executor.submit(new Thread(new Runner("1号选手")));
		executor.submit(new Thread(new Runner("2号选手")));
		executor.submit(new Thread(new Runner("3号选手")));
	}
}

class Runner implements Runnable{
	
	private String name;
	
	public Runner(String name){
		this.name = name;
	}
	
	public void run(){
		System.out.println(name + " 准备好了");
		try {
			JdkCylicBarrier.barrier.await();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println(name + " 起跑！");
	}
}