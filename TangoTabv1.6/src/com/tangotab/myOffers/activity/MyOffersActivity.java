package com.tangotab.myOffers.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
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
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.DateFormatUtil;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.map.activity.MyOffersMapingActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.adapter.MyOffersAdapter;
import com.tangotab.myOffers.service.MyOffersService;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Get all claimed offers in the my offers tab from web service.
 * 
 * <br> Class :MyOffersActivity
 * <br> layout :myoffers.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MyOffersActivity extends ListActivity
{
	/*
	 * Meta Definitions
	 */
	private int pageCount=1;
	private LinearLayout llShowMore = null;
	private ListView offerListView =null;
	private Button map = null;
	private EditText editSearch;
	private TextView emptyList;
	private List<OffersDetailsVo> finalOfferList = new ArrayList<OffersDetailsVo>();
	private List<OffersDetailsVo> listForFiltering = new ArrayList<OffersDetailsVo>();
	private List<OffersDetailsVo> myOffersList=new ArrayList<OffersDetailsVo>();
	public TangoTabBaseApplication application;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private boolean isMyoffersPage = false;
	private String fromPage = "";
	/**
	 * Execution will be start here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myoffers);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);	
		
		/*
		 * Google tracker added.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.MYOFFER_PAGE);
		tracker.trackEvent("MyOffers", "TrackEvent", "myOffers", 1);
		
        
		/**
		 * Google analytics implementation		
		 *//*
		tracker = GoogleAnalyticsConnector.getConnector(this);
		tracker.startNewSession(AppConstant.GA_REG_KEY, 10, this);
		tracker.trackPageView(AppConstant.MY_OFFERS);
		tracker.dispatch();
		tracker.setDebug(true);
		tracker.stopSession();*/
		
		application = (TangoTabBaseApplication) getApplication();		
		/**
		 * 	get UI Widgets	
		 */
		emptyList = (TextView) findViewById(R.id.emptylist);
		offerListView = (ListView) findViewById(android.R.id.list);
		llShowMore = (LinearLayout) getLayoutInflater().inflate(R.layout.showmorecell, null);
		map = (Button) findViewById(R.id.map);
		editSearch = (EditText) findViewById(R.id.edittextsearch);
		final Button restSearch =(Button)findViewById(R.id.restserch);
		if(!ValidationUtil.isNullOrEmpty(getIntent().getStringExtra("fromPage")))
		{
			fromPage=getIntent().getExtras().getString("fromPage");
		}
		
		
		/**
		 * get Offers list from session and display
		 */
		List<OffersDetailsVo> offersList =  application.getOffersList();
		if(!ValidationUtil.isNullOrEmpty(offersList) && !isMyoffersPage && !fromPage.equalsIgnoreCase("ClaimActivity"))
		{
			pageCount = application.getMyOfferPageCount();
			emptyList.setVisibility(View.GONE);
			offerListView.setVisibility(View.VISIBLE);
			Log.v("offersList is", String.valueOf(offersList.size()));
			MyOffersAdapter  myOffersAdapter = new MyOffersAdapter(MyOffersActivity.this,offersList,llShowMore);
			offerListView = getListView();
			offerListView.setCacheColorHint(Color.TRANSPARENT);
			offerListView.removeFooterView(llShowMore);
			offerListView.addFooterView(llShowMore);				
			setListAdapter(myOffersAdapter);
			
		}
		else
		{
			/**
			 * Get first 10 offers.
			 */
			getOfferList(pageCount);	
		}
		/**
		 * Menu information from UI
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
		
		myoffersRL.setBackgroundResource(R.drawable.hover_img);
		nearmeRL.setBackgroundDrawable(null);
		searchRL.setBackgroundDrawable(null);
		settingsRL.setBackgroundDrawable(null);
		/**
		 * On click listener for near me button.
		 */
		nearmeRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v)
					{
						myVib.vibrate(50);
						isMyoffersPage=true;
						onMenuSelected(1);
					}
				});
		/**
		 * On click listener for my offers button.
		 */
		myoffersRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v)
					{
						myVib.vibrate(50);
						if(isMyoffersPage)
							onMenuSelected(0);
					}
				});
		/**
		 * On click listener for Search button.
		 */
		searchRL.setOnClickListener(new OnClickListener()
				{					
					@Override
					public void onClick(View v) 
					{
						myVib.vibrate(50);
						isMyoffersPage =true;
						onMenuSelected(2);
					}
				});
		
		/**
		 * On click listener for Settings button.
		 */
		settingsRL.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						myVib.vibrate(50);
						isMyoffersPage =true;
						onMenuSelected(3);
					}
				});
		
		/**
		 * On click listener for show more button.
		 */		
		llShowMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{

				myVib.vibrate(50);
				pageCount++;
				getOfferList(pageCount);	
			}
		});
		
		/**
		 * Map button on click listener added.	
		 */
		map.setOnClickListener(new OnClickListener() {
			public void onClick(final View v)
			{
				myVib.vibrate(50);				
				new Handler().postDelayed(new Runnable() {
					public void run() {
						v.setClickable(true);
					}
				}, 600);

				if (checkInternetConnection()) {
					myOffersList = null;
					if(!ValidationUtil.isNullOrEmpty(listForFiltering)){
						myOffersList = listForFiltering;
					}else{
						myOffersList = application.getOffersList();
					}
			
					if (ValidationUtil.isNullOrEmpty(myOffersList))
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
										Bundle bundle = new Bundle(); 
										/**
										 * Start map activity in order to display offers into the map
										 */
                                        Intent intent = new Intent(getApplicationContext(),	MyOffersMapingActivity.class);
                                        bundle.putParcelableArrayList("offerList", (ArrayList<? extends Parcelable>) myOffersList);
                                        intent.putExtras(bundle);
										startActivity(intent);
									}
								});
							}
						}).start();
						
					}
				} else
					showDialog(10);
			}

		});
		
		/**
		 * Rest search on click listener added.
		 */
		restSearch.setOnClickListener(new OnClickListener()
			{			
				@Override
				public void onClick(View v)
				{
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(restSearch.getWindowToken(), 0);
				}
			});
		
		/**
		 * Search text change listener.
		 */
		editSearch.addTextChangedListener(new TextWatcher()
			{

			public void afterTextChanged(Editable s) {
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			}

			public void beforeTextChanged(CharSequence s, int start, int count,int after)
			{
			}

			public void onTextChanged(CharSequence s, int start, int before,int count)
			{
				
				int textlength = editSearch.getText().length();
				if(!ValidationUtil.isNullOrEmpty(listForFiltering))
					listForFiltering.clear();
				for (int i = 0; i < application.getOffersList().size(); i++) {
					if (textlength <= application.getOffersList().get(i).getBusinessName().length()) 
					{
						String editSearchText  = editSearch.getEditableText().toString();
						String businessName = application.getOffersList().get(i).getBusinessName();
						if (businessName.toLowerCase().contains(editSearchText.toLowerCase()))
						{
							listForFiltering.add(application.getOffersList().get(i));
						}
					}
				}
				if (listForFiltering.isEmpty())
				{
					emptyList.setVisibility(0);
					if (textlength <= 0) {
						
						emptyList.setText("You have not selected any offer. "
										+ "Please search for a offer and reserve it.");
					} else
						emptyList.setText("Sorry, no offers match the search criteria "
										+ "or you have no offers reserved yet.");
				} else
					emptyList.setVisibility(8);
				
				if(!ValidationUtil.isNullOrEmpty(listForFiltering))
				{
					MyOffersAdapter myOffersAdapter = new MyOffersAdapter(MyOffersActivity.this,listForFiltering,llShowMore);
					offerListView.setAdapter(myOffersAdapter);
				}
			}
		});
	
		/**
		 * Edit listener for search offers
		 */	
		editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
		    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
		    {		    
		        if (actionId == EditorInfo.IME_ACTION_SEARCH)
		        {
		        	   	InputMethodManager imm = (InputMethodManager)getSystemService(
						      Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);		
		        }
		        return false;
		    }
		});
	}
	/**
	 * Get list of offers for given page.
	 * 
	 * @param pageCount
	 */
	private void getOfferList(int pageCount)
	{
		SharedPreferences spc = getSharedPreferences("UserName", 0);
		String userId = spc.getString("username", "");
		String password =spc.getString("password", "");
		LoginVo loginvo = new LoginVo();
		loginvo.setUserId(userId);
		loginvo.setPassword(password);
		application.setMyOfferPageCount(pageCount);
		/**
		 * Aynctask call for get list of offers from web service.
		 */
		new MyOffersListAsyncTask().execute(loginvo);
	}
	/**
	 * AsyncTask call to retrieve all the offers from service using different thread
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	public class MyOffersListAsyncTask extends AsyncTask<LoginVo, Void, List<OffersDetailsVo>>
	{
		private ProgressDialog mDialog=null;
		@Override
		protected void onPreExecute()
		{
			mDialog = ProgressDialog.show(MyOffersActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
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
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			
			if(!ValidationUtil.isNullOrEmpty(offersList))
			{
				//Set the alaram for local notifications for offers
				sendLocalNotification(offersList);
				/**
				 * get Previous list of Offers
				 */
				List<OffersDetailsVo> myOfferList =  application.getOffersList();
				if(!ValidationUtil.isNullOrEmpty(myOfferList) && finalOfferList.size()==0 )
				{
					if(!ValidationUtil.isNullOrEmpty(offersList))
						myOfferList.addAll(offersList);
					finalOfferList.addAll(myOfferList);
				}
				else 
				{
					finalOfferList.addAll(offersList);
				}
				application.setOffersList(finalOfferList);
				MyOffersAdapter  myOffersAdapter =new MyOffersAdapter(MyOffersActivity.this,finalOfferList,llShowMore);
				/**
				 * cursor to be point into next 10 records.
				 */
				if(!ValidationUtil.isNullOrEmpty(finalOfferList) && finalOfferList.size()>10)
				{
					getListView().postDelayed(new Runnable() {          
					    @Override
					    public void run() {
					    	offerListView.setSelection((pageCount-1)*10);
					    }
					},100L);
					
				}
				emptyList.setVisibility(View.GONE);
				offerListView.setCacheColorHint(Color.TRANSPARENT);
				offerListView.removeFooterView(llShowMore);
				offerListView.addFooterView(llShowMore);
				setListAdapter(myOffersAdapter);	
						
			}
			else
			{
				emptyList.setVisibility(View.VISIBLE);
			}
			
		}		
	}
	/**
	 * OnPause functionality added .
	 * 
	 */
	@Override
	protected void onPause()
	{
		super.onPause();
	   	/**
		 * Save scroll position into shared preferences.
		 */
	    SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
		SharedPreferences.Editor edit = preferences.edit();
		if(offerListView!=null){
		int scroll = offerListView.getFirstVisiblePosition();
		edit.putInt("scrollValue", scroll);
		View v = offerListView.getChildAt(0);
		int top = (v == null) ? 0 : v.getTop();
		edit.putInt("Top", top);
		edit.commit();
		}
	}

	@Override	
	protected void onResume() 
	{
		super.onResume();	
		SharedPreferences preferences = getSharedPreferences("OFFERSCROLL", 0);
	    int scroll = preferences.getInt("scrollValue", 0);
	    int top = preferences.getInt("Top", 0);
	    if(offerListView!=null)
	    	offerListView.setSelectionFromTop(scroll, top); 
	};
	/**
	 * Menu selected item
	 */
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
	/**
	 * Send local notification for List of Offers
	 * 
	 * @param offersDetailsList
	 */
	private void sendLocalNotification(List<OffersDetailsVo> offersDetailsList)
	{	
		if(ValidationUtil.isNullOrEmpty(offersDetailsList))
			return;
		Log.v("Invoking is ", "consumerdealsList = "+offersDetailsList.size());
		for(OffersDetailsVo offersDetailsVo:offersDetailsList)
		{
			 /**
		      * First remove the local notification for the offer
		      */
		     removeAlarmForNotification(offersDetailsVo);
		}
		for(OffersDetailsVo offersDetailsVo:offersDetailsList)
		{	
			int isCheckin =0;
			if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getIsConsumerShownUp()))
			{
					isCheckin =Integer.parseInt(offersDetailsVo.getIsConsumerShownUp());
			}
			Log.v("MyOffers","isCheckin = " + isCheckin);
			/**
			 * If offers neither manually or nor auto check in
			 */
			if(isCheckin ==0)
			{
				String reserveDate = offersDetailsVo.getReserveTimeStamp();
				StringBuilder finalClaimDate = new StringBuilder();
				if(!ValidationUtil.isNullOrEmpty(reserveDate))
				{
					int index = reserveDate.indexOf(" ");	
					String claimDate = offersDetailsVo.getEndTime();
					Log.v("ClaimDate is ", claimDate);
					finalClaimDate.append(reserveDate.substring(0,index).trim()).append(" ").append(claimDate);
					Log.v("finalClaimDate is ", finalClaimDate.toString());
				}
							
				Date finalEndTime = DateFormatUtil.parseIntoDifferentFormat(finalClaimDate.toString(),"yyyy-MM-dd hh:mm aa");
				Log.v("finalEndTime is ", finalEndTime.toString());
				String currentDate = offersDetailsVo.getCurrentDate();
				Date current = DateFormatUtil.parseIntoDifferentFormat(currentDate,"yyyy-MM-dd HH:mm:ss.SSSSSS");
				
				boolean isExpiredOffer =(current.after(finalEndTime));		
			    
			     /**
			      * Set the Local notification for the offer if expired.
			      */
			     
			    	 Calendar alarmCal =  Calendar.getInstance();
			    	 alarmCal.setTime(finalEndTime);		    	 
			    	 
			    	 Long reserveTime = null;
			    	 long diffInMilisecond =Math.abs(alarmCal.getTimeInMillis()- System.currentTimeMillis());
			    	 if(alarmCal.getTimeInMillis()>System.currentTimeMillis())
			    	 {
			    		 reserveTime = diffInMilisecond+(60*60*1000);			    		 
			    	 }
			    	 else
			    	 {
			    		 reserveTime = (60*60*1000)-diffInMilisecond;
			    	 }			   
			    	 
			    	 
			    	setAlaramForNotification(reserveTime, offersDetailsVo);
			     
			}
			else
			{
				Log.v("Don't send local notification already checked in",String.valueOf(isCheckin));
			}
		}
	}

     


	/**
	 * Set Alarm for local notification for individual offers.
	 * 
	 * @param alramTime
	 * @param offersDetailsVo
	 */
	private void setAlaramForNotification(long alramTime ,OffersDetailsVo offersDetailsVo)
	{
		Log.v("Invoking setAlaramForNotification() method ", "alramTime ="+alramTime+" businessName ="+offersDetailsVo.getBusinessName()+" claimDate = "+offersDetailsVo.getReserveTimeStamp());
		int dealId =0;
		if(!ValidationUtil.isNullOrEmpty(offersDetailsVo.getDealId()))
			dealId = Integer.parseInt(offersDetailsVo.getDealId());
			Bundle b = new Bundle();
	        b.putParcelable("selectOffers", offersDetailsVo);
	        Intent alarmIntent = new Intent(AppConstant.ALARM_ACTION_NAME);		
	        alarmIntent.putExtras(b);
	        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), dealId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE); 
	        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+alramTime, pendingIntent);  
		    
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
	 * Display the Dialog message
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(MyOffersActivity.this);
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
		}
		return null;
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
	protected void onDestroy() 
	{
		super.onDestroy();
		tracker.stopSession();
	}
}
