package com.internousdev.olive.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

public class GoLoginAction extends ActionSupport implements SessionAware{

	public Map<String, Object> session;

	public String execute(){

		String ret = SUCCESS;

		if(!session.containsKey("tempUserId")){
			ret="sessionTimeout";
		}

		return ret;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session=session;
	}
}
