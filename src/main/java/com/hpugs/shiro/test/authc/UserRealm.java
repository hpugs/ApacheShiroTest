package com.hpugs.shiro.test.authc;

import com.hpugs.base.common.utils.CollectionUtil;
import com.hpugs.shiro.test.DTO.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class UserRealm extends AuthorizingRealm {

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("执行了=》授权doGetAuthorizationInfo");
        // 拿到当前登录的这个对象
        Subject subject = SecurityUtils.getSubject();
        UserDTO currentUser = (UserDTO) subject.getPrincipal(); // 拿到user对象

        List<String> permissions = currentUser.getPermissions();
        if (!CollectionUtil.isEmpty(permissions) || !permissions.stream().allMatch(StringUtils::hasLength)) {
            Iterator<String> iterator = permissions.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (!StringUtils.hasLength(next)) {
                    iterator.remove();
                }
            }
        }
        if (CollectionUtil.isEmpty(permissions)) {
            return null;
        }

        //设置当前对象的权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addStringPermissions(permissions);
        return info;
    }

    // 认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        log.info("认证：=》doGetAuthenticationInfo");
        // 用户名，密码 数据库中取
        UserDTO userDTO = new UserDTO();
        userDTO.setAccount("hpugs");
        userDTO.setPassword("123456");

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        if (!token.getUsername().equals(userDTO.getAccount())) {
            throw new UnknownAccountException(); // 抛出异常
        }

        // 密码认证，shiro做
        String password = userDTO.getPassword();
        return new SimpleAuthenticationInfo(userDTO, password, "testAuthorizingRealm");
    }

}
