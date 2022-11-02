package com.hpugs.shiro.test;

import com.hpugs.base.common.result.EnumError;
import com.hpugs.base.common.result.Result;
import com.hpugs.shiro.test.authc.PermissionCodeConstant;
import com.hpugs.shiro.test.authc.UserRealm;
import com.hpugs.shiro.test.authc.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.hpugs.shiro.test.authc.PermissionCodeConstant.FunctionCode.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserRealm userRealm;

    @GetMapping("/login")
    public Result login(@RequestParam(name = "account") String account,
                        @RequestParam(name = "password") String password,
                        HttpServletResponse response) {
        if (!StringUtils.hasLength(account) || !StringUtils.hasLength(password)) {
            return Result.buildFail(EnumError.PARAMS_ERROR);
        }

        // 获取当前用户
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            String realmName = subject.getPrincipals().getRealmNames().iterator().next();
            //第一个参数为用户DTO,第二个参数为realmName
            SimplePrincipalCollection principals = new SimplePrincipalCollection(subject.getPrincipal(), realmName);
            subject.runAs(principals);
            userRealm.clearCache(principals);
            subject.releaseRunAs();
        } else {
            // 封装用户的登录数据
            UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(account, password);
            try {
                subject.login(usernamePasswordToken); // 执行登录的方法，如果没有异常就说明ok了
            } catch (UnknownAccountException e) { //用户名不存在
                return Result.buildFail("用户名错误!");
            } catch (IncorrectCredentialsException e) { //密码不存在
                return Result.buildFail("密码错误!");
            }
        }
        return Result.buildSuccess(subject.getSession().getId().toString());
    }

    @GetMapping("/logout")
    public Result logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject != null) {
            subject.logout();
        }
        return Result.buildSuccess();
    }

    @GetMapping("/getUser")
    public Result getUser() {
        return Result.buildSuccess(UserUtil.getCurrentUser());
    }

    @RequiresPermissions(
            value = {DEL_USER}
    )
    @GetMapping("/delUser")
    public Result delUser() {
        return Result.buildSuccess();
    }

    @GetMapping("/editUser")
    public Result editUser() {
        Subject subject = SecurityUtils.getSubject();
        subject.checkPermission(EDIT_USER);
        return Result.buildSuccess();
    }

}
