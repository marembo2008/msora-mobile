package com.variance.msora.contacts.selection;

import java.util.List;

import com.variance.msora.contacts.Contact;

public interface OnContactSelectionComplete {
	void contactSelected(List<Contact> selectedContacts);
}
