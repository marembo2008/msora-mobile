package com.variance.msora.contacts.task;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.ui.GeneralTabActivity;
import com.variance.msora.ui.LiveLinkRequestsActivity;
import com.variance.msora.ui.LoginActivity;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.PhonebookActivity;
import com.variance.msora.ui.dashboard.DashBoardActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.Settings;
import com.variance.msora.util.IntentConstants;

public class LoginTask extends AsyncTask<String, Void, String> {
	private String password;
	private Activity activity;
	private String userName;
	private boolean executeOnBackground;

	public LoginTask(String user, String password, Activity activity) {
		this.activity = activity;
		this.userName = user;
		this.password = password;
	}

	public LoginTask(String user, String password, Activity activity,
			boolean executeOnBackground) {
		this.activity = activity;
		this.userName = user;
		this.password = password;
		this.executeOnBackground = executeOnBackground;
	}

	@Override
	protected void onPreExecute() {
		Settings.setSessionInitializing();
		if (!executeOnBackground) {
			PersonalPhonebookActivity.showProgress("Signing in...", activity,
					this);
		}
	}

	@Override
	protected String doInBackground(String... url) {
		if (!Settings.isDebugging()) {
			try {
				return HttpRequestManager.doRequest(Settings.getSigninURL(),
						Settings.getLoginParameters(this.userName,
								this.password), true, activity);
			} catch (Exception e) {
				Log.e("log_tag", "Error converting result " + e.toString());
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		try {
			if (result == null) {
				return;
			}
			if (Settings.isDebugging()) {
				PhonebookActivity.startGeneralActivity(activity, "Msora",
						DashBoardActivity.class);
			} else if (result.equals("")
					|| result
							.equals(HttpRequestManager.NETWORK_CONNECTION_UNAVAILABLE)) {
				String codeValue = "Sorry! There is no internet Connection.";
				if (HttpRequestManager.getRequestCodeValue(result) != null) {
					codeValue = HttpRequestManager.getRequestCodeValue(result);
				}
				Toast.makeText(activity, codeValue, Toast.LENGTH_LONG).show();
			} else {
				if (result.startsWith("success")) {
					String sessionID = "";
					int start = result.indexOf("<id>") + "<id>".length();
					int end = result.indexOf("</id>");
					sessionID = result.substring(start, end);
					Settings.saveSessionID(activity, sessionID);
					startmsora();
				} else {
					Toast.makeText(activity,
							"Error: "+result,
							Toast.LENGTH_LONG).show();
				}
			}
		} finally {
			if (!executeOnBackground) {
				PersonalPhonebookActivity.endProgress();
			}
		}
	}

	private void startmsora() {
		// initialize settings. At this point, the class may have loaded, and
		// you cannot depend on the class loading initialization since the user
		// setting from server may not have been retrieved.
		GeneralManager.initAll();
		Intent intent = new Intent(activity, GeneralTabActivity.class);
		Intent callingintent = activity.getIntent();
		if (callingintent != null) {
			intent.putExtras(callingintent);
		}
		String className = callingintent
				.getStringExtra(IntentConstants.Msora_PROTECT_ACTIVITY_CLASS);
		String title = callingintent
				.getStringExtra(IntentConstants.Msora_PROTECT_ACTIVITY_TITLE);
		Log.i("Msora_PROTECT_ACTIVITY_CLASS0", className + "");
		Log.i("Msora_PROTECT_ACTIVITY_TITLE0", title + "");
		intent.putExtras(activity.getIntent());
		if (className == null
				|| className.equals(LoginActivity.class.getName())) {
			className = DashBoardActivity.class.getName();
			title = "Msora";
		}
		if (callingintent.getBooleanExtra(
				IntentConstants.ON_LIVELINK_NOTIFICATION_EXTRA,
				false)) {
			className = LiveLinkRequestsActivity.class.getName();
			title = "Livelink";
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_TABVIEW_LAYOUT,
					R.layout.usercontact_tabview);
		}
		if (title == null) {
			title = "Msora";
		}
		Log.i("Msora_PROTECT_ACTIVITY_CLASS", className + "");
		Log.i("Msora_PROTECT_ACTIVITY_TITLE", title);
		intent.putExtra(IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
				className);
		intent.putExtra(IntentConstants.Msora_PROTECT_ACTIVITY_TITLE,
				title);
		intent.putExtra(
				IntentConstants.Msora_RPOTECT_SIGNED_UP_OPTION, true);
		activity.startActivity(intent);
		activity.finish(); // this is certainly start activity
	}
}
