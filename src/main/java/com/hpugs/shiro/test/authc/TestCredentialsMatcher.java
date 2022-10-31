package com.hpugs.shiro.test.authc;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.springframework.stereotype.Component;

/**
 * @author rugen
 * 2021 - 11 - 09
 */
@Component
public class TestCredentialsMatcher implements CredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //验证权限
//        CrmEmployeePrincipal crmEmployeePrincipal = (CrmEmployeePrincipal) info.getPrincipals().getPrimaryPrincipal();
//        List<Long> roleIdList = empRoleMappingManager.listByCorpIdAndUserId(
//                crmEmployeePrincipal.getWwxCorpId(), crmEmployeePrincipal.getWwxUserid());
//        return CollectionUtils.isNotEmpty(roleIdList);
        return true;
    }

}
