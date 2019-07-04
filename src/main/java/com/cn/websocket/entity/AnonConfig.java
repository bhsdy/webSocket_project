package com.cn.websocket.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties
public class AnonConfig {

	private List<String> anon = new ArrayList<String>();
	
	public List<String> getAnon() {
		return anon;
	}

	public void setAnon(List<String> anon) {
		this.anon = anon;
	}

}