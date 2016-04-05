package com.dasudian.dsdaaaexample;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class DsdAAAUtils {
	private final static String TAG = "DsdAAACheckResult";
	
	public DsdAAAUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean checkResult(Context context, String retString) {
		if (retString == null) {
    		return false;
    	}
    	Log.d(TAG, retString);
    	try {
			JSONObject json = new JSONObject(retString);
			String result = json.getString("result");
			if (!result.isEmpty() && "success".equals(result)) {
				Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
				return true;
			} else {
				Toast.makeText(context, "失败", Toast.LENGTH_SHORT).show();
				return false;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isEmailValid(String email) {
        //TODO change for your own logic
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }
	
	public static boolean stringisEmpty(String str) {
		return TextUtils.isEmpty(str.trim());
	}

}
