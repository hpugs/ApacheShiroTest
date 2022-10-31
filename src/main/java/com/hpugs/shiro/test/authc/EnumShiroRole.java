package com.hpugs.shiro.test.authc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnumShiroRole {

    ANON("anon", "无需认证直接访问"),
    AUTHC("authc", "必须认证才能访问"),
    USER("user", "必须拥有 记住我 功能才能使用"),
    PERMS("perms", "拥有对某个资源的权限才能拥有"),
    ROLE("role", "拥有某个角色权限才能访问");

    private String role;

    private String desc;

}
