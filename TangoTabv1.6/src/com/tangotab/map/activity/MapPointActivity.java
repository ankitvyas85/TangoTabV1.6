package com.tangotab.map.activity;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.tangotab.R;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.map.util.MapItemizedOverlay;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;

/**
 * Display a point in the google map
 * 
 * <br> Class: MapPointActivity
 * <br> layout:mappoint.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MapPointActivity extends MapActivity
{
	/* FIELDS */
	private MapController mapController = null;
	private MapView MView = null;
	public static int index, position;
	TangoTabBaseApplication application;
	Geocoder gc=null;
	GeoPoint point=null;
	Button back=null;
	List<Address> foundGeocode=null;
	MapItemizedOverlay itemizedOverlay=null;
	public String isFrom=null;
	private Vibrator vibrator;
	/**
	 * Execution will be start here.
	 */
	 @Override
	public void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		final boolean customTitleSupported = requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mappoint);
		
		
		application = (TangoTabBaseApplication) getApplication();
		if (customTitleSupported)
		{
			getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);
		}
		MView = (MapView) findViewById(R.id.mapview);
		itemizedOverlay =  new MapItemizedOverlay( getResources().getDrawable(R.drawable.green_map_pin),MapPointActivity.this);
		
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		final String from = getIntent().getStringExtra("from");
		
		
		RelativeLayout Content=(RelativeLayout)findViewById(R.id.content);
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		/**
		 * Menu widgets from UI
		 */
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		if(!ValidationUtil.isNullOrEmpty(from) && from.equals("myoffer"))
		{
			Content.setBackgroundResource(R.drawable.checkin_top);
		}
		if(!ValidationUtil.isNullOrEmpty(from) && from.equals("myoffer")){
			myofferButton.setBackgroundResource(R.drawable.myoffers_active);
			myoffersRL.setBackgroundResource(R.drawable.hover_img);
		}else{
			myofferButton.setBackgroundResource(R.drawable.mydeals);
			myoffersRL.setBackgroundDrawable(null);
		}
		if(!ValidationUtil.isNullOrEmpty(from) && from.equals("search")){
			searhMenuButton.setBackgroundResource(R.drawable.search_active);
			searchRL.setBackgroundResource(R.drawable.hover_img);
		}else{
			searhMenuButton.setBackgroundResource(R.drawable.search);
			searchRL.setBackgroundDrawable(null);
		}
		if(!ValidationUtil.isNullOrEmpty(from) && from.equals("nearme")){
			nearmeMenuItem.setBackgroundResource(R.drawable.nearme_active);
			nearmeRL.setBackgroundResource(R.drawable.hover_img);
		}else{
			nearmeMenuItem.setBackgroundResource(R.drawable.places);
			nearmeRL.setBackgroundDrawable(null);
		}
		settingsMenuButton.setBackgroundResource(R.drawable.settings);
		settingsRL.setBackgroundDrawable(null);
		/**
		 * On click listener added for near me menu button
		 */
		nearmeRL.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(1);
					}
				});
		/**
		 * On click listener added for my offers menu button
		 */
		myoffersRL.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(0);
					}
				});
		/**
		 * On click listener added for search menu button
		 */
		searchRL.setOnClickListener(new OnClickListener() 
				{
					@Override
					public void onClick(View v) {
						vibrator.vibrate(50);
						onMenuSelected(2);
					}
				});
		
		/**
		 * On click listener added for Settings menu button
		 */
		settingsRL.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						vibrator.vibrate(50);
						onMenuSelected(3);
					}
				});
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}
	
	 @Override
	  public boolean onKeyUp(int keyCode, KeyEvent event)
	 {
	      if (keyCode == KeyEvent.KEYCODE_BACK) {
	          onBackPressed();
	          return true;
	      }
	      return super.onKeyUp(keyCode, event);
	  }	
	/**
	 * Back button functionality added.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();	
	}
	/**
	 * OnResume functionality added.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
		MView.invalidate();
		myMapView();
	}
	/**
	 * View the map
	 */
	public void myMapView()
	{
		
		mapController = MView.getController();
		MView.setBuiltInZoomControls(true);
		MView.displayZoomControls(true);
		Intent in = getIntent();
		String address = in.getStringExtra("itemAddress");
		String businessName = in.getStringExtra("businessname");
		gc = new Geocoder(this);
		try {
				Log.v("address","address"+address);
				foundGeocode = new Geocoder(getApplicationContext()).getFromLocationName(address, 1);
				Log.v("size",""+foundGeocode.size());
				if(foundGeocode.size()>0){
				point = new GeoPoint((int) (foundGeocode.get(0).getLatitude() * 1E6),
						(int) (foundGeocode.get(0).getLongitude() * 1E6));
				mapController.setCenter(point);
				mapController.setZoom(17);
				OverlayItem overlayItem = new OverlayItem(point,businessName,address);
				itemizedOverlay.addOverlay(overlayItem);
				MView.getOverlays().add(itemizedOverlay);
				mapController.animateTo(point);
				}
				if(foundGeocode.size()<=0)
				{
					point = new GeoPoint((int) (0.0 * 1E6),
							(int) (0.0 * 1E6));
					mapController.setCenter(point);
					OverlayItem overlayItem = new OverlayItem(point,businessName,address);
					itemizedOverlay.addOverlay(overlayItem);
					MView.getOverlays().add(itemizedOverlay);
					mapController.animateTo(point);
				}
			} catch (IOException e)
			{
				Log.e("Exception occured in displaying deal in Map", e.getLocalizedMessage());
				e.printStackTrace();
			}	
		}
	/**
	 * Menu selected functionality added.
	 * 
	 * @param item
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