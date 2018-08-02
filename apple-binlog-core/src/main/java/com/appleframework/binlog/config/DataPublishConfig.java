package com.appleframework.binlog.config;

import java.util.Set;

public class DataPublishConfig {

	private static Set<String> PublishSet;

	public static Set<String> getPublishSet() {
		return PublishSet;
	}

	public static void setPublishSet(Set<String> publishSet) {
		PublishSet = publishSet;
	}	

}