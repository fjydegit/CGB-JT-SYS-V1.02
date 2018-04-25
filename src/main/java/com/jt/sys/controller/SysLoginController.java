package com.jt.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jt.common.vo.JsonResult;
import com.jt.sys.service.SysUserService;

@Controller
public class SysLoginController {
	@Autowired
	private SysUserService loginService;
	@RequestMapping("/loginUI")
	public String login(){
		return "login";
	}
	
	
	/**登录操作*/
	@RequestMapping("/doLogin")
	@ResponseBody
	public JsonResult login(String username,String password){
		System.out.println(username+"/"+password);
	    loginService.login(username, password);
		return new JsonResult("login ok");
	}
	
	
	@RequestMapping("/doLogout")
	public String logout(){
		return "login";
	}
	
}

