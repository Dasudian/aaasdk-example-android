package com.dasudian.dsdaaaexample;

import com.dasudian.AAA.DsdLibAAA;

import android.app.Activity;
import android.content.Intent;
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

				String aucServer = "https://aaa_auc.test.dsd:8443/auc_app";
				String appId = "101_96TlceWKEJcJasPsDg_A";
				String appKey = "115e22e3f7b726bf";
				int ret = DsdLibAAA.dsdAAAInit(aucServer, appId, appKey);
				return ret;
			}

			protected void onPostExecute(Integer ret) {
				if (ret == 0) {
					Toast.makeText(LoginActivity.this, "初始化成功", Toast.LENGTH_SHORT).show();
				} else {
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
				String retString = DsdLibAAA.dsdAAALogin(phoneNumber, password);
				if (DsdAAAUtils.checkResult(LoginActivity.this, retString)) {
					Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
					intent.putExtra("phoneNumber", phoneNumber);
					startActivity(intent);
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
}
