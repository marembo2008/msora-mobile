package com.variance.msora.chat.settings;

import android.content.Context;

import com.variance.msora.util.GeneralManager;

public class ChatSettings {

	public static void saveGmailCredentials(Context context, String user,
			String password) {

	}

	public static void saveFacebookCredentials(Context context, String user,
			String password) {

	}

	public static ChatConfiguration getMsoraConfigurations() {
		ChatConfiguration configs = new ChatSettings().ChatConfiguration();
		configs.host = "192.168.0.19";
		configs.port = 5222;
		configs.service = "kenn-one";
		return configs;
	}

	public static ChatConfiguration getGmailConfigurations() {
		ChatConfiguration configs = new ChatSettings().ChatConfiguration();
		configs.host = "talk.google.com";
		configs.port = 5222;
		configs.service = "gmail.com";
		return configs;
	}

	public static ChatConfiguration getFacebookConfigurations() {
		ChatConfiguration configs = new ChatSettings().ChatConfiguration();
		configs.host = "chat.facebook.com";
		configs.port = 5222;
		configs.service = "chat.facebook.com";
		return configs;
	}

	private ChatConfiguration ChatConfiguration() {
		return new ChatConfiguration();
	}

	public static LoginCredentials getMsoraCredentials() {
		return GeneralManager.getUserSetting().getmsoraCredential();
	}

	public static LoginCredentials getFacebookCredentials() {
		return GeneralManager.getUserSetting().getFacebookCredential();
	}

	public static LoginCredentials getGmailCredentials() {
		return GeneralManager.getUserSetting().getGtalkCredential();
	}

	public static class ChatConfiguration {
		public String host;
		public int port;
		public String service;

		@Override
		public String toString() {
			return "host: " + host + ", port: " + port + ", service: "
					+ service;
		}
	}

}
