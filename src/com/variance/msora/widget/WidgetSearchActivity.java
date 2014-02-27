package com.variance.msora.widget;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;

import com.variance.mimiprotect.R;
import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.SplashScreenActivity;
import com.variance.msora.util.IntentConstants;

public class WidgetSearchActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usercontact_widgetphonebooksearch);
		addSoftKeyEnterActionOnSearch();
	}

	public void handleSearch(View view) {
		String searchTerm = ((EditText) findViewById(R.id.txtWidgetSearch))
				.getText().toString();
		Map<String, String> intentExtras = new HashMap<String, String>();
		intentExtras.put(IntentConstants.Msora_PROTECT_SEARCH_TERM,
				searchTerm);
		SplashScreenActivity.initializeLoadmsora(this, getIntent(),
				PersonalPhonebookActivity.class, "My Phonebook", intentExtras,
				true);
	}

	private void addSoftKeyEnterActionOnSearch() {
		EditText searchText = (EditText) findViewById(R.id.txtWidgetSearch);
		searchText.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					switch (keyCode) {
					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						handleSearch(null);
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
	}

}
