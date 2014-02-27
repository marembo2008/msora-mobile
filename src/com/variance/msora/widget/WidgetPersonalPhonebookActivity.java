package com.variance.msora.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.SplashScreenActivity;

public class WidgetPersonalPhonebookActivity extends
		AbstractActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// From here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadmsora(this, getIntent(),
				PersonalPhonebookActivity.class, "My Phonebook",
				new HashMap<String, String>(), false);
	}
}
