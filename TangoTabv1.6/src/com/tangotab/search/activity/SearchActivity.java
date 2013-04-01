package com.tangotab.search.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.GeoCoderUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.adapter.SearchListAdapter;
import com.tangotab.search.service.SearchService;
import com.tangotab.search.vo.SearchVo;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Search activity class will be used for search the restaurant.
 * 
 * <br> Class :SearchActivity
 * <br> Layout :search.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class SearchActivity extends ListActivity implements LocationListener
{
	/*
	 * Meta Definitions
	 */
	private EditText restName;
	private EditText editAddress;
	private int pageCount=1;
	private ListView itemsList=null;
	private String provider;
	private LinearLayout llShowMore;
	private List<DealsDetailVo> finalDealList = new ArrayList<DealsDetailVo>();	
	public TangoTabBaseApplication application;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private TextView emptyListView;
	private boolean isShowMore=false;
	private boolean isSeacrchPage=false;
	
	/**
	 * Execution will start here.
	 */
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		// Google Analytics
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SEARCH_PAGE);
		tracker.trackEvent("Search", "TrackEvent", "search", 1);
			
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		restName = (EditText) findViewById(R.id.restname);
		editAddress = (EditText) findViewById(R.id.searchaddress);
		emptyListView =(TextView) findViewById(R.id.emptylist);
				
		
		final Button restSearch = (Button)findViewById(R.id.restsearch);
		final Button addSearch =  (Button)findViewById(R.id.addsearch);
		
		application = (TangoTabBaseApplication) getApplication();	
		
		llShowMore = (LinearLayout) getLayoutInflater().inflate(R.layout.showmorecell, null);
		/**
		 * Display the deals from session
		 */
		List<DealsDetailVo> searchList = application.getSearchList();
		if(!ValidationUtil.isNullOrEmpty(searchList))
		{
			Log.v("DealsListAsyncTask size of the finalDealList is", String.valueOf(searchList.size()));
			SearchVo searchvo =application.getSearchVo();
			if(!ValidationUtil.isNull(searchvo))
			{
				restName.setText(searchvo.getRestName());
				editAddress.setText(searchvo.getAddress());
				pageCount = Integer.parseInt(searchvo.getPageIndex());
			}
			SearchListAdapter  searchListAdapter= new SearchListAdapter(SearchActivity.this,searchList,true,llShowMore);
			itemsList = getListView();
			itemsList.setCacheColorHint(Color.TRANSPARENT);
			itemsList.removeFooterView(llShowMore);
			itemsList.addFooterView(llShowMore);
			emptyListView.setVisibility(View.GONE);
			setListAdapter(searchListAdapter);
			SharedPreferences preferences = getSharedPreferences("SCROLL", 0);
		    int scroll = preferences.getInt("ScrollValue", 0);
		    int top = preferences.getInt("Top", 0);
		    if(itemsList!=null)
		    itemsList.setSelectionFromTop(scroll, top);
		}
		/**
		 * Menu button from UI
		 */
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		
		nearmeMenuItem.setBackgroundResource(R.drawable.places);
		myofferButton.setBackgroundResource(R.drawable.mydeals);
		searhMenuButton.setBackgroundResource(R.drawable.search_active);
		settingsMenuButton.setBackgroundResource(R.drawable.settings);
		
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		searchRL.setBackgroundResource(R.drawable.hover_img);
		nearmeRL.setBackgroundDrawable(null);
		myoffersRL.setBackgroundDrawable(null);
		settingsRL.setBackgroundDrawable(null);
		/**
		 * Get customURlHandler information.
		 * 
		 */
		Bundle bundel = getIntent().getExtras();
		if(bundel!=null)
		{
			String fromPage = (String) bundel.getString("fromPage");
			if(!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equals("customURL"))
			{
				String address = (String) bundel.getString("address");
				if(!ValidationUtil.isNullOrEmpty(address))
				{
					editAddress.setText(address);
					search(1);
				}
				else
				{
					DealsDetailVo  dealsDetailVo = (DealsDetailVo) bundel.getSerializable("selectDeal");
					String dealId = (String)bundel.getString("dealId");
					if(!ValidationUtil.isNull(dealsDetailVo) && !ValidationUtil.isNullOrEmpty(dealId))
					{
					/**
					 * Start the claim offer activity with deal id and deal date.
					 */
					Intent claimIntent = new Intent(getApplicationContext(), ClaimOfferActivity.class);
					claimIntent.putExtra("from", "search");
					claimIntent.putExtra("fromPage", "customURL");
					claimIntent.putExtra("dealId", dealId);
					claimIntent.putExtra("selectDeal",  dealsDetailVo);
					startActivity(claimIntent);
					}
					else{
					
						SearchVo searchVo = new SearchVo();
						/**
						 * Get the current location details
						 */
						initalizeLocationManagerService(searchVo);
						/**
						 * Call the asynctask to get the deals list.
						 */
						new DealsListAsyncTask().execute(searchVo);
					}
				}
				
			}
		}
		/**
		 * on click listener on near me menu button.
		 */
		nearmeRL.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					isSeacrchPage=true;
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
					isSeacrchPage=true;
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
					if(isSeacrchPage)	
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
					isSeacrchPage=true;
					onMenuSelected(3);
				}
			});
		/**
		 * On click handler for restaurant search
		 */
		restSearch.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(final View v)
					{
						pageCount = 1;
						myVib.vibrate(50);
						llShowMore.setVisibility(View.INVISIBLE);
						if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
							application.getSearchList().clear();
						finalDealList.clear();
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(restSearch.getWindowToken(), 0);
							
						search(pageCount);	
						/*
						 * Focus on restaurant search
						 */
						restSearch.requestFocus();
					}
		});
		
		/**
		 * on click handler for address search.
		 */		
		addSearch.setOnClickListener(new OnClickListener()
			{
			
				public void onClick(final View v)
				{
					pageCount = 1;
					myVib.vibrate(50);
					llShowMore.setVisibility(View.INVISIBLE);
					finalDealList.clear();
					if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
						application.getSearchList().clear();
					InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(addSearch.getWindowToken(), 0);
						
					search(pageCount);
					/*
					 * Focus on address search
					 */
					addSearch.requestFocus();
					
				}
			});
		
		/**
		 * Edit listener for edit Address name edit.
		 */
		
		editAddress.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
		    {		    
		        if (actionId == EditorInfo.IME_ACTION_SEARCH)
		        {
		        	pageCount = 1;
		        	llShowMore.setVisibility(View.INVISIBLE);
		        	InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
						finalDealList.clear();
						if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
							application.getSearchList().clear();
		        	search(pageCount);
		        }
		        return false;
		    }
		});
		
		/**
		 * Edit listener for restaurant name edit.
		 */
		
		restName.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
		    {		    
		        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
		        	pageCount = 1;
		        	llShowMore.setVisibility(View.INVISIBLE);
		        	InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(restName.getWindowToken(), 0);
						finalDealList.clear();
						if(!ValidationUtil.isNullOrEmpty(application.getSearchList()))
							application.getSearchList().clear();
		        	search(pageCount);
		        }
		        return false;
		    }
		});
		/**
		 * On click listener for show more deals
		 */
		llShowMore.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					myVib.vibrate(50);
					pageCount++;
					isShowMore = true;
					search(pageCount);	
				}
			});
		
		/**
		 * Auto scroll down offers
		 * 
		*/
		
		/*if(itemsList!=null)
		{
		itemsList.setOnScrollListener(new OnScrollListener(){
			
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {}
								
					@Override
					public void onScroll(AbsListView view, int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						
						int lastInScreen = firstVisibleItem + visibleItemCount;				

						//is the bottom item visible & not loading more already ? Load more !
						if((lastInScreen == totalItemCount) && totalItemCount != 1 && totalItemCount != totalItemCount +10 && totalItemCount == 11)
						{					
							pageCount++;
							search(pageCount);			
							
						}
					}
				});
		
		}*/
		
		
	}
	/**
	 * search the deals from page count
	 * 
	 * @param pageCount
	 */
	private void search(int pageCount)
	{
		SearchVo searchVo = new SearchVo();
		searchVo.setPageIndex(String.valueOf(pageCount));
		String addressText = editAddress.getText().toString();
		searchVo.setAddress(addressText);
		String restNameText = restName.getText().toString();
		searchVo.setRestName(restNameText);
		Double locationLat =0.0;
		Double locationLong = 0.0;
		tracker.trackEvent("Search", "TrackEvent", ""
				+ editAddress.getText().toString()
				+ restName.getText().toString(), 1);
		/**
		 * Get the address information from the Geo coder.
		 */
		if(!ValidationUtil.isNullOrEmpty(addressText) || !ValidationUtil.isNullOrEmpty(restNameText)){
			if(!ValidationUtil.isNullOrEmpty(addressText))
			{
				tracker.trackEvent("Search", "TrackEvent", ""
						+ editAddress.getText().toString()
						+ restName.getText().toString(), 1);
				List<Address> address;
				final Geocoder coder = new Geocoder(getBaseContext());
				try {
					address = coder.getFromLocationName(addressText.trim(), 5);
					Address location = address.get(0);
					locationLat = location.getLatitude();
					locationLong = location.getLongitude();
					
				} catch (Exception e)
				{
					Log.e("Exception occured in Geocoder to retrieve the location", "Exception", e);
					GeoCoderUtil.getLatLong(addressText);
					locationLat = AppConstant.locationLat;
					locationLong = AppConstant.locationLong;
				}
			}
			else if(!ValidationUtil.isNullOrEmpty(restNameText))
			{
				LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				try{
				Criteria criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, false);
				Location location = locationManager.getLastKnownLocation(provider);
					if (!ValidationUtil.isNull(location)) 
					{
						Log.i("GEO LOCATION", "Provider " + provider+ " has been selected.");
						locationLat = location.getLatitude();
						locationLong = location.getLongitude();
					} 
					else 
					{
						Log.i("GEO LOCATION", "Provider is not available");
						locationLat = 0.0;
						locationLong = 0.0;
					}
				}catch (Exception e) 
				{
					Log.e("Exception occured in Geocoder to retrieve the location", "Exception", e);
					GeoCoderUtil.getLatLong(restNameText);
					locationLat = AppConstant.locationLat;
					locationLong = AppConstant.locationLong;
				}
			}
			Log.v("Laonitude and latitude ", "longitute = "+locationLong+" latitude ="+locationLat);
			 /**
			  * put location lat and long into shared preferences.
			  */
			 SharedPreferences location = getSharedPreferences("LocationDetails", 0);
			 SharedPreferences.Editor edit = location.edit();
			 edit.putString("locLat", String.valueOf(locationLat));
			 edit.putString("locLong", String.valueOf(locationLong));
			 edit.commit();
			 
			searchVo.setLocLaong(String.valueOf(locationLong));
			searchVo.setLocLat(String.valueOf(locationLat));
			SharedPreferences spc1 = getSharedPreferences("Distance", 0);
			String distance_set = spc1.getString("distancevalue", "");
			/**
			 * Take the default distance if distance is nul or empty
			 */
				if(ValidationUtil.isNullOrEmpty(distance_set))
				{
					distance_set = "20";
				}				
			searchVo.setDistance(distance_set);	
			/**
			 * Get user details from shared preferences.
			 */
			SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
			String userId = spc3.getString("UserId", "");	
			searchVo.setUserId(userId);
			application.setSearchVo(searchVo);
			/**
			 * Call the asynctask to get the deals list.
			 */
			new DealsListAsyncTask().execute(searchVo);
		}
	}
	
	/**
	 * Web service call to get list of Deals from the search criteria by using AsyncTask.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class DealsListAsyncTask extends AsyncTask<SearchVo, Void, List<DealsDetailVo>>
	{
		private ProgressDialog mDialog=null;
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SearchActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
		}
		
		@Override
		protected List<DealsDetailVo> doInBackground(SearchVo... searchVo)
		{
			List<DealsDetailVo> dealsList =null;
			try
			{
				SearchService service = new SearchService();
				dealsList = service.getSearchList(searchVo[0]);	
				
			}catch(Exception e)
			{
				Log.e("Exception:", "Exception occured at the time of retrieve list of deals from search criteria.", e);
				dealsList =null;
				Log.e("", "", e);
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
				List<DealsDetailVo> oldDealList =application.getSearchList();
				if(!ValidationUtil.isNullOrEmpty(oldDealList) && finalDealList.size()==0)
				{				
					oldDealList.addAll(dealsList);					
					finalDealList.addAll(oldDealList);
				}
				else
				{
					finalDealList.addAll(dealsList);
				}
				application.setSearchList(finalDealList);
				Log.v("DealsListAsyncTask size of the finalDealList is", String.valueOf(finalDealList.size()));	
				/**
				 * Display the list of deal using adapter
				 */
				SearchListAdapter  searchListAdapter= new SearchListAdapter(SearchActivity.this,finalDealList,true,llShowMore);

				if(!ValidationUtil.isNullOrEmpty(finalDealList) && finalDealList.size()>10)
				{
					getListView().postDelayed(new Runnable() {          
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
				emptyListView.setVisibility(View.GONE);
				setListAdapter(searchListAdapter);
			}
			else
			{
				SearchListAdapter  searchListAdapter =null;
				if(isShowMore)
				{
					if(finalDealList.size()!=0){
						searchListAdapter= new SearchListAdapter(SearchActivity.this,finalDealList,true,llShowMore);
						application.setSearchList(finalDealList);
					}
					else
					{
						List<DealsDetailVo> dealList = application.getSearchList();
						searchListAdapter= new SearchListAdapter(SearchActivity.this,dealList,true,llShowMore);
						application.setSearchList(dealList);
					}
					
					getListView().postDelayed(new Runnable()
					{          
					    @Override
					    public void run() {
					    	itemsList.setSelection((pageCount-1)*10);
					    }
					},100L);
					
					itemsList = getListView();
					itemsList.setCacheColorHint(Color.TRANSPARENT);
					itemsList.removeFooterView(llShowMore);
					setListAdapter(searchListAdapter);
				}
				else
				{
					emptyListView.setVisibility(View.VISIBLE);
					//itemsList.setVisibility(View.GONE);
				}
			}
			
		}
	}
	/**
	 * On Pause implementation added.
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
	    // Save scroll position
	    SharedPreferences preferences = getSharedPreferences("SCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if(itemsList!=null){
		int scroll = itemsList.getFirstVisiblePosition();
		edit.putInt("ScrollValue", scroll);
		View v = itemsList.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		edit.putInt("Top", top);
		edit.commit();
		}
	}
	/**
	 * On resume implementation added.
	 */
	@Override
	protected void onResume() 
	{
		super.onResume();
		SharedPreferences preferences = getSharedPreferences("SCROLL", 0);
	    int scroll = preferences.getInt("ScrollValue", 0);
	    int top = preferences.getInt("Top", 0);
	    if(itemsList!=null)
	    itemsList.setSelectionFromTop(scroll, top); 
	};
	
	/**
	 * Menu functionality added here.
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
	 * Initalizes the Location Manager and attaches the LocationUpdates Listener for the device current Lat Lng values
	 */
	private void initalizeLocationManagerService(SearchVo searchVo)
	{
		/*
		 * GPS Configuration 
		 */
		
		searchVo.setType("A");
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		locationManager.requestLocationUpdates(provider, 10000L, 500.0f, this);
	   
		 searchVo.setNoOfDeals("0");
	     searchVo.setPageIndex("0");
		// Initialize the location fields
		if (!ValidationUtil.isNull(location)) 
		{
			Log.i("GEO LOCATION" , "Provider " + provider + " has been selected.");
			double lat = location.getLatitude();
			double lng = location.getLongitude();
			searchVo.setLocLaong(String.valueOf(lng));
			searchVo.setLocLat(String.valueOf(lat));
			/**
			 * Put all location details into the shared preferences.
			 */
			 SharedPreferences currentLocation = getSharedPreferences("LocationDetails", 0);
			 SharedPreferences.Editor edit = currentLocation.edit();
			 edit.putString("locLat", String.valueOf(lat));
			 edit.putString("locLong", String.valueOf(lng));
			 edit.commit();
			
			Geocoder geocoder = new Geocoder(this, Locale.ENGLISH); 
		    {
		        try {
		            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);

		            if(addresses != null) {
		                Address returnedZip = addresses.get(0);
		                String zipCode =returnedZip.getPostalCode();
		                String address = returnedZip.getAddressLine(0);
		                searchVo.setAddress(address);
		                searchVo.setZipCode(zipCode);
		              
		              }		          
		        } catch (Exception e)
		        {
		          Log.e("Exception:", "Exception occured to find the currentLocation");		           
		        }
		    }
		    SharedPreferences spc1 = getSharedPreferences("Distance", 0);
			String distance = spc1.getString("distancevalue", "");
			/**
			 * Take the default distance if distance is nul or empty
			 */
				if(ValidationUtil.isNullOrEmpty(distance))
				{
					distance = "20";
				}		
			searchVo.setDistance(distance);	   
			
			/**
			 * Get user details from shared preferences.
			 */
			SharedPreferences spc3 = getSharedPreferences("UserDetails", 0);
			String userId = spc3.getString("UserId", "");	
			searchVo.setUserId(userId);
			searchVo.setVersionName(application.getAppVerison());
			application.setSearchVo(searchVo);
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
	
	
	/**
	 * Back button functionality added.	
	 */
	
	@Override
	public void onBackPressed()
	{
		moveTaskToBack(true);
	}
	
	@Override
    public void onDestroy() {       
        super.onDestroy();
        // Stop the tracker when it is no longer needed.
        tracker.stopSession();
    }
}
	
	

