package com.hpugs.shiro.test.authc;

import com.alibaba.fastjson.JSON;
import com.hpugs.base.common.result.EnumError;
import com.hpugs.base.common.result.Result;
import org.apache.shiro.web.filter.authc.UserFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rugen
 * 2021 - 11 - 09
 */
public class TestSessionFilter extends UserFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest req, ServletResponse res) throws Exception {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        Result<Void> result = Result.buildFail(EnumError.USER_NOT_LOGIN);
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json");
        response.getWriter().write(JSON.toJSONString(result));
        return false;
    }

    @Override
    public String getLoginUrl() {
        return "/login";
    }
}
