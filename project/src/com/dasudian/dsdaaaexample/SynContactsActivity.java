package com.dasudian.dsdaaaexample;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dasudian.AAA.DsdLibAAA;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SynContactsActivity extends Activity {
	private final String TAG = "SynContactsActivity";
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;  
    private static final int PHONES_NUMBER_INDEX = 1;  
    
    ArrayList<ContactsInfo> infoList = new ArrayList<ContactsInfo>();
    private ListAdapter adapter;
    
    private String phoneNumber;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syncontacts);
        
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        
        getPhoneContacts();
        getSIMContacts();
        
        adapter = new ListAdapter(SynContactsActivity.this, R.layout.list_item, infoList);
		ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(adapter);
		
		Button uploadButton = (Button)findViewById(R.id.upload);
		uploadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * 同步联系人到服务器
				 */
				AsyncTask<String, Void, String> upload = new AsyncTask<String, Void, String>() {
	      			protected String doInBackground(String... paramAnonymousArrayOfString) {
	      				JSONArray jsonArray = new JSONArray();
	      				
	      				try {
	      					for (int i = 0; i < infoList.size(); i++) {
	      						JSONObject jsonObject = new JSONObject();
								jsonObject.put("name", infoList.get(i).getName());
								jsonObject.put("phone", infoList.get(i).getPhoneNumber());
								jsonArray.put(i, jsonObject);
	      					}
						} catch (JSONException e1) {
							e1.printStackTrace();
						}
	      				
	      				Log.d(TAG, jsonArray.toString());
	      				return DsdLibAAA.dsdAAASyncContact(jsonArray.toString());
	      			}
	      			protected void onPostExecute(String retString) {
	      				DsdAAAUtils.checkResult(SynContactsActivity.this, retString);
	      			}
	            };
	            upload.execute();
			}
		});
		
		Button downloadButton = (Button)findViewById(R.id.download);
		downloadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/**
				 * 从服务器下载联系人信息
				 */
				String retString = DsdLibAAA.dsdAAAGetContact(phoneNumber);
				DsdAAAUtils.checkResult(SynContactsActivity.this, retString);
			}
		});
    }
    
    private void getPhoneContacts() {
		ContentResolver resolver = this.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber)) {
				    continue;
				}
				
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);	
				infoList.add(new ContactsInfo(contactName, phoneNumber));
				Log.d(TAG, "contactName:" + contactName + ", phoneNumber:" + phoneNumber);	
		    }
		    phoneCursor.close();
		}
    }
    
    private void getSIMContacts() {
		ContentResolver resolver = this.getContentResolver();
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null, null);
	
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				if (TextUtils.isEmpty(phoneNumber))
				    continue;
				String contactName = phoneCursor
					.getString(PHONES_DISPLAY_NAME_INDEX);
						
				infoList.add(new ContactsInfo(contactName, phoneNumber));
				Log.d(TAG, "contactName:" + contactName + ", phoneNumber:" + phoneNumber);
		    }
		    phoneCursor.close();
		}
    }
}
