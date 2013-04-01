package com.tangotab.nearMe.activity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.tangotab.R;
import com.tangotab.appNotification.activity.AppNotificationActivity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ApplicationDetails;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.map.activity.MappingActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.adapter.NearMeListAdapter;
import com.tangotab.nearMe.service.NearMeService;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.vo.NearMeVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * This class will be display list of deals from near to your selected distance location.
 * 
 * <bR> Class : NearMeActivity
 * <br>Layout : nearme.xml
 * 
 *  @author Dillip.Lenka
 *
 */
public class NearMeActivity extends ListActivity implements LocationListener
{
	/*
	 * Meta Definitions
	 */
	private String userId;
	private String zipCode;
	private String cityName;
	private String provider;
	private double dev_lat ;
	private double dev_lang ;		
	private LocationListener locationListener;
	private LocationManager locationManager;
	private LinearLayout llShowMore;
	private ListView itemsList;
	private List<DealsDetailVo> finalDealList = new ArrayList<DealsDetailVo>();
	private int pageCount=1;
	private LoginVo loginvo;
	private TextView emptyList;	
	public TangoTabBaseApplication application;
	private List<OffersDetailsVo> offersList = null;	
	private Vibrator vibrator;
	private boolean isShowMore =false;
	private GoogleAnalyticsTracker tracker;
	private boolean isNearMePage=false;
	private static final int enable_service = 222;
	private boolean isSettingsChanged = false;
		
	/**
	 * Execution will start here
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.nearme);
		
		
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.NEARME_PAGE);		
		tracker.trackEvent("NearMe", "TrackEvent", "nearMe", 1);
		//tracker.dispatch();
		
		//Check whether location services are enabled
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//final boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		final boolean isNProviderEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if (!isNProviderEnable) {
	        showDialog(enable_service);
	    }
		
		//Initialize the GPS service for obtaining the Device Latitude and Longitude Values
        initalizeLocationManagerService();
		
		emptyList = (TextView) findViewById(R.id.emptylist);
		itemsList = (ListView) findViewById(android.R.id.list);
		llShowMore = (LinearLayout) getLayoutInflater().inflate(R.layout.showmorecell, null);		
		Button map = (Button) findViewById(R.id.map);
		
		/**
		 * Menu widgets	from UI	
		 */
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		
		nearmeMenuItem.setBackgroundResource(R.drawable.nearme_active);
		myofferButton.setBackgroundResource(R.drawable.mydeals);
		searhMenuButton.setBackgroundResource(R.drawable.search);
		settingsMenuButton.setBackgroundResource(R.drawable.settings);
		/**
		 * Menu widgets from UI
		 */
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		nearmeRL.setBackgroundResource(R.drawable.hover_img);
		myoffersRL.setBackgroundDrawable(null);
		searchRL.setBackgroundDrawable(null);
		settingsRL.setBackgroundDrawable(null);
		vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		
		
		
		/**
		 * on click listener on near me menu button.
		 */
		nearmeRL.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(isNearMePage)
					{
					vibrator.vibrate(50);
					onMenuSelected(1);
					}
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
					vibrator.vibrate(50);
					isNearMePage=true;
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
					vibrator.vibrate(50);
					isNearMePage =true;
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
					vibrator.vibrate(50);
					isNearMePage=true;
					onMenuSelected(3);
				}
			});
		
		application = (TangoTabBaseApplication) getApplication();	
		/**
		 * get deals list from session and display
		 */
		List<DealsDetailVo> nearMeList =  application.getDealsList();
		if(!ValidationUtil.isNullOrEmpty(nearMeList) && !isNearMePage)
		{
			/**
			 * Get all offers and validate the offers	
			 */
			getOfferList();	
			
			pageCount = application.getNearMePageCount();
			emptyList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			Log.v("NearListAsyncTask size of the finalDealList is", String.valueOf(finalDealList.size()));
			NearMeListAdapter  nearMeListAdapter= new NearMeListAdapter(NearMeActivity.this,nearMeList,llShowMore);
			itemsList = getListView();
			itemsList.setCacheColorHint(Color.TRANSPARENT);
			itemsList.removeFooterView(llShowMore);
			itemsList.addFooterView(llShowMore);				
			setListAdapter(nearMeListAdapter);
			
		}
		else
		{
			/**
			 * Get list of Deals from web service
			 * 
			 */
			getDealList(pageCount);	
			/**
			 * Get all offers form web service	and set the app notification
			 */
			getOfferList();				
		}
		/**
		 * Disable the emptyList if latitude and longitude are both 0.0
		 */
			if(AppConstant.dev_lat == 0.0 && AppConstant.dev_lang == 0.0)
			{
				emptyList.setVisibility(View.GONE);
			}
			
		/**
		 * On click listener for show more click
		 */
		llShowMore.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					pageCount++;
					isShowMore = true;
					getDealList(pageCount);	
				}
			});
		
		
		/**
		 *  set onClickListeners for Buttons(Nearby, Expiring, Recent and Map).
		 */		

		map.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(final View v) {
				
				Vibrator myVib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
				 myVib.vibrate(50);
				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						v.setClickable(true);
					}
				}, 600);

				if (checkInternetConnection())
				{
					/**
					 * get list of deals from application.
					 */
					List<DealsDetailVo> mapDealsList = application. getDealsList();
					if (ValidationUtil.isNullOrEmpty(mapDealsList))
					{
						showDialog(0);
					}
					else {

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
										final Context appContext = getApplicationContext();
										/**
										 * Start map activity
										 */
									    Intent mapIntent = new Intent(appContext, MappingActivity.class);
									   	startActivity(mapIntent);	
									}
								});
							}
						}).start();
						
					}
				} else
					showDialog(10);
			}

		});
				
	}
	
	private void enableLocationSettings() {
	    Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(settingsIntent);
	}
	
	private boolean debugModeOn() {
		// TODO Auto-generated method stub
		return false;
	}
	/**
	 * Get list of offers for notifications.
	 * 
	 */
	private void getOfferList()
	{
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		String userId = spc.getString("username", "");
		String password =spc.getString("password", "");
		loginvo = new LoginVo();
		loginvo.setUserId(userId);
		loginvo.setPassword(password);
		/**
		 * Get all the offers from asyncTask call
		 */
		new MyOffersListAsyncTask().execute(loginvo);
		
	}
	/**
	 * Retrieve all the deals from number of page count
	 * @param pageCount
	 */
	private void getDealList(int pageCount)
	{
		/**
		 * Get distance value from shared prefereneces.
		 */
		SharedPreferences spc1 = getSharedPreferences("Distance", 0);
		String distance_set = spc1.getString("distancevalue", "");			
			if(ValidationUtil.isNullOrEmpty(distance_set))
			{
				distance_set = "20";
			}
		
		SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
		userId = spc3.getString("UserId", "");		
		/**
		 * get city and zipCode from shared preferences.
		 */
		SharedPreferences spc = getSharedPreferences("cityandzip", 0);
		cityName =spc.getString("cityname", "");
		zipCode =spc.getString("zipcode", "");
		String lat = spc.getString("devLat", "");
		String lang  = spc.getString("devLang", "");
		String s = lat+" "+lang;
		
		if(s.contains("E"))
		{
			dev_lat=Double.valueOf(lat)/1E6;
			dev_lang = Double.valueOf(lang)/1E6;
		}
		/**
		 * Set all the data in near me vo object
		 */
		NearMeVo nearMeVo = new NearMeVo();
		nearMeVo.setCityName(cityName);
		nearMeVo.setZipCode(zipCode);
		nearMeVo.setLattitude(dev_lat);
		nearMeVo.setLongittude(dev_lang);
		nearMeVo.setSetDistance(distance_set);
		nearMeVo.setUserId(userId);
		nearMeVo.setPageIndex(pageCount);
		application.setNearMePageCount(pageCount);
		Log.v("nearMeVo is ", nearMeVo.toString());
		/**
		 * Get list of Deals from web services.
		 */
		if(!ValidationUtil.isNull(nearMeVo))
		{
			new NearListAsyncTask().execute(nearMeVo);
		}	
		
	
	}
	/**
	 * AsyncTask call for get list of deals in near me activity
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class NearListAsyncTask extends AsyncTask<NearMeVo, Void, List<DealsDetailVo>>
	{
		private ProgressDialog mDialog=null;
		@Override
		protected void onPreExecute()
		{
				mDialog = ProgressDialog.show(NearMeActivity.this, "Please Wait", "Loading...");
				mDialog.setCancelable(true);
		}
		@Override
		protected List<DealsDetailVo> doInBackground(NearMeVo... nearMeVo)
		{
			List<DealsDetailVo> dealsList =null;
			try{
				NearMeService nearService = new NearMeService();
				dealsList = nearService.getListOfDeals(nearMeVo[0]);
			}catch(Exception e)
			{
				Log.e("Exception occured", "Exception occured at the time of login",e);
			}
			return dealsList;
		}
		@Override
		protected void onPostExecute(List<DealsDetailVo> dealsList)
		{
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			if(!ValidationUtil.isNullOrEmpty(dealsList))
			{
				List<DealsDetailVo> nearMeList =  application.getDealsList();
				if(!ValidationUtil.isNullOrEmpty(nearMeList) && finalDealList.size()==0 )
				{
					if(!ValidationUtil.isNullOrEmpty(dealsList))
						nearMeList.addAll(dealsList);
					finalDealList.addAll(nearMeList);
				}
				else 
				{
					finalDealList.addAll(dealsList);
				}
				emptyList.setVisibility(View.GONE);
				itemsList.setVisibility(View.VISIBLE);
				application.setOffersList(offersList);
				Log.v("NearListAsyncTask size of the finalDealList is", String.valueOf(finalDealList.size()));
				application.setDealsList(finalDealList);
				AppConstant.dealsList = finalDealList;
				/**
				 * Display list of deals by using adapter.
				 */
				NearMeListAdapter  nearMeListAdapter= new NearMeListAdapter(NearMeActivity.this,finalDealList,llShowMore);
				/**
				 * Cursor to be point in next 10 records.
				 */
				if(!ValidationUtil.isNullOrEmpty(finalDealList) && finalDealList.size()>10)
				{
					getListView().postDelayed(new Runnable()
					{          
					    @Override
					    public void run() {
					    	itemsList.setSelection((pageCount-1)*10);
					    }
					},100L);
					
				}
				itemsList = getListView();
				itemsList.setCacheColorHint(Color.TRANSPARENT);
				itemsList.removeFooterView(llShowMore);
				itemsList.addFooterView(llShowMore);
				setListAdapter(nearMeListAdapter);
				SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
			    int scroll = preferences.getInt("scrollValue", 0);
			    int top = preferences.getInt("Top", 0);
			    if(itemsList!=null)
			    itemsList.setSelectionFromTop(scroll, top);
			}
			else{
				NearMeListAdapter  nearMeListAdapter =null;
				if(isShowMore)
				{
					if(finalDealList.size()!=0){
						nearMeListAdapter= new NearMeListAdapter(NearMeActivity.this,finalDealList,llShowMore);
					}
					else
					{
						List<DealsDetailVo> dealList = application.getDealsList();
						nearMeListAdapter= new NearMeListAdapter(NearMeActivity.this,dealList,llShowMore);
					}
					
					getListView().postDelayed(new Runnable()
					{          
					    @Override
					    public void run() {
					    	itemsList.setSelection((pageCount-1)*10);
					    }
					},100L);
					application.setDealsList(finalDealList);
					itemsList = getListView();
					itemsList.setCacheColorHint(Color.TRANSPARENT);
					itemsList.removeFooterView(llShowMore);
					setListAdapter(nearMeListAdapter);
				}
				else
				{
					emptyList.setVisibility(View.VISIBLE);
					itemsList.setVisibility(View.GONE);
					if(AppConstant.dev_lat == 0.0 && AppConstant.dev_lang == 0.0)
					{
						emptyList.setVisibility(View.GONE);
					}
				}
			}
			
		}
		
	}
	

	/**
	 * AsyncTask call to retrieve all the offers from service using different thread
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	public class MyOffersListAsyncTask extends AsyncTask<LoginVo, Void, List<OffersDetailsVo>>
	{
		@Override
		protected void onPreExecute()
		{
			
		}
		@Override
		protected List<OffersDetailsVo> doInBackground(LoginVo... loginVo)
		{
			List<OffersDetailsVo> offersList = null;			
			try 
			{				
				MyOffersService service = new MyOffersService();
				offersList = service.getOffers(pageCount, loginVo[0]);				
                
			} catch (Exception e)
			{
				Log.e("Exception occured get list of offers", "", e);
			}			
			return offersList;
		}
		@Override
		protected void onPostExecute(List<OffersDetailsVo> offersList)
		{
			if(!ValidationUtil.isNullOrEmpty(offersList))
			{
				/**
				 * Send appNotifications for Expired offers
				 */
				sendAppNotification(offersList);
			}
			/**
			 * Validate the offers for list of offers
			 */
			if(!ValidationUtil.isNullOrEmpty(offersList))
				validateTheOffers();
		}	
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		/**
		 * Save scroll position in shared preferences.
		 */
	    SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if(itemsList!=null){
		int scroll = itemsList.getFirstVisiblePosition();
		edit.putInt("scrollValue", scroll);
		View v = itemsList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		edit.putInt("Top", top);
		edit.commit();
		}
	}

	@Override	
	protected void onResume() 
	{
		super.onResume();	
		SharedPreferences preferences = getSharedPreferences("NEARSCROLL", 0);
	    int scroll = preferences.getInt("scrollValue", 0);
	    int top = preferences.getInt("Top", 0);
	    if(itemsList!=null)
	    itemsList.setSelectionFromTop(scroll, top); 
	    if(isSettingsChanged)
	    {
	    	getDealList(pageCount);
	    	isSettingsChanged =false;
	    }
	};
	/**
	 * Convert point to geo point and get the address details.
	 * 
	 * @param point
	 */
	public void ConvertToPoint(GeoPoint point)
	{
		Geocoder geoCoder = new Geocoder(getBaseContext(),Locale.getDefault());
		try {
			List<Address> addresseses = geoCoder.getFromLocation(point.getLatitudeE6() / 1E6,point.getLongitudeE6() / 1E6, 1);
			dev_lat = addresseses.get(0).getLatitude();
			dev_lang =addresseses.get(0).getLongitude();	
			AppConstant.dev_lat =dev_lat;
			AppConstant.dev_lang = dev_lang;
			
			Log.v("dev_lat and dev_lang ","dev_lang = "+dev_lang+"dev_lang = "+dev_lang);
			
			if (addresseses.size() > 0) 
			{
				zipCode = addresseses.get(0).getPostalCode();
				cityName = addresseses.get(0).getSubAdminArea();				
			}
			
		} catch (IOException e)
		{
			Log.e("Exception:", "Exception occuerd at the time of getting address from point", e);
			e.printStackTrace();
		}
	}
  	
 	public void StopLocationUpdates()
  	{
  		if (locationManager != null && locationListener != null)
  			locationManager.removeUpdates(locationListener);
  	}

	/**
	 * Menu select functionality added.
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
		finish();
	}
	/**
	 * Method will validate the offers and set the local and app notifications for expired offers.
	 * 
	 */
	private void validateTheOffers()
	{
		boolean isFirstTimeRun =false;
		final String PREFS_NAME = "MyPrefsFile";

		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

		if (settings.getBoolean("my_first_time", true)) {
			isFirstTimeRun =true;
			 settings.edit().putBoolean("my_first_time", false).commit(); 
		}
		boolean isPackageExists = isPackageExists(AppConstant.PACKAGE_NAME);		
		
		if(isPackageExists)
		{
			ApplicationDetails appDetails = null;
			try {
				appDetails = getApplicationInfo(false,AppConstant.PACKAGE_NAME);
			} catch (NameNotFoundException e)
			{						
				e.printStackTrace();
			}
			/**
			 * Check if application version is 1.6 or not and then implement
			 */
			if(!ValidationUtil.isNull(appDetails))
			{
				String appVersion = appDetails.getVersionName();
				application.setAppVerison(appVersion);
				if(appVersion.equals(AppConstant.INSTLL_VERSION) && isFirstTimeRun)
				{
					String installDate = appDetails.getAppInstallDate();
					//Save the date of v 1.6 installation in the app Settings
					application.setInstallDate(installDate);
					Log.v("Application install Date is ", installDate);				
					if(!ValidationUtil.isNullOrEmpty(offersList))
					{
							validateTheOffersAndNotify(offersList);	
					}
				}
			}
			
		}
	}
	
	/**
	 * Check whether the package exists in the device or not.
	 * 
	 * @param targetPackage
	 * @return
	 */
	private boolean isPackageExists(String targetPackage)
	{
		Log.v("Invoking isPackageExists() method ", "targetPackage ="+targetPackage);
		PackageManager pm=getPackageManager();
		try
		{
		    PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
		} catch(NameNotFoundException e)
		{
			Log.e("Error", "Error ocuured in checking whther the package exists into the device or not.");
		    return false;
		}  
		return true;
	 } 
	
	
	/**
	 * Get all the application information from package.
	 * @param getSysPackages
	 * @return
	 * @throws NameNotFoundException 
	 */
	private ApplicationDetails getApplicationInfo(boolean getSysPackages , String packageName) throws NameNotFoundException
	{
		Log.v("Invoking getApplicationInfo() method ", "getSysPackages ="+getSysPackages+" packageName = "+packageName);
	    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
	    ApplicationDetails appDetails = null;
	    for(int i=0;i<packs.size();i++)
	    {
	        PackageInfo packInfo = packs.get(i);
	        if ((!getSysPackages) && (packInfo.versionName == null)) {
	            continue ;
	        }
	        /**
	         * Check if package name is matching with application package name
	         */
	        if(packInfo.packageName.equals(packageName))
	        {
		        appDetails = new ApplicationDetails();
		        appDetails.setAppName(packInfo.applicationInfo.loadLabel(getPackageManager()).toString());
		        appDetails.setAppPackage(packInfo.packageName);
		        appDetails.setVersionName(packInfo.versionName);
		        appDetails.setVersionCode(String.valueOf(packInfo.versionCode));
		        PackageManager pm = getApplicationContext().getPackageManager();		        
		        try {
		        	//Find the installation time of the given application.
		        	ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
			        String appFile = appInfo.sourceDir;
			        long installed = new File(appFile).lastModified();		           
		            appDetails.setAppInstallDate(new Date(installed).toString());
		          }catch (IllegalArgumentException e)
		          {
		        	  Log.e("Error", "Error ocuured in getting application installation time.");
		        	  e.printStackTrace();
		          }	       
	       }
	    }
	    Log.v("AppDetails object is ", appDetails.toString());
	    return appDetails; 
	}
	
	/**
	 * This method will validate all the offers and add notify to the offers.
	 * 
	 * @param consumerDealList
	 */
	private void validateTheOffersAndNotify(List<OffersDetailsVo> offerList)
	{
		Log.v("Invoking validateTheOffersAndNotify( ) method", "consumerDealList ="+offerList.size());
		for(OffersDetailsVo offersDetailsVo:offerList)
			{
				String installDate =application.getInstallDate();
				Calendar cal = Calendar.getInstance();
				Log.v("installDate is ", installDate);				
				Date finalInstallDate =DateFormatUtil.parseGMTFormatDate(installDate);
				Log.v("finalInstallDate after parsing the install date", finalInstallDate.toString());
				
				String reserveDate = offersDetailsVo.getReserveTimeStamp();
				StringBuilder finalReserveDate = new StringBuilder();
				if(!ValidationUtil.isNullOrEmpty(reserveDate))
				{
					int index = reserveDate.indexOf(" ");	
					String endDate = offersDetailsVo.getEndTime();
					Log.v("ClaimDate is ", endDate);
					finalReserveDate.append(reserveDate.substring(0,index).trim()).append(" ").append(endDate);
					Log.v("finalClaimDate is ", finalReserveDate.toString());
				}
								
											
				//Check whether the claim date after install application date
				boolean isClaimDateAfterInstallDate = DateFormatUtil.isClaimDateAfterInstallDate(installDate,finalReserveDate.toString());
				Log.v("isClaimDateAfterInstallDate = ", String.valueOf(isClaimDateAfterInstallDate));
				
				//Get the claim date after two weeks
				Date claimDateAfterTwoWeeks = DateFormatUtil.dateAfterSomeTimePeriod(finalReserveDate.toString(),"Week",2,"yyyy-MM-dd hh:mm aa");				
				Log.v("claim Date after two weeks is ", claimDateAfterTwoWeeks.toString());
				
				//Check whether claims older than two weeks or not.
				boolean isTwoWeeksOlderClaim = (claimDateAfterTwoWeeks.after(cal.getTime()))?false:true;
				
				Log.v("isTwoWeeksOlderClaim = ",String.valueOf(isTwoWeeksOlderClaim));
				Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(finalReserveDate.toString(),"yyyy-MM-dd hh:mm aa");
				String currentDate = offersDetailsVo.getCurrentDate();
				Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
				if(isClaimDateAfterInstallDate && !isTwoWeeksOlderClaim)
				{					
					if(finalEndTime !=null)
					{
						
						boolean isExpiredOffer =(finalEndTime.after(current))?true:false;
						if(isExpiredOffer)			
						{
							Bundle bundle = new Bundle();
							bundle.putParcelable("selectOffers", offersDetailsVo);	
							/**
							 * Start new activity
							 */
							Intent appNotification = new Intent(getApplicationContext(),AppNotificationActivity.class);
							appNotification.putExtras(bundle);
						    startActivity(appNotification);		
						}
					}
				}	
				
		Date expireDate = DateFormatUtil.dateAfterSomeTimePeriod(finalReserveDate.toString(),"hour",2,"yyyy-MM-dd hh:mm aa");
		boolean notYetExpired =(expireDate.before(current))	?true:false;			
		if(notYetExpired && !isTwoWeeksOlderClaim)
		{
			Log.v("Offer to be notify ", "Please notify the given offer");
			sendLocalNotification(offersDetailsVo);                    
		}
								
	}	
	}
	/**
	 * Send app notifications for list of Expired offers.
	 * 
	 * @param offersList
	 */
	private void sendAppNotification(List<OffersDetailsVo> offersList)
	{
		if(ValidationUtil.isNullOrEmpty(offersList))
			return;
		Log.v("Invoking is ", "consumerdealsList = "+offersList.size());
	 	for(OffersDetailsVo offersDetailsVo:offersList)
		{			
			int isCheckin =0;
			if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getIsConsumerShownUp()))
			{
					isCheckin =Integer.parseInt(offersDetailsVo.getIsConsumerShownUp());
			}
			Log.v("isCheckin = ",String.valueOf(isCheckin));
			/**
			 * If the offer neither manual or auto check in
			 */
			if(isCheckin ==0)
			{
				String reserveDate = offersDetailsVo.getReserveTimeStamp();
				StringBuilder finalClaimDate = new StringBuilder();
				if(!ValidationUtil.isNullOrEmpty(reserveDate))
				{
					int index = reserveDate.indexOf(" ");	
					String claimDate =offersDetailsVo.getEndTime();
					Log.v("ClaimDate is ", claimDate);
					finalClaimDate.append(reserveDate.substring(0,index).trim()).append(" ").append(claimDate);
					Log.v("finalClaimDate is ", finalClaimDate.toString());
				}
								
				Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(finalClaimDate.toString(),"yyyy-MM-dd hh:mm aa");
				if(finalEndTime!=null)
				{
					//Check whether the offer expired or not.
					String currentDate = offersDetailsVo.getCurrentDate();
					Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
					
					boolean isExpiredOffer =(current.after(finalEndTime))?true:false;
					
					Log.v("isExpiredOffer = ",String.valueOf(isExpiredOffer));
		
					if(isExpiredOffer)			
					{
						//Toast.makeText(getApplicationContext(), "Offers to validate APP Notification for "+offersDetailsVo.getBusinessName(), Toast.LENGTH_LONG).show();
						Bundle bundle = new Bundle();
						bundle.putParcelable("selectOffers", offersDetailsVo);	
						Intent appNotification = new Intent(getApplicationContext(),AppNotificationActivity.class);
						appNotification.putExtras(bundle);
					    startActivity(appNotification);	
					   	/**
					   	 * Remove the local notification				    		
					   	 */
					    int dealId =0;
						if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
							dealId = Integer.parseInt(offersDetailsVo.getDealId());
						
						Log.v("dealId for remove local Notification ", String.valueOf(dealId));
						
			    		Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
				        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);   
				        alarmManager.cancel(pendingIntent);
				        pendingIntent.cancel();			  
					    
				}
				}
			}
			else
			{
				Log.v("Don't send local notification already checked in",String.valueOf(isCheckin));
			}
		}
	}
	/**
	 * Send the local notification to the given offers.
	 * 
	 * @param offersDetailsVo
	 */
	private void sendLocalNotification(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking sendLocalNotification() method ", "claimDate ="+offersDetailsVo.getReserveTimeStamp()+"businessName ="+offersDetailsVo.getBusinessName());
		int isCheckin =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getIsConsumerShownUp()))
		{
				isCheckin =Integer.parseInt(offersDetailsVo.getIsConsumerShownUp());
		}
		Log.v("isCheckin = ",String.valueOf(isCheckin));
		
		if(isCheckin ==0)
		{
			 /**
		      * First remove the local notification for the offer
		      */
		     removeAlarmForNotification(offersDetailsVo);
		     
			String reserve = offersDetailsVo.getReserveTimeStamp();
			int index = reserve.indexOf(" ");
			String claimTime = offersDetailsVo.getEndTime();
			Log.v("claimTime is ", claimTime);
						
			StringBuilder finalClaimDate = new StringBuilder();
			finalClaimDate.append(reserve.substring(0,index).trim()).append(" ").append(claimTime);
			Log.v("finalClaimDate is ", finalClaimDate.toString());	
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
			Date reserveDate=null;
	        try {
	              reserveDate = format.parse(finalClaimDate.toString());                         
	        	} catch (ParseException e)
	        	{
	        		Log.e("Error", "Error occured in parsing the calim date.");
	        		e.printStackTrace();
	        	}
	        if(reserveDate!=null)
	        {
	        
	        Calendar alarmCal = Calendar.getInstance();
	    	alarmCal.setTime(reserveDate);
	    	Long reserveTime = null;
	    	 long diffInMilisecond =Math.abs(alarmCal.getTimeInMillis()-System.currentTimeMillis());
	    	 if(alarmCal.getTimeInMillis()>System.currentTimeMillis())
	    	 {
	    		 reserveTime = diffInMilisecond;
	    		 
	    	 }
	    	 else
	    	 {
	    		 reserveTime = -diffInMilisecond;
	    	 }
	    		 
	    	setAlaramForNotification(reserveTime, offersDetailsVo);
	        }
		}
       
	}
	/**
	 * Set alarm for individual offers.
	 * 
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void setAlaramForNotification(long alramTime ,OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking setAlaramForNotification() method ", "alramTime ="+alramTime+" businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getReserveTimeStamp());
		/*
		 * Create an Alaram intent
		 * 
		 */
		int dealId =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
		{
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
		}
		
			Bundle bundle = new Bundle();
			bundle.putParcelable("selectOffers", offersDetailsVo);
			Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
	        alarmIntent.putExtras(bundle);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);      
	        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis() + (alramTime), pendingIntent);
	       		
	}
	
	/**
	 * Remove the alarm for the offers .
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void  removeAlarmForNotification(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking removeAlarmForNotification() method ", " businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getReserveTimeStamp());
		int dealId =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
		Log.v("dealId for remove alarm ", String.valueOf(dealId));
		Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);   
	    alarmManager.cancel(pendingIntent);
	    pendingIntent.cancel();
	}
	
	/**
	 * This method will check the Internet connection for the application.
	 * 
	 * @return
	 */
	private boolean checkInternetConnection() 
	{
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		return (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable() && conMgr.getActiveNetworkInfo().isConnected())? true:false;
		
	}
	/**
	 * Dialog message display.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(NearMeActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("No offers are found to display on map");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab.create();
		case 16:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(NearMeActivity.this);				
			ab2.setTitle("Location Services Denied");
			ab2.setMessage("Your device is configured to deny Location Services to TangoTab. Some features of TangoTab require these services to work. Please enable Location Services in the Settings App to continue.");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab2.create();

		case 17:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(NearMeActivity.this);
			ab3.setTitle("Location Services Denied");
			ab3.setMessage("Your account is inactive, Please contact TangoTab Administrator.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab3.create();
		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(NearMeActivity.this);
			ab10.setTitle("TangoTab");
			ab10.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");

			ab10.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}

				}
			});
			return ab10.create();
			
		case enable_service:
			AlertDialog.Builder ab7 = new AlertDialog.Builder(NearMeActivity.this);
			ab7.setTitle("My Location");
			ab7.setMessage("Not all location sources are currently enabled. For the fastest and most accurate location detection: Turn on GPS and wireless networks in location settings");
			ab7.setPositiveButton("Settings", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					enableLocationSettings();
					dialog.dismiss();
					isSettingsChanged = true;
				}
			});
			ab7.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab7.create();
		}
		return null;
	}
	
 
	/*
	 * ==================================================================================================================
	 * Geo Informaation of the Device
	 * ==================================================================================================================
	 */
	/**
	 * Initalizes the Location Manager and attaches the LocationUpdates Listener for the device current Lat Lng values
	 */
	private void initalizeLocationManagerService()
	{
		/*
		 * GPS Configuration 
		 */
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 10000L, 500.0f, this);
	   
		// Initialize the location fields
		if (!ValidationUtil.isNull(location)) 
		{
			Log.i("GEO LOCATION" , "Provider " + provider + " has been selected.");
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			Log.i("GEO LOCATION DETAILS" , "LAT and Long " + lat + "  "  + lng );
			//Push the Lat Lng values into Global Execution Context
			AppConstant.dev_lat  = lat;
			AppConstant.dev_lang = lng;
			
		}
		else 
		{
			Log.i("GEO LOCATION" , "Provider is not available");
			AppConstant.dev_lat  = 0.0;
			AppConstant.dev_lang = 0.0;
		}
	}

	public void onLocationChanged(Location location) 
	{
		double lat = location.getLatitude();
		double lng = location.getLongitude();
		Log.i("LocationChanged", "Lat = " + lat + "   : Lng = " + lng);
		
		//Push the Lat Lng values into Global Execution Context
		AppConstant.dev_lat  = lat;
		AppConstant.dev_lang = lng;
		
		
	}

	
	public void onProviderDisabled(String arg0) 
	{
		Log.i("LocationProvider", "[" + arg0 + "] has been Disabled!");
	}

	
	public void onProviderEnabled(String arg0) 
	{
		Log.i("LocationProvider", "[" + arg0 + "] has been Enabled!");
	}

	
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) 
	{
	}
	
	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
	}
	@Override
    public void onDestroy() 
	{      
        super.onDestroy();
        // Stop the tracker when it is no longer needed.
        tracker.stopSession();
    }
}
