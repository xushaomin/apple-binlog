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
     * 列出数据
     *
     * @return
     */
    Set<ClientInfo> listClient();
    
    /**
     * 清除数据
     *
     * @return
     */
    void clearClient();
    
    /**
     * 删除服务器端及本地client信息
     * @param clientInfo
     */
    void deleteClient(ClientInfo clientInfo);
    
    /**
     * 判断服务器端及本地client信息是否存在
     * @param clientInfo
     */
    boolean isExistClient(ClientInfo clientInfo);
    
    /**
     * 判断服务器端及本地client信息是否存在
     * @param database, table
     */
    boolean isExistClient(String database, String table);

}
