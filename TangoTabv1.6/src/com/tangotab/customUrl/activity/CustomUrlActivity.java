package com.tangotab.customUrl.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.customUrl.service.CustonUrlService;
import com.tangotab.customUrl.vo.CustomDealVo;
import com.tangotab.login.activity.SplashScreenActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.activity.SearchActivity;
/**
 * This class will handle the URL and parameter and open the application with respective activity.
 * 
 * <br> Class :CustomUrlActivity
 * <br> layout:null
 * 
 * @author Dillip.Lenka
 *
 */
public class CustomUrlActivity extends Activity
{
	/**
	 * Meta definitions
	 */
	private String dealId  =null;
	public TangoTabBaseApplication application;
	
	/**
	 * Execution will start here
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{		
		super.onCreate(savedInstanceState);
		
		application = (TangoTabBaseApplication) getApplication();
		/**
		 * Get the URL and parameter and start the respective activity.
		 */
		findTheURl();
		
		
	}
	/**
	 * Find the URL and parameter from intent filter and start the respective activity.
	 * 
	 */
	private void findTheURl()
	{
		Intent intent = getIntent();
		if(intent!=null)
		{
			/**
			 * Get the full URL from intent.
			 */
			Uri myURI=intent.getData();
			if(myURI!=null)
			{
				String uri = String.valueOf(myURI);
				String param =null;
				int partIndex = uri.indexOf("//");
				if(partIndex!=-1)
				{
					String uriPart =uri.substring(partIndex+2);
					int index = uriPart.indexOf("/");
					if(index!=-1)
					{						
						param =uriPart.substring(index+1);
						/**
						 * Select activity with parameter.
						 */
						selectActvity(uriPart.substring(0,index),param);
					}
					else
					{
						/**
						 * Select activity without parameter
						 */
						selectActvity(uriPart,param);	
					}
					
				}
								
			}
		
		}
	}
	/**
	 * This method will select the activity on the basis of URL.
	 * 
	 * @param uriPath
	 */
	private void selectActvity(String uriPath,String parameter)
	{
		Log.v("Invoking methods for selectActvity() with parameter", "uriPath = "+uriPath+" parameter ="+parameter);
		/**
		 * Check authentication first if user not authenticate ask for authentication.
		 */
		SharedPreferences user = getSharedPreferences("UserName", 0);
		String userName= user.getString("username","");
		String password = user.getString("password","");
		if(ValidationUtil.isNullOrEmpty(userName) || ValidationUtil.isNullOrEmpty(password))
		{
			/**
			 * Start the splashScreenActivity
			 */
			Intent splashIntent = new Intent(getApplicationContext(), SplashScreenActivity.class);
			startActivity(splashIntent);
		}
		else{
			/**
			 * Start near me activity with no parameter.
			 */
			if(uriPath.equalsIgnoreCase("nearMe"))
			{
				Intent nearMeIntent = new Intent(getApplicationContext(), NearMeActivity.class);
				startActivity(nearMeIntent);
			}
			if(uriPath.equalsIgnoreCase("search"))
			{
				String city =null;
				application.getSearchList().clear();
				int index = parameter.indexOf("/");
				if(index!=-1)
				{
					String[] param = parameter.split("/");
					city =param[0];
					/**
					 * Set the spMailingid,spUserId and spJobId into session.
					 */
					try{
						application.setSpMailingID(param[2]);
						application.setSpUserId(param[3]);
						application.setSpJobId(param[4]);
						Log.v("spMailingId ,spUserdId ,spJobId from dealSilver pop link", "spMailingId ="+param[2]+" spUserdId="+param[3]+" spJobId="+param[4]);
						}catch(ArrayIndexOutOfBoundsException e)
						{
							Log.v("Exception:", "Exception occured without passing parameter spMailingId.");
						}
				}
				else
				{
					city =parameter;
				}
				Log.v("parameter for search from custom url ", "City name  ="+city);
				/**
				 * Start the search activity with city as parameter.
				 */
				Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
				searchIntent.putExtra("fromPage", "customURL");
				searchIntent.putExtra("address", city);
				startActivity(searchIntent);
			}
			/**
			 * Start claim offer activity from here.
			 */
			if(uriPath.equalsIgnoreCase("deal"))
			{
				
				String date =null;
						
				if(!ValidationUtil.isNullOrEmpty(parameter))
				{
					String param[] =parameter.split("/");
					dealId =param[0];
					date = param[1];
					Log.v("Deal id and Deal deal date from URL is ", "dealId ="+dealId+" Deal Date "+date);
					/**
					 * Set the spMailingid,spUserId and spJobId into session.
					 */
					try{
					application.setSpMailingID(param[2]);
					application.setSpUserId(param[3]);
					application.setSpJobId(param[4]);
					Log.v("spMailingId ,spUserdId ,spJobId from dealSilver pop link", "spMailingId ="+param[2]+" spUserdId="+param[3]+" spJobId="+param[4]);
					}catch(ArrayIndexOutOfBoundsException e)
					{
						Log.v("Exception:", "Exception occured without passing parameter spMailingId.");
					}

				}
							
				/**
				 * Get location lat and long from the shared preferences.
				 */
				 SharedPreferences location = getSharedPreferences("LocationDetails", 0);
				 String locLat = location.getString("locLat", "");
				 String locLong = location.getString("locLong", "");
				 /**
				  * Create new CustomDealVo object
				  */
				 CustomDealVo customDealVo = new CustomDealVo();
				 customDealVo.setDealId(dealId);
				 customDealVo.setDealDate(date);
				 customDealVo.setLocLat(locLat);
				 customDealVo.setLocLong(locLong);	
				 /**
				  * Get deal from deal id and Date.
				  */
				 
				 new GetDealAsyncTask().execute(customDealVo);
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
			mDialog = ProgressDialog.show(CustomUrlActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
		}
		@Override
		protected DealsDetailVo doInBackground(CustomDealVo... customDealVo)
		{
			DealsDetailVo dealsDetailVo =null;
			try{
				CustonUrlService customService  = new CustonUrlService();
				dealsDetailVo = customService.getCustomDeal(customDealVo[0]);	
			}catch(Exception e)
			{
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
			/**
			 * Start search activity
			 */
			Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
			searchIntent.putExtra("fromPage", "customURL");
			searchIntent.putExtra("dealId", dealId);
			searchIntent.putExtra("selectDeal",  dealsDetailVo);
			startActivity(searchIntent);

				
		}	
	}
	
}
