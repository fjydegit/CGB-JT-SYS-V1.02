package com.jt.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface SysUserRoleDao {
	List<Integer> findRoleIdsByUserId(Integer userId);
	
	int insertObject(
			@Param("userId")Integer userId,
			@Param("roleIds")String[]roleIds);
	
	int deleteObject(@Param("userId")Integer userId,@Param("roleId")Integer roleId);
}
