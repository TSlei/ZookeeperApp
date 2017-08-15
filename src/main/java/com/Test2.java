package com;

import java.util.LinkedList;
import java.util.List;

public class Test2 {
	
	public static List<Integer> getList(){
		List<Integer> list = new LinkedList<Integer>();
		Integer num1 = 0,num2 = 0;
		while(num1 + num2 != 20){
			num1 = (int)(Math.random()*21);
			num2 = (int)(Math.random()*21);
		}
		list.add(num1);
		list.add(num2);
		return list;
	}
	
	
	public static void main(String[] args) {
		int n = 100;
		long startTime =0,endTime=0;
		while(n > 0){
			startTime = System.nanoTime();
			List<Integer> list = new LinkedList<Integer>();
			list.addAll(getList());
			list.addAll(getList());
			list.addAll(getList());
			list.addAll(getList());
			list.addAll(getList());
			for(Integer num : list){
				System.out.print(num + " ");
			}
			endTime = System.nanoTime(); 
			System.out.println((endTime-startTime) + "ns"); 
			n--;
		}
	}
}
