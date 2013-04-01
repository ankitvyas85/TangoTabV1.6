package com.tangotab.settings.activity;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.Session;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.CreateSqliteHelper;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;

/**
 * Setting tab for Sign out and set the Distance information.
 * 
 * <br> Class:SettingsActivity
 * <br> layout :settings.xml
 * 
 * @author dillip.lenka
 *
 */
public class SettingsActivity extends Activity 
{
	/*
	 * Meta Definitions
	 */
	private Button signOut;
	private Spinner distance=null;
	private TextView accountID;
	private String sel_distance=null;
	CreateSqliteHelper csh=null;
	SQLiteDatabase db=null;
	private String dis=null;
	private static int pos;
	private String disvalues[] = { "5 Miles", "10 Miles", "20 Miles","50 Miles", "50+ Miles" };
	private String disvaluesKm[] = { "5 Km", "10 Km", "20 Km","50 Km", "50+ Km" };
	public boolean flag;
	private Geocoder geocoder;
	private List<Address> addressList;
	private ArrayAdapter<String> adapter=null;	
	public TangoTabBaseApplication application;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private boolean isSettingsPage = false;
	
	
	/**
	 * Execution start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.settings);
		
		/*
		 * Google tracker added.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SETTING_PAGE);
		tracker.trackEvent("Settings", "TrackEvent", "settings", 1);
		

		application = (TangoTabBaseApplication) getApplication();	
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		/**
		 * UI Widgets
		 */
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);

		nearmeMenuItem.setBackgroundResource(R.drawable.places);
		myofferButton.setBackgroundResource(R.drawable.mydeals);
		searhMenuButton.setBackgroundResource(R.drawable.search);
		settingsMenuButton.setBackgroundResource(R.drawable.settings_active);
		/**
		 * Menu widgets
		 */
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		settingsRL.setBackgroundResource(R.drawable.hover_img);
		nearmeRL.setBackgroundDrawable(null);
		searchRL.setBackgroundDrawable(null);
		myoffersRL.setBackgroundDrawable(null);
		/**
		 * on click listener on near me menu button.
		 */
		nearmeRL.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					isSettingsPage =true;
					onMenuSelected(1);
				}
			});
		
		/**
		 * on click listener on my offers menu button.
		 */		
		myoffersRL.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					isSettingsPage =true;
					onMenuSelected(0);
				}
			});
		
		/**
		 * on click listener on search menu button.
		 */		
		searchRL.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					isSettingsPage =true;
					onMenuSelected(2);
				}
			});
		/**
		 * on click listener on settings menu button.
		 */
		settingsRL.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					if(isSettingsPage)
						onMenuSelected(3);
				}
			});
		/**
		 * Collect the UI Widgets
		 */
		signOut = (Button) findViewById(R.id.logout);
		distance = (Spinner) findViewById(R.id.distance);
		accountID = (TextView)findViewById(R.id.textAccountId);
		
		/**
		 * get user name from shared preferences.
		 */		
		SharedPreferences spc2 = getSharedPreferences("UserName", 0);
		String userName= spc2.getString("username","");
		accountID.setText(userName);
		
		/**
		 * SignOut button on click handler./
		 */
		signOut.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					myVib.vibrate(50);
					doSignOut();
				}
			});
		
		/**
		 * Get addresses from geo coder.
		 */
		geocoder = new Geocoder(this);
		
		try 
		{
			addressList = geocoder.getFromLocation(AppConstant.dev_lat, AppConstant.dev_lang, 1);
		} catch (IOException e)
		{
			Log.e("Exception:", "Exception occuerd at the time getting address list from Geo Coder.");
			e.printStackTrace();
		}
		
		if(!ValidationUtil.isNullOrEmpty(addressList) && addressList.get(0).getCountryName().equalsIgnoreCase("Canada"))
		{
			adapter = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_item, disvaluesKm);
		}
		else
		{
			adapter = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_item, disvalues);
		}
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		distance.setAdapter(adapter);
		MySettings();
	}
	/**
	 * Sign out functionality
	 */
	private void doSignOut()
	{	
		/**
		 * Clear the fields from the application.
		 */
		clearFromApplication();
		 SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
		 SharedPreferences.Editor offersEdit = preferences.edit();
		 offersEdit.putInt("scrollValue", 0);
		 offersEdit.putInt("Top", 0);
		 offersEdit.commit();
		/**
		 * Clear the login information from shared preferences.
		 */
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		SharedPreferences.Editor edit = spc.edit();
		edit.putString("username", "");
		edit.putString("password", "");
		edit.commit();
		/**
		 * Put the distance in shred preferences.
		 */
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		SharedPreferences.Editor edit1 = spc1.edit();
		edit1.putString("distancevalue", "20");
		edit1.commit();
		
		
		csh = new CreateSqliteHelper(getApplicationContext());
		db = csh.getReadableDatabase();
		db.delete("LOGIN", "ID=" + 1, null);
		db.close();	
		
			Session session = Session.getActiveSession();
	        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
	            session.closeAndClearTokenInformation();
	        }
	        /**
	         * Start new activity
	         */
	        Intent facebookLoginIntent = new Intent(getApplicationContext(), FacebookLogin.class);
	        facebookLoginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(facebookLoginIntent);
			setResult(RESULT_OK);
			finish();

		 
	}
	/**
	 * Function to Fetch existing user settings
	 */

	public void MySettings()
	{		
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		dis = spc1.getString("distancevalue",dis);
		if (dis == null)
			dis = "20 Miles";
		if (dis.equalsIgnoreCase("1000"))
			dis = "50+ Miles";
		if (dis.equalsIgnoreCase("5"))
			dis = "5 Miles";
		if (dis.equalsIgnoreCase("10"))
			dis = "10 Miles";
		if (dis.equalsIgnoreCase("20"))
			dis = "20 Miles";
		if (dis.equalsIgnoreCase("50"))
			dis = "50 Miles";

		for (int i = 0; i < disvalues.length; i++) {
			if (disvalues[i].equals(dis))
				pos = i;
		}
		/**
		 * Distance set on item select listener 
		 */
		distance.setSelection(pos);
		
		/**
		 * Distance on item select listener added.
		 */
		
		distance.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				sel_distance = parent.getItemAtPosition(position).toString();

				if (!dis.equalsIgnoreCase(sel_distance)) {
					if (sel_distance.equalsIgnoreCase("50+ Miles") || sel_distance.equalsIgnoreCase("50+ Km")) {
						sel_distance = "1000";
					}
					if (sel_distance.equalsIgnoreCase("50 Miles") || sel_distance.equalsIgnoreCase("50 Km")) {
						sel_distance = "50";
					}
					if (sel_distance.equalsIgnoreCase("20 Miles") || sel_distance.equalsIgnoreCase("20 Km")) {
						sel_distance = "20";
					}
					if (sel_distance.equalsIgnoreCase("10 Miles") || sel_distance.equalsIgnoreCase("10 Km")) {
						sel_distance = "10";
					}
					if (sel_distance.equalsIgnoreCase("5 Miles") || sel_distance.equalsIgnoreCase("5 Km")) {
						sel_distance = "5";
					}
					
					/**
					 * put the distance information in shared preferences.
					 */
					SharedPreferences spc = getSharedPreferences("Distance", 0);
					SharedPreferences.Editor edit = spc.edit();
					edit.putString("distancevalue", sel_distance);
					edit.commit();
					AppConstant.IS_SETTINGSCHANGED = "99999";
					/**
					 * Clear the dfeals from the application.
					 */
					clearFromApplication();
					
				}
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	/**
	 * Added the menu information
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
	finish();
}
/**
 * clear the information from the application object.
 * 
 */
private void clearFromApplication()
{
	if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
		application.getSearchList().clear();
	if(!ValidationUtil.isNullOrEmpty(application.getOffersList()))
		application.getOffersList().clear();
	if(!ValidationUtil.isNullOrEmpty(application.getDealsList()))
		application.getDealsList().clear();
	
	/**
	 * Put the scroll to top of the list.
	 */
	 SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
	 SharedPreferences.Editor ScrollEdit = preferences.edit();
	 ScrollEdit.putInt("scrollValue", 0);
	 ScrollEdit.putInt("Top", 0);
	 ScrollEdit.commit();
	 /**
	 * Put the scroll to top of the list.
	 */
	 SharedPreferences searchPreferences = getSharedPreferences("SCROLL", 0);
	 SharedPreferences.Editor searchEdit = searchPreferences.edit();		
	 searchEdit.putInt("ScrollValue", 0);		
	 searchEdit.putInt("Top", 0);
	 searchEdit.commit();
	}
	/**
	 * Back button functionality 
	 */
	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		tracker.stopSession();
	}
}