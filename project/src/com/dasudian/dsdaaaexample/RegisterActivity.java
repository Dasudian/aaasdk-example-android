package com.dasudian.dsdaaaexample;

import com.dasudian.AAA.DsdLibAAA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	private final String TAG = "RegisterActivity";
	private EditText passwordEditText;
	private EditText nameEditText;
	private RadioGroup sexRadioGroup;
	private Button birthdayButton;
	private EditText emailEditText;
	private Button areaButton;
	private EditText signatureEditText;
	private Button registerButton;
	private String sexString = "male";
	private String phoneNumber;
	private String verifyCode;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        verifyCode = intent.getStringExtra("verifyCode");
        
        passwordEditText = (EditText)findViewById(R.id.re_password);
        nameEditText = (EditText)findViewById(R.id.name);
        sexRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        sexRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	        @Override
	        public void onCheckedChanged(RadioGroup arg0, int arg1) {
	             int radioButtonId = arg0.getCheckedRadioButtonId();
	             RadioButton rb = (RadioButton)RegisterActivity.this.findViewById(radioButtonId);
	             sexString = rb.getText().toString().equals("男") ? "male" : "female";
	             Log.d(TAG, sexString);
	         }
	    });
        birthdayButton = (Button)findViewById(R.id.birthday);
        emailEditText = (EditText)findViewById(R.id.email);
        areaButton = (Button)findViewById(R.id.area);
        signatureEditText = (EditText)findViewById(R.id.signature);
        registerButton = (Button)findViewById(R.id.register);
        registerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String password = passwordEditText.getText().toString();
				if (password.length() == 0) {
					Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String name = nameEditText.getText().toString();
				if (name.length() == 0) {
					Toast.makeText(RegisterActivity.this, "名字不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String birthday = birthdayButton.getText().toString();
				if (birthday.length() == 0) {
					Toast.makeText(RegisterActivity.this, "生日不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String email = emailEditText.getText().toString();
				if (email.length() == 0) {
					Toast.makeText(RegisterActivity.this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!DsdAAAUtils.isEmailValid(email)) {
					Toast.makeText(RegisterActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String area = areaButton.getText().toString();
				if (area.length() == 0) {
					Toast.makeText(RegisterActivity.this, "地区不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String signature = signatureEditText.getText().toString();
				if (signature.length() == 0) {
					Toast.makeText(RegisterActivity.this, "个性签名不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				String retString = DsdLibAAA.dsdAAARegister(phoneNumber, verifyCode, name, sexString, 
						birthday, password, email, area, signature);
				DsdAAAUtils.checkResult(RegisterActivity.this, retString);
			}
		});
        
        
    }
}
