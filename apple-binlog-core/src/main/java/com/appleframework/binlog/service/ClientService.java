package com.appleframework.binlog.service;

import java.util.Set;

import com.appleframework.binlog.model.ClientInfo;

public interface ClientService {
    /**
     * 添加服务器端及本地client信息
     *
     * @param clientInfo
     */
    void addClient(ClientInfo clientInfo);

    /**
     * 列出正常队列
     *
     * @return
     */
    Set<ClientInfo> listClient();
    
    /**
     * 删除服务器端及本地client信息
     * @param clientInfo
     */
    void deleteClient(ClientInfo clientInfo);

}
