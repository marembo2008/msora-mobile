package com.variance.msora.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.task.LocationUpdateTask;
import com.variance.msora.contacts.task.LoginTask;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.IntentConstants;
import com.variance.msora.util.Settings;

public class LoginActivity extends Activity {
	private EditText txtUsername;
	private EditText txtPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initLogin();
		initCurrentLocations();
		// initialize that we are set
		Settings.setPreference(this, IntentConstants.ON_msora_FINISH_EXTRA,
				false + "");
		// set the fact that we are away from sign up and hence don't need the
		// landing page again.
		Settings.setSignedUp(this);
		Log.i("Starting Login Activity: ", "onCreate Login Activity");
	}

	private void initCurrentLocations() {
		LocationUpdateTask lut = new LocationUpdateTask(LoginActivity.this);
		lut.execute(new String[] {});
	}

	private void initLogin() {
		setContentView(R.layout.mimi_connect_loginscreen);
		txtUsername = (EditText) findViewById(R.id.txtUser);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		showAutomaticLogin();
	}

	public void myLoginhHandler(View view) {
		switch (view.getId()) {
		case R.id.btnSignIn:
			validateUSer();
			break;
		case R.id.btnGoToSignup:
			signUp();
			break;
		case R.id.fogotPassword:
			doCheckPassword();
			break;
		}
	}

	private void showAutomaticLogin() {
		if (GeneralManager.hasAccessibility()) {
			boolean autoLogin = GeneralManager.getUserSettingOverride()
					.isEnableAutomaticLogin();
			((CheckBox) findViewById(R.id.automaticallyLogin))
					.setChecked(autoLogin);
		}
	}

	public void automaticallyLoginHandler(View view) {
		switch (view.getId()) {
		case R.id.automaticallyLogin:
			boolean isChecked = ((CheckBox) view).isChecked();
			if (GeneralManager.hasAccessibility()) {
				GeneralManager.getUserSettingOverride()
						.setEnableAutomaticLogin(isChecked);
				GeneralManager.updateUserSetting();
			}
			break;
		}
	}

	public void doCheckPassword() {
		Intent webView = new Intent(Intent.ACTION_VIEW);
		webView.setData(Uri.parse("http://www.m-sora.com"));
		startActivity(webView);
	}

	private void validateUSer() {
		String name = txtUsername.getText().toString().trim();
		String password = txtPassword.getText().toString().trim();
		validateUSer(name, password, this);
	}

	public void validateUSer(String name, String password, Activity context) {
		Log.i("Credential:", "" + name + "=" + password);
		LoginTask task = new LoginTask(name, password, context);
		task.execute(new String[] { "" });
	}

	private void signUp() {
		PhonebookActivity.startGeneralActivity(this, "Msora",
				SignupActivity.class, R.layout.usercontact_whitetitled_tabview);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return false;
	}

	@Override
	public void finish() {
		// we release the preferences that we are done
		Settings.setPreference(this, IntentConstants.ON_msora_FINISH_EXTRA,
				"true");
		super.finish();
	}

}
