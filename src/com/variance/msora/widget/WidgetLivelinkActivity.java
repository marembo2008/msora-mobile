package com.variance.msora.widget;

import java.util.HashMap;

import android.os.Bundle;

import com.variance.msora.ui.LiveLinkRequestsActivity;
import com.variance.msora.ui.AbstractActivity;
import com.variance.msora.ui.SplashScreenActivity;

public class WidgetLivelinkActivity extends AbstractActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// from here. we simply call livelink activity request
		SplashScreenActivity.initializeLoadmsora(this, getIntent(),
				LiveLinkRequestsActivity.class, "Livelinks",
				new HashMap<String, String>(), true);
	}

}
