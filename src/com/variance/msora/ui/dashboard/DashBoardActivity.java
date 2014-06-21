package com.variance.msora.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.variance.mimiprotect.R;
import com.variance.msora.SessionInitializationListener;
import com.variance.msora.business.directory.BusinessDirectoryActivity;
import com.variance.msora.business.meeting.BusinessMeetingActivity;
import com.variance.msora.c2dm.pack.C2DMSettings;
import com.variance.msora.chat.ChatManagerImpl;
import com.variance.msora.contacts.User;
import com.variance.msora.contacts.business.BusinessContactActivity;
import com.variance.msora.contacts.business.NewBusinessActivity;
import com.variance.msora.contacts.task.HttpRequestTask;
import com.variance.msora.contacts.task.HttpRequestTaskListener;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseConstants;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.response.HttpResponseStatus;
import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.ui.LiveLinkRequestsActivity;
import com.variance.msora.ui.LoginActivity;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.PhonebookActivity;
import com.variance.msora.ui.ProfileActivity;
import com.variance.msora.ui.contact.DiscoveryActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.GeneralManager.UserProfileListener;
import com.variance.msora.util.Settings;
import com.variance.msora.util.UserSetting;
import com.variance.msora.util.Utils;

public class DashBoardActivity extends AbstractActivity implements
		UserProfileListener {
	public static DashBoardActivity DASH_BOARD_ACTIVITY;
	private ChatManagerImpl chatManager;
	private volatile boolean hasBusinessPhonebook;
	private volatile String businessName;
	private static final String BUSINESS_NAME = "_62626_BUSINESS_NAME";
	private static final String HAS_BUSINESS_PHONEBOOK = "_72732783_HAS_BUSINESS_PHONEBOOK";

	@Override
	public void onUserProfileUpadate(User currentUser) {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		DASH_BOARD_ACTIVITY = this;
		// set the current session id if we are coming from recreating
		if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
			// business name
			// if this is the initial initialization, we would still be null
			businessName = savedInstanceState.getString(BUSINESS_NAME);
			hasBusinessPhonebook = savedInstanceState
					.getBoolean(HAS_BUSINESS_PHONEBOOK);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_dashboard_layout);
		init();
	}

	@Override
	protected void onResume() {
		DASH_BOARD_ACTIVITY = this;
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// save the settings here.
		outState.putString(BUSINESS_NAME, businessName);
		outState.putBoolean(HAS_BUSINESS_PHONEBOOK, hasBusinessPhonebook);
		super.onSaveInstanceState(outState);
	}

	private void init() {
		// set listener to user profile loading.
		GeneralManager.addUserProfileListener(this);
		checkMsoraWalletInstalled();
		addDashboardControls();
		doMsoraSettingSync();
	}

	public ChatManagerImpl getChatManager() {
		return chatManager;
	}

	public synchronized boolean isHasBusinessPhonebook() {
		Log.e("hasBusinessPhonebook", hasBusinessPhonebook + "");
		return hasBusinessPhonebook;
	}

	public synchronized void setHasBusinessPhonebook(
			boolean hasBusinessPhonebook) {
		this.hasBusinessPhonebook = hasBusinessPhonebook;
	}

	private String getLocalOfficePhonebookName() {
		if (GeneralManager.hasAccessibility()) {
			UserSetting userSetting = GeneralManager.getUserSettingOverride();
			User user = GeneralManager.getCurrentUser();
			if (user != null && userSetting != null
					&& user.getUserName().endsWith(userSetting.getUsername())) {
				if (userSetting.isBusinessPhonebookUser()) {
					String name = userSetting.getBusinessPhonebookName();
					if (!Utils.isNullStringOrEmpty(name)) {
						return name;
					}
				}
			}
		}
		return "Office Phonebook";
	}

	public String getBusinessName() {
		if (Utils.isNullStringOrEmpty(businessName)) {
			synchronized (this) {
				if (Utils.isNullStringOrEmpty(businessName)) {
					businessName = getLocalOfficePhonebookName();
				}
			}
		}
		return businessName;
	}

	private void checkMsoraWalletInstalled() {
		View view = findViewById(R.id.dashboard_header);
		if (view != null) {
			// view.setVisibility(View.GONE);
		}
	}

	private void loginToChatManager() {
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if (Settings.isLoggedIn()
						&& (chatManager == null || !chatManager.isConnected())) {
					chatManager = new ChatManagerImpl();
					chatManager.login();
				}
				return null;
			}
		}.execute();
	}

	private void initActionListeners() {
		View view = findViewById(R.id.dashboard_header);
		if (view != null) {
			TextView txt = (TextView) view.findViewById(R.id.txtDashboardTitle);
			txt.setText((Settings.isLoggedIn() ? "online" : "offline"));
		}
		final Button btnPhonebook = (Button) findViewById(R.id.btn_phonebook);
		final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);
		final Button btnDirectory = (Button) findViewById(R.id.btn_business_directory);
		final Button btnDiscover = (Button) findViewById(R.id.btn_discover);
		final Button btnProfile = (Button) findViewById(R.id.btn_profile);
		final Button btnLivelink = (Button) findViewById(R.id.btn_livelinks);
		if (!Settings.isSessionInitializing()) {
			btnPhonebook.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()
							|| GeneralManager.hasCurrentPhoneLock()) {
						startDashboardActivity("My Phonebook",
								PersonalPhonebookActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"You must be logged in to view your phonebook on this phone.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
			btnCompanyphonebook.setText(getBusinessName());
			btnCompanyphonebook.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					showBusinessContact(getBusinessName());
				}
			});
			btnDirectory.setOnClickListener(new View.OnClickListener() {
				private final Activity activity = DashBoardActivity.this;

				public void onClick(View v) {
					showBusinessDirectory(activity);
				}
			});
			btnDiscover.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()) {
						startDashboardActivity("Discover",
								DiscoveryActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"Sorry! You must be logged in to perform discovery on your phone contacts.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
			btnProfile.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					startDashboardActivity("My Profile", ProfileActivity.class);
				}
			});
			btnLivelink.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					if (Settings.isLoggedIn()) {
						startDashboardActivity("Livelink",
								LiveLinkRequestsActivity.class);
					} else {
						Toast.makeText(
								DashBoardActivity.this,
								"Sorry! You must be logged in to view or send livelink requests.",
								Toast.LENGTH_LONG).show();
					}
				}
			});
		}
	}

	private void initializeAllControlls() {
		// initialize controlls by logging in to
		loginToChatManager();
		new AsyncTask<Void, Void, HttpResponseData>() {
			final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (HttpRequestManager.isOnline(DashBoardActivity.this)) {
					return getBusinessInformation();
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData response) {
				try {
					synchronized (DashBoardActivity.this) {
						if (response != null
								&& response.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							hasBusinessPhonebook = Boolean
									.parseBoolean(response.getMessage());
							if (hasBusinessPhonebook) {
								businessName = response
										.getExtra(HttpResponseConstants.HTTP_RESPONSE_BUSINESS_NAME);
								Log.i("HTTP_RESPONSE_BUSINESS_NAME",
										businessName + "");
								if (Utils.isNullOrEmpty(businessName)) {
									businessName = "Office phonebook";
									hasBusinessPhonebook = false;
								} else {
									GeneralManager.getUserSetting()
											.setBusinessPhonebookName(
													businessName);
									GeneralManager.getUserSetting()
											.setBusinessPhonebookUser(
													hasBusinessPhonebook);
									GeneralManager.updateUserSetting();
								}
							}
						}
						if (hasBusinessPhonebook
								&& !Utils.isNullOrEmpty(businessName)) {
							btnCompanyphonebook.setText(businessName);
						} else {
							businessName = getLocalOfficePhonebookName();
						}
					}
					Log.e("initializeAllControlls", businessName);
				} finally {
					initActionListeners();
				}
			}

		}.execute();
	}

	public void onOfficePhonebookCreated() {
		new AsyncTask<Void, Void, HttpResponseData>() {
			final Button btnCompanyphonebook = (Button) findViewById(R.id.btn_company);

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				return getBusinessInformation();
			}

			@Override
			protected void onPostExecute(HttpResponseData response) {
				try {
					synchronized (DashBoardActivity.this) {
						if (response != null
								&& response.getResponseStatus() == HttpResponseStatus.SUCCESS) {
							hasBusinessPhonebook = Boolean
									.parseBoolean(response.getMessage());
							if (hasBusinessPhonebook) {
								businessName = response
										.getExtra(HttpResponseConstants.HTTP_RESPONSE_BUSINESS_NAME);
								Log.i("HTTP_RESPONSE_BUSINESS_NAME",
										businessName + "");
								if (Utils.isNullOrEmpty(businessName)) {
									businessName = "Office phonebook";
									hasBusinessPhonebook = false;
								} else if (GeneralManager.hasAccessibility()) {
									GeneralManager.getUserSetting()
											.setBusinessPhonebookName(
													businessName);
									GeneralManager.getUserSetting()
											.setBusinessPhonebookUser(
													hasBusinessPhonebook);
									GeneralManager.updateUserSetting();
								}
							}
						}
						if (!Utils.isNullOrEmpty(businessName)) {
							btnCompanyphonebook.setText(businessName);
						} else {
							businessName = getLocalOfficePhonebookName();
						}
					}
					Log.e("initializeAllControlls", businessName);
				} finally {
					initActionListeners();
				}
			}

		}.execute();
	}

	private void addSessionInitializationListenerIfNecessary() {
		boolean listenerAdded = false;
		if (Settings.isSessionInitializing()) {
			SessionInitializationListener listener = new SessionInitializationListener() {

				public void sessionInitialized() {
					Log.i("SessionInitializationListener",
							"SessionInitializationListener");
					initializeAllControlls();
				}

				public void sessionDestroyed() {
					synchronized (DashBoardActivity.this) {
						hasBusinessPhonebook = false;
					}
				}
			};
			if ((listenerAdded = Settings.isSessionInitializing())) {
				listenerAdded = Settings
						.addSessionInitializationListener(listener);
			}
		}
		if (!listenerAdded) {
			// session is already initialized
			// initialize here.
			initializeAllControlls();
		}
	}

	private void addDashboardControls() {
		addSessionInitializationListenerIfNecessary();
		initActionListeners();
	}

	private HttpResponseData getBusinessInformation() {
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getBusinessContactUrl(),
				Settings.makeUserHasBusinessContactParameters());
	}

	private void showBusinessContact(String businessName) {
		if (hasBusinessPhonebook
				|| (GeneralManager.hasAccessibility() && GeneralManager
						.getUserSettingOverride().isBusinessPhonebookUser())) {
			startDashboardActivity(businessName, BusinessContactActivity.class);
		} else if (Settings.isLoggedIn()) {
			startDashboardActivity("Office Phonebook",
					NewBusinessActivity.class);
		} else {
			Toast.makeText(
					this,
					"You must be logged in to view/create office phonebook on this phone",
					Toast.LENGTH_LONG).show();
		}
	}

	public void downloadMsoraMobile(View view) {
		String url = "https://www.Msoramobile.com/Msorawallet.apk";
		Intent webView = new Intent(Intent.ACTION_VIEW);
		webView.setData(Uri.parse(url));
		startActivity(webView);
	}

	public void showActionOptions(View view) {
		View actionBar = findViewById(R.id.dashboardActionBar);
		if (actionBar != null) {
			if (actionBar.getVisibility() == View.GONE) {
				actionBar.setVisibility(View.VISIBLE);
			} else {
				actionBar.setVisibility(View.GONE);
			}
		}
	}

	public void handleBusinessMeeting(View view) {
		try {
			if (Settings.isLoggedIn()) {
				PhonebookActivity.startGeneralActivity(this,
						"Business Meeting", BusinessMeetingActivity.class,
						R.layout.usercontact_tabview);
			} else {
				Toast.makeText(
						this,
						"Sorry! You must be logged in to schedule or join a meeting.",
						Toast.LENGTH_LONG).show();
			}
		} finally {
			View actionBar = findViewById(R.id.dashboardActionBar);
			if (actionBar != null) {
				actionBar.setVisibility(View.GONE);
			}
		}
	}

	private void showBusinessDirectory(Activity activity) {
		if (Settings.isLoggedIn()) {
			startDashboardActivity("Business Listing",
					BusinessDirectoryActivity.class);
		} else {
			Toast.makeText(this,
					"Sorry! You must be logged in to view business listings",
					Toast.LENGTH_SHORT).show();
		}
	}

	public void startDashboardActivity(String title, Class<?> activityClass) {
		PhonebookActivity.startGeneralActivity(this, title, activityClass,
				R.layout.usercontact_tabview, false);
	}

	private void doMsoraSettingSync() {
		if (GeneralManager.hasAccessibility()) {
			Log.e("domsoraSettingSync", "domsoraSettingSync");
			doGCMRegistration();
			HttpRequestTaskListener<Void, HttpResponseData> httpRequestTaskHorse = new HttpRequestTaskListener<Void, HttpResponseData>() {

				public void onTaskStarted() {
				}

				public void onTaskCompleted(HttpResponseData data) {
					String statusSet = data != null ? data
							.getExtra(Settings.msora_ANDROID_STATUS) : null;
					if (statusSet != null && statusSet.trim().equals("false")) {
						HttpRequestManager
								.doRequest(
										Settings.getSigninURL(),
										Settings.makemsoraSettings(DashBoardActivity.this));
						PersonalPhonebookActivity
								.showMessage(
										"Msora SIM Settings",
										"Your SIM Settings seems to have changed, please update your contact information!"
												+ "\nSTORE, CONNECT, SHARE",
										DashBoardActivity.this);
					}
				}

				public HttpResponseData doTask(Void... params) {
					return HttpRequestManager
							.doRequestWithResponseData(
									Settings.getSigninURL(),
									Settings.makemsoraSettingStatusRequest(DashBoardActivity.this));
				}
			};
			new HttpRequestTask<Void, Void, HttpResponseData>(
					httpRequestTaskHorse, "Checking Msora Settings",
					DashBoardActivity.this).executeInBackground();
		}
	}

	private void doGCMRegistration() {
		HttpRequestTaskListener<Void, HttpResponseData> httpRequestTaskHorse = new HttpRequestTaskListener<Void, HttpResponseData>() {

			public void onTaskStarted() {
			}

			public void onTaskCompleted(HttpResponseData data) {
				String statusSet = data != null ? data
						.getExtra(Settings.msora_GCM_KEY_REGISTERED) : null;
				if (statusSet != null && statusSet.trim().equals("false")) {
					HttpRequestManager.doRequest(Settings.getSigninURL(),
							Settings.makemsoraSettings(DashBoardActivity.this));
					try {
						GCMRegistrar.checkDevice(DashBoardActivity.this);
						GCMRegistrar.checkManifest(DashBoardActivity.this);
						GCMRegistrar.register(DashBoardActivity.this,
								C2DMSettings.SENDER_ID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			public HttpResponseData doTask(Void... params) {
				return HttpRequestManager.doRequestWithResponseData(Settings
						.getLiveLinkRegistrationURL(), Settings
						.makemsoraGCMStatusRequest(DashBoardActivity.this));
			}
		};
		new HttpRequestTask<Void, Void, HttpResponseData>(httpRequestTaskHorse,
				"Checking Msora Settings", DashBoardActivity.this)
				.executeInBackground();
	}

	@Override
	public void onBackPressed() {
		View actionBar = findViewById(R.id.dashboardActionBar);
		if (actionBar.getVisibility() == View.VISIBLE) {
			actionBar.setVisibility(View.GONE);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void finish() {
		setHasBusinessPhonebook(false);
		PersonalPhonebookActivity.logout(this, false,
				new OnRequestComplete<Boolean>() {

					@Override
					public void requestComplete(Boolean result) {
						PhonebookActivity.startGeneralActivity(
								DashBoardActivity.this, "Msora",
								LoginActivity.class);
						DashBoardActivity.super.finish();
					}

				});
	}

}
