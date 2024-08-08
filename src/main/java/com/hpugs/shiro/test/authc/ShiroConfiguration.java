package com.hpugs.shiro.test.authc;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
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
    protected CacheManager cacheManager(@Qualifier("redisManager") RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        redisCacheManager.setKeyPrefix("test_shiro_");
        return redisCacheManager;
    }

    @Bean
    public SessionDAO redisSessionDao(@Qualifier("redisManager") RedisManager redisManager) {
        RedisSessionDAO sessionDAO = new RedisSessionDAO();
        sessionDAO.setKeyPrefix("test_session_");
        sessionDAO.setExpire(1000 * 60 * 60 * 12);
        sessionDAO.setRedisManager(redisManager);
        return sessionDAO;
    }

    @Bean
    public DefaultWebSessionManager sessionManager(@Qualifier("redisSessionDao") SessionDAO sessionDAO) {
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        sessionManager.setSessionDAO(sessionDAO);
        Cookie sessionIdCookie = sessionManager.getSessionIdCookie();
        sessionIdCookie.setSameSite(Cookie.SameSiteOptions.NONE);
        sessionIdCookie.setSecure(true);
        // 会话超时时间（单位毫秒）
        sessionManager.setGlobalSessionTimeout(1000 * 60 * 60 * 12);
        return sessionManager;
    }

    @Bean
    public SecurityManager securityManager(@Qualifier("userRealm") UserRealm userRealm,
                                           @Qualifier("shiroCacheManager") CacheManager cacheManager,
                                           @Qualifier("sessionManager") DefaultWebSessionManager sessionManager) {
        userRealm.setCachingEnabled(true);
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(userRealm);
        webSecurityManager.setSessionManager(sessionManager);
        webSecurityManager.setCacheManager(cacheManager);
        SecurityUtils.setSecurityManager(webSecurityManager);
        return webSecurityManager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(@Qualifier("securityManager") SecurityManager securityManager) {

        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        // 用户登录过滤器
        Map<String, Filter> filterMap = shiroFilterFactoryBean.getFilters();
        filterMap.put("user_session", new TestSessionFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        //URI过滤
        Map<String, String> map = new LinkedHashMap<>();
        //所有路径进行校验
        map.put("/webStatus", EnumShiroRole.ANON.getRole());
        map.put("/user/login", EnumShiroRole.ANON.getRole());
        map.put("/user/logout", EnumShiroRole.ANON.getRole());
        map.put("/user/getUser", EnumShiroRole.AUTHC.getRole());
        map.put("/**", "user_session");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    /**
     * 自定义身份认证 realm;
     * <p>
     */
    @Bean("userRealm")
    public UserRealm shiroRealm(@Qualifier("testCredentialsMatcher") TestCredentialsMatcher testCredentialsMatcher) {
        UserRealm shiroRealm = new UserRealm();
        //指定自定义密码匹配逻辑
        shiroRealm.setCredentialsMatcher(testCredentialsMatcher);
        shiroRealm.setCachingEnabled(false);
        return shiroRealm;
    }

    @Bean
    public TestCredentialsMatcher testCredentialsMatcher() {
        return new TestCredentialsMatcher();
    }

    /**
     * 开启Shiro Spring AOP支持《一》
     * 注解式的权限控制需要配置两个Bean，第一个是AdvisorAutoProxyCreator，代理生成器，
     * 需要借助SpringAOP来扫描@RequiresRoles和@RequiresPermissions等注解，生成代理类实现功能增强，从而实现权限控制。
     * 需要配合AuthorizationAttributeSourceAdvisor一起使用，否则权限注解无效。
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    /**
     * 开启Shiro Spring AOP支持《二》
     * 注解式的权限控制需要配置两个Bean,第二个是AuthorizationAttributeSourceAdvisor,
     * 这个类就相当于切点了，两个一起才能实现注解权限控制。
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor attributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor result = new AuthorizationAttributeSourceAdvisor();
        result.setSecurityManager(securityManager);
        return result;
    }

}
