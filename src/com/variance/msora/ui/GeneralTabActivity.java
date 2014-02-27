package com.variance.msora.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.msora.SessionInitializationListener;
import com.variance.msora.contacts.task.LoginTask;
import com.variance.msora.ui.contact.NewContactActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.IntentConstants;
import com.variance.msora.util.Settings;
import com.variance.msora.util.UserSetting;
import com.variance.msora.util.Utils;

public class GeneralTabActivity extends TabActivity {
	private TabHost tabHost;
	public static GeneralTabActivity GENERAL_TAB_ACTIVITY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		GENERAL_TAB_ACTIVITY = this;
		if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
			// add all reinstating of states here.
			String sessionId = savedInstanceState
					.getString(Settings.SESSION_ID);
			Log.i("sessionId", sessionId + "");
			if (!Utils.isNullOrEmpty(sessionId)) {
				Settings.setSessionId(sessionId);
			}
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_tabcontent);
		tabHost = getTabHost(); // The activity TabHost
		// Initialize a TabSpec for each tab and add it to the TabHost
		// add phone book tab
		setUI();
		tabHost.setCurrentTab(0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save the settings here.
		outState.putString(Settings.SESSION_ID, Settings.getSessionID());
		super.onSaveInstanceState(outState);
	}

	public TextView getTitleView() {
		return (TextView) findViewById(R.id.txtTabViewTitle);
	}

	private void setUI() {
		Intent i = getIntent();
		if (i != null) {
			String iClass = i
					.getStringExtra(IntentConstants.Msora_PROTECT_ACTIVITY_CLASS);
			String title = i
					.getStringExtra(IntentConstants.Msora_PROTECT_ACTIVITY_TITLE);
			int tabviewLayout = i
					.getIntExtra(
							IntentConstants.Msora_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
							R.layout.usercontact_whitetitled_tabview);
			boolean toLogin = false;
			boolean addingContactFromExternal = isOnFromExternalIntentFiredForAddContact();
			if (addingContactFromExternal) {
				if (!i.hasExtra(IntentConstants.Msora_PROTECT_FROM_CREATE_CONTACT)) {
					if (!tryLogin(
							NewContactActivity.class.getName(),
							"My Phonebook",
							IntentConstants.Msora_PROTECT_FROM_CREATE_CONTACT)) {
						toLogin = true;
					}
				}
				if (toLogin) {
					iClass = LoginActivity.class.getName();
					title = "Msora";
				} else {
					iClass = NewContactActivity.class.getName();
					title = "My Phonebook";
				}
			} else if (i
					.hasExtra(IntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA)
					&& i.getBooleanExtra(
							IntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA,
							false)) {
				// check if we are logged in first
				if (!tryLogin(
						iClass,
						"Livelink Requests",
						IntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA)) {
					iClass = LoginActivity.class.getName();
					title = "My Phonebook";
					toLogin = true;
				}
			}
			if (iClass != null) {
				try {
					Class<?> iClazz = Class.forName(iClass);
					View tabView = LayoutInflater.from(this).inflate(
							tabviewLayout, null);
					if (title != null && !"".equals(title.trim())) {
						final TextView txtView = (TextView) tabView
								.findViewById(R.id.txtTabViewTitle);
						if (txtView != null) {
							txtView.setText(title);
							setOnlineStatus(tabView);
						}
					}
					Intent intent = new Intent(this, iClazz);
					intent.putExtras(i);
					if (toLogin || addingContactFromExternal) {
						intent.putExtra(
								IntentConstants.Msora_PROTECT_FROM_CREATE_CONTACT,
								true);
						intent.putExtra(
								IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
								NewContactActivity.class.getName());
						intent.putExtra(
								IntentConstants.Msora_PROTECT_ACTIVITY_TITLE,
								"My Phonebook");
					}
					TabHost.TabSpec spec = tabHost
							.newTabSpec("general_activity_tab")
							.setIndicator(tabView).setContent(intent);
					tabHost.addTab(spec);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setOnlineStatus(View tabView) {
		final ImageView imageView = (ImageView) tabView
				.findViewById(R.id.networkState);
		if (Settings.isLoggedIn()) {
			imageView.setImageResource(R.drawable.mimi_connect_network_online);
		} else {
			// we add a listener
			Settings.addSessionInitializationListener(new SessionInitializationListener() {

				@Override
				public void sessionInitialized() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (Settings.isLoggedIn()) {
								imageView
										.setImageResource(R.drawable.mimi_connect_network_online);
							}
						}
					});
				}

				@Override
				public void sessionDestroyed() {
				}
			});
		}
	}

	private boolean isOnFromExternalIntentFiredForAddContact() {
		Intent ii = getIntent();
		if ((ii != null && ii.getAction() != null && ii.getAction().equals(
				Intent.ACTION_INSERT))) {
			return true;
		}
		return false;
	}

	/**
	 * We try to login if we are going straight to create new contact if
	 * possible
	 */
	private boolean tryLogin(String iClassName, String title,
			String booleanExtraContstant) {
		Log.i("GeneralTabActivity::tryLogin()", "tryLogin()");
		if (Settings.getSessionID() != null) {
			Log.i("GeneralTabActivity::tryLogin():Already Logged in:",
					true + "");
			return true; // we are already logged in
		}
		// we init login
		GeneralManager.initUserSetting(this);
		UserSetting settings = GeneralManager
				.getUserSettingOverride();
		if (settings.isEnableAutomaticLogin()) {
			Intent intent = getIntent();
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
					iClassName);
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_TITLE,
					title);
			intent.putExtra(booleanExtraContstant, true);
			String user = settings.getUsername();
			String password = settings.getPassword();
			new LoginTask(user, password, this, true).execute(new String[] {});
			return true;
		}
		return false;
	}
}
