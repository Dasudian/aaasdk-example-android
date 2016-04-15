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
    
    ArrayList<ContactsInfo> infoList = new ArrayList<ContactsInfo>();// 用来保存所有联系人
    ArrayList<ContactsInfo> ContactsToShow = new ArrayList<ContactsInfo>();// 用来保存要显示在listview上的联系人
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
        
        adapter = new ListAdapter(SynContactsActivity.this, R.layout.list_item, ContactsToShow);
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
	      				for (int i = 0; i < infoList.size(); i++) {
							jsonArray.put(infoList.get(i).getPhoneNumber()
									.replace(" ", "")// 去掉所有的空格和'-',否则服务器无法正常解析
									.replace("-", ""));
						}
	      				
	      				Log.d(TAG, jsonArray.toString());
	      				return DsdLibAAA.dsdAAASyncContact(jsonArray.toString());
	      			}
	      			protected void onPostExecute(String retString) {
	      				filterContacts(retString);
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
				AsyncTask<String, Void, String> download = new AsyncTask<String, Void, String>() {
	      			protected String doInBackground(String... paramAnonymousArrayOfString) {
	      				JSONArray jsonArray = new JSONArray();
	      				
	      				for (int i = 0; i < infoList.size(); i++) {
							jsonArray.put(infoList.get(i).getPhoneNumber());
						}
	      				
	      				Log.d(TAG, jsonArray.toString());
	      				return DsdLibAAA.dsdAAAGetContact(phoneNumber);
	      			}
	      			protected void onPostExecute(String retString) {
	      				filterContacts(retString);
	      			}
	            };
	            download.execute();
			}
		});
    }
    
    /**
     * 根据服务器返回的结果，从所有联系人列表中过滤出已经注册的联系人，并加入ContactsToShow列表。
     * @param retString
     */
    private void filterContacts(String retString) {
    	ContactsToShow.removeAll(ContactsToShow);
		try {
			JSONObject json = new JSONObject(retString);
			String resultString = json.getString("result");
			if (resultString.length() != 0 && resultString.equals("success")) {
				String contactsString = json.getString("contacts");
				JSONArray jsonArray = new JSONArray(contactsString);
				
				for (int i = 0; i < jsonArray.length(); i++) {
					String phoneString = jsonArray.get(0).toString();
					for (int j = 0; j < infoList.size(); j++) {
						ContactsInfo contact = infoList.get(j);
						if (contact.getPhoneNumber().equals(phoneString)) {
							ContactsToShow.add(new ContactsInfo(contact.getName(), 
									contact.getPhoneNumber()));
							break;
						}
					}
				}
				adapter.notifyDataSetChanged();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
