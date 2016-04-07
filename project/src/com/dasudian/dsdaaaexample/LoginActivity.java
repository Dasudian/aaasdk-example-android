package com.dasudian.dsdaaaexample;

import org.json.JSONException;
import org.json.JSONObject;

import com.dasudian.AAA.DsdLibAAA;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final String TAG = "LoginActivity";
	private Button newUserButton;
	private Button forgetPasswordButton;
	private Button loginButton;
	private EditText phoneNumberEditText;
	private EditText passwordEditText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        /**
         * 初始化sdk
         */
        AsyncTask<Object, Void, Integer> dsdAAAInit = new AsyncTask<Object, Void, Integer>() {
			protected Integer doInBackground(Object... paramAnonymousArrayOfString) {

//				String aucServer = "https://aaa_auc.test.dsd:8443/auc_app";
//				String appId = "101_96TlceWKEJcJasPsDg_A";
//				String appKey = "115e22e3f7b726bf";
				// joe
				String appId = "139_A_92ECUvrZ4A6IrP3tz6";
				String appKey = "0a03d9c67217092b";
				String aucServer = "https://192.168.1.47:8443/auc_app";
				int ret = DsdLibAAA.dsdAAAInit(aucServer, appId, appKey);
				return ret;
			}

			protected void onPostExecute(Integer ret) {
				SharedPreferences pref = getSharedPreferences("cookie",MODE_PRIVATE);
				if (ret == 0) {
					Log.d(TAG, "初始化成功");
					Toast.makeText(LoginActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
					
					/**
					 * 自动登录,如果有cookie存在则尝试自动登录。
					 */
					String cookie = pref.getString("cookie", "");
					String phoneNumber = pref.getString("phone", "");
					if (cookie != null) {
						Log.d(TAG, "cookie = " + cookie+"phone = "+phoneNumber);
						String retString = DsdLibAAA.dsdAAAAutoLogin(cookie);
						if (DsdAAAUtils.checkResult(LoginActivity.this, retString)) {
							toUserInfoActivity(phoneNumber);
						}
					}
				} else {
					Log.d(TAG, "初始化失败");
					Toast.makeText(LoginActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
				}
			}
		};
		dsdAAAInit.execute();
        
		
        phoneNumberEditText = (EditText)findViewById(R.id.phone_number);
        passwordEditText = (EditText)findViewById(R.id.password);
        loginButton = (Button)findViewById(R.id.login);
        loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (TextUtils.isEmpty(phoneNumber.trim())) {
					Toast.makeText(LoginActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String password = passwordEditText.getText().toString();
				if (TextUtils.isEmpty(password.trim())) {
					Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 登录
				 */
				String retString = DsdLibAAA.dsdAAALogin(phoneNumber, password);
				try {
					JSONObject jsonObject = new JSONObject(retString);
					String result = jsonObject.getString("result");
					if (result.length() != 0 && result.equals("success")) {
						String cookie = jsonObject.getString("cookie");
						if (cookie.length() != 0) {
							/**
							 * 保存cookie，以便自动登录时使用
							 */
							SharedPreferences.Editor editor = 
									getSharedPreferences("cookie",MODE_PRIVATE).edit();
							editor.putString("cookie", cookie);
							editor.putString("phone", phoneNumber);
							editor.commit();
						}
						toUserInfoActivity(phoneNumber);
					} else {
						Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
        
        newUserButton = (Button)findViewById(R.id.new_user);
        newUserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, PreRegisterActivity.class);
				startActivity(intent);
			}
		});
        
        forgetPasswordButton = (Button)findViewById(R.id.forget_password);
        forgetPasswordButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
			}
		});
        
        
    }
    
    private void toUserInfoActivity(String phoneNumber) {
    	Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
}
