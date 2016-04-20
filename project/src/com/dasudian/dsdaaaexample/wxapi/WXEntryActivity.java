package com.dasudian.dsdaaaexample.wxapi;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.dasudian.dsdaaaexample.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private final String TAG = "WXEntryActivity";
    private Button wechatButton;
	private final String appId = "wx5b545565f1a8cd7c";
	private final String appSec = "c3926fd708d5213d20ea4017bbd9b67f";
	private IWXAPI api;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_wechat);
        
        api = WXAPIFactory.createWXAPI(this, appId, false);
        Boolean ret = api.registerApp(appId);
        Log.e(TAG, "注册结果:" + ret);
        ret = api.handleIntent(getIntent(), this);
        Log.e(TAG, "handleIntent结果:" + ret);
        
        wechatButton = (Button)findViewById(R.id.wechat);
        wechatButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// send oauth request 
//		 	    final SendAuth.Req req = new SendAuth.Req();
//		 	    req.scope = "snsapi_userinfo";
//		 	    req.state = "wechat_sdk_demo_test1";
//		 	    Boolean ret = api.sendReq(req);
		 	    
		 	    final SendAuth.Req req = new SendAuth.Req();
				req.scope = "post_timeline";
				req.state = "none";
				Boolean ret = api.sendReq(req);
		 	    Log.e(TAG, "发送消息微信的结果：" +ret);
			}
		});
        
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
        	
        	AsyncTask<String, Void, String> localMF = new AsyncTask<String, Void, String>() {
    			protected String doInBackground(
    					String... paramAnonymousArrayOfString) {
    				String result = new String();
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
			            
			            Log.d(TAG, "result = " + result);
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
					return result;
    			}

    			protected void onPostExecute(String result) {
    				
    			}
    		};
    		localMF.execute(httpUrl);
             
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