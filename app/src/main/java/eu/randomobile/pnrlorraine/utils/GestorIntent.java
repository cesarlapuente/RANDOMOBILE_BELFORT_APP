package eu.randomobile.pnrlorraine.utils;

import java.io.Serializable;

import android.content.Intent;
import android.os.Parcelable;

public abstract class GestorIntent {

	public static Parcelable getParcelableObject(final Intent i,
			final String objectName) {
		Parcelable parcelable = null;
		try {
			parcelable = i.getExtras().getParcelable(objectName);
		} catch (Exception ex) {
		}
		return parcelable;
	}

	public static Serializable getSerializableObject(final Intent i,
			final String objectName) {
		Serializable parcelable = null;
		try {
			parcelable = i.getExtras().getSerializable(objectName);
		} catch (Exception ex) {
		}
		return parcelable;
	}

	public static String getString(final Intent i, final String objectName) {
		String value = "";
		try {
			value = i.getExtras().getString(objectName);
		} catch (Exception ex) {
		}
		return value;
	}

}
