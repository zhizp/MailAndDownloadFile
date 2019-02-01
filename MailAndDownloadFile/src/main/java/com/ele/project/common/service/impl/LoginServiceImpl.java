package com.ele.project.common.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ele.project.common.dao.LoginDao;
import com.ele.project.common.service.LoginService;
import com.ele.project.sysmanager.user.pojo.UserDTO;
import com.ele.project.util.MySendMailThread;

@Transactional
@Service("loginService")
public class LoginServiceImpl implements LoginService {
	@Resource
	private LoginDao loginDao;
	
	public UserDTO selectUser(UserDTO user) {
		return loginDao.selectUser(user);
	}
	public List<Map<String,Object>> getUserDTOByName(Map userMap){
		return loginDao.getUserDTOByName(userMap);
	}
	public int insertUser(Map userMap) {
		String email=userMap.get("email").toString();
		//利用正则表达式（可改进）验证邮箱是否符合邮箱的格式
	    if(!email.matches("^\\w+@(\\w+\\.)+\\w+$")){
	        return 0;
	    }
	    //生成激活码
	    String code=UUID.randomUUID().toString();
	    userMap.put("code", code);
		int i= loginDao.insertUser(userMap);
		UserDTO user=new UserDTO();
		user.setUsername(userMap.get("username").toString());
		user.setEmail(userMap.get("email").toString());
		user.setCode(code);
		user.setId(userMap.get("id").toString());
		if(i>0) {
			new Thread(new MySendMailThread(user)).start();
	        return 1;
		}
	    return 0;
	}
	public int updateFilePath(Map userMap) {
		return loginDao.updateFilePath(userMap);
	}
	
	public List<Map<String,Object>> queryUserList(Map userMap){
		return loginDao.queryUserList(userMap);
	}
	public int queryUserCount(Map userMap) {
		return loginDao.queryUserCount(userMap);
	}
	public int deleteUserById(String id) {
		return loginDao.deleteUserById(id);
	}
	
	public Map<String,Object> getFilePathByid(Map userMap){
		return loginDao.getFilePathByid(userMap);
	}
	
	public int updateUserState(Map userMap) {
		return loginDao.updateUserState(userMap);
	}
}
