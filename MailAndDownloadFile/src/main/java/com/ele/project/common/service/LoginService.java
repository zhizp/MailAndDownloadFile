package com.ele.project.common.service;

import java.util.List;
import java.util.Map;

import com.ele.project.sysmanager.user.pojo.UserDTO;

public interface LoginService {

	public UserDTO selectUser(UserDTO user);
	public List<Map<String,Object>> getUserDTOByName(Map userMap);
	public int insertUser(Map userMap);
	public int updateFilePath(Map userMap);
	public List<Map<String,Object>> queryUserList(Map userMap);
	public int queryUserCount(Map userMap);
	public int deleteUserById(String id);
	public Map<String,Object> getFilePathByid(Map userMap);
	public int updateUserState(Map userMap);
}
