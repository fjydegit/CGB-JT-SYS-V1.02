package com.jt.sys.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.jt.sys.dao.SysUserDao;
import com.jt.sys.entity.SysUser;

public class ShiroUserRealm extends AuthorizingRealm{
	@Autowired
	private SysUserDao sysUserDao;

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		String username= (String)principals.getPrimaryPrincipal();		
		List<String> permsList = new ArrayList<>();
		permsList = sysUserDao.findUserPermissions(username);
		//用户权限列表
		Set<String> permsSet = new HashSet<String>();
		for(String perm : permsList){
			if(perm!=null && !("".equals(perm))){
				permsSet.add(perm);
			}
		}
		System.out.println("permsSet="+permsSet);
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		info.setStringPermissions(permsSet);
		return info;
	}


	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
			throws AuthenticationException {
		//1. 把 AuthenticationToken 转换为 UsernamePasswordToken 
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		//2. 从 UsernamePasswordToken 中来获取 username
		String username = upToken.getUsername();
		//判断用户名是否存在，若存在，返回user对象
		SysUser user =sysUserDao.findUserByUserName(username);
		//盐值. 
		ByteSource credentialsSalt = ByteSource.Util.bytes(user.getSalt());
		//自动完成密码比对   - 密码的比对:
		//通过 AuthenticatingRealm 的 credentialsMatcher 属性来进行的密码的比对!
		SimpleAuthenticationInfo info =
		new SimpleAuthenticationInfo(
		username, user.getPassword(),credentialsSalt,getName());
		return info;

	}

}
