package com.variance.msora.livelink.util;

import java.util.List;

import com.anosym.vjax.annotations.v3.GenericCollectionType;

public class LivelinkRequests {
	@GenericCollectionType(LiveLinkRequest.class)
	private List<LiveLinkRequest> livelinkRequests;

	public List<LiveLinkRequest> getLivelinkRequests() {
		return livelinkRequests;
	}

	public void setLivelinkRequests(List<LiveLinkRequest> livelinkRequests) {
		this.livelinkRequests = livelinkRequests;
	}

}
