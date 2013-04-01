package com.tangotab.login.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.service.ForgetPasswordService;
import com.tangotab.login.vo.LoginVo;
/**
 * This class will be used if user forgot his password and also he can retrieve the password 
 * from his given email id.
 * 
 * <br> Class :ForgetPasswordActivity
 * <br> Layout: forgetpassword.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class ForgetPasswordActivity extends Activity
{
	/**
	 * Meta definitions
	 */
	private final int messageId = 0;
	private final int reserComp = 1;
	private final int emailval = 2;	
	private EditText email=null;
	private ProgressDialog mDialog=null;	
	private LoginVo loginVo;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	
	/**
	 * Execution will be start here
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgetpassword);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.FORGOT_PASSWORD_PAGE);
		tracker.trackEvent("Forgetpassword", "TrackEvent", "forgetpassword", 1);
				
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * Button widgets		
		 */
		email = (EditText)findViewById(R.id.email);
		Button send = (Button)findViewById(R.id.send);
		Button cancel =(Button)findViewById(R.id.cancel);
		/**
		 * Cancel button on click listener 
		 */
		cancel.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				Intent homeIntent = new Intent(getApplicationContext(), FacebookLogin.class);
				startActivity(homeIntent);
				myVib.vibrate(50);
				finish();
				
			}
		});
		/**
		 * Send button on Click Listener
		 */
		send.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{	
				myVib.vibrate(50);
				if(!ValidationUtil.isNullOrEmpty(email.getText().toString().trim()))
				{
				if (checkInternetConnection())
				{
				 String emailId = email.getText().toString();
				 
				 loginVo = new LoginVo();
				 loginVo.setUserId(emailId);
				 
				/**
				  * AsyncTask call for forget password web services.
				  */
				 new ForgetPasswordAsyncTask().execute();
				}
				
			}else
				showDialog(emailval);
				}
		});
	}
	
	/**
	 * ForgetPasswordAsyncTask for execute service in different thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
	
	public class ForgetPasswordAsyncTask extends AsyncTask<Void, Void, String> 
	{
		@Override
		protected void onPreExecute()
		{
			mDialog = ProgressDialog.show(ForgetPasswordActivity.this, "Please Wait","Loading...");
			mDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params)
		{
			String message =null;
			try
			{
				ForgetPasswordService service = new ForgetPasswordService();
				message =service.forgetPassword(loginVo);
				Log.v("Forget password response message ", message);
			}
			catch(TangoTabException e)
			{
				Log.e("EXception:", "Exception occures at the time of send password through email.", e);
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
			
			if(!ValidationUtil.isNullOrEmpty(message) && message.equals("true"))
			{
				showDialog(messageId);
			}
			else{					
					showDialog(reserComp);
				}
		}
	}
	/**
	 * Method will be used to display the different dialog messages.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case messageId:
			AlertDialog.Builder ab = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab.setTitle("TangoTab");
			ab.setMessage("Your password has been sent to your Email Id");
			ab.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					email.setText("");
					Intent homeIntent = new Intent(getApplicationContext(), FacebookLogin.class);
					startActivity(homeIntent);
					finish();
				}
			});
			return ab.create();
		case reserComp:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("Please Provide Valid Email Format");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					email.setText("");
				}
			});
			return ab1.create();
			
		case emailval:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage("Please Enter Email Address");
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
			return ab5.create();
		case 15:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(ForgetPasswordActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					email.setText("");
				}
			});
			return ab15.create();
		}
		return null;
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
	protected void onDestroy()
	{
		super.onDestroy();
		tracker.stopSession();
	}
}
