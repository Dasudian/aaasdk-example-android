package com.dasudian.dsdaaaexample;

import com.dasudian.AAA.DsdLibAAA;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgetPasswordActivity extends Activity {
	private Button getVerifycodeButton;
	private EditText phoneNumberEditText;
	private EditText verifyCodeEditText;
	private EditText newPasswordEditText;
	private Button verifyPhoneNumberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        
        phoneNumberEditText = (EditText)findViewById(R.id.fp_phone_number);
        getVerifycodeButton = (Button)findViewById(R.id.fp_get_verifycode);
        getVerifycodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(ForgetPasswordActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String retString = DsdLibAAA.dsdAAARequestVericode(phoneNumber);
				DsdAAAUtils.checkResult(ForgetPasswordActivity.this, retString);
			}
		});
        
        verifyCodeEditText = (EditText)findViewById(R.id.fp_verifycode);
        newPasswordEditText = (EditText)findViewById(R.id.new_password);
        verifyPhoneNumberButton = (Button)findViewById(R.id.forget_password);
        verifyPhoneNumberButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(ForgetPasswordActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String verifyCode = verifyCodeEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(ForgetPasswordActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String newPassword = newPasswordEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(ForgetPasswordActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String retString = DsdLibAAA.dsdAAAForgetPassword(phoneNumber, verifyCode, newPassword);
				if (DsdAAAUtils.checkResult(ForgetPasswordActivity.this, retString)) {
					Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
				
			}
		});
    }
}
