package com.example.connexus2;

public class LoginMsg {
	protected String id;
	protected String pwd;
	
	public String getId(){
		return this.id;
	}
	
	public String getPwd(){
		return this.pwd;
	}
	
	public LoginMsg (String id, String pwd){
		this.id = id;
		this.pwd =pwd;
	}
}