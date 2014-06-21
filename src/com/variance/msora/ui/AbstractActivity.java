package com.variance.msora.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.variance.mimiprotect.R;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.response.HttpResponseStatus;
import com.variance.msora.ui.backuprestore.BackupRestoreActivity;
import com.variance.msora.ui.dashboard.DashBoardActivity;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.Settings;

public class AbstractActivity extends Activity {
	private static final Set<AbstractActivity> activities = new HashSet<AbstractActivity>();
	private static volatile boolean loggedOut = false;

	public static Activity getAnyCurrentActivity() {
		if (activities.isEmpty()) {
			return GeneralTabActivity.GENERAL_TAB_ACTIVITY;
		}
		return activities.iterator().next();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mimi_connect_about);
		activities.add(this);
	}

	@Override
	public void finish() {
		try {
			activities.remove(this);
			if (!loggedOut && activities.isEmpty()) {
				doLogoutInbackground(this, false);
			}
			super.finish();
		} finally {
			loggedOut = false;
		}
	}

	private void addLogoutOptions(Menu menu) {
		MenuItem logoutMenuItem = menu.add("Log out");
		logoutMenuItem.setIcon(R.drawable.mimi_connect_logout);
		logoutMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						logout(AbstractActivity.this, true);
						return true;
					}
				});
	}

	private void addUserSettingMenu(Menu menu) {
		MenuItem userSettingMenuItem = menu.add("Settings");
		userSettingMenuItem.setIcon(R.drawable.mimi_connect_settings);
		userSettingMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								AbstractActivity.this, "Settings",
								UserSettingActivity.class,
								R.layout.usercontact_tabview, false);
						return true;
					}
				});
	}

	private void addAboutmsoraMenu(Menu menu) {
		MenuItem userSettingMenuItem = menu.add("About");
		userSettingMenuItem.setIcon(R.drawable.mimi_connect_about);
		userSettingMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								AbstractActivity.this, "About Msora",
								AboutActivity.class,
								R.layout.usercontact_tabview);
						return true;
					}
				});
	}

	protected void addBackupRestoreOptionsMenu(Menu menu) {
		MenuItem backupRestoreMenuItem = menu.add("Backup/Restore");
		backupRestoreMenuItem.setIcon(R.drawable.mimi_connect_restoreimage);
		backupRestoreMenuItem
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						PhonebookActivity.startGeneralActivity(
								AbstractActivity.this, "Backup/Restore",
								BackupRestoreActivity.class,
								R.layout.usercontact_tabview, false);
						return true;
					}
				});
	}

	protected void addMenuOptions(Menu menu) {
		// does nothin
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// addBackupRestoreOptionsMenu(menu);
		addUserSettingMenu(menu);
		addAboutmsoraMenu(menu);
		addLogoutOptions(menu);
		addMenuOptions(menu);
		return super.onCreateOptionsMenu(menu);
	}

	private static void doLogout(final Activity context, final boolean finish) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					PersonalPhonebookActivity.endProgress();
					Settings.releaseSessionOnLogout();
					GeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// To avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<AbstractActivity> actTmps = new ArrayList<AbstractActivity>(
							activities);
					for (AbstractActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait, logging out.....", context);
			}

		}.execute();
	}

	private static void doLogout(final Activity context, final boolean finish,
			final OnRequestComplete<Boolean> requestComplete) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					PersonalPhonebookActivity.endProgress();
					Settings.releaseSessionOnLogout();
					GeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// To avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<AbstractActivity> actTmps = new ArrayList<AbstractActivity>(
							activities);
					for (AbstractActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
					if (requestComplete != null) {
						requestComplete.requestComplete(true);
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress(
						"Please wait, logging out.....", context);
			}

		}.execute();
	}

	private static void doLogoutInbackground(final Activity context,
			final boolean finish) {

		new AsyncTask<Void, Void, HttpResponseData>() {

			@Override
			protected HttpResponseData doInBackground(Void... params) {
				if (Settings.getSessionID() != null) {
					return HttpRequestManager.doRequestWithResponseData(
							Settings.getLogoutURL(),
							Settings.makeLogoutParameters());
				}
				return null;
			}

			@Override
			protected void onPostExecute(HttpResponseData result) {
				try {
					if (result != null
							&& result.getResponseStatus() != HttpResponseStatus.UNAVAILABLE) {
						Log.i("Logout: ", result.toString());
					}
				} finally {
					Settings.releaseSessionOnLogout();
					GeneralManager.clearSettings();
					if (finish) {
						context.finish();
					}
					// remove all other remaining activities;
					// on the activity finish method, we remove the activities
					// from the set.
					// to avoid ConcurrentModificationException, we iterate
					// through the activities through a temporary list.
					List<AbstractActivity> actTmps = new ArrayList<AbstractActivity>(
							activities);
					for (AbstractActivity act : actTmps) {
						if (act != context) {
							act.finish();
						}
					}
				}
			}

		}.execute();
	}

	public static void logout(final Activity context, final boolean finish) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirm logout");
		builder.setMessage("Are you sure, you want to log out?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loggedOut = true;
				if (DashBoardActivity.DASH_BOARD_ACTIVITY != null
						&& DashBoardActivity.DASH_BOARD_ACTIVITY
								.getChatManager() != null) {
					DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager()
							.logout();
				}
				doLogout(context, finish);
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void logout(final Activity context, final boolean finish,
			final OnRequestComplete<Boolean> requestComplete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Confirm logout");
		builder.setMessage("Are you sure, you want to log out?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				loggedOut = true;
				if (DashBoardActivity.DASH_BOARD_ACTIVITY != null
						&& DashBoardActivity.DASH_BOARD_ACTIVITY
								.getChatManager() != null) {
					DashBoardActivity.DASH_BOARD_ACTIVITY.getChatManager()
							.logout();
				}
				doLogout(context, finish, requestComplete);
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static void showYesOrNoOption(final Activity context,
			final String message, final String title,
			final OnRequestComplete<Boolean> onRequestComplete) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onRequestComplete.requestComplete(true);
			}
		});
		builder.setNegativeButton("No", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				onRequestComplete.requestComplete(false);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static <T> void showSelectOption(final String title,
			final Activity context, final List<T> selectionData,
			final OnRequestComplete<T> onRequestComplete,
			final StringConverter<T> converter) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		ArrayAdapter<T> adapter = new ArrayAdapter<T>(context,
				R.layout.usercontact_singlecontact_simpleview, selectionData) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View rowView = null;
				T data = selectionData.get(position);
				String dataStr = converter.toString(data);
				rowView = inflater.inflate(
						R.layout.usercontact_singlecontact_simpleview, parent,
						false);
				TextView textView = (TextView) rowView
						.findViewById(R.id.viewContact);
				textView.setText(dataStr);
				return rowView;
			}
		};
		builder.setAdapter(adapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				onRequestComplete.requestComplete(selectionData.get(which));
			}
		});
		builder.setNegativeButton("Cancel", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public static interface OnRequestComplete<Result> {
		void requestComplete(Result result);
	}

	public static interface StringConverter<Ob> {
		String toString(Ob ob);
	}
}
