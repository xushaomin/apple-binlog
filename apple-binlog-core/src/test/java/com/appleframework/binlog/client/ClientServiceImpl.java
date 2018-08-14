package com.appleframework.binlog.client;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.appleframework.binlog.model.ClientInfo;
import com.appleframework.binlog.service.ClientService;

@Service
@Lazy(false)
public class ClientServiceImpl implements ClientService {

	private static Set<ClientInfo> set = new HashSet<>();
	
	@PostConstruct
	public void init() {
		addClient(new ClientInfo("base", "by_device"));
		addClient(new ClientInfo("base", "by_device_type"));
		addClient(new ClientInfo("base", "by_device_config"));
	}
	
	@Override
	public void addClient(ClientInfo clientInfo) {
		set.add(clientInfo);
	}

	@Override
	public Set<ClientInfo> listClient() {
		return set;
	}

	@Override
	public void deleteClient(ClientInfo clientInfo) {
		set.remove(clientInfo);
	}
	
}