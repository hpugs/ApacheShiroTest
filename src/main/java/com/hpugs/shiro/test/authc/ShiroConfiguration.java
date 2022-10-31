package com.hpugs.shiro.test.authc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author rugen
 * 2021 - 11 - 09
 */
@Configuration
public class ShiroConfiguration {

    /**
     * redis服务host
     */
    @Value("${redis.host}")
    private String host;

    /**
     * 端口
     */
    @Value("${redis.port}")
    private Integer port;

    /**
     * 密码
     */
    @Value("${redis.password}")
    private String password;

    /**
     * 数据库编号
     */
    @Value("${redis.databaseIndex}")
    private Integer databaseIndex;

    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(String.format("%s:%d", host, port));
        redisManager.setDatabase(databaseIndex);
        redisManager.setPassword(password);
        return redisManager;
    }
    @Bean("shiroCacheManager")
    protected CacheManager cacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        redisCacheManager.setKeyPrefix("test_shiro_");
        return redisCacheManager;
    }
    @Bean
    public SessionDAO redisSessionDao(RedisManager redisManager) {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setKeyPrefix("test_session_");
        sessionDAO.setExpire(1000 * 60 * 60 * 12);
        sessionDAO.setRedisManager(redisManager);
        return sessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(SessionDAO sessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        Cookie sessionIdCookie = sessionManager.getSessionIdCookie();
        sessionIdCookie.setSameSite(Cookie.SameSiteOptions.NONE);
        sessionIdCookie.setSecure(true);
        // 会话超时时间（单位毫秒）
        sessionManager.setGlobalSessionTimeout(1000 * 60 * 60 * 12);
        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(UserRealm userRealm,
                                           CacheManager cacheManager,
                                           DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(userRealm);
        webSecurityManager.setSessionManager(sessionManager);
        webSecurityManager.setCacheManager(cacheManager);
        SecurityUtils.setSecurityManager(webSecurityManager);
        return webSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 用户登录过滤器
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("user_session", new TestSessionFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        //URI过滤
        Map<String, String> map = new LinkedHashMap<>();
        //所有路径进行校验
        map.put("/webStatus", EnumShiroRole.ANON.name());
        map.put("/user/login", EnumShiroRole.ANON.name());
        map.put("/user/logout", EnumShiroRole.ANON.name());
        map.put("/user/getUser", EnumShiroRole.AUTHC.name());
        map.put("/**", "user_session");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    /**
     * 开启Shiro Spring AOP支持
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor attributeSourceAdvisor (SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor result = new AuthorizationAttributeSourceAdvisor();
        result.setSecurityManager(securityManager);
        return result;
    }

}
