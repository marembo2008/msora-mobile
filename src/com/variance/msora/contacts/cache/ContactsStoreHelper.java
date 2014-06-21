package com.variance.msora.contacts.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ContactsStoreHelper extends SQLiteOpenHelper {

	public static final int Msora_PROTECT_DB_VERSION = 15;
	public static final String Msora_PROTECT_DB_NAME = "Msora_PROTECT_CONTACT_DB2013";

	public ContactsStoreHelper(Context context) {
		super(context, Msora_PROTECT_DB_NAME, null, Msora_PROTECT_DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.e("onCreate", "creating database");
		try {
			db.execSQL(ContactsTable.CONTACTS_TABLE_CREATE_QUERY);
			db.execSQL(ChatTable.CHAT_TABLE_CREATE_QUERY);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldDbVersion, int newDbVersion) {
		try {
			Log.i("onUpgrade: oldDbVersion", oldDbVersion + "");
			Log.i("onUpgrade: newDbVersion", newDbVersion + "");
			if (oldDbVersion != newDbVersion) {
				Log.i("onUpgrade", "Upgrading sqlite database.....");
				db.execSQL("DROP TABLE IF EXISTS " + ChatTable.CHAT_TABLE_NAME);
				db.execSQL("DROP TABLE IF EXISTS "
						+ ContactsTable.CONTACTS_TABLE_NAME);
				onCreate(db);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		Log.i("onOpen", "Database read only: " + db.isReadOnly());
		Log.i("onOpen", "Database read only: " + db.isOpen());
	}
}
