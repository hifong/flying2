package com.flying.thread;

import com.flying.common.http.JsonHttpUtils;

public class Tests {
	public static void main(String[] args)  throws Exception {
		for(int i=0; i < 3; i++) {
			new Task(i).start();
		}
	}
	
	static class Task extends Thread {
		int index;
		Task (int id) {
			index = id;
		}
		public void run() {
			long total = 0;
			for(int i=0; i< 10000; i++){
				long start = System.currentTimeMillis();
				try {
					//JsonHttpUtils.get("http://localhost:8080/decor/base_province/findById.do?id=1");
					JsonHttpUtils.get("http://localhost:8080/cms/e.html");
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				total += System.currentTimeMillis() - start;
				if(i % 100 == 0 && i > 0) 
					System.out.println(index + " - avg of "+total+":"+(total / i));
			}
			System.out.println(index + " - total avg:"+(total / 10000));
		}
	}
}
