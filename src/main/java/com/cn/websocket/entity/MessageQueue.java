package com.cn.websocket.entity;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 消息队列
 * @author Jesse
 *
 * @param <T>
 */
@Slf4j
public abstract class MessageQueue<T> implements Runnable {

	protected LinkedBlockingQueue<T> messageQueue = new LinkedBlockingQueue<T>();
	
	protected Map<Object, T> messageMap = new HashMap<>();
	
	private boolean isAllowSameMessage = true;
	
	private boolean isTerminated;
	
	private long timeout = 500;
	
	private long warnSize = 100;
	
	private String name;
	
	public MessageQueue() {
		this(500);
	}
	
	public MessageQueue(long timeout) {
		this(true, timeout);
	}
	
	public MessageQueue(boolean isAllowSameMessage, long timeout) {
		this.isAllowSameMessage = isAllowSameMessage;
		this.timeout = timeout;
	}
	
	private Lock lock = new ReentrantLock();
	
	@Override
	public void run() {
		while(!isTerminated || messageQueue.size() > 0) {
			try {
				lock.lock();
				T message = null;
				boolean isUnlock = false;
				try {
					if(messageQueue.size() == 0) {
						lock.unlock();
						isUnlock = true;
					}
					//弹出队顶元素，队列为空时，返回空null，获取并移除队列的头部，在指定的等待时间前等待可用的元素
					message = messageQueue.poll(timeout, TimeUnit.MILLISECONDS);
					if(message != null) {
						Object key = getMessageKey(message);
						if(key != null) {
							T tmpMessage = messageMap.remove(key);
							if(!isAllowSameMessage) {
								message = tmpMessage;
							}
						}
					}
				} finally {
					if(!isUnlock) {
						lock.unlock();
					}
				}
				if(message != null) {
					//执行处理message
					execute(message);
				}
				log(message);
			} catch (Exception e) {
				log.error("", e);
			} 
		}
	}
	
	public boolean addMessage(T message) {
		if(!isTerminated) {
			lock.lock();
			try {
				if(isAllowAdd(message)) {
					if(messageQueue.add(message)) {
						Object key = getMessageKey(message);
						if(key != null) {
							messageMap.put(key, message);
						}
						return true;
					}
				}
				return false;
			} finally {
				lock.unlock();
				if(messageQueue.size() > warnSize) {
					log.warn("{} queue size {}", name, messageQueue.size());
				}
			}
		}
		return false;
	}
	
	protected boolean isAllowAdd(T message) {
		return true;
	}
	
	protected Object getMessageKey(T message) {
		return null;
	}
	
	protected abstract void execute(T message);
	
	protected void log(T message){
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void start() {
		new Thread(this).start();
		if(name == null) 
			name = this.getClass().getName();
		log.info("{} is started", name);
	}
	
	public void setWarnSize(int size) {
		this.warnSize = size;
	}
	
	@PreDestroy
	public void terminated() {
		this.isTerminated = true;
	}
}
