package com.goods2go.models.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskQueue {
	
	private static ExecutorService executor = null;
	
	public static void enqueueTask(Runnable runnable) {
        
		if(executor == null || executor.isTerminated()) {
        	
        	synchronized(TaskQueue.class) {
        		
        		if(executor == null || executor.isTerminated()) {
        			executor = Executors.newSingleThreadExecutor();
        		} 
        	}    	
        }
		
		executor.execute(runnable);
        
    }


}
