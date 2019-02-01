package com.ele.project.common;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ele.project.common.service.LoginService;

@Controller
@RequestMapping("/ActiveController")
public class ActiveController {
	
	@Resource 
	private LoginService loginservice;
	
	
	@RequestMapping("/activation")
	@ResponseBody
	public Map<String,Object> activation(String code,String userid) {
		Map<String,Object> resultMap=new HashMap<String,Object>();
		Map<String,Object> paramMap=new HashMap<String,Object>();
		paramMap.put("id", userid);
		Map<String,Object> userMap=loginservice.getFilePathByid(paramMap);
		if(userMap.get("code")!=null) {
			if(!code.equals(userMap.get("code").toString())) {
				resultMap.put("rs", false);
				resultMap.put("msg", "Activation failed...！");
				return resultMap;
			}
		}
		int i=loginservice.updateUserState(paramMap);
		if(i<0) {
			resultMap.put("rs", false);
			resultMap.put("msg", "Failed to update user status, please contact administrator...！");
			return resultMap;
		}
		resultMap.put("rs", true);
		resultMap.put("msg", "Successful activation！");
		return resultMap;
	}
	
	@RequestMapping(value = "/toActivation")
    public String toActivation(String code,String userid,Model model){
		model.addAttribute("code", code);
		model.addAttribute("userid", userid);
        return  "activation";
    }

}
