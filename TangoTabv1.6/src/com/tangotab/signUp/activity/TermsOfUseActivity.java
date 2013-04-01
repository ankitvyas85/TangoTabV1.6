package com.tangotab.signUp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
 * <br> Class : TermsOfUseActivity
 * <br> Layout :terms.xml
 * 
 * @author dillip.lenka
 *
 */
public class TermsOfUseActivity extends Activity
{
	/*
	 * Meta Definitions
	 */
	private WebView moreWebView=null;
	/**
	 * Exceution will start here
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.terms);
		Button back=(Button)findViewById(R.id.Back);	
	
		// Refer to Buttons from Layout
		moreWebView = (WebView) findViewById(R.id.webView);
		moreWebView.loadUrl("file:///android_asset/terms.html");
		back.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v)
			{				
				finish();
			}
		});
	}
	/**
	 * Added the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu); 
		MenuInflater menuInflater=getMenuInflater();
		menuInflater.inflate(R.menu.menubar, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.myoffers:
				Intent homeIntent=new Intent(this, MyOffersActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);
				break;
			
			case R.id.nearme:
				Intent businessearchIntent=new Intent(this, NearMeActivity.class);
				businessearchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(businessearchIntent);
				break;
		
			case R.id.search:
				Intent contactmanagerIntent=new Intent(this, SearchActivity.class);
				contactmanagerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(contactmanagerIntent);
				break;				
			case R.id.settings:
				Intent followupIntent=new Intent(this, SettingsActivity.class);
				followupIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(followupIntent);
				break;		
			default:
				return super.onOptionsItemSelected(item);
		}		
		finish();
		return true;
	}
}
