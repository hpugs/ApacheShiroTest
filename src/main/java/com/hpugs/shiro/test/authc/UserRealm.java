package com.hpugs.shiro.test.authc;

import com.hpugs.base.common.utils.CollectionUtil;
import com.hpugs.shiro.test.DTO.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Resource
    private TestCredentialsMatcher testCredentialsMatcher;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("执行了=》授权doGetAuthorizationInfo");
        // 拿到当前登录的这个对象
        Subject subject = SecurityUtils.getSubject();
        UserDTO currentUser = (UserDTO) subject.getPrincipal(); // 拿到user对象

        List<String> permissions = currentUser.getPermissions();
        if (!CollectionUtil.isEmpty(permissions) && !permissions.stream().allMatch(StringUtils::hasLength)) {
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
        userDTO.setId(RandomUtils.nextLong());
        userDTO.setAccount("hpugs");
        userDTO.setPassword("123456");
        List<String> permissions = new ArrayList<>(1);
        permissions.add(PermissionCodeConstant.FunctionCode.EDIT_USER);
        userDTO.setPermissions(permissions);

        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        if (!token.getUsername().equals(userDTO.getAccount())) {
            throw new UnknownAccountException(); // 抛出异常
        }

        // 密码认证，shiro做
        String password = userDTO.getPassword();
        return new SimpleAuthenticationInfo(userDTO, password, "testAuthorizingRealm");
    }

    public void clearCache(SimplePrincipalCollection simplePrincipalCollection) {
        super.getAuthorizationCache().remove(simplePrincipalCollection);
    }

    @Override
    public CredentialsMatcher getCredentialsMatcher() {
        return testCredentialsMatcher;
    }

}
