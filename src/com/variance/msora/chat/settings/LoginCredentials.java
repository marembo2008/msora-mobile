package com.variance.msora.chat.settings;

public class LoginCredentials {
	private String username;
	private String password;

	public LoginCredentials(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public LoginCredentials() {
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "username: " + username + ", password: " + password;
	}
}
