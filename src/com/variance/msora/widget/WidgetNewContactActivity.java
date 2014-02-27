package com.variance.msora.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.ui.SplashScreenActivity;
import com.variance.msora.ui.contact.NewContactActivity;

public class WidgetNewContactActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// from here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadmsora(this, getIntent(),
				NewContactActivity.class, "New Contact",
				new HashMap<String, String>(), true);
	}

}
