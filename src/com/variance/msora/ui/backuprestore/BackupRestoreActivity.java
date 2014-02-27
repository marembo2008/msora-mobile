package com.variance.msora.ui.backuprestore;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.variance.mimiprotect.R;
import com.variance.msora.ui.GeneralTabActivity;
import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.util.IntentConstants;

public class BackupRestoreActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_backuprestore);
	}

	public void backupRestoreOption(View view) {
		switch (view.getId()) {
		case R.id.btnBackupOption: {
			Intent intent = new Intent(this, GeneralTabActivity.class);
			Intent callingintent = this.getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
					BackupActivity.class.getName());
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_TITLE,
					"Msora");
			this.startActivity(intent);
		}
			break;
		case R.id.btnRestoreOption: {
			Intent intent = new Intent(this, GeneralTabActivity.class);
			Intent callingintent = this.getIntent();
			if (callingintent != null) {
				intent.putExtras(callingintent);
			}
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_CLASS,
					RestoreActivity.class.getName());
			intent.putExtra(
					IntentConstants.Msora_PROTECT_ACTIVITY_TITLE,
					"Msora");
			this.startActivity(intent);
		}
			break;
		}
	}
}
