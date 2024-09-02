package com.hpugs.shiro.test.authc;

import com.hpugs.shiro.test.DTO.UserDTO;
import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 自定义身份认证 realm;
 * @author xinge
 */
@Slf4j
public class TestCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        log.info("密码验证 =》doCredentialsMatch");

        // 登录信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = String.copyValueOf(token.getPassword());

        // 账户信息
        SimpleAuthenticationInfo info = (SimpleAuthenticationInfo) authenticationInfo;
        UserDTO userDTO = (UserDTO) info.getPrincipals().getPrimaryPrincipal();
        if (userDTO.getPassword().equals(password)) {
            return true;
        }
        return false;
    }

}
