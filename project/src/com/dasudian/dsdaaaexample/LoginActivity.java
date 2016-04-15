package com.dasudian.dsdaaaexample;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.dasudian.AAA.DsdLibAAA;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final String TAG = "LoginActivity";
	private Button newUserButton;
	private Button forgetPasswordButton;
	private Button loginButton;
	private EditText phoneNumberEditText;
	private EditText passwordEditText;
	// qq
	public static Tencent mTencent;
	private final String appId = "222222";
	private Button loginUseQQButton;
	private Button loginUseWechatButton;
	private Boolean isLogin = false;
	private Button getQQUserInfoButton;
	private TextView qqNameTextView;
	private TextView qqSexTextView;
	private TextView qqAreaTextView;
	private ImageView qqAvatarImageView;
	// wechat
	private final String appIdWechat = "wxd930ea5d5a258f4f";
	private IWXAPI apiIwxapi;
	
	// 注册到微信
	private void regToWx() {
		apiIwxapi = WXAPIFactory.createWXAPI(this, appIdWechat, true);
		Boolean ret = apiIwxapi.registerApp(appIdWechat);
		Log.e(TAG, "微信注册返回值是："+ret);
	}
	
	private void sendOauthReq() {
	    // send oauth request 
	    final SendAuth.Req req = new SendAuth.Req();
	    req.scope = "snsapi_userinfo";
	    req.state = "wechat_sdk_demo_test";
	    apiIwxapi.sendReq(req);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        mTencent = Tencent.createInstance(appId, this.getApplicationContext());
        regToWx();
        
        /**
         * 初始化sdk
         */
        AsyncTask<Object, Void, Integer> dsdAAAInit = new AsyncTask<Object, Void, Integer>() {
			protected Integer doInBackground(Object... paramAnonymousArrayOfString) {
//				String aucServer = "https://aaa_auc.test.dsd:8443/auc_app";
//				String appId = "101_96TlceWKEJcJasPsDg_A";
//				String appKey = "115e22e3f7b726bf";
				// joe
				String appId = "2_96wIgCcCSJrnsrvmqW_A";
				String appKey = "d7f19a669255351e";
				String aucServer = "http://192.168.1.45:8443/auc_app";
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
					if (!cookie.isEmpty()) {
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
				if (retString != null) {
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
				} else {
					Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
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
        
        /**
         * 使用qq登录，用户授权成功后同时将信息发送到大数点AAA服务器
         */
        loginUseQQButton = (Button)findViewById(R.id.login_qq);
        loginUseQQButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!mTencent.isSessionValid() && isLogin == false) {
					mTencent.login(LoginActivity.this, "all", loginListener);
					Log.d("SDKQQAgentPref", "FirstLaunch_SDK:" + SystemClock.elapsedRealtime());
				} else {
					mTencent.logout(LoginActivity.this);
					Log.d(TAG, "退出qq登录");
					loginUseQQButton.setText("使用qq登录");
					isLogin = false;
				}
			}
		});
        
        /**
         * 使用qq登录成功并将qq用户信息发送到大数点服务器后，调用下面的方法从大数点服务器获取用户信息
         */
        qqNameTextView = (TextView)findViewById(R.id.qq_name);
        qqSexTextView = (TextView)findViewById(R.id.qq_sex);
        qqAreaTextView = (TextView)findViewById(R.id.qq_area);
        qqAvatarImageView = (ImageView)findViewById(R.id.qq_avatar);
        getQQUserInfoButton = (Button)findViewById(R.id.get_qq_user_info);
        getQQUserInfoButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isLogin) {
					String retString = DsdLibAAA.dsdAAAOauthUserInfo(mTencent.getOpenId(), 1);
					if (retString != null && retString.length() != 0) {
						Log.d(TAG, "获取qq用户信息的结果是："+retString);
						try {
							JSONObject jsonObject = new JSONObject(retString);
							String userInfoString = jsonObject.getString("user_info");
							if (userInfoString != null && userInfoString.length() != 0) {
								JSONObject jsonUserInfo = new JSONObject(jsonObject.getString("user_info"));
								String name = jsonUserInfo.getString("nickname");
								String sex = jsonUserInfo.getString("sex");
								String area = jsonUserInfo.getString("province") + "/"
												+ jsonUserInfo.getString("city");
								if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(sex) 
										&& !TextUtils.isEmpty(area)) {
									qqNameTextView.setText(name);
									qqSexTextView.setText(sex);
									qqAreaTextView.setText(area);
								}
								
								String headImgUrl = jsonUserInfo.getString("headimgurl");
								getAvatar(headImgUrl);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else {
					Toast.makeText(LoginActivity.this, "请先使用qq登录", Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
        loginUseWechatButton = (Button)findViewById(R.id.login_wechat);
        loginUseWechatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendOauthReq();
			}
		});
    }
    
    /**
     * 获取头像
     * @param headImgUrl 
     */
    private void getAvatar(final String headImgUrl) {
    	AsyncTask<Object, Void, Bitmap> getHeadImg = new AsyncTask<Object, Void, Bitmap>() {
			protected Bitmap doInBackground(Object... paramAnonymousArrayOfString) {
				 try {  
				      URL url = new URL(headImgUrl);  
				      HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
				      conn.setDoInput(true);
				      conn.setConnectTimeout(3000);
				      conn.connect();  
				      InputStream is = conn.getInputStream();  
				      Bitmap bitmap = BitmapFactory.decodeStream(is);  
				      is.close();  
				      return bitmap;  
				  } catch (Exception e) {  
				      e.printStackTrace(); 
				  }
				return null;
			}

			protected void onPostExecute(Bitmap bitmap) {
				if (bitmap != null) {
					qqAvatarImageView.setImageBitmap(bitmap);
				}
			}
		};
		getHeadImg.execute();
    }
    
    private void toUserInfoActivity(String phoneNumber) {
    	Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
    }
    
    /**
     * =========================已下都是qq登录相关==================
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG, "-->onActivityResult " + requestCode  + " resultCode=" + resultCode);
    	super.onActivityResult(requestCode, resultCode, data);
    	Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
    }
    
    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
                        
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
                
                isLogin = true;
                loginUseQQButton.setText("退出qq登录");
                Log.e(TAG, "token = "+token+", openId = "+openId+", appId = "+appId);
                /**
                 * 将信息同步到大数点服务器
                 */
                String retString = DsdLibAAA.dsdAAAqq(openId, token, appId);
                DsdAAAUtils.checkResult(this, retString);
            }
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }
    
    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
        	Log.e("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
        }
    };
    
    private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(Object response) {
            if (null == response) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                Util.showResultDialog(LoginActivity.this, "返回为空", "登录失败");
                return;
            }
			Util.showResultDialog(LoginActivity.this, response.toString(), "登录成功");
			doComplete((JSONObject)response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Util.toastMessage(LoginActivity.this, "onError: " + e.errorDetail);
			Util.dismissDialog();
		}

		@Override
		public void onCancel() {
			Util.toastMessage(LoginActivity.this, "onCancel: ");
			Util.dismissDialog();
		}
	}
}
