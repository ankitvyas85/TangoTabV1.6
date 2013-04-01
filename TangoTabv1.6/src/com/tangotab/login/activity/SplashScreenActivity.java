package com.tangotab.login.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.tangotab.R;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.nearMe.activity.NearMeActivity;
/**
 * This activity will be the First activity for TangoTap application. 
 * 
 * <br> Class :SplashScreenActivity
 * <br> Layout:splashscreen.xml
 * 
 * @author dillip.lenka
 *
 */
		
public class SplashScreenActivity extends Activity
{
	/**
	 * Execution will start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		
		new Handler().postDelayed(new Runnable()
		{
			public void run() 
			{
				SharedPreferences spc = getSharedPreferences("UserName", 0);
				String userName =	spc.getString("username", "");
				String password =spc.getString("password", "");
				/**
				 * If already user login into application by using FaceBook or TangoTab credential then start NearMeActivity.
				 */
				if(!ValidationUtil.isNullOrEmpty(userName) && !ValidationUtil.isNullOrEmpty(password))
				{
					Intent nearIntent = new Intent(SplashScreenActivity.this, NearMeActivity.class);
					nearIntent.putExtra("frmPage", "faceBook");
					startActivity(nearIntent);	
				}
				else{ 
					/**
					 * If user not login through FaceBook or TangoTab Credentials start FaceBookLogin Activity
					 */
					Intent loginIntent = new Intent(SplashScreenActivity.this, FacebookLogin.class);
					startActivity(loginIntent);
				}
				
				finish();
			}
		}, 2000);
		
	}
	
}
