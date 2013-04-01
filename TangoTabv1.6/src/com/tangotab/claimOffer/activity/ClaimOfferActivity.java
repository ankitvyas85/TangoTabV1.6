package com.tangotab.claimOffer.activity;


import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.tangotab.claimOffer.service.ClaimOfferService;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.customUrl.service.CustonUrlService;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.map.activity.MapPointActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.myOffers.activity.MyOffersActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
import com.tangotab.settings.activity.SettingsActivity;
/**
 * Class will give offer detail information with a chance to claim the offer.
 * 
 * <br> class :ClaimOfferActivity
 * <br> layout :offerclaim.xml
 * 
 * @author dillip.lenka
 *
 */
public class ClaimOfferActivity extends Activity
{
	/**
	 * Meta definitions
	 */
	
	private TextView dealTime ;
	private TextView businessName;
	private TextView restAddress;
	private TextView cusineType;
	private TextView dealName;
	private TextView dealDescription;
	private TextView dealRestriction;
	private ImageView image ;
	private  Vibrator myVib;
	private DealsDetailVo dealsDetailVo;	
	public TangoTabBaseApplication application;	
	private String message;
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * Execution will start here.
	 */
	@Override
	public void onCreate(Bundle savedInstances) 
	{
		super.onCreate(savedInstances); 		
		setContentView(R.layout.offerclaim);
		
		myVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.CLAIM_OFFER_PAGE);
		tracker.trackEvent("ClaimNow", "TrackEvent", "claimnow", 1);
				
		application = (TangoTabBaseApplication) getApplication();	
		/**
		 * Field details from UI
		 */
		Button map = (Button) findViewById(R.id.map);		
		Button claimNow = (Button) findViewById(R.id.clmwantit);
		
		dealTime = (TextView) findViewById(R.id.dealTime);
		businessName = (TextView) findViewById(R.id.businessName);
		restAddress = (TextView) findViewById(R.id.locationrestaddress);
		cusineType = (TextView) findViewById(R.id.cusinetype);
		dealName = (TextView) findViewById(R.id.dealname);
		dealDescription = (TextView) findViewById(R.id.dealdescription);
		dealRestriction = (TextView) findViewById(R.id.dealrestrictions);
		image = (ImageView) findViewById(R.id.image);
		final String from = getIntent().getStringExtra("from");
		final String fromPage = getIntent().getStringExtra("fromPage");
		
		
		/**
		 * Retrieve the selected deals from near me list
		 */
		dealsDetailVo = (DealsDetailVo) getIntent().getSerializableExtra("selectDeal");
		if(!ValidationUtil.isNull(dealsDetailVo))
		{
			/**
			 * Set all the field informations
			 */
			setData(dealsDetailVo);	
		}
		
	/**
	 * On click listener on claim Now.
	 */
		claimNow.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				myVib.vibrate(50);
				if(checkInternetConnection())
				{
					/**
					 * Get user information from userName shared preferences.
					 */
					SharedPreferences user = getSharedPreferences("UserName", 0);
					String userName = user.getString("username", "");
					dealsDetailVo.setUserId(userName);
					/**
					 * Claim the offers by using the asyncTask.
					 */
					new InsertDealAsyncTask().execute(dealsDetailVo);
					/**
					 * If offer came from customUrl handler
					 */
					if(!ValidationUtil.isNullOrEmpty(fromPage) && fromPage.equalsIgnoreCase("customURL"))
					{
						String spMailingId = application.getSpMailingID();
						String spUserId = application.getSpUserId();
						String spJobId = application.getSpJobId();
						String dealId = getIntent().getStringExtra("dealId");
						/*
						 * Download the image from url and set into the image view
						 */
						if(!ValidationUtil.isNullOrEmpty(spMailingId) && !ValidationUtil.isNullOrEmpty(spUserId) && !ValidationUtil.isNullOrEmpty(spJobId) && !ValidationUtil.isNullOrEmpty(dealId))
						{
							StringBuilder imageUrl  = new StringBuilder();
							imageUrl.append("http://recp.mkt51.net/cot?m=").append(spMailingId).append("&r=").append(spUserId).append("&j").append(spJobId).append("&a=Android").append("&d=").append(dealId).append("&amt=4");
							new DownloadImageTask().execute(imageUrl.toString());
						}
					}
						
				 }else{
			    	 showDialog(10);
				}
				
			}
		
	});
		
		/**
		 * On click listener on map button.
		 */
		
		map.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				myVib.vibrate(50);
			 if(checkInternetConnection())
				{
				 /**
				  * Pass to Map point activity in order to display 
				  */
				 Intent mapIntent = new Intent(getApplicationContext(), MapPointActivity.class);
				 if(!ValidationUtil.isNullOrEmpty(from)){
					 mapIntent.putExtra("from", from);
					}
				 mapIntent.putExtra("businessname", dealsDetailVo.getBusinessName());
				 mapIntent.putExtra("itemAddress", dealsDetailVo.getAddress());
				 startActivity(mapIntent);
				}
			 else
				 showDialog(10);			
			}
		});
		/**
		 * Menu button informations
		 */
		Button nearmeMenuItem = (Button) findViewById(R.id.nearmeMenuButton);
		Button myofferButton = (Button) findViewById(R.id.myoffersMenuButton);
		Button searhMenuButton = (Button) findViewById(R.id.searchMenuButton);
		Button settingsMenuButton = (Button) findViewById(R.id.settingsMenuButton);
		/**
		 * Menu UI Widgets
		 */
		RelativeLayout nearmeRL = (RelativeLayout) findViewById(R.id.nearmeRL);
		RelativeLayout myoffersRL = (RelativeLayout) findViewById(R.id.myoffersRL);
		RelativeLayout searchRL = (RelativeLayout) findViewById(R.id.searchRL);
		RelativeLayout settingsRL = (RelativeLayout) findViewById(R.id.settingsRL);
		
		settingsRL.setBackgroundResource(R.drawable.hover_img);
		nearmeRL.setBackgroundDrawable(null);
		searchRL.setBackgroundDrawable(null);
		myoffersRL.setBackgroundDrawable(null);

		myofferButton.setBackgroundResource(R.drawable.mydeals);
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
		 * On click listener added for nearme menu button
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
		 * On click listener added for my offers menu button
		 */
		
		myoffersRL.setOnClickListener(new OnClickListener() 
				{					
					@Override
					public void onClick(View v)
					{
						myVib.vibrate(50);
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
						myVib.vibrate(50);
						onMenuSelected(2);
					}
				});
		
		/**
		 * On click listener added for Settings menu button
		 */
		
		settingsRL.setOnClickListener(new OnClickListener()
				{				
					@Override
					public void onClick(View v) {
						myVib.vibrate(50);
						onMenuSelected(3);
					}
				});
		
	}
	/**
	 * Inner class for executing service in back ground thread in order to insert deal into the data base.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class InsertDealAsyncTask extends AsyncTask<DealsDetailVo, Void, Void> 
	{
		private ProgressDialog mDialog =null;
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(ClaimOfferActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
		}
		@Override
		protected Void doInBackground(DealsDetailVo... dealsDetailVo)
		{
			try{
				ClaimOfferService claimService  = new ClaimOfferService();
				message = claimService.claimTheOffer(dealsDetailVo[0]);	
				Log.v("Calim service mesage ", message);
			}catch(Exception e)
			{
				message = null;
				Log.e("Exception occured", "Exception occured at the time of login",e);
			}			
			return null;
		}
		@Override
		protected void onPostExecute(Void result)
		{
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			if(!ValidationUtil.isNullOrEmpty(message) && message.equals("You have successfully claimed this offer."))
				{
				List<OffersDetailsVo> offersList =application.getOffersList();
				if(!ValidationUtil.isNullOrEmpty(offersList))
					offersList.clear();
					showDialog(1);					
				}
			else{					
				showDialog(0);
			}
		}	
	}
	/**
	 * Inner class for executing service in back ground thread in order to get deal from deal id and Deal date.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class GetDealAsyncTask extends AsyncTask<CustomDealVo, Void, DealsDetailVo> 
	{
		private ProgressDialog mDialog =null;
		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(ClaimOfferActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
		}
		@Override
		protected DealsDetailVo doInBackground(CustomDealVo... customDealVo)
		{
			try{
				CustonUrlService customService  = new CustonUrlService();
				dealsDetailVo = customService.getCustomDeal(customDealVo[0]);	
				Log.v("Custom deal is ", dealsDetailVo.toString());
			}catch(Exception e)
			{
				message = null;
				Log.e("Exception occured", "Exception occured at the time of login",e);
			}			
			return dealsDetailVo;
		}
		@Override
		protected void onPostExecute(DealsDetailVo dealsDetailVo)
		{
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			if(ValidationUtil.isNull(dealsDetailVo))
			{
				showDialog(2);
			}
			else
			{
				/**
				 * Set all the field informations
				 */
				setData(dealsDetailVo);	
			}
		}	
	}
	/**
	 * Download the image from URL.
	 * 
	 * @author Dillip.Lenka
	 *
	 */
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
	{
		 
		  @Override
		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		    	 InputStream in = new java.net.URL(urldisplay).openStream();
		    	 mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }
		  @Override
		  protected void onPostExecute(Bitmap image)
		  {
			  /**
			   * Image to be set.
			   */
			  //image.setImageBitmap(image);//Pending task
			  
		  }
		  
		}
	/**
	 * Menu information added here.
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
	/**
	 * Set the data into the UI
	 * @param dealsDetailVo
	 */
	private  void setData(DealsDetailVo dealsDetailVo)
	{
		businessName.setText(dealsDetailVo.getBusinessName());
		restAddress.setMaxWidth(400);
		restAddress.setText(dealsDetailVo.address);
		cusineType.setText(dealsDetailVo.getCuisineTypeId());
		ImageLoader imageLoader=new ImageLoader(getApplicationContext());
		imageLoader.DisplayImage(dealsDetailVo.getImageUrl(), image);	
		dealName.setText(dealsDetailVo.getDealName());
		dealDescription.setMaxWidth(180);
		dealDescription.setText(dealsDetailVo.getDealDescription());//.replace('', 'Â'));		
		dealRestriction.setMaxWidth(180);
		dealRestriction.setText(dealsDetailVo.getDealRestriction());
		String restDealStartDate = dealsDetailVo.getDealAvailableStartDate();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String mTimeStamp =null;
		try {
			Date dt = formatter.parse(restDealStartDate);
			SimpleDateFormat formatter2 = new SimpleDateFormat("EEE, MMM dd yyyy");
			mTimeStamp = formatter2.format(dt);
		} catch (Exception e)
		{
			Log.e("Exception occured :", "Exception ocuured at the time parsing the date");
			e.printStackTrace();
		}
        dealTime.setText(mTimeStamp + " "+dealsDetailVo.getStartTime()+" to " + dealsDetailVo.getEndTime());
		
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
	
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab = new AlertDialog.Builder(ClaimOfferActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage(message);
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
		case 1:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(ClaimOfferActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage(message);
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					/**
					 * Back to my offers page.
					 */
					Intent calimedIntent = new Intent(getApplicationContext(),MyOffersActivity.class);
					calimedIntent.putExtra("fromPage", "ClaimActivity");
					startActivity(calimedIntent);
				}
			});
			return ab1.create();
		case 10:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(ClaimOfferActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
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
		}
		return null;
	}
	@Override
    public void onDestroy() {        
        super.onDestroy();
        tracker.stopSession();
    }
}
