package com.example.macos.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author HieuND9
 */
public final class SharedPreferenceManager {
	private static final String CONFIGURATION_NAME = "ROAD PROJECT APPLICATION CONFIG";
	private SharedPreferences _shareRefs = null;
	
	public SharedPreferenceManager(Context context) {
		_shareRefs = context.getSharedPreferences(CONFIGURATION_NAME,
				Activity.MODE_PRIVATE);
	}

	public SharedPreferenceManager(Activity active) {
		_shareRefs = active.getSharedPreferences(CONFIGURATION_NAME,
				Activity.MODE_PRIVATE);
	}

	public void saveDouble(String key, double value) {
		String dValue = String.valueOf(value);
		Editor editor = _shareRefs.edit();
		editor.putString(key, dValue);
		editor.commit();
	}

	public double getDouble(String key, double defVa) {
		String strDefVa = String.valueOf(defVa);
		String dValue = _shareRefs.getString(key, strDefVa);
		return (dValue.equals(strDefVa) == true) ? defVa : Double
				.valueOf(dValue);
	}

	public void saveFloat(String key, float value) {
		Editor editor = _shareRefs.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public float getFloat(String key, float defVa) {
		return _shareRefs.getFloat(key, defVa);
	}

	public void saveBoolean(String key, boolean value) {
		Editor editor = _shareRefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, boolean defVa) {
		return _shareRefs.getBoolean(key, defVa);
	}

	public void saveString(String key, String value) {
		Editor editor = _shareRefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void saveStringSetting(String key1, String key2, String value1,
			String value2) {
		Editor editor = _shareRefs.edit();
		editor.putString(key1, value1);
		editor.commit();
	}

	public String getString(String key, String defVa) {
		return _shareRefs.getString(key, defVa);
	}

	public void saveInt(String key, int value) {

		Editor editor = _shareRefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key, int defVa) {
		return _shareRefs.getInt(key, defVa);
	}

	public void saveLong(String key, long value) {

		Editor editor = _shareRefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getLong(String key, long defVa) {
		return _shareRefs.getLong(key, defVa);
	}

	/**
	 * clear all sharedPreferences
	 */
	public void clearAll() {
		Editor editor = _shareRefs.edit();
		editor.clear();
		editor.commit();
	}

	/**
	 * remove value sharePreference with key
	 * 
	 * @param key
	 */
	public void remove(String key) {
		Editor editor = _shareRefs.edit();
		editor.remove(key);
		editor.commit();
	}
}