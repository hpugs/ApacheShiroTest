package com.hpugs.shiro.test;

import com.hpugs.base.common.result.EnumError;
import com.hpugs.base.common.result.Result;
import com.hpugs.shiro.test.authc.PermissionCodeConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/login")
    public Result login(@RequestParam(name = "account") String account,
                        @RequestParam(name = "password") String password){
        if(!StringUtils.hasLength(account) || !StringUtils.hasLength(password)) {
            return Result.buildFail(EnumError.PARAMS_ERROR);
        }

        // 获取当前用户
        Subject subject = SecurityUtils.getSubject();
        // 封装用户的登录数据
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(account, password);
        try {
            subject.login(usernamePasswordToken); // 执行登录的方法，如果没有异常就说明ok了
            return Result.buildSuccess(subject.getSession().getId().toString());
        } catch (UnknownAccountException e) { //用户名不存在
            return Result.buildFail("用户名错误!");
        } catch (IncorrectCredentialsException e){ //密码不存在
            return Result.buildFail("密码错误!");
        }
    }

    @GetMapping("/logout")
    public Result logout(){
        Subject subject = SecurityUtils.getSubject();
        if(subject != null){
            subject.logout();
        }
        return Result.buildSuccess();
    }

    @GetMapping("/getUser")
    public Result getUser(){
        Map user = new HashMap();
        user.put("account", "hpugs");
        return Result.buildSuccess(user);
    }

    @GetMapping("/delUser")
    @RequiresPermissions(
           value = {PermissionCodeConstant.FunctionCode.DEL_USER}
    )
    public Result delUser(){
        Map user = new HashMap();
        user.put("account", "hpugs");
        return Result.buildSuccess(user);
    }

}
