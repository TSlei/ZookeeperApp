package com;


public class Test {
	
	public static void main(String[] args) {
		for(int i = 0; i < 1000; i ++){
			long time = System.nanoTime();
			
			int[] arr = new int[10];
			int v1 = (int) (Math.random()*20+1);
			int v2 = (int) (Math.random()*20+1);
			int v3 = (int) (Math.random()*20+1);
			int v4 = (int) (Math.random()*20+1);
			int v5 = (int) (Math.random()*20+1);
			int v6 = (int) (Math.random()*20+1);
			int v7 = (int) (Math.random()*20+1);
			int v8 = (int) (Math.random()*20+1);
			int v9 = (int) (Math.random()*20+1);
			int v10 = (int) (Math.random()*20+1);
			
			arr[0] = v1;
			arr[1] = v2;
			arr[2] = v3;
			arr[3] = v4;
			arr[4] = v5;
			arr[5] = v6;
			arr[6] = v7;
			arr[7] = v8;
			arr[8] = v9;
			arr[9] = v10;
			
			int sum = getSum(arr);
			
			while(sum != 100){
				if(sum > 100){
					int maxIndex = getMax(arr);
					arr[maxIndex] = (int) (Math.random()*20+1);
					sum = getSum(arr);
					
				}
				if(sum < 100){
					int minIndex = getMin(arr);
					arr[minIndex] = (int) (Math.random()*20+1);
					sum = getSum(arr);
				}
			}
			printArr(arr);
			
			System.out.println(System.nanoTime() - time);
			System.out.println("--------------------------------------");
		}
	}
	
	public static int getMin(int[] arr){
		int min = 21;
		int index = -1;
		for(int i = 0; i < 10; i ++){
			if(arr[i] < min){
				min = arr[i];
				index = i;
			}
		}
		return index;
	}
	
	public static int getSum(int[] arr){
		int sum = 0;
		for(int value : arr){
			sum = sum + value;
		}
		return sum;
	}
	
	public static void printArr(int[] arr){
		
		for(int value : arr){
			System.out.print(value + " ");
			
		}
		System.out.println();
	}
	
	public static int getMax(int[] arr){
		int max = -1;
		int index = -1;
		for(int i = 0; i < 10; i ++){
			if(arr[i] > max){
				max = arr[i];
				index = i;
			}
		}
		return index;
	}
}
