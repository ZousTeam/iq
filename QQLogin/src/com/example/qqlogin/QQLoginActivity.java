package com.example.qqlogin;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.connect.UserInfo;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * 2015.10.21
 * 
 * @author jy626760171
 */
public class QQLoginActivity extends Activity {

	private final static String APP_ID = "1104912038";

	private Tencent mTencent;
	private TextView resultView;
	private Button loginBtn, getInfoBtn;
	// loginListener 用于监听登录返回数据

	private IUiListener loginListener, userInfoListener;

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.loginbtn:
				login();

				break;
			case R.id.getinfobtn:
				getInfo();

				break;
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		initData();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTencent.logout(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 此处登陆操作必须写
		Tencent.handleResultData(data, loginListener);
	}

	private void initView() {
		resultView = (TextView) findViewById(R.id.resultView);
		loginBtn = (Button) findViewById(R.id.loginbtn);
		loginBtn.setOnClickListener(mClickListener);
		getInfoBtn = (Button) findViewById(R.id.getinfobtn);
		getInfoBtn.setOnClickListener(mClickListener);
	}

	private void initData() {
		// 初始化QQ对象
		mTencent = Tencent.createInstance(APP_ID, this);
		/**
		 * { "ret": 0, "pay_token": "C1A1AC14C7A5BB0186A8F47E52FF4332", "pf": "desktop_m_qq-10000144-android-2002-", "query_authority_cost": 279, "authority_cost": -379414438, "openid": "4BF4E005A897E9D7E1D07DEF29D7AC05", "expires_in": 7776000, "pfkey": "39f57bcbc8e794b793a25ec91a252e89", "msg": "", "access_token": "E25FF61F09E8ED613F1724870D57B75D", "login_cost": 1120 }
		 */

		loginListener = new IUiListener() {

			@Override
			public void onError(UiError e) {
				showResult("loginListener.onError", "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
			}

			@Override
			public void onComplete(Object value) {
				showResult("loginListener.onComplete", "login success::" + value);
				Log.d("loginListener.onComplete", "" + value);
				if (value == null) return;
				if (value instanceof JSONObject) {
					try {
						JSONObject result = (JSONObject) value;
						if (result.optInt("ret") == 0) {
							String openID = result.optString("openid");
							String accessToken = result.optString("access_token");
							String expires = result.optString("expires_in");
							mTencent.setOpenId(openID);
							mTencent.setAccessToken(accessToken, expires);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onCancel() {
				showResult("loginListener.onCancel", "onCancel");
			}
		};
		/**
		 * { "is_yellow_year_vip": "0", "ret": 0, "figureurl_qq_1": "http://q.qlogo.cn/qqapp/1104912038/4BF4E005A897E9D7E1D07DEF29D7AC05/40", "figureurl_qq_2": "http://q.qlogo.cn/qqapp/1104912038/4BF4E005A897E9D7E1D07DEF29D7AC05/100", "nickname": "小毅", "yellow_vip_level": "0", "is_lost": 0, "msg": "", "city": "图兹拉－波德里涅", "figureurl_1": "http://qzapp.qlogo.cn/qzapp/1104912038/4BF4E005A897E9D7E1D07DEF29D7AC05/50", "vip": "0", "level": "0", "figureurl_2":
		 * "http://qzapp.qlogo.cn/qzapp/1104912038/4BF4E005A897E9D7E1D07DEF29D7AC05/100", "province": "", "is_yellow_vip": "0", "gender": "男", "figureurl": "http://qzapp.qlogo.cn/qzapp/1104912038/4BF4E005A897E9D7E1D07DEF29D7AC05/30" }
		 */
		userInfoListener = new IUiListener() {

			@Override
			public void onError(UiError e) {
				showResult("getInfoListener.onError", "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:" + e.errorDetail);
			}

			@Override
			public void onComplete(Object value) {
				showResult("getInfoListener.onComplete", "getinfo success::" + value);
				Log.d("test", "" + value);
			}

			@Override
			public void onCancel() {
				showResult("getInfoListener.onCancel", "onCancel");
			}
		};
	}

	private void login() {
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", loginListener);
		}
	}

	protected void getInfo() {
		if (mTencent.getQQToken() == null) {
			System.out.println("qqtoken == null");
		}
		UserInfo userInfo = new UserInfo(this, mTencent.getQQToken());
		userInfo.getUserInfo(userInfoListener);
	}

	private void showResult(final String tag, final String msg) {
		resultView.post(new Runnable() {

			@Override
			public void run() {
				resultView.setText(tag + "\t" + msg);
			}
		});
	}

}
