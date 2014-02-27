package com.variance.msora.contacts.business;

import java.util.List;

import com.anosym.vjax.annotations.v3.GenericCollectionType;
import com.anosym.vjax.v3.VObjectMarshaller;

public class SmsMessage {
	@GenericCollectionType(String.class)
	private List<String> contactIds;
	private String message;

	public List<String> getContactIds() {
		return contactIds;
	}

	public void setContactIds(List<String> contactIds) {
		this.contactIds = contactIds;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SmsMessage() {
		super();
	}

	public SmsMessage(List<String> contactIds, String message) {
		super();
		this.contactIds = contactIds;
		this.message = message;
	}

	@Override
	public String toString() {
		return new VObjectMarshaller<SmsMessage>(SmsMessage.class)
				.doMarshall(this);
	}
}
