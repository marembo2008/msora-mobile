package com.variance.msora.contacts.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.variance.msora.c2dm.pack.C2DMSettings;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.response.HttpResponseStatus;
import com.variance.msora.ui.GeneralTabActivity;
import com.variance.msora.ui.LoginActivity;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.PostSignupWizardActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.IntentConstants;
import com.variance.msora.util.Settings;

public class SignUpTask extends AsyncTask<String, Void, HttpResponseData> {
	private String user;
	private String password;
	private String cpassword;
	private String email;
	private Activity activity;

	public SignUpTask(String name, String password, String cpassword,
			String email, Activity activity) {
		super();
		this.password = password;
		this.cpassword = cpassword;
		this.email = email;
		this.user = name;
		this.activity = activity;
	}

	public SignUpTask(String user, String password, String cpassword,
			Activity activity) {
		super();
		this.user = user;
		this.password = password;
		this.cpassword = cpassword;
		this.activity = activity;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Signing up...", activity, this);
	}

	@Override
	protected HttpResponseData doInBackground(String... url) {
		try {
			return HttpRequestManager.doSessionlessRequestWithResponseData(
					Settings.getSignupURL(), Settings.makeSignupParameters(
							user, email, password, cpassword));
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
		}
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		try {
			if (result == null) {
				Toast.makeText(activity,
						"Sorry! There is no internet Connection.",
						Toast.LENGTH_LONG).show();
			} else if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
				Toast.makeText(activity, "You have successfully signed up",
						Toast.LENGTH_LONG).show();
				// we are successfully signed up.
				activity.finish();
				Intent intent = new Intent(activity, GeneralTabActivity.class);
				intent.putExtra(IntentConstants.SIGININ_DO_NOT_START_msora,
						true);
				intent.putExtra(IntentConstants.SIGININ_USERNAME, user);
				intent.putExtra(IntentConstants.SIGININ_PASSWORD, password);
				String className = PostSignupWizardActivity.class.getName();
				if (!GeneralManager.hasDefaultAccessibility()) {
					className = LoginActivity.class.getName();
				}
				intent.putExtra(IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
						className);
				activity.startActivity(intent);
				if (GeneralManager.hasDefaultAccessibility()) {
					PersonalPhonebookActivity.setShortCut(activity, "Msora");
				}
			} else {
				Toast.makeText(activity,
						"You are unable to signup: " + result.toString(),
						Toast.LENGTH_LONG).show();
			}
		} finally {
			doGCMRegistration();
			PersonalPhonebookActivity.endProgress();
		}
	}

	private void doGCMRegistration() {
		try {
			GCMRegistrar.checkDevice(activity);
			GCMRegistrar.checkManifest(activity);
			final String regId = GCMRegistrar.getRegistrationId(activity);
			if (regId.equals("")) {
				GCMRegistrar.register(activity, C2DMSettings.SENDER_ID);
			} else {
				Log.v("GCM Registration", "Already registered");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
