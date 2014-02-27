package com.variance.msora.livelink;

import java.util.List;

import android.widget.ListView;

import com.variance.mimiprotect.R;
import com.variance.msora.livelink.util.LiveLinkRequest;
import com.variance.msora.ui.LiveLinkRequestsActivity;

public class LivelinkViewManager {
	private LiveLinkRequestsActivity requestsActivity;
	private List<LiveLinkRequest> requests;

	public LivelinkViewManager(LiveLinkRequestsActivity requestsActivity,
			List<LiveLinkRequest> requests) {
		super();
		this.requestsActivity = requestsActivity;
		this.requests = requests;
	}

	public void initialize() {
		ListView view = (ListView) requestsActivity
				.findViewById(R.id.liveLinkRequestsView);
		view.setAdapter(new LivelinkListAdapter(requestsActivity,
				R.layout.mimi_connect_singlelinkrequest, requests));
	}
}
