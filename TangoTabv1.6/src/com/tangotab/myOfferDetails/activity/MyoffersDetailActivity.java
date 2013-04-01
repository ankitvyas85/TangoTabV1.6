package com.tangotab.myOfferDetails.activity;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.map.activity.MapPointActivity;
import com.tangotab.myOfferDetails.service.MyOffersDetailService;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;

/**
 * Retrieve deal detail information from the selected offers and do auto or Manual checkin to that offer
 * 
 * <br> Class :MyoffersDetailActivity
 * <br> layout :myofferdetails.xml
 * 
 * @author dillip.lenka
 *
 */
public class MyoffersDetailActivity extends Activity
{
	/**
	 * Meta definitions
	 */
	private OffersDetailsVo offersDetailsVo;
	private ProgressDialog mDialog;
	private ProgressDialog mlocDialog;
	private boolean isAutoCheckIn =false;
	private boolean isLocationAvailable=false;
	private float distanceInmiles;
	private String provider=null;
	private String message;
	private LocationManager locationManager;
	private Button checkin;
	private Vibrator myVib;
	private boolean isTimeToAutoCheck;
	private boolean isValidOffer;
	private boolean isTodaysOffer;
	
	private int autoCheckInCount;
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * Exceution will start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myofferdetails);
		
		/*
		 * Google tracker added.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.DEALS_DETAIL_PAGE);
		tracker.trackEvent("CheckIn", "TrackEvent", "checkIn", 1);
		
		Bundle bundle = getIntent().getExtras();
		/**
		 * Get selected offers from intent
		 */
		if(bundle!=null)
		{
			offersDetailsVo =(OffersDetailsVo)bundle.get("selectOffers");	
		}
		Button map = (Button) findViewById(R.id.mapAdress);
		checkin = (Button) findViewById(R.id.checkin);
		checkin.setVisibility(View.GONE);
		Button navigate = (Button) findViewById(R.id.navButton);
		
		// Refer to TextView from Layout
		TextView businessName = (TextView) findViewById(R.id.mybusinessName);
		TextView dealName = (TextView) findViewById(R.id.mydealname);
		TextView dealAddress = (TextView) findViewById(R.id.mydealaddress);
		TextView cusineType = (TextView) findViewById(R.id.mydealcusinetype);
		TextView dealDescription = (TextView) findViewById(R.id.mydealdescription);
		TextView dealRestriction = (TextView) findViewById(R.id.mydealrestrictions);
		TextView dealDate = (TextView) findViewById(R.id.mydealdate);
		TextView confirmationCode = (TextView) findViewById(R.id.confirmationCode);
		/**
		 * Menu widgets from UI
		 */
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		
		nearmeMenuItem.setBackgroundResource(R.drawable.places);
		myofferButton.setBackgroundResource(R.drawable.myoffers_active);
		searhMenuButton.setBackgroundResource(R.drawable.search);
		settingsMenuButton.setBackgroundResource(R.drawable.settings);
		
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		settingsRL.setBackgroundDrawable(null);
		nearmeRL.setBackgroundDrawable(null);
		searchRL.setBackgroundDrawable(null);
		myoffersRL.setBackgroundResource(R.drawable.hover_img);
		
		/**
		 * On click listener for near me button.
		 */
		nearmeRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						onMenuSelected(1);
					}
				});
		/**
		 * On click listener for my offers button.
		 */
		myoffersRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						onMenuSelected(0);
					}
				});
		/**
		 * On click listener for Search button.
		 */
		searchRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						onMenuSelected(2);
					}
				});
		
		/**
		 * On click listener for Settings button.
		 */
		settingsRL.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						onMenuSelected(3);
					}
				});
		
		// Refer to ImageView from Layout
		ImageView mydealImage = (ImageView) findViewById(R.id.mydealimage);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		Date startDate =null;
		String timestamp = offersDetailsVo.getReserveTimeStamp();
		String sdate = timestamp.substring(0, 11);
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		try {
			startDate = formatter.parse(sdate);
		} catch (ParseException e)
		{
			Log.e("Error ", e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		SimpleDateFormat formatter2 = new SimpleDateFormat("EEE, MMM dd yyyy");
		String mystartDate = formatter2.format(startDate);
		// imageDownloader.download(ImageUrl,mydealImage);
		ImageLoader imageLoader = new ImageLoader(getApplicationContext());
		imageLoader.DisplayImage(offersDetailsVo.getImageUrl(), mydealImage);
		businessName.setText(offersDetailsVo.getBusinessName());
		dealName.setText(offersDetailsVo.getDealName());
		cusineType.setText(offersDetailsVo.getConResId());
		dealRestriction.setText(offersDetailsVo.getDealRestriction());
		confirmationCode.setText("Confirmation Code: " + offersDetailsVo.getConResId());
		dealAddress.setText(offersDetailsVo.getAddress());
		dealDate.setText(mystartDate + "  " + offersDetailsVo.getStartTime() + " to "+ offersDetailsVo.getEndTime());
		dealDescription.setMaxWidth(210);
		dealDescription.setText(offersDetailsVo.getDealDescription());
		/**
		 * on click listener for navigate
		 */	
		navigate.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					isAutoCheckIn=false;
					myVib.vibrate(50);
					Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:q=" + offersDetailsVo.getAddress()));
					startActivity(intent); 						
				}
			});	
		/**
		 * Time check in order to do manual or auto check in.
		 */
		String reserveTime = offersDetailsVo.getReserveTimeStamp();
		StringBuilder stDate = new StringBuilder();
		StringBuilder endDate = new StringBuilder();
		if(!ValidationUtil.isNullOrEmpty(reserveTime))
		{
			int index = reserveTime.indexOf(" ");	
			String endTime = offersDetailsVo.getEndTime();
			if (endTime.equals("12:00 AM"))
			{
				endTime ="11:59 PM";
			}			
			String startTime = offersDetailsVo.getStartTime();
			if(index!=-1)
			{
				String reserveDate = reserveTime.substring(0,index).trim();
				String reserveStartTime = stDate.append(reserveDate).append(" ").append(startTime).toString();
				String reserveEndTime  = endDate.append(reserveDate).append(" ").append(endTime).toString();
				Date finalReservStartTime = DateFormatUtil.dateAfterSomeTimePeriod(reserveStartTime,"mins",15,"yyyy-MM-dd hh:mm aa");
				Date finalReservEndTime = DateFormatUtil.dateAfterSomeTimePeriod(reserveEndTime,"hour",2,"yyyy-MM-dd hh:mm aa");
				Log.v(" finalReservStartTime is ", finalReservStartTime.toString());
				Log.v("finalReservEndTime is ", finalReservEndTime.toString());
				String currentDate = offersDetailsVo.getCurrentDate();
							
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
				String localTime = sdf.format(new Date());
				
                if(!ValidationUtil.isNullOrEmpty(localTime))
                {
                	//Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
					Date currentLocalTime = DateFormatUtil.parseIntoDifferentFormat(localTime,"yyyy-MM-dd HH:mm:ss.SSSSSS");
                	isTimeToAutoCheck = ((finalReservStartTime.before(currentLocalTime)) && (finalReservEndTime.after(currentLocalTime)))?true:false;
					isValidOffer = ((finalReservStartTime.before(currentLocalTime)))?true:false;
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Calendar cal = Calendar.getInstance();
					String currentTime= df.format(cal.getTime()).toString();
					isTodaysOffer = (reserveDate.equalsIgnoreCase(currentTime));
                }
			}
		}
		
		/* getting location of device */
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
		new FetchCordinates().execute();
		
		/**
		 * Check in button on click Listener added.
		 */
		checkin.setOnClickListener(new OnClickListener()
			{	
				@Override
				public void onClick(View v){}
			});

		/**
		 * Map button on click listener
		 */
		map.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				myVib.vibrate(50);
				if(checkInternetConnection())
				{
					/**
					 * Start Map activity in order to display offers in map
					 */
					Intent myOfferMapIntent = new Intent(getApplicationContext(), MapPointActivity.class);
					myOfferMapIntent.putExtra("businessname", offersDetailsVo.getBusinessName());
					myOfferMapIntent.putExtra("itemAddress", offersDetailsVo.getAddress());
					myOfferMapIntent.putExtra("IsFromPlaceOrSearch", "FromPlace");
					myOfferMapIntent.putExtra("from", "myoffer");
					startActivity(myOfferMapIntent);
				}
				else
					 showDialog(10);				
			}
		});
	}
	
		
	private final LocationListener locationListener = new LocationListener()
	{ 
		public void onLocationChanged(Location location)
		{ 
			isLocationAvailable=true;
			isAutoCheckIn=true;
			updateWithNewLocation(location);
			locationManager.removeUpdates(locationListener);
		}

		public void onProviderDisabled(String provider)
		{
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) 
		{
			
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
			
		}
	};
	/**
	 * Display Dialog messages for different validations.
	 * 
	 */
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage(message.replace("%20", " "));
			ab.setPositiveButton("Dismiss", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab.create();
		case 1:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("You have successfully checked in this offer");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab1.create();
		case 10:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

				}
			});

			return ab3.create();
		case 111:
			AlertDialog.Builder ab6 = new AlertDialog.Builder(MyoffersDetailActivity.this);
			ab6.setTitle("Alert:");
			ab6.setMessage("The offer is not valid yet");
			ab6.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			return ab6.create();
		}
		return null;
	}
	/**
	 * This method will be update with the new location.
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location)
	{
		if (location != null)
		{
			double dealLat, dealLong = 0.0;
			double locationLat,locationLong=0.0;
			locationLat = location.getLatitude();
			locationLong = location.getLongitude();
			AppConstant.locationLat =locationLat;
			AppConstant.locationLong = locationLong;
			Log.v("Device Latttitude is ", String.valueOf(locationLat));
			Log.v("Device Longitude is ", String.valueOf(locationLong));
			
			Location locationA = new Location("DeviceLocation");

			locationA.setLatitude(locationLat);
			locationA.setLongitude(locationLong);

			Location locationB = new Location("UserLocation");
			if(!ValidationUtil.isNull(offersDetailsVo))
			{
				dealLat = Double.parseDouble(offersDetailsVo.getLatitude());
				dealLong = Double.parseDouble(offersDetailsVo.getLongitude());
				
				Log.v("Restrurant Latttitude is ", String.valueOf(dealLat));
				Log.v("Restrurant Longitude is ", String.valueOf(dealLong));
				
				locationB.setLatitude(dealLat);
				locationB.setLongitude(dealLong);
			}		
			
			//distance between Device and restaurant.
			
			float distance = locationA.distanceTo(locationB);

			distanceInmiles = (float) (distance * 0.000621371);
			
			Log.v("distance in miles is ", String.valueOf(distanceInmiles));
			/**
			 * If distance between restaurant and device is less than 1/4th of
			 * miles.
			 */
			//Toast.makeText(getApplicationContext(), "distance ="+distanceInmiles, 2000).show();
			

			if (isTimeToAutoCheck && distance<= Float.valueOf(AppConstant.NEAR_TO_RESTRURANT))

			{	
				if(isAutoCheckIn && autoCheckInCount==0)
				{
					isAutoCheckIn=false;
					doCheckIn("Y");
				}
			}

			else if (isTodaysOffer && !(isValidOffer)&& distance<= Float.valueOf(AppConstant.NEAR_TO_RESTRURANT)) 
				{
				showDialog(111);
			}else 
			{
				Log.v("Auto check in status", "Auto Checkin is not possible");
			}	
		}
	}
	
	/**
	 * This method will take care all the check in functionality.
	 * 
	 * @param autoCheckIn
	 */
	private void doCheckIn(String autoCheckIn)
	{
		if (checkInternetConnection())
		{
				//Get all user details from sharedPreferences.				
				SharedPreferences spc = getSharedPreferences("UserDetails", 0);
				String firstName = spc.getString("firstName", "");
				String lastName = spc.getString("lastName", "");
				offersDetailsVo.setFirstName(firstName);
				offersDetailsVo.setLastName(lastName);
				offersDetailsVo.setAutoCheckIn(autoCheckIn);
				/** 
				 * Call the asyntask to execute the service
				 */
				new InsertDealAsyncTask().execute();
		}
		else{
				showDialog(10);
			}
		}
	
		
	/**
	 * This call will be used to run the check an offer in service in different thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
		public class InsertDealAsyncTask extends AsyncTask<Void, Void, String>
		{		
			@Override
			protected void onPreExecute()
			{
				mDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Please Wait", "Loading...");
				mDialog.setCancelable(true);
			}
			@Override
			protected String doInBackground(Void... params)
			{
				//message =null;
				try {
					MyOffersDetailService service = new MyOffersDetailService();
					message = service.checkIn(offersDetailsVo);
				}
				catch (Exception e)
				{
					Log.e("Error", "Error ocuuered in invoking check in service url and detailurl =",e);
					message =null;
				}
				return message;
			}
			
			@Override
			protected void onPostExecute(String message) 
			{
				try{
					mDialog.dismiss();
				}catch(Exception e)
				{
					Log.e("EXception:", "Exception occured before dismiss dilog.");
				}
				
				if (message != null) 
				{
					/*
					 * Remove the alarm if already set.
					 */
					if(!ValidationUtil.isNull(offersDetailsVo))
						removeAlarmForNotification(offersDetailsVo);
				 	if (message.equals("Successfully CheckIn."))
					{
						
				 		Log.v("result ", "Successfully CheckIn");
						autoCheckInCount++;		
						showDialog(1);
					}
					else
					{
						showDialog(0);
					}         	
		           
				}			
			
			}	

		}	
		
	/**
	 * AsyncTask to get user location update
	 * @author Mahantesh.Tavag
	 *
	 */
	
    public class FetchCordinates extends AsyncTask<Void, Void, String>
    {

        @Override
        protected void onPreExecute()
        {
        	Criteria criteria = new Criteria();
    		provider = locationManager.getBestProvider(criteria, true);
    
    		if (provider != null) {
    			Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
    			autoCheckInCount=0;
        	    updateWithNewLocation(lastKnownLocation);
    			locationManager.requestLocationUpdates(provider, 0, 0,
    					locationListener);
    			
    			new CountDownTimer(5000, 1000) {
   			     public void onTick(long millisUntilFinished) {
   			    	 
   			     }
   			     public void onFinish() {
   			    	 if(!isLocationAvailable)
   			    	 {
   			    		 new FetchCordinates().cancel(true);
   			    		 locationManager.removeUpdates(locationListener);
   			    	 }
   			     }
   			  }.start();
    			
    		}
        }
        @Override
        protected String doInBackground(Void... params)
        {
          return null;  
        }
        @Override
        protected void onPostExecute(String result)
        {
        	isAutoCheckIn=true;
        }
        @Override
        protected void onCancelled()
        {
            Log.v("Cancel","Cancelled by user!");
            locationManager.removeUpdates(locationListener);
        }       
    }
		
		/**
		 * Asynctask class added for Spinning wheel fro 5 mins.
		 * 
		 * @author Dillip.Lenka
		 *
		 */
		public class SpinningTask extends AsyncTask<Void,Void,Void>
		{
			
			@Override
			protected void onPreExecute()
			{
	        	mlocDialog = ProgressDialog.show(MyoffersDetailActivity.this, "Please wait", "Loading...");
				mlocDialog.setCancelable(true);
	        }
			
			 @Override
		        protected void onCancelled(){
				 mlocDialog.dismiss();
		        }
			 
	        @Override
	        protected Void doInBackground(Void... params)
	        {
	        	try {
	                    Thread.sleep(5000);
	                } catch (InterruptedException e)
	                {
	                    
	                    e.printStackTrace();
	                }
	        	mlocDialog.dismiss();

	            return null;
	        }
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
	 * Remove the alarm for the offers .
	 * @param alramTime
	 * @param consumerDeals
	 */
	private void  removeAlarmForNotification(OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking removeAlarmForNotification() method ", " businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getCurrentDate());
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
	 * OnResume functionality added.
	 */
	@Override
	protected void onResume()
	{
		super.onResume();
	}
	/**
	 * Back button functionality added.
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		locationManager.removeUpdates(locationListener);
		isAutoCheckIn=false;
		finish();
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
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		tracker.stopSession();
	}
}
