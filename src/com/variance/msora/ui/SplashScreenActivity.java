package com.variance.msora.ui;

import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.task.HttpRequestTask;
import com.variance.msora.contacts.task.HttpRequestTaskListener;
import com.variance.msora.contacts.task.LocationUpdateTask;
import com.variance.msora.contacts.task.LoginTask;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.ui.dashboard.DashBoardActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.IntentConstants;
import com.variance.msora.util.Settings;
import com.variance.msora.util.UserSetting;

public class SplashScreenActivity extends Activity {

	// how long until we go to the next SplashScreenActivity.this

	private static final int splashTime = 3000;

	/** Called when the SplashScreenActivity.this is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_splash);
		init();
	}

	private void init() {
		if (DashBoardActivity.DASH_BOARD_ACTIVITY != null
				&& Settings.isLoggedIn()) {
			// we are already initialized, someone has come from somewhere i do
			// not know!
			startmsora();
			finish();
		} else {
			initializeLocationUpdate();
			loadmsora();
		}
	}

	private void initializeLocationUpdate() {
		new LocationUpdateTask(this).execute(new String[] {});
	}

	public static void initializeLoadmsora(final Activity context,
			final Intent i, final Class<?> activityClass, final String title,
			final Map<String, String> intentExtras,
			final boolean redirectToLoginOnFailure) {
		if (i == null
				|| !i.hasExtra(IntentConstants.SHORTCUT_INTENT_EXTRA)
				|| Settings.getBooleanPreference(context,
						IntentConstants.ON_msora_FINISH_EXTRA)) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						if (!Settings.isSignedUp(context)) {
							Intent ii = new Intent(context,
									LandingPageActivity.class);
							context.startActivity(ii);
						} else {
							GeneralManager
									.initializeDefaults(context);
							// we override the checking of lock
							UserSetting setting = GeneralManager
									.getUserSettingOverride();
							if (setting.isEnableAutomaticLogin()) {
								loginAndInitialize(context, i, activityClass,
										title, intentExtras,
										redirectToLoginOnFailure);
							} else {
								PhonebookActivity
										.startGeneralActivity(
												context,
												"Msora",
												LoginActivity.class,
												R.layout.usercontact_whitetitled_tabview);
							}
						}
					} finally {
						synchronized (context) {
							try {
								// wait for a few millis before the othe
								// activity starts, otherwise we are going to
								// shut off the entire app.
								context.wait(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						context.finish();
					}
					return null;
				}
			}.execute();
		}
	}

	private void loadmsora() {
		Intent i = getIntent();
		if (i == null
				|| !i.hasExtra(IntentConstants.SHORTCUT_INTENT_EXTRA)
				|| Settings.getBooleanPreference(this,
						IntentConstants.ON_msora_FINISH_EXTRA)) {
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					try {
						synchronized (SplashScreenActivity.this) {
							PersonalPhonebookActivity.setShortCut(
									SplashScreenActivity.this, "msora");
							SplashScreenActivity.this.wait(splashTime);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
			}.execute();
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {
					if (!Settings.isSignedUp(SplashScreenActivity.this)) {
						finish();
						Intent ii = new Intent(SplashScreenActivity.this,
								LandingPageActivity.class);
						startActivity(ii);
					} else {
						GeneralManager
								.initializeDefaults(SplashScreenActivity.this);
						// we override the checking of lock
						UserSetting setting = GeneralManager
								.getUserSettingOverride();
						if (setting.isEnableAutomaticLogin()) {
							loginAndInitialize();
						} else {
							finish();
							PhonebookActivity.startGeneralActivity(
									SplashScreenActivity.this, "Msora",
									LoginActivity.class,
									R.layout.usercontact_whitetitled_tabview);
						}
					}
					return null;
				}
			}.execute();
		} else {
			finish();
		}
	}

	public void finish() {
		synchronized (this) {
			this.notifyAll();
		}
		super.finish();
	}

	private void loginAndInitialize() {
		HttpRequestTaskListener<Void, String> listener = new HttpRequestTaskListener<Void, String>() {

			public void onTaskStarted() {
				Settings.setSessionInitializing();
			}

			public void onTaskCompleted(String result) {
				String sessionID = null;
				try {
					if (result == null) {
						return;
					}
					if (Settings.isDebugging()) {
						PhonebookActivity.startGeneralActivity(
								SplashScreenActivity.this, "Msora",
								DashBoardActivity.class);
					} else if (result.equals("")
							|| result
									.equals(HttpRequestManager.NETWORK_CONNECTION_UNAVAILABLE)) {
						String codeValue = "Sorry! There is no internet Connection.";
						if (HttpRequestManager.getRequestCodeValue(result) != null) {
							codeValue = HttpRequestManager
									.getRequestCodeValue(result);
						}
						Toast.makeText(SplashScreenActivity.this, codeValue,
								Toast.LENGTH_SHORT).show();
						Toast.makeText(SplashScreenActivity.this,
								"Logging in locally", Toast.LENGTH_SHORT)
								.show();
						/**
						 * Since we are doing automatic login, we simply send a
						 * request to the server to get the session id.
						 * Otherwise we login the user automatically.
						 */
						finish();
						startmsora();
					} else {
						if (result.startsWith("success")) {
							Log.i("Logged in. Session Set:", result);
							int start = result.indexOf("<id>")
									+ "<id>".length();
							int end = result.indexOf("</id>");
							sessionID = result.substring(start, end);
							Settings.saveSessionID(SplashScreenActivity.this,
									sessionID);
							// Load the user profile from the server and sync
							// cache if possible.
							// Check it it is possible to sync the cache
							GeneralManager
									.init(SplashScreenActivity.this);
							/**
							 * Since we are doing automatic login, we simply
							 * send a request to the server to get the session
							 * id. Otherwise we login the user automatically.
							 */
							finish();
							startmsora();
						} else {
							/**
							 * The authentication from the server did not work
							 * as expected. Finish the dashboard activity if we
							 * are already there.
							 */
							if (DashBoardActivity.DASH_BOARD_ACTIVITY != null) {
								try {
									// be sure not to stop twice
									DashBoardActivity.DASH_BOARD_ACTIVITY
											.finish();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							Toast.makeText(
									SplashScreenActivity.this,
									"Access Denied. Invalid details. please try again",
									Toast.LENGTH_LONG).show();
							// go to login page
							finish();
							PhonebookActivity.startGeneralActivity(
									SplashScreenActivity.this, "Msora",
									LoginActivity.class);
						}
					}
				} finally {
					Settings.saveSessionID(SplashScreenActivity.this, sessionID);
				}
			}

			public String doTask(Void... params) {
				if (!Settings.isDebugging()) {
					try {
						UserSetting setting = GeneralManager
								.getUserSettingOverride();
						return HttpRequestManager.doRequest(
								Settings.getSigninURL(),
								Settings.getLoginParameters(
										setting.getUsername(),
										setting.getPassword()), true,
								SplashScreenActivity.this);
					} catch (Exception e) {
						Log.e("log_tag",
								"Error converting result " + e.toString());
					}
				}
				return null;
			}
		};

		HttpRequestTask<Void, Void, String> task = new HttpRequestTask<Void, Void, String>(
				listener, "", this);
		task.executeInBackground();
	}

	private static void loginAndInitialize(final Activity context,
			final Intent intent, final Class<?> activityClass,
			final String title, final Map<String, String> intentExtras,
			final boolean redirectToLoginOnFailure) {
		HttpRequestTaskListener<Void, String> listener = new HttpRequestTaskListener<Void, String>() {

			public void onTaskStarted() {
				Settings.setSessionInitializing();
				/**
				 * Since we are doing automatic login, we simply send a request
				 * to the server to get the session id. Otherwise we login the
				 * user automatically.
				 */
				startmsora(context, activityClass, title, intentExtras);
			}

			public void onTaskCompleted(String result) {
				String sessionID = null;
				try {
					if (result == null) {
						return;
					}
					if (Settings.isDebugging()) {
						PhonebookActivity.startGeneralActivity(context, "Msora",
								DashBoardActivity.class);
					} else if (result.equals("")
							|| result
									.equals(HttpRequestManager.NETWORK_CONNECTION_UNAVAILABLE)) {
						String codeValue = "Sorry! There is no internet Connection.";
						if (HttpRequestManager.getRequestCodeValue(result) != null) {
							codeValue = HttpRequestManager
									.getRequestCodeValue(result);
						}
						Toast.makeText(context, codeValue, Toast.LENGTH_SHORT)
								.show();
						Toast.makeText(context, "Logging in locally",
								Toast.LENGTH_SHORT).show();
					} else {
						if (result.startsWith("success")) {
							Log.i("Logged in. Session Set:", result);
							int start = result.indexOf("<id>")
									+ "<id>".length();
							int end = result.indexOf("</id>");
							sessionID = result.substring(start, end);
							Settings.saveSessionID(context, sessionID);
							// Load the user profile from the server and sync
							// cache if possible.
							// Check it it is possible to sync the cache
							GeneralManager.init(context);
						} else if (redirectToLoginOnFailure) {
							try {
								context.finish();
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							/**
							 * The authentication from the server did not work
							 * as expected. Finish the dashboard activity if we
							 * are already there.
							 */
							if (DashBoardActivity.DASH_BOARD_ACTIVITY != null) {
								try {
									// be sure not to stop twice
									DashBoardActivity.DASH_BOARD_ACTIVITY
											.finish();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							Toast.makeText(
									context,
									"Access Denied. Invalid details. please try again",
									Toast.LENGTH_LONG).show();
							// go to login page
							PhonebookActivity.startGeneralActivity(context,
									"Msora", LoginActivity.class);
						}
					}
				} finally {
					Settings.saveSessionID(context, sessionID);
				}
			}

			public String doTask(Void... params) {
				if (!Settings.isDebugging()) {
					try {
						UserSetting setting = GeneralManager
								.getUserSettingOverride();
						return HttpRequestManager.doRequest(
								Settings.getSigninURL(),
								Settings.getLoginParameters(
										setting.getUsername(),
										setting.getPassword()), true, context);
					} catch (Exception e) {
						Log.e("log_tag",
								"Error converting result " + e.toString());
					}
				}
				return null;
			}
		};

		HttpRequestTask<Void, Void, String> task = new HttpRequestTask<Void, Void, String>(
				listener, "", context);
		task.executeInBackground();
	}

	private void startmsora() {
		PhonebookActivity.startGeneralActivity(this, "Msora",
				DashBoardActivity.class,
				R.layout.usercontact_whitetitled_tabview, true);
	}

	private static void startmsora(Activity context,
			Class<?> activityClass, String title,
			final Map<String, String> intentExtras) {
		if (activityClass != null) {
			PhonebookActivity.startGeneralActivity(context, title,
					activityClass, R.layout.usercontact_whitetitled_tabview,
					true, intentExtras);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return true;
	}

	public void validateUSer(String name, String password, Activity context) {
		LoginTask task = new LoginTask(name, password, context, true);
		task.execute(new String[] { "" });
	}
}
