package com.dasudian.dsdaaaexample;

import org.json.JSONException;
import org.json.JSONObject;

import com.dasudian.AAA.DsdLibAAA;
import com.dasudian.AAA.DsdLibAAAGetAvatarListener;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends Activity {
	private String phoneNumber;
	private EditText nameEditText;
	private EditText signatureEditText;
	private Button changeNameButton;
	private Button changeSignatureButton;
	private Button changeIconButton;
	private TextView sexTextView;
	private TextView birthdayTextView;
	private TextView emailTextView;
	private TextView phoneNumberTextView;
	private TextView areaTextView;
	private ImageView iconImageView;
	private EditText oldPasswordEditText;
	private EditText newPasswordEditText;
	private Button changePasswordButton;
	public static final int SET_ICON = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
        
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        
        
        iconImageView = (ImageView)findViewById(R.id.icon);
        nameEditText = (EditText)findViewById(R.id.ui_name);
        changeNameButton = (Button)findViewById(R.id.change_name);
        changeNameButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = nameEditText.getText().toString();
				if (DsdAAAUtils.stringisEmpty(name)) {
					Toast.makeText(UserInfoActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 修改昵称
				 */
				String retString = DsdLibAAA.dsdAAAChangeName(name);
				DsdAAAUtils.checkResult(UserInfoActivity.this, retString);
			}
		});
        signatureEditText = (EditText)findViewById(R.id.ui_signature);
        changeSignatureButton = (Button)findViewById(R.id.change_signature);
        changeSignatureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String signature = signatureEditText.getText().toString();
				if (DsdAAAUtils.stringisEmpty(signature)) {
					Toast.makeText(UserInfoActivity.this, "个性签名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 修改个性签名
				 */
				String retString = DsdLibAAA.dsdAAAChangeSignature(signature);
				DsdAAAUtils.checkResult(UserInfoActivity.this, retString);
			}
		});
        sexTextView = (TextView)findViewById(R.id.ui_sex);
        birthdayTextView = (TextView)findViewById(R.id.ui_birthday);
        emailTextView = (TextView)findViewById(R.id.ui_email);
        phoneNumberTextView = (TextView)findViewById(R.id.ui_phonenumber);
        areaTextView = (TextView)findViewById(R.id.ui_area);
        
        changeIconButton = (Button)findViewById(R.id.change_icon);
        changeIconButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("android.intent.action.GET_CONTENT");
				intent.setType("image/*");
				startActivityForResult(intent, SET_ICON);
			}
		});
        
        
        /**
         * 获取用户信息
         */
        AsyncTask<Object, Void, String> dsdAAAInit = new AsyncTask<Object, Void, String>() {
			protected String doInBackground(Object... paramAnonymousArrayOfString) {
				return DsdLibAAA.dsdAAAGetUser(phoneNumber);
			}

			protected void onPostExecute(String retString) {
				if (retString != null) {
					Log.d("UserInfoActivity", retString);
					try {
						JSONObject jsonObject = new JSONObject(retString);
						String result = jsonObject.getString("result");
						if (result.length() != 0 && result.equals("success")) {
							JSONObject userInfo = new JSONObject(jsonObject.getString("info"));
							String name = userInfo.getString("name");
							nameEditText.setText(name);
							
							String signature = userInfo.getString("signature");
							signatureEditText.setText(signature);
							
							String sex = userInfo.getString("sex");
							if (sex.length() != 0 && sex.equals("male")) {
								sexTextView.setText("男");
							} else {
								sexTextView.setText("女");
							}
							
							String birthday = userInfo.getString("birthday");
							birthdayTextView.setText(birthday);
							
							String phone_num = userInfo.getString("phone_num");
							phoneNumberTextView.setText(phone_num);
							
							String email = userInfo.getString("email");
							emailTextView.setText(email);
							
							String area = userInfo.getString("area");
							areaTextView.setText(area);
							
							String UUID = userInfo.getString("avatar");
							DsdLibAAA.dsdAAAGetAvatar(UUID, new DsdLibAAAGetAvatarListener() {
								
								@Override
								public void onSuccess(Bitmap bitmap) {
									iconImageView.setImageBitmap(bitmap);
								}
								
								@Override
								public void onFailed(String arg0) {
									
								}
							});
						} else {
							Toast.makeText(UserInfoActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			}
		};
		dsdAAAInit.execute();
        
		oldPasswordEditText = (EditText)findViewById(R.id.old_password);
		newPasswordEditText = (EditText)findViewById(R.id.new_password);
		changePasswordButton = (Button)findViewById(R.id.change_password);
		changePasswordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String oldPassword = oldPasswordEditText.getText().toString();
				if (DsdAAAUtils.stringisEmpty(oldPassword)) {
					Toast.makeText(UserInfoActivity.this, "旧密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String newPassword = newPasswordEditText.getText().toString();
				if (DsdAAAUtils.stringisEmpty(newPassword)) {
					Toast.makeText(UserInfoActivity.this, "新密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 修改密码
				 */
				String retString = DsdLibAAA.dsdAAAChangePasswd(oldPassword, newPassword);
				DsdAAAUtils.checkResult(UserInfoActivity.this, retString);
			}
		});
    }
    
    
    @Override
  	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  		super.onActivityResult(requestCode, resultCode, data);
          if (requestCode == SET_ICON && resultCode == RESULT_OK && null != data) {
              Uri selectedImage = data.getData();
              String[] filePathColumn = { MediaStore.Images.Media.DATA };
              Cursor cursor = getContentResolver().query(selectedImage,
                      filePathColumn, null, null, null);
              cursor.moveToFirst();
              int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
              String picturePath = cursor.getString(columnIndex);
              iconImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
              cursor.close();
              
              
              /**
               * 上传头像
               */
              String retString = DsdLibAAA.dsdAAASetAvatar(picturePath);
              DsdAAAUtils.checkResult(UserInfoActivity.this, retString);
          }
  	}
}
