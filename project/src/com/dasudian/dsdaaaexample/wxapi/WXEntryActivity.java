package com.dasudian.dsdaaaexample.wxapi;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.json.JSONException;
import org.json.JSONObject;

import com.dasudian.AAA.DsdLibAAA;
import com.dasudian.dsdaaaexample.DsdAAAUtils;
import com.dasudian.dsdaaaexample.R;
import com.dasudian.dsdaaaexample.UserInfo;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private final String TAG = "WXEntryActivity";
    private Button wechatButton;
	private final String appId = "wx5b545565f1a8cd7c";
	private final String appSec = "c3926fd708d5213d20ea4017bbd9b67f";
	private IWXAPI api;
	private ImageView avatarImageView;
	private TextView nameTextView;
	private TextView sexTextView;
	private TextView areaTextView;
    	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_wechat);
        
        initView();
        
        api = WXAPIFactory.createWXAPI(this, appId, false);
        api.registerApp(appId);
        api.handleIntent(getIntent(), this);     
    }
    
    private void initView() {
    	avatarImageView = (ImageView)findViewById(R.id.wechat_avatar);
    	nameTextView = (TextView)findViewById(R.id.wechat_name);
    	sexTextView = (TextView)findViewById(R.id.wechat_sex);
    	areaTextView = (TextView)findViewById(R.id.wechat_area);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		Log.e(TAG, "onReq");
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "onResp");
        switch (resp.errCode) {  
        case BaseResp.ErrCode.ERR_OK:  
        	String code = ((SendAuth.Resp) resp).code; 
        	Log.d(TAG, "code = " + code);
        	String httpUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?"
        			+"appid="+ appId
        			+"&secret="+ appSec
        			+"&code=" + code
        			+"&grant_type=authorization_code";
        	Log.d(TAG, "httpUrl = " + httpUrl);
        	
        	AsyncTask<String, Void, UserInfo> getAccessToken = new AsyncTask<String, Void, UserInfo>() {
    			protected UserInfo doInBackground(
    					String... paramAnonymousArrayOfString) {
    				String result = new String();
    				UserInfo userInfo = new UserInfo();
					try {
						URL url = new URL(paramAnonymousArrayOfString[0]);
						HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			            // Create the SSL connection
			            SSLContext sc;
			            sc = SSLContext.getInstance("TLS");
			            sc.init(null, null, new java.security.SecureRandom());
			            conn.setSSLSocketFactory(sc.getSocketFactory());
		
			            // set Timeout and method
			            conn.setReadTimeout(7000);
			            conn.setConnectTimeout(7000);
			            conn.setRequestMethod("POST");
			            conn.setDoInput(true);
		
			            // Add any data you wish to post here
			            conn.connect();
			            
			            InputStream is =  conn.getInputStream();
			            BufferedReader in = new BufferedReader(new InputStreamReader(is));
			            String inputLine;
			            while ((inputLine = in.readLine()) != null) {
			                result += inputLine;            
			            }
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (KeyManagementException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String avatarUrl = new String();
					if (!Util.isEmpty(result)) {
		            	try {
							JSONObject jsonObject = new JSONObject(result);
							String accessToken = jsonObject.getString("access_token");
							String openid = jsonObject.getString("openid");
							String refreshToken = jsonObject.getString("refresh_token");
							if (accessToken != null && openid != null) {
								/**
								 * 1. 将用户信息发送到大数点服务器
								 */
								String resultString = DsdLibAAA.dsdAAAwechat(openid, accessToken,
										appId, android.os.Build.MODEL);
				            	Log.d(TAG, "dsdAAAwechat 的结果是 = " + resultString);
				            	
				            	
				            	/**
				            	 * 2.1. 从服务器拉取用户信息
				            	 */
				            	resultString = DsdLibAAA.dsdAAAOauthUserInfo(openid);
				            	Log.d(TAG, "dsdAAAOauthUserInfo 的结果是 = " + resultString);
				            	
				            	JSONObject userInfoJsonObject = new JSONObject(resultString);
				            	avatarUrl = userInfoJsonObject.getString("avatar");
				            	String name = userInfoJsonObject.getString("name");
				            	String sex = userInfoJsonObject.getString("sex");
				            	String province = userInfoJsonObject.getString("province");
				            	String city = userInfoJsonObject.getString("city");
				            	userInfo.setName(name);
				            	userInfo.setSex(sex);
				            	userInfo.setArea(province + "/" + city);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
					/**
					 * 2.2. 获取用户头像
					 */
					if (!Util.isEmpty(avatarUrl)) {
						  URL url;
						try {
							url = new URL(avatarUrl);
							HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
						      conn.setDoInput(true);
						      conn.setConnectTimeout(3000);
						      conn.connect();  
						      InputStream is = conn.getInputStream();  
						      Bitmap bitmap = BitmapFactory.decodeStream(is);
						      userInfo.setAvatar(bitmap);
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  
					      
					}

					return userInfo;
    			}

    			protected void onPostExecute(UserInfo userInfo) {
    				if (userInfo != null && userInfo.getName() != null
    						&& userInfo.getSex() != null && userInfo.getAvatar() != null
    						&& userInfo.getArea() != null) {
    					nameTextView.setText(userInfo.getName());
    					sexTextView.setText(userInfo.getSex());
    					areaTextView.setText(userInfo.getArea());
    					avatarImageView.setImageBitmap(userInfo.getAvatar());
    				}
    			}
    		};
    		getAccessToken.execute(httpUrl);
             
            break;
        case BaseResp.ErrCode.ERR_AUTH_DENIED:
        	break;
        case BaseResp.ErrCode.ERR_USER_CANCEL:
        	break;
        default:  
            break;  
        }  
	}
}