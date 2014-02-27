package com.variance.msora.widget;

import java.util.ArrayList;
import java.util.List;

import com.anosym.vjax.annotations.v3.GenericCollectionType;
import com.anosym.vjax.v3.VObjectMarshaller;
import com.variance.msora.contacts.Contact;

public class FastDialContacts {
	public static final String FAST_DIAL_CONTACT_PREFERENCE_ID = "fastDial_0x77888888";

	/**
	 * Currently we only support three contacts for fast dial. All other
	 * contacts will be ignored.
	 */
	@GenericCollectionType(Contact.class)
	private List<Contact> fastDials;

	public FastDialContacts() {
		fastDials = new ArrayList<Contact>();
	}

	public List<Contact> getFastDials() {
		return fastDials;
	}

	public void setFastDials(List<Contact> fastDials) {
		this.fastDials = fastDials;
	}

	@Override
	public String toString() {
		return new VObjectMarshaller<FastDialContacts>(FastDialContacts.class)
				.doMarshall(this);
	}
}
