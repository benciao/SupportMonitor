package benciao.android.supportmonitor.util;

import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import benciao.android.supportmonitor.domain.User;

public class SupportMonitorUtil
{
	/**
	 * Name des Telefonkontaktes, unter welchem die eigene Bereitschaftsnummer
	 * gespeichert werden muss.
	 */
	public static final String	SUPPORT_TELEPHONE_CONTACT	= "Meine ECG Bereitschaftsnummer";

	public static String getRegisteredMobilePhoneNumber(
			ContentResolver contentResolver)
	{
		String mobilePhoneNumber = "0";
		try
		{
			Cursor cursor = contentResolver.query(
					ContactsContract.Contacts.CONTENT_URI, null, null, null,
					null);
			if (cursor.getCount() > 0)
			{
				while (cursor.moveToNext())
				{
					String contactId = cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts._ID));

					String name = cursor
							.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					if (name.equalsIgnoreCase(SUPPORT_TELEPHONE_CONTACT))
					{
						if (Integer
								.parseInt(cursor.getString(cursor
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
						{
							Cursor numberCursor = contentResolver
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = ?",
											new String[] { contactId }, null);
							while (numberCursor.moveToNext())
							{
								mobilePhoneNumber = numberCursor
										.getString(numberCursor
												.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							}
							numberCursor.close();
						}
					}
				}

				cursor.close();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return mobilePhoneNumber;
	}

	public static User getCurrentUser(List<User> users,
			ContentResolver contentResolver)
	{
		User result = null;
		String currentUserId = getRegisteredMobilePhoneNumber(contentResolver);

		for (User user : users)
		{
			if (user.getMobilePhone().equals(Long.parseLong(currentUserId)))
			{
				result = user;
				break;
			}
		}

		return result;
	}
}
