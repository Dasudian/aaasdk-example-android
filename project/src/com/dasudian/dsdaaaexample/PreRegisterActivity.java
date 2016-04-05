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

public class PreRegisterActivity extends Activity {
	private Button getVerifycodeButton;
	private EditText phoneNumberEditText;
	private EditText verifyCodeEditText;
	private Button verifyPhoneNumberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preregister);
        
        phoneNumberEditText = (EditText)findViewById(R.id.pre_phone_number);
        getVerifycodeButton = (Button)findViewById(R.id.get_verifycode);
        getVerifycodeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(PreRegisterActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String retString = DsdLibAAA.dsdAAARegisterPhoneNumber(phoneNumber);
				DsdAAAUtils.checkResult(PreRegisterActivity.this, retString);
			}
		});
        
        verifyCodeEditText = (EditText)findViewById(R.id.verifycode);
        verifyPhoneNumberButton = (Button)findViewById(R.id.verify_phoneNumber);
        verifyPhoneNumberButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String verifyCode = verifyCodeEditText.getText().toString();
				if (verifyCode.length() == 0) {
					Toast.makeText(PreRegisterActivity.this, "验证码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String phoneNumber = phoneNumberEditText.getText().toString();
				if (phoneNumber.length() == 0) {
					Toast.makeText(PreRegisterActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String retString = DsdLibAAA.dsdAAAVerifyPhoneNumber(phoneNumber, verifyCode);
				if (DsdAAAUtils.checkResult(PreRegisterActivity.this, retString)) {
					Intent intent = new Intent(PreRegisterActivity.this, RegisterActivity.class);
					intent.putExtra("phoneNumber", phoneNumber);
					intent.putExtra("verifyCode", verifyCode);
					startActivity(intent);
				}
				
			}
		});
    }
}
