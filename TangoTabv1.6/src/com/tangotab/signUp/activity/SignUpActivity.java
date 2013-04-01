package com.tangotab.signUp.activity;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.activity.LogInActivity;
import com.tangotab.signUp.service.SignUpService;
import com.tangotab.signUp.vo.UserVo;
/**
 * Activity class for new user to sign up and get authenticate to login into the Tangotap Application
 * 
 * <br> Class :SignUpActivity
 * <br> Layout :signup.xml
 * 
 * @author dillip.lenka
 *
 */
public class SignUpActivity extends Activity
{
	/*
	 * Meta Definitions
	 */
	ProgressDialog mDialog = null;
	private EditText firstName;
	private EditText lastName;
	private EditText email ;
	private EditText password;
	private EditText confirmpassword;
	private EditText zipCode;
	private EditText promoCode;
	private EditText mobileNumber;
	private CheckBox checkTerms;
	private TextView privacyPolicy;
	private TextView terms;	
	private UserVo userVo;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private boolean isSignup =true;
	
	
	/**
	 * Execution will start from here.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.SIGN_UP_PAGE);
		tracker.trackEvent("SignUp", "TrackEvent", "signUp", 1);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		/**
		 * All the Edit text id from XML
		 */
		firstName = (EditText) findViewById(R.id.fname);
		
		lastName = (EditText) findViewById(R.id.lname);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		confirmpassword = (EditText) findViewById(R.id.password);
		zipCode = (EditText) findViewById(R.id.zipcode);
		promoCode = (EditText) findViewById(R.id.promocode);
		mobileNumber = (EditText) findViewById(R.id.mnumber);
		checkTerms = (CheckBox) findViewById(R.id.checkbox);
		privacyPolicy = (TextView) findViewById(R.id.policy);
		terms = (TextView) findViewById(R.id.terms);
		Button signUp = (Button) findViewById(R.id.signup);
		Button signIn = (Button) findViewById(R.id.signin);
		
		/**
		 * on click Listener for Sign in button
		 */
		signIn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0) 
			{
				myVib.vibrate(50);
				Intent loginIntent = new Intent(SignUpActivity.this, FacebookLogin.class);
				loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(loginIntent);
				finish();
			}
		});
		
		/**
		 * OnTouch Listener for privacy policy TextView color can changed
		 */
		
		privacyPolicy.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					privacyPolicy.setTextColor(Color.GRAY);
				} else 
				{
					privacyPolicy.setTextColor(Color.RED);
				}
				return false;
			}
		});
		
		/**
		 * OnClick Listener for privacy policy TextView
		 */
		
		privacyPolicy.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				myVib.vibrate(50);
				/**
				 * Start privacy policy activity
				 */
				Intent privacyPolicyIntent = new Intent(getApplicationContext(),PrivacyPolicyActivity.class);
				startActivity(privacyPolicyIntent);
			}
		});
		
		/**
		 * OnTouch Listener for terms TextView color can changed
		 * 
		 */
		
		terms.setOnTouchListener(new OnTouchListener() 
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					terms.setTextColor(Color.GRAY);
				} else {
					terms.setTextColor(Color.RED);
				}
				return false;
			}
		});
		
		/**
		 * OnClick Listener for terms TextView
		 */
		
		terms.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				myVib.vibrate(50);
				/**
				 * Start a new activity
				 */
				Intent in = new Intent(getApplicationContext(),TermsOfUseActivity.class);
				startActivity(in);
			}
		});
		
		/**
		 * OnClick Listener for signup Button
		 */
		
		signUp.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				myVib.vibrate(50);
				if(isSignup)
				{
					doSignUp();	
					isSignup =false;
				}

			}
		});
		
	}
	/**
	 * This method will do all Field validation and will call web service for Sign Up
	 * 
	 */
	private void doSignUp()
	{
		Log.v("Method Invokation ", "Invoking doSignUp() method for sign up");
		/**
		 * Validate the text enter by the user
		 */
		if (ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString()) 
				&& ValidationUtil.isNullOrEmpty(email.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(password.getEditableText().toString())
				&& ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()) && ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString()))
			{
				showDialog(0);
			}
		else if (ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()))
			{
				showDialog(1);
			}			
		else if (ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString()))
			{
				showDialog(2);
			}			
		else if (ValidationUtil.isNullOrEmpty(email.getEditableText().toString()))
			{
				showDialog(3);
			}			
		else if (ValidationUtil.isNullOrEmpty(password.getEditableText().toString()))
			{
				showDialog(4);
			}
			
		else if (ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()))
			{
				showDialog(5);
			}		

		else if (ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString()))
			{
				showDialog(7);
			}			
		else if (checkTerms.isChecked() == false)
			{
				showDialog(8);
			}			
		else if (!ValidationUtil.isNullOrEmpty(firstName.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(lastName.getEditableText().toString())
				&& !ValidationUtil.isNullOrEmpty(email.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(password.getEditableText().toString())
				&& !ValidationUtil.isNullOrEmpty(confirmpassword.getEditableText().toString()) && !ValidationUtil.isNullOrEmpty(zipCode.getEditableText().toString())
				&& zipCode.getEditableText().length() <= 7 	&& checkTerms.isChecked()) {
			String emailstring = email.getEditableText().toString();
			if (emailstring.length() != 0) {
				boolean isValidEmail = ValidationUtil.eMailValidation(emailstring);
				if (isValidEmail)
				{
					if (password.getEditableText().length() >= 6) 
					{
						if (password.getEditableText().toString().equals(confirmpassword.getEditableText().toString())) {
							if (checkInternetConnection())
							{
								/**
								 * Get all the sign up informations from the user.
								 */
								String fName = firstName.getEditableText().toString();
								String lName = lastName.getEditableText().toString();
								String newEmail = email.getEditableText().toString();
								String newPass = password.getEditableText().toString();
								String newNumber = mobileNumber.getEditableText().toString();
								String newZip = zipCode.getEditableText().toString();
								String promoText = promoCode.getEditableText().toString();
								userVo = new UserVo(fName,lName,newZip,newNumber,null,newEmail,newPass,promoText);
								/**
								 * Sign up asyntask in order to sign up with new user.
								 */
								new SignupAsyncTask().execute();
								
							}
							else
								showDialog(15);
						}
						else
						{
							showDialog(9);
						}
					} 
					else
						showDialog(5);
				} 
				else
					showDialog(12);			}
		} else if (mobileNumber.getEditableText().length() > 0) {
			if (mobileNumber.getEditableText().length() != 10)
				showDialog(10);
		} else if (zipCode.getEditableText().length() != 6)
			showDialog(11);
	}
	/**
	 * Asynctask for Signup service to be run in background thread.
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class SignupAsyncTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(SignUpActivity.this, "Please Wait", "Fetching Details..");
			mDialog.setCancelable(true);
		}
		
		@Override
		protected String doInBackground(String... params)
		{
			String message =null;
			try{
				SignUpService service = new SignUpService();
				message = service.doSignUp(userVo);
				Log.v("Response message for Sign up", message);
			}catch(Exception e)
			{
				Log.e("Exception occured in sign up service", e.getLocalizedMessage());
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
			
			Log.v("Signup response message ", message);
			if(!ValidationUtil.isNullOrEmpty(message))
			{
				if(message.equals("You have Signed Up Successfully."))
					showDialog(13);
				if(message.equals("Email already exists."))
					showDialog(14);
			}
			else
			{
				isSignup = true;
			}
						
		}

	}
	/**
	 * Various dialog message to be displayed.
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			AlertDialog.Builder ab0 = new AlertDialog.Builder(SignUpActivity.this);
			ab0.setTitle("TangoTab");
			ab0.setMessage("Please provide First Name");
			ab0.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					
				}
			});
			return ab0.create();
		case 1:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(SignUpActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage("Please provide First Name");
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					
				}
			});
			return ab1.create();
		case 2:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(SignUpActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("Please provide Last Name");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab2.create();
		case 3:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(SignUpActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("Please provide Email address");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab3.create();
		case 4:
			AlertDialog.Builder ab4 = new AlertDialog.Builder(SignUpActivity.this);
			ab4.setTitle("TangoTab");
			ab4.setMessage("Please provide password");
			ab4.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab4.create();
		case 5:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(SignUpActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage("Password should be more than 6 characters");
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab5.create();

		case 7:
			AlertDialog.Builder ab7 = new AlertDialog.Builder(SignUpActivity.this);
			ab7.setTitle("TangoTab");
			ab7.setMessage("Please provide Zip/Postal Code");
			ab7.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab7.create();
		case 8:
			AlertDialog.Builder ab8 = new AlertDialog.Builder(SignUpActivity.this);
			ab8.setTitle("TangoTab");
			ab8.setMessage("Please Agree Privacy policy and terms of use");
			ab8.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab8.create();

		case 10:
			AlertDialog.Builder ab10 = new AlertDialog.Builder(SignUpActivity.this);
			ab10.setTitle("TangoTab");
			ab10.setMessage("Please provid valid Mobile Number");
			ab10.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab10.create();
		case 11:
			AlertDialog.Builder ab11 = new AlertDialog.Builder(SignUpActivity.this);
			ab11.setTitle("TangoTab");
			ab11.setMessage("Zip/Post Code Should be max of 7 characters");
			ab11.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab11.create();
		case 12:
			AlertDialog.Builder ab12 = new AlertDialog.Builder(SignUpActivity.this);
			ab12.setTitle("TangoTab");
			ab12.setMessage("Please provide  Valid E-Mail format");
			ab12.setPositiveButton("OK", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab12.create();
		case 13:
			AlertDialog.Builder ab13 = new AlertDialog.Builder(SignUpActivity.this);
			ab13.setTitle("TangoTab");
			ab13.setMessage("You have Signed Up Successfully.");
			ab13.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which) 
				{
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					/**
					 * start a new activity 
					 */
					Intent mainIntent = new Intent(getApplicationContext(),LogInActivity.class);
					startActivity(mainIntent);
					finish();

				}
			});
			return ab13.create();
		case 14:
			AlertDialog.Builder ab14 = new AlertDialog.Builder(SignUpActivity.this);
			ab14.setTitle("TangoTab");
			ab14.setMessage("Email already exists.");
			ab14.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int which)
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
					email.getEditableText().clear();
					password.getEditableText().clear();
					confirmpassword.getEditableText().clear();
					
				}
			});
			return ab14.create();
		case 15:
			AlertDialog.Builder ab15 = new AlertDialog.Builder(SignUpActivity.this);
			ab15.setTitle("TangoTab");
			ab15.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab15.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					isSignup = true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab15.create();
		}
		return mDialog;
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
	 * Back button functionality added.
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Intent homeActivity = new Intent(SignUpActivity.this, FacebookLogin.class);
		homeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeActivity);
		finish();
	}
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		tracker.stopSession();
	}
}
