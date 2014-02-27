package com.variance.msora.ui.backuprestore;

import java.util.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.task.BackupTask;
import com.variance.msora.contacts.task.HttpRequestTask;
import com.variance.msora.contacts.task.HttpRequestTaskListener;
import com.variance.msora.internal.FileManagementActivity;
import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.util.GeneralManager;

public class BackupActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_backuprestore_options);
	}

	public void backupAction(View view) {
		switch (view.getId()) {
		case R.id.llContactsAction:
		case R.id.llContactsActionRb:
			new BackupTask(this).execute(new Void[] {});
			cleanCacheOnBackup();
			break;
		case R.id.llDocumentsAction:
		case R.id.llMusicOrRingtonAction:
		case R.id.llPhotosAction:
		case R.id.llDocumentsActionRb:
		case R.id.llMusicOrRingtonActionRb:
		case R.id.llPhotosActionRb: {
			Intent backupFilesIntent = new Intent(this,
					FileManagementActivity.class);
			backupFilesIntent.putExtra(
					FileManagementActivity.BACKUP_RESTORE_CODE,
					FileManagementActivity.BACKUP_FILES_CODE);
			startActivity(backupFilesIntent);
		}
			break;
		}
	}

	private void cleanCacheOnBackup() {
		HttpRequestTaskListener<Void, Void> listener = new HttpRequestTaskListener<Void, Void>() {

			public void onTaskStarted() {

			}

			public void onTaskCompleted(Void result) {
			}

			public Void doTask(Void... params) {
				GeneralManager.clearCache();
				Calendar cal = Calendar.getInstance();
				cal.set(1972, 0, 1);
				GeneralManager.getUserSetting().setLastCacheUpdate(
						cal);
				GeneralManager.updateUserSetting();
				return null;
			}
		};
		new HttpRequestTask<Void, Void, Void>(listener, null, this)
				.executeInBackground();
	}
}
