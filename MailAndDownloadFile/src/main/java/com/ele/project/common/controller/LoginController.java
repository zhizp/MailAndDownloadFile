package com.ele.project.common.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ele.project.common.SupportAction;
import com.ele.project.common.service.LoginService;
import com.ele.project.sysmanager.user.pojo.UserDTO;
import com.ele.project.util.MD5;
import com.ele.project.util.PageUtils;

@Controller
@RequestMapping(value = "/")
public class LoginController extends SupportAction {
	@Resource 
	private LoginService loginService;	
	
	public LoginService getLoginService() {
		return loginService;
	}

	/**
     * 向用户登录页面跳转
     */
    @RequestMapping(value = "/toLogin")
    public String toLogin(){
        return  "login";
    }
    /**
     * 向用户注册页面跳转
     */
    @RequestMapping(value = "/toregist")
    public String toregist(){
    	return  "regist";
    }
    
    @RequestMapping(value = "/toindex")
    public String toindex(){
    	return  "user";
    }

    /**
     * 用户登录，根据用户登录名和密码判断数据库中是否有此用户
     * @param user
     * @param model
     * @param session
     * @return
     * @throws IOException 
     */
    @RequestMapping(value = "/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletRequest req,@RequestBody Map<String, Object> paramsMap) throws IOException{
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
		 UserDTO user=new UserDTO();
	     //获取登录名和密码
		 if(paramsMap.containsKey("username")){
			 user.setUsername(paramsMap.get("username").toString());
         }
		 if(paramsMap.containsKey("password")){
			 user.setPassword(MD5.getMD5(paramsMap.get("password").toString().getBytes()));
		 }
	     List<Map<String,Object>> userlist = loginService.getUserDTOByName(paramsMap);
	     if(userlist.size()>0) {
	    	 if(!"1".equals(userlist.get(0).get("state").toString())) {
		    	 result.put("success", false);
	    		 result.put("msg", "The account has not been activated yet. Please go to the registered mailbox to activate the account.!");
	    		 return result;
	    	 }
	    	 UserDTO returnUser=loginService.selectUser(user);
	    	 if(returnUser!=null) {
		    	 result.put("success", true);
				 result.put("userid", returnUser.getId());
				 req.getSession().setAttribute("user", returnUser);
				 req.setAttribute("user", returnUser);
	    	 }else {
	    		 result.put("success", false);
		    	 result.put("msg", "Password error, please re-enter!");
	    	 }
	     }else {
	    	 result.put("success", false);
	    	 result.put("msg", "There is no such user!");
	     }
    	}catch(Exception e) {
    		e.printStackTrace();
			result.put("success", false);
			result.put("msg", "login error," + e.getMessage());
    	}
    	return result;
    }

    @RequestMapping(value = "/registe")
    @ResponseBody
    public Map<String, Object> registe(HttpServletRequest req,@RequestBody Map<String, Object> paramsMap) throws IOException{
    	Map<String, Object> result = new HashMap<String, Object>();
    	try {
		Map<String,Object> userMap=new HashMap<String,Object>();
	     //获取登录名和密码
		 if(paramsMap.containsKey("username")){
			 userMap.put("username", paramsMap.get("username").toString());
         }
		 if(paramsMap.containsKey("password")){
			 userMap.put("password",MD5.getMD5(paramsMap.get("password").toString().getBytes()));
		 }
		 if(paramsMap.containsKey("email")){
			 userMap.put("email", paramsMap.get("email").toString());
		 }
		 List<Map<String,Object>> hasList=loginService.getUserDTOByName(userMap);
		 if(hasList.size()>0) {
			 result.put("success", false);
			 result.put("msg", "This username exists, please re-enter it!");
			 return result;
		 }
		 userMap.put("rs", "1");
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	 String nowdate=sdf.format(new Date());
		 userMap.put("createdate", nowdate);
		 userMap.put("id", UUID.randomUUID().toString());
	     int i = loginService.insertUser(userMap);
	     if(i>0) {
	    	 result.put("success", true);
	     }else {
	    	 result.put("success", false);
	    	 result.put("msg", "register error");
	     }
    	}catch(Exception e) {
    		e.printStackTrace();
			result.put("success", false);
			result.put("msg", "register error," + e.getMessage());
    	}
    	return result;
    }
    
    /**
     * 向主页面跳转
     */
    @RequestMapping(value = "/main")
    public String toIndex(HttpServletResponse response,HttpServletRequest request){
    	UserDTO user=(UserDTO)request.getSession().getAttribute("user");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String nowdate=sdf.format(new Date());
    	request.setAttribute("nowdate", nowdate);
    	request.getSession().setAttribute("user", user);
        return  "main";
    }
    
    
    @RequestMapping(value = "/logout")
    public String logout(HttpSession session){
        //清除session
        session.invalidate();
        //重定向到登录页面的跳转方法
        return "redirect:toLogin";
    }
    
    
    @RequestMapping("/userlist")
	@ResponseBody
	public Map<String, Object> userlist(HttpServletRequest req) {
		List<Map<String,Object>> userlist=new ArrayList<Map<String,Object>>();
    	Map<String, Object> result = new HashMap<String, Object>();
    	Map<String,Object> userMap=new HashMap<String,Object>();
    	UserDTO user=(UserDTO) req.getSession().getAttribute("user");
    	String username=user.getUsername();
    	if("admin".equals(username)) {
    		username=null;
    	}
    	int pageSize=20;
    	int startRow=0;
    	if(req.getParameter("pagesize")!=null) {
    		pageSize=Integer.parseInt(req.getParameter("pagesize").toString());
    	}
    	if(req.getParameter("currentpage")!=null) {
    		startRow=Integer.parseInt(req.getParameter("currentpage").toString())-1;
    	}
    	if(startRow==-1) {
    		startRow=0;
    	}
    	userMap.put("username", username);
    	userMap.put("startRow", startRow);
    	userMap.put("pageSize", pageSize);
    	userlist=loginService.queryUserList(userMap);
    	int count=loginService.queryUserCount(userMap);
    	if(startRow==0) {
    		startRow=1;
    	}
		result.put("total", count);
		result.put("data", userlist);
		result.put("pageSize", pageSize);
		result.put("currentpage", startRow);
		result.put("path", user.getPath());
    	return result;
	}
    
    @RequestMapping("/deleteUserById")
    @ResponseBody
    public Map<String, Object> deleteUserById(HttpServletRequest req) {
    	Map<String, Object> params = PageUtils.getParameters(req);
    	Map<String, Object> returnMap = new HashMap<String, Object>();
    	int i=loginService.deleteUserById(params.get("id").toString());
    	if(i>0) {
    		returnMap.put("rs", true);
    		return returnMap;
    	}
    	returnMap.put("rs", false);
    	return returnMap;
    }
    
    @RequestMapping("/getFilePathByid")
    @ResponseBody
    public Map<String, Object> getFilePathByid(HttpServletRequest req) {
    	Map<String, Object> resultmap = new HashMap<>();
        try {
            Map<String, Object> parametersmap = PageUtils.getParameters(req);
            resultmap= loginService.getFilePathByid(parametersmap);
        } catch (Exception e) {
            
        }
        return resultmap;
    }
    
    @RequestMapping("/updateFilePath")
    @ResponseBody
    public Map<String, Object> updateFilePath(HttpServletRequest req,@RequestBody Map<String, Object> paramsMap) {
        Map<String, Object> returnMap=new HashMap<String, Object>();
        try {

            int i=loginService.updateFilePath(paramsMap);
            if(i<1){
                returnMap.put("rt",false);
                return returnMap;
            }
            returnMap.put("rt",true);
        } catch (Exception e) {
            returnMap.put("rt",false);
        }
        return returnMap;
    }
    @RequestMapping("/getFilePath")
    @ResponseBody
    public Map<String, Object> getFilePath(HttpServletRequest req) {
    	Map<String, Object> returnMap=new HashMap<String, Object>();
    	UserDTO user=(UserDTO) req.getSession().getAttribute("user");
    	Map<String,Object> userMap=new HashMap<String,Object>();
    	userMap.put("id", user.getId());
    	Map fileMap=loginService.getFilePathByid(userMap);
    	try {
	    	if(!fileMap.isEmpty()) {
	    		if(fileMap.get("path")==null) {
	    			returnMap.put("rs", false);
					returnMap.put("msg", "File Path is Empty!");
					return returnMap;
	    		}
	    		URL url = new URL(fileMap.get("path").toString());
	    		/*InputStream in = url.openStream(); */
	    		returnMap.put("rs", true);
	        	returnMap.put("path", fileMap.get("path").toString());
	    	}
		} catch (Exception e) {
			e.printStackTrace();
			returnMap.put("rs", false);
			returnMap.put("msg", "Invalid File Path!");
		}  
    	return returnMap;
    }
    
    
    
	
}
