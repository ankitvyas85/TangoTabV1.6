package com.tangotab.signUp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.tangotab.R;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Class for privacy policy activity
 * 
 * <br> class :PrivacyPolicyActivity
 * <br> layout :privacypolicy.xml
 * 
 * @author dillip.lenka
 *
 */
public class PrivacyPolicyActivity extends Activity
{
	/*
	 * Meta Definitions
	 */
	private WebView moreWebView=null;	
	private Vibrator vibrator;

	/**
	 * Execution will start form here.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.privacypolicy);
		Button back=(Button)findViewById(R.id.Back);

		moreWebView = (WebView) findViewById(R.id.webView);
		/**
		 * Load the HTML file
		 */
		moreWebView.loadUrl("file:///android_asset/privacypolicy.html");
		
		/**
		 * Back button on click listner added.
		 */
		back.setOnClickListener(new OnClickListener()
		{		
			public void onClick(View v) {
				finish();
			}
		});
		/**
		 * UI widgets
		 */
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		/**
		 * Near me menu button on click listener added.
		 */
		nearmeMenuItem.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(1);
					}
				});
		/**
		 * My offer menu button on click listener added.
		 */
		myofferButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						vibrator.vibrate(50);
						onMenuSelected(0);
					}
				});
		
		/**
		 * Search menu button on click listener added.
		 */
		searhMenuButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(2);
					}
				});
		/**
		 * Settings menu button on click listener added.
		 */
		settingsMenuButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(3);
					}
				});
	}
	
	/**
	 * Added the menu
	 */
	
	public void onMenuSelected(int item) 
	{
		switch (item) 
		{
			case 0:
				Intent homeIntent=new Intent(this, MyOffersActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
				break;
			
			case 1:
				Intent businessearchIntent=new Intent(this, NearMeActivity.class);
				businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(businessearchIntent);
				break;
		
			case 2:
				Intent contactmanagerIntent=new Intent(this, SearchActivity.class);
				contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(contactmanagerIntent);
				break;				
			case 3:
				Intent followupIntent=new Intent(this, SettingsActivity.class);
				followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(followupIntent);
				break;		
		}		
	}
}
