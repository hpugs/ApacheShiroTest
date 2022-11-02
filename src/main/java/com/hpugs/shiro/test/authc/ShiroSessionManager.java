package com.hpugs.shiro.test.authc;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.Serializable;
import java.util.Enumeration;

public class ShiroSessionManager extends DefaultWebSessionManager {

    private static final String AUTHORIZATION = "authToken";

    private static final String REFERENCED_SESSION_ID_SOURCE = "Stateless request";

    public ShiroSessionManager() {
        super();
    }

    @Override
    protected Serializable getSessionId(ServletRequest request, ServletResponse response) {
        Enumeration<String> headers = WebUtils.toHttp(request).getHeaderNames();
        String id = WebUtils.toHttp(request).getHeader(AUTHORIZATION);
        if (StringUtils.isEmpty(id)) {
            //如果没有携带id参数则按照父类的方式在cookie进行获取
//            System.out.println("super："+super.getSessionId(request, response));
            return super.getSessionId(request, response);
        } else {
            //如果请求头中有 authToken 则其值为sessionId
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, REFERENCED_SESSION_ID_SOURCE);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID, id);
            request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_IS_VALID, Boolean.TRUE);
            return id;
        }
    }

}
