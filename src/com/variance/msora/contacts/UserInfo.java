package com.variance.msora.contacts;

public class UserInfo {
	private String userName;
	private String actualName;

	public UserInfo() {
		super();
	}

	public UserInfo(String userName, String actualName) {
		super();
		this.userName = userName;
		this.actualName = actualName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getActualName() {
		return actualName;
	}

	public void setActualName(String actualName) {
		this.actualName = actualName;
	}

}
