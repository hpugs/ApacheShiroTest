package com.hpugs.shiro.test.authc;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

//@Configuration
public class ShiroConfig {

    //ShrioFilterFactoryBean
    @Bean(name = "shiroFilterFactoryBean")
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(@Qualifier("getDefaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager){
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        // 设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);

//        添加shiro的内置过滤器
        /**
         * anon：无需认证直接访问
         * authc：必须认证才能访问
         * user：必须拥有 记住我 功能才能使用
         * perms：拥有对某个资源的权限才能拥有
         * role：拥有某个角色权限才能访问
         */
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/user/login","anon");
        filterChainDefinitionMap.put("/user/logout","anon");
        filterChainDefinitionMap.put("/user/getUser","authc");

        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        //设置登录的请求
        bean.setLoginUrl("/doLogin.html");
        bean.setSuccessUrl("/home.html");
        bean.setUnauthorizedUrl("/unAuth.html");
        return bean;
    }

    //DefaultWebSecurityManager:2
    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }

}
