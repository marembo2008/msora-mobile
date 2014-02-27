package com.variance.msora.contacts.business.task;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.variance.mimiprotect.R;
import com.variance.msora.contacts.Contact;
import com.variance.msora.contacts.business.BusinessContactActivity;
import com.variance.msora.contacts.business.SelectBusinessContactFromPersonalContactActivity;
import com.variance.msora.request.HttpRequestManager;
import com.variance.msora.response.HttpResponseData;
import com.variance.msora.response.HttpResponseStatus;
import com.variance.msora.ui.PersonalPhonebookActivity;
import com.variance.msora.util.DataParser;
import com.variance.msora.util.GeneralManager;
import com.variance.msora.util.SearchParameter;
import com.variance.msora.util.Settings;

public class BusinessContactLoadFromPersonalContactTask extends
		AsyncTask<SearchParameter, Void, HttpResponseData> {
	private SelectBusinessContactFromPersonalContactActivity context;
	private SearchParameter searchParameter;
	private ListView view;
	private List<Contact> selectedContacts;

	public BusinessContactLoadFromPersonalContactTask(
			SelectBusinessContactFromPersonalContactActivity context,
			List<Contact> selectedContacts) {
		this.context = context;
		view = (ListView) context
				.findViewById(R.id.listSelectFromPersonalContactView);
		this.selectedContacts = selectedContacts;
	}

	@Override
	protected void onPostExecute(HttpResponseData result) {
		PersonalPhonebookActivity.endProgress();
		if (result != null
				&& result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
			final ArrayList<Contact> contacts = DataParser
					.getPersonalContacts(result.getMessage());
			BusinessContacSelectFromPersonalContacttAdapter adapter = new BusinessContacSelectFromPersonalContacttAdapter(
					context,
					R.layout.businesscontact_selectfrompersonalcontact_singlecontactview,
					contacts, selectedContacts);
			view.setAdapter(adapter);
		}
	}

	@Override
	protected void onPreExecute() {
		PersonalPhonebookActivity.showProgress("Loading contacts...", context,
				this);
	}

	@Override
	protected HttpResponseData doInBackground(SearchParameter... params) {
		// We load all the personal contacts
		searchParameter = params[0];
		return HttpRequestManager.doRequestWithResponseData(
				Settings.getSearchContactUrl(),
				Settings.makeLoadAllPersonalContactsParameter(searchParameter));
	}

	public void addBusinessContacts() {
		new AsyncTask<String, String, HttpResponseData>() {

			@Override
			protected void onPostExecute(HttpResponseData result) {
				PersonalPhonebookActivity.endProgress();
				if (result != null) {
					Toast.makeText(
							context,
							result.getResponseStatus() + ": "
									+ result.getMessage(), Toast.LENGTH_SHORT)
							.show();
					searchParameter.setMaxRecords(BusinessContactActivity
							.maxBusinessRecords());
					// context.doSearch();
					if (result.getResponseStatus() == HttpResponseStatus.SUCCESS) {
						// cache this to the phone
						if (GeneralManager.hasCurrentPhoneLock()) {
							GeneralManager
									.onNewContactAdded(context);
						}
					}
				}
			}

			@Override
			protected void onPreExecute() {
				PersonalPhonebookActivity.showProgress("Adding contacts...",
						context);
			}

			@Override
			protected HttpResponseData doInBackground(String... params) {
				return HttpRequestManager
						.doRequestWithResponseData(
								Settings.getBusinessContactUrl(),
								Settings.makeAddBusinessContactFromPersonalContactParameter(selectedContacts));
			}

		}.execute(new String[] {});
	}
}
