package com.variance.msora.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.Contact;
import com.variance.msora.contacts.task.BackupTask;
import com.variance.msora.contacts.task.HttpRequestTask;
import com.variance.msora.contacts.task.HttpRequestTaskListener;
import com.variance.msora.contacts.task.LoginTask;
import com.variance.msora.livelink.LivelinkOptionManager;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.util.DataParser;
import com.variance.msora.util.IntentConstants;
import com.variance.msora.util.Settings;

public class PostSignupWizardActivity extends Activity {
	private ViewFlipper viewSwitcher;
	private int previousId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontacts_post_signup_wizard);
		viewSwitcher = (ViewFlipper) findViewById(R.id.viewFlipperWizard);
		if (!getIntent().hasExtra(
				IntentConstants.Msora_RPOTECT_SIGNED_UP_OPTION)
				|| !getIntent()
						.getBooleanExtra(
								IntentConstants.Msora_RPOTECT_SIGNED_UP_OPTION,
								false)) {
			tryLogin();
		}
	}

	public void addControls(View view) {
		switch (view.getId()) {
		case R.id.btnQuitWizard:
			if (previousId > 0) {
				viewSwitcher.showPrevious();
				previousId--;
				Button b = (Button) findViewById(R.id.btnWizardNextOption);
				if (b != null) {
					b.setText("Next");
				}
				if (previousId == 0) {
					Button bb = (Button) findViewById(R.id.btnQuitWizard);
					if (bb != null) {
						bb.setText("Quit");
					}
				}
			} else {
				toSignIn();
			}
			break;
		case R.id.btnWizardNextOption:
			viewSwitcher.showNext();
			checkToSignIn();
			previousId++;
			break;
		case R.id.btnLivelinkWithContactsOnMimi:
			linkContactsOnMsoraConnect();
			break;
		case R.id.btnLivelinkRequestOnMyContacts:
			doLivelinkRequestForContacts();
			break;
		case R.id.btnBackupWizard:
			doBackup();
			break;
		}
	}

	private void checkToSignIn() {
		if (previousId == 1) {
			Button b = (Button) findViewById(R.id.btnWizardNextOption);
			if (b != null) {
				b.setText("Finish");
			}
		} else if (previousId == 2) {
			toSignIn();
		}
		Button bb = (Button) findViewById(R.id.btnQuitWizard);
		if (bb != null) {
			bb.setText("Previous");
		}
	}

	private void doBackup() {
		// prior, we need to sign in the user first.
		// if we have session id set, then we are good to go
		if (Settings.getSessionID() != null) {
			// we are logged in
			new BackupTask(PostSignupWizardActivity.this) {

				@Override
				protected void onPostExecute(List<String> result) {
					super.onPostExecute(result);
					// call next for livelink
					viewSwitcher.showNext();
					previousId++;
				}

			}.execute(new Void[] {});
		}
	}

	private void linkContactsOnMsoraConnect() {
		HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(HttpResponseData result) {
				// call next for livelink
				if (result != null && result.getMessage() != null) {
					ArrayList<Contact> personalContacts = DataParser
							.getPersonalContacts(result.getMessage());
					new LivelinkOptionManager(PostSignupWizardActivity.this,
							true)
							.doShowContactOnMsoraForLivelinkOptionAfterSignup(personalContacts);
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(Settings
						.getLivelinkUrl(), Settings
						.makeLivelinkRequestForContactsOnMsoraParameter(),
						PostSignupWizardActivity.this);
			}
		};

		HttpRequestTask<Void, Void, HttpResponseData> task = new HttpRequestTask<Void, Void, HttpResponseData>(
				listener, "Finding your contacts on Msora", this);
		task.execute();
	}

	private void doLivelinkRequestForContacts() {
		HttpRequestTaskListener<Void, HttpResponseData> listener = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(HttpResponseData result) {
				if (result != null && result.getMessage() != null) {
					ArrayList<Contact> personalContacts = DataParser
							.getPersonalContacts(result.getMessage());
					new LivelinkOptionManager(PostSignupWizardActivity.this,
							true)
							.doShowContactOnMsoraForLivelinkOptionAfterSignup(personalContacts);
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(Settings
						.getLivelinkUrl(), Settings
						.makeLivelinkRequestForContactsNotOnMsoraParameter(),
						PostSignupWizardActivity.this);
			}
		};

		HttpRequestTask<Void, Void, HttpResponseData> task = new HttpRequestTask<Void, Void, HttpResponseData>(
				listener, "Finding your contacts on Msora", this);
		task.execute();
	}

	private void toSignIn() {
		PhonebookActivity.startGeneralActivity(this, "Msora",
				LoginActivity.class, R.layout.usercontact_whitetitled_tabview);
	}

	private void tryLogin() {
		Log.i("PostSignupWizardActivity::tryLogin()", "tryLogin()");
		Intent intent = getIntent();
		String user = intent
				.getStringExtra(IntentConstants.SIGININ_USERNAME);
		String password = intent
				.getStringExtra(IntentConstants.SIGININ_PASSWORD);
		Log.i("PostSignupWizardActivity::user()", user);
		Log.i("PostSignupWizardActivity::password", password);
		new LoginTask(user, password, this).execute(new String[] {});
	}
}
