package com.tangotab.map.activity;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.GeoCoderUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.map.util.MapItemizedOverlay;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Map offers in google map
 * 
 * <br> Class :MyOffersMapingActivity
 * <br> layout:myoffersmap.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MyOffersMapingActivity extends MapActivity 
{
	/* FIELDS */
	private MapController mapController = null;
	private MapView MView = null;
	
	Button back = null;
	ArrayList<GeoPoint> itemAddress = null;
	GeoPoint point1 = null;
	private ProgressBar progressBar = null;
	private Vibrator vibrator;	
	public TangoTabBaseApplication application;
	/**
	 * Execution start here.
	 */
	@Override
	public void onCreate(Bundle bundle)
	{
	
		super.onCreate(bundle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.myoffersmap);

		application = (TangoTabBaseApplication) getApplication();
		
		itemAddress = new ArrayList<GeoPoint>();
		progressBar = (ProgressBar) findViewById(R.id.progressBarMap1);
		progressBar.setVisibility(View.VISIBLE);

		MView = (MapView) findViewById(R.id.mapviewdeals);
		MView.invalidate();
		
		vibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		
		nearmeMenuItem.setBackgroundResource(R.drawable.places);
		myofferButton.setBackgroundResource(R.drawable.myoffers_active);
		searhMenuButton.setBackgroundResource(R.drawable.search);
		settingsMenuButton.setBackgroundResource(R.drawable.settings);
		/**
		 * Menu widgets from UI
		 */
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		nearmeRL.setBackgroundDrawable(null);
		myoffersRL.setBackgroundResource(R.drawable.hover_img);
		searchRL.setBackgroundDrawable(null);
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
		
		new Thread(new Runnable()
		{
			@Override
			public void run() 
			{
				runOnUiThread(new Runnable()
				{					
					@Override
					public void run() 
					{
						myMapView();
					}
				});
			}
		}).start();

	}

	protected boolean isRouteDisplayed() {
		return false;
	}

	/**
	 * Back button functionality added.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		}
	/**
	 * Zoom the view
	 */
	public void zoomOption() 
	{
		int minLat = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int minLon = Integer.MAX_VALUE;
		int maxLon = Integer.MIN_VALUE;
				
		if(itemAddress != null)
			for (GeoPoint item : itemAddress) 
			{
				int lat = item.getLatitudeE6();
				int lon = item.getLongitudeE6();	 
				maxLat = Math.max(lat, maxLat);
				minLat = Math.min(lat, minLat);
				maxLon = Math.max(lon, maxLon);
				minLon = Math.min(lon, minLon);
			}
		GeoPoint myLocation = new GeoPoint((int) (AppConstant.dev_lat * 1E6),(int) (AppConstant.dev_lang * 1E6));
		maxLat = Math.max(myLocation.getLatitudeE6(), maxLat);
		minLat = Math.min(myLocation.getLatitudeE6(), minLat);
		maxLon = Math.max(myLocation.getLongitudeE6(), maxLon);
		minLon = Math.min(myLocation.getLongitudeE6(), minLon);	
		mapController = MView.getController();
		if(mapController != null)
		{
			double borderFactor = 2.5;
			mapController.zoomToSpan((int) (Math.abs(maxLat - minLat) * borderFactor), (int)(Math.abs(maxLon - minLon) * borderFactor));		
			mapController.animateTo(myLocation);
		}
	}

	@Override
	public void onResume() 
	{
		super.onResume();
	}

	@SuppressWarnings("unchecked")
	public void myMapView() 
	{

		MapItemizedOverlay itemizedOverlay =  new MapItemizedOverlay( getResources().getDrawable(R.drawable.green_map_pin), MyOffersMapingActivity.this, "myoffersMap");	

		mapController = MView.getController();
		
		MView.setBuiltInZoomControls(true);
		MView.displayZoomControls(true);

		List<OffersDetailsVo> offersList=new ArrayList<OffersDetailsVo>();
		Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
        	offersList = (List<OffersDetailsVo>) bundle.get("offerList");    
        }
			
		AppConstant.offersList = offersList;
		if(!ValidationUtil.isNullOrEmpty(offersList))
		{
			Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
			int offerCount=1;
			for (OffersDetailsVo offersDetailsVo:offersList) 
			{
				if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getAddress()))
				{
					GeoPoint point = null;
					try 
					{
						List<Address> addresses = geoCoder.getFromLocationName(offersDetailsVo.getAddress(), 1);
		
						if (addresses != null && addresses.size() > 0)
						{
							point = new GeoPoint((int) (addresses.get(0).getLatitude() * 1E6),(int) (addresses.get(0).getLongitude() * 1E6));
						}
						else
						{
						
							GeoCoderUtil.getLatLong(URLEncoder.encode(offersDetailsVo.getAddress(), "UTF-8"));
							point = new GeoPoint((int) (AppConstant.locationLat * 1E6),(int) (AppConstant.locationLong * 1E6));	
						}
						if(point!=null)
						{
							itemAddress.add(point);
							StringBuilder businessName = new StringBuilder();
							businessName.append(offerCount).append(")").append(" ").append(offersDetailsVo.getBusinessName());
							OverlayItem overlayItem = new OverlayItem(point, businessName.toString(), offersDetailsVo.getAddress());
							itemizedOverlay.addOverlay(overlayItem);
							MView.getOverlays().add(itemizedOverlay);	
						}
						offerCount++;
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}	
				}
				
			}
	}
		MapItemizedOverlay itemizedOverlay1 =  new MapItemizedOverlay( getResources().getDrawable(R.drawable.location__icon),MyOffersMapingActivity.this);	

		MView.getOverlays().add(itemizedOverlay1);
		point1 = new GeoPoint((int) (AppConstant.dev_lat * 1E6),(int) (AppConstant.dev_lang * 1E6));

		OverlayItem overlayItem = new OverlayItem(point1, "You Are Here", "");

		itemizedOverlay1.addOverlay(overlayItem);

		zoomOption();
		progressBar.setVisibility(View.INVISIBLE);
	}
	/**
	 * Menu select functionality added.
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