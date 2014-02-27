package com.variance.msora.ui.contact.task;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.Contact;
import com.variance.msora.contacts.managers.ContactViewManager;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.response.HttpResponseStatus;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.ui.contact.DiscoveryActivity;
import com.variance.msora.util.DataParser;
import com.variance.msora.util.SearchParameter;
import com.variance.msora.util.Settings;

public class DiscoveryTask extends
		AsyncTask<SearchParameter, Void, HttpResponseData> {
	private DiscoveryActivity context;
	private SearchParameter searchParameter;

	public DiscoveryTask(DiscoveryActivity context,
			SearchParameter searchParameter) {
		super();
		this.context = context;
		this.searchParameter = searchParameter;
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Please wait, discovering....",
				context, this);
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		PersonalPhonebookActivity.endProgress();
		if (result == null) {
			Toast.makeText(context, "Sorry! There is no internet Connection.",
					Toast.LENGTH_LONG).show();
			return;
		} else if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			loadCustomView(result.getMessage());
		} else {
			Toast.makeText(context, "An error has occurred: " + result,
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	protected HttpResponseData doInBackground(SearchParameter... params) {
		try {
			Log.e("DiscoveryTask:", "DiscoveryTask");
			return HttpRequestManager.doRequestWithResponseData(Settings
					.getDiscoverySearchContactUrl(), Settings
					.makeDiscoverySearchContactParameter(searchParameter),
					context);
		} catch (Exception e) {
			Log.e("log_tag", "Error converting result " + e.toString());
			return null;
		}
	}

	private void loadCustomView(String result) {
		Log.i("Search Result", result + "");
		if (result != null && !"".equals(result)) {
			ArrayList<Contact> businessContacts = DataParser
					.getCorporateContacts(result);
			ArrayList<Contact> personalContacts = DataParser
					.getPersonalContacts(result);
			ContactViewManager contactViewManager = new ContactViewManager(
					businessContacts, personalContacts, (Activity) context);
			boolean demarcate = false;
			contactViewManager.initialize(R.id.personalContactView, true,
					false, demarcate);
		}
	}
}
