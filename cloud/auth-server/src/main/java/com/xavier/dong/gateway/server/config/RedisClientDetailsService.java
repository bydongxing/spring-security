package com.xavier.dong.gateway.server.config;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.xavier.dong.gateway.server.common.Constant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

import javax.sql.DataSource;

/**
 * 重写原生方法支持redis缓存
 *
 * @author xavierdong
 */
public class RedisClientDetailsService extends JdbcClientDetailsService {
    public RedisClientDetailsService(DataSource dataSource, PasswordEncoder passwordEncoder) {
        super(dataSource);
        super.setPasswordEncoder(passwordEncoder);
        super.setSelectClientDetailsSql(Constant.DEFAULT_SELECT_STATEMENT);
        super.setFindClientDetailsSql(Constant.DEFAULT_FIND_STATEMENT);
    }

    @Override
    @Cached(name = Constant.CLIENT_DETAILS_KEY, key = "#clientId", area = "token", cacheType = CacheType.REMOTE)
    public ClientDetails loadClientByClientId(String clientId) {
        return super.loadClientByClientId(clientId);
    }
}