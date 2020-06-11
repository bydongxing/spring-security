package com.xavier.dong.gateway.server.common;

/**
 * @author XavierDong
 **/
public final class Constant {

    private Constant() {
    }

    /**
     * sys_oauth_client_details 表的字段，不包括client_id、client_secret
     */
    public static final String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    public static final String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from oauth_client_details";

    /**
     * 按条件client_id 查询
     */
    public static final String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    /**
     * 默认的查询语句
     */
    public static final String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";


    /**
     * oauth 缓存前缀
     */
    public static final String OAUTH_ACCESS = "oauth:access:";

    /**
     * oauth 客户端信息
     */
    public static final String CLIENT_DETAILS_KEY = "oauth:client:details:";


    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    public static final String TOKEN_PREFIX = "Bearer";
    public static final String HEADER_STRING = "Authorization";
}
