package com.tangotab.facebook.activity;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.FacebookActivity;
import com.facebook.GraphUser;
import com.facebook.LoginButton;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.session.TangoTabBaseApplication;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.service.FacebookLoginService;
import com.tangotab.facebook.vo.FaceBookVo;
import com.tangotab.login.activity.LogInActivity;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.signUp.activity.SignUpActivity;

/**
 * Class will be used for Face book login authentication.
 * 
 * <br> Class :FacebookLogin
 * <br> layout :facebooklogin.xml
 * 
 * @author lakshmipathi.p
 *
 */
public class FacebookLogin extends FacebookActivity
{
	/**
	 * Meta definitions
	 */
	private LoginButton facebook;
	private Button LoginEmail;
	private Button SignupEmail;	
	private String encriptPwd = null;
	private String encriptUserName = null;
	private String userName =null;
	private String message = null;
	private String firstName = null;
	private String lastName = null;
	private String userId = null;	
	private GraphUser user;	
	Map<String, String> response = new HashMap<String, String>();    
    public TangoTabBaseApplication application;    
    static final int PICK_ZIPCODE = 1;
    static final int PICK_ZIPCODE_UPDATING = 0;    
    private Vibrator myVib;
    private FaceBookVo faceBookVo;
    private GoogleAnalyticsTracker tracker;
    
    
    /**
     * On create method where execution will start.
     */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebooklogin);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.FACEBOOK_LOGIN);
		tracker.trackEvent("FaceBookLogin", "TrackEvent", "facebookLogin", 1);
		
		
		/**
		 * UI widgets
		 */
		facebook=(LoginButton)findViewById(R.id.facebook);
		LoginEmail=(Button)findViewById(R.id.login_email);
		SignupEmail=(Button)findViewById(R.id.signup_email);
		
		application = (TangoTabBaseApplication) getApplication();
		faceBookVo = new FaceBookVo();
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
       	
       	facebook.setApplicationId(AppConstant.APP_ID);
        List<String> permissions = new ArrayList<String>();
        			 permissions.add("email");
        			 permissions.add("user_birthday");
        			 permissions.add("user_checkins");
        			 permissions.add("user_likes");
        			 permissions.add("user_location");
        			 permissions.add("offline_access");
                     facebook.setReadPermissions(permissions);
        /**
         * Face book user info changed call back added             
         */
       	facebook.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback()
       	{
			
			@Override
			public void onUserInfoFetched(GraphUser user) {
				FacebookLogin.this.user = user;
				if(user != null){
					Log.v("response", user.getInnerJSONObject().toString());
					new CheckIfUserPresentElseRegisterAsynTask().execute();
				}
			}
		});
	/**
	 * Login using email on click listener added.
	 */
	LoginEmail.setOnClickListener(new OnClickListener() 
	{
		public void onClick(View v) 
		{
			 myVib.vibrate(50);
			 Intent login=new Intent(getBaseContext(),LogInActivity.class);
			 startActivity(login);
			 finish();
		}
	});
	/**
	 * Sign up with email on click listener.
	 */
	SignupEmail.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v) 
			{
				 myVib.vibrate(50);
				 Intent signup=new Intent(getBaseContext(),SignUpActivity.class);
				 startActivity(signup);
				 finish();
			}
		});
	
	
}
	/**
	 * Check whether the user already present else register the new user.
	 * 
	 * @author lakshmipathi.p
	 *
	 */
	public class CheckIfUserPresentElseRegisterAsynTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute() 
		{
			progressDialog =ProgressDialog.show(FacebookLogin.this, "Please Wait", "Loading...");
			progressDialog.setCancelable(true);
			if(user != null){
				userName = null;
				firstName = null;
				lastName = null;
				String facebookId = null;
				String password = null;
				/**
				 * Validate the fields.
				 */
				if(!ValidationUtil.isNull(user.getProperty("email")))
					userName = user.getProperty("email").toString().trim();
				faceBookVo.setUser_Id(userName);
				if(!ValidationUtil.isNull(user.getProperty("first_name")))
					firstName = user.getProperty("first_name").toString().trim();
				faceBookVo.setFirst_name(firstName);
				if(!ValidationUtil.isNull(user.getProperty("last_name")))
					lastName = user.getProperty("last_name").toString().trim();
				faceBookVo.setLast_name(lastName);
				if(!ValidationUtil.isNull(user.getProperty("id")))
				{
					facebookId = user.getProperty("id").toString().trim();
					password = user.getProperty("id").toString().trim();
					faceBookVo.setFacebookid(facebookId);
					faceBookVo.setPassword(password);
				}
					
				if(!ValidationUtil.isNullOrEmpty(password))
				{
					encriptPwd = Base64.encodeToString(password.getBytes(),Base64.DEFAULT);
					encriptPwd.trim();
					encriptPwd = encriptPwd.substring(0,encriptPwd.length() - 1);
				}
				if(!ValidationUtil.isNullOrEmpty(userName))
				{
					encriptUserName = generateShaCode(userName);
					encriptUserName.trim();
					encriptUserName = encriptUserName.substring(0,encriptUserName.length()-1);
				}
			}
		}
		@Override
		protected Void doInBackground(Void... params)
		{
			if(user != null)
			{
				try{
					FacebookLoginService facebookLoginService = new FacebookLoginService();
					response = facebookLoginService.checkForUser(faceBookVo);
					message = response.get("message");
					userId = response.get("userId");
				}catch(Exception e)
				{
					message = null;
					Log.e("Exception occured", "Exception occured at the time of facebook login",e);
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			try{
				progressDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			
			if(!ValidationUtil.isNull(message) && message.equalsIgnoreCase("Invalid Email ID or Password.")){
				Intent zipIntent = new Intent(FacebookLogin.this, ZipActivity.class);
				startActivityForResult(zipIntent, PICK_ZIPCODE);
			}else if(!ValidationUtil.isNull(message) && message.equalsIgnoreCase("") && ValidationUtil.isNullOrEmpty(response.get("zipCode"))){
				Intent zipIntent = new Intent(FacebookLogin.this, ZipActivity.class);
				startActivityForResult(zipIntent, PICK_ZIPCODE_UPDATING);
			}
			else{
				/**
				 * put credential details in shared preferences.
				 */
				SharedPreferences spc = getSharedPreferences("UserName", 0);
				SharedPreferences.Editor edit = spc.edit();
				edit.putString("username", userName);
				edit.putString("password", encriptPwd);
				edit.putString("enuser", encriptUserName);
				edit.commit();
				/**
				 * put user details in shared preferences.
				 */
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("firstName", firstName);
				edits.putString("lastName", lastName);
				edits.putString("UserId", userId);
				edits.commit();	
				/**
				 * start near me activity
				 */
				Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
				mainIntent.putExtra("selectedId", 0);
				mainIntent.putExtra("frmPage", "faceBook");
				startActivity(mainIntent);
				finish();
			}
		}
		
	}
	/**
	 * AsyncTask call to register the new user through face book login credentials.
	 * 
	 * @author lakshmipathi.p
	 *
	 */
	public class SignupAsyncTask extends AsyncTask<Void, Void, Void> 
	{
		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute()
		{
			progressDialog =ProgressDialog.show(FacebookLogin.this, "Please Wait", "Loding...");
			progressDialog.setCancelable(true);
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			try
			{
				FacebookLoginService facebookLoginService = new FacebookLoginService();
				facebookLoginService.signUpToTangoTab(faceBookVo);		
			}catch(Exception e)
			{
				message = null;
				Log.e("Exception occured", "Exception occured at the time of facebook signup process",e);
			}
			/**
			 * Check for existing user.
			 */
			try
			{
				FacebookLoginService facebookLoginService = new FacebookLoginService();
				response = facebookLoginService.checkForUser(faceBookVo);		
				message = response.get("message");
				userId = response.get("userId");
			}catch(Exception e)
			{
				message = null;
				Log.e("Exception occured", "Exception occured at the time of facebook login",e);
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			try{
				progressDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			if(!ValidationUtil.isNull(message) && !message.equalsIgnoreCase("Invalid Email ID or Password."))
			{
				/**
				 * Put user credentials into shared preferences.
				 */
				SharedPreferences spc = getSharedPreferences("UserName", 0);
				SharedPreferences.Editor edit = spc.edit();
				edit.putString("username", userName);
				edit.putString("password", encriptPwd);
				edit.putString("enuser", encriptUserName);
				edit.commit();
				/**
				 * put user details into shared preferences.
				 */
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("firstName", firstName);
				edits.putString("lastName", lastName);
				edits.putString("UserId", userId);
				edits.commit();				
				/**
				 * start near me activity
				 */
				Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
				mainIntent.putExtra("selectedId", 0);
				startActivity(mainIntent);
				finish();
			}
		}		
	}
	
	/**
	 * Generate shadow code for user id.
	 * 
	 * @param userId
	 * @return
	 */
	private String generateShaCode(String userId) {
		String userName = userId;
		MessageDigest md = null;
		try
		{
			md = MessageDigest.getInstance("SHA-256");
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		StringBuffer hexString =null;
		if(md!=null)
		{
			md.update(userName.getBytes());
 
        byte byteData[] = md.digest();
        if(byteData!=null)
        	{
		        //convert the byte to hex format method 1
		        StringBuffer sb = new StringBuffer();
		        for (int count = 0; count < byteData.length; count++)
		        {
		         sb.append(Integer.toString((byteData[count] & 0xff) + 0x100, 16).substring(1));
		        }
		        //convert the byte to hex format method 2
		        hexString = new StringBuffer();
		    	for (int count=0;count<byteData.length;count++)
		    	{
		    		String hex=Integer.toHexString(0xff & byteData[count]);
		   	     	if(hex.length()==1) hexString.append('0');
		   	     	hexString.append(hex);
		    	}
        	}
		}
    	return hexString.toString();
    }
	/**
	 * Method will start execute once child activity completed and will register the Zip Code. 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		String postalZip=null;
		if(requestCode == PICK_ZIPCODE)
		{
			if (resultCode == RESULT_OK) 
			{
				postalZip = data.getStringExtra("Zip_Code");
				faceBookVo.setZip_code(postalZip);
			}
			/**
			 * AsyncTask call for sign up new user with face book login.
			 */
			new SignupAsyncTask().execute();
		}
		else if(requestCode == PICK_ZIPCODE_UPDATING)
		{
			if (resultCode == RESULT_OK)
			{
				postalZip = data.getStringExtra("Zip_Code");
			}
			faceBookVo.setZip_code(postalZip);
			/**
			 * AsyncTask call for update ZipCode.
			 */
			new UpdateZipCodeAsyncTask().execute();
		}
	}
	/**
	 * AsyncTask call for update ZipCode for the face book login user.
	 * 
	 * @author lakshmipathi.p
	 *
	 */
	public class UpdateZipCodeAsyncTask extends AsyncTask<Void, Void, Void>
	{
		private ProgressDialog progressDialog;
		@Override
		protected void onPreExecute() {
			progressDialog =ProgressDialog.show(FacebookLogin.this, "Please Wait", "Loading...");
			progressDialog.setCancelable(true);
		}		

		@Override
		protected Void doInBackground(Void... params) 
		{
			try{
				FacebookLoginService facebookLoginService = new FacebookLoginService();
				facebookLoginService.updateZipForUser(faceBookVo);	
			}catch(Exception e)
			{
				message = null;
				Log.e("Exception occured", "Exception occured at the time of updating Zip",e);
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result)
		{
			try{
				progressDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			/**
			 * Put user credentials in to shared preferences.
			 */
			SharedPreferences spc = getSharedPreferences("UserName", 0);
			SharedPreferences.Editor edit = spc.edit();
			edit.putString("username", userName);
			edit.putString("password", encriptPwd);
			edit.putString("enuser", encriptUserName);
			edit.commit();
			/**
			 * Put user details into shred preferences.
			 */
			SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
			SharedPreferences.Editor edits = spc1.edit();
			edits.putString("firstName", firstName);
			edits.putString("lastName", lastName);
			edits.putString("UserId", userId);
			edits.commit();	
			/**
			 * Start near me activity
			 */
			Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
			mainIntent.putExtra("selectedId", 0);
			startActivity(mainIntent);
			finish();
		}
		
		
	}
	/**
	 * Back button functionality added here.
	 * 
	 */
	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		moveTaskToBack(true);
	}
}
