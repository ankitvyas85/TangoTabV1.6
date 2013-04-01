package com.tangotab.login.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.tangotab.R;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.utils.CreateSqliteHelper;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.facebook.activity.FacebookLogin;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.service.LoginService;
import com.tangotab.login.vo.LoginVo;
import com.tangotab.nearMe.activity.NearMeActivity;
import com.tangotab.signUp.activity.SignUpActivity;
import com.tangotab.signUp.vo.UserVo;

/**
 * Class will be used for Authenticate the user to enter into TangoTab Application.
 * 
 * <br> class :LogInActivity
 * <br> Layout: login.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class LogInActivity extends Activity 
{
	/**
	 * Meta Definitions
	 */
	private final int Invalid_User = 0;
	private final int I_User = 1;
	private final int Dialog_id = 2;
	private final int Inactive_User = 3;
	private ProgressDialog mDialog=null;	
	private EditText username;
	private EditText password;
	private Button signIn;
	private LoginVo loginVo;	
	private String message;
	private List<UserVo> userVoList =null;
	private Vibrator myVib;
	private GoogleAnalyticsTracker tracker;
	private boolean isLogin =true;
	
	/**
	 * Execution starting point
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * Google analytics implementation added here.
		 */
		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession(AppConstant.GA_REG_KEY,10, this);
		tracker.setDebug(true);
		tracker.trackPageView(AppConstant.LOGIN_PAGE);
		tracker.trackEvent("SignIn", "TrackEvent", "signIn", 1);
		
		/**
		 * Get login informations from the data base.
		 */
		doLoginFromDataBase();
		
		setContentView(R.layout.login);
		
		/**
		 * Collect all the UI Widgets
		 */
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		signIn = (Button) findViewById(R.id.signIn);
		Button signUp = (Button)findViewById(R.id.signup);
		TextView forgetpasswrd =(TextView)findViewById(R.id.forgetpassword);
		
		
		
		/**
		 * Sign up button on click handler.
		 */
		signUp.setOnClickListener(new OnClickListener()
		{
				public void onClick(View v)
				{
					myVib.vibrate(50);
					Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
					startActivity(signUpIntent);
					finish();
				}
			});		
		/**
		 * signIn button on click handler.
		 */
		signIn.setOnClickListener(new OnClickListener()
		{
				public void onClick(View v)
				{
					myVib.vibrate(50);
					if(isLogin)
					{
						doSignIn();
						isLogin =false;
					}
				}
			});
		
		/**
		 * forget password button on click handler.
		 */
		forgetpasswrd.setOnClickListener(new OnClickListener()
		{
			
			public void onClick(View arg0)
			{
				myVib.vibrate(50);
				Intent forgetPassIntent = new Intent(LogInActivity.this,ForgetPasswordActivity.class);
				startActivity(forgetPassIntent);
				finish();
			}
		});	
		
		/**
		 * password button on Edit listener added.
		 */
		password.setOnEditorActionListener(new EditText.OnEditorActionListener()
		{

			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_DONE){
					doSignIn();
				return true;
				}
				return false;
			}
			
		});
	}
	
	/**
	 * User validation asyncTask used for to get authenticate for new user..
	 * 
	 * @author dillip.lenka
	 *
	 */
	public class UserValidationAsyncTask extends AsyncTask<Void, Void, String>
	{

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(LogInActivity.this, "Please Wait", "Loading...");
			mDialog.setCancelable(true);
		}
		@Override
		protected String doInBackground(Void... params)
		{
			try{
				LoginService loginService = new LoginService();
				message = loginService.doSignIn(loginVo);
				if(ValidationUtil.isNullOrEmpty(message))
				{				
					userVoList = LoginDao.userVoList;				
				}
			}catch(Exception e)
			{
				Log.e("Exception occured", "Exception occured at the time of login",e);
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
					
			if (!ValidationUtil.isNullOrEmpty(userVoList) && ValidationUtil.isNullOrEmpty(message)) 
			{
				SharedPreferences spfc = getSharedPreferences("sharelogout", 0);
				SharedPreferences.Editor sEditor = spfc.edit();
				sEditor.putBoolean("sharelogout_value", true);
				sEditor.commit();

				SharedPreferences spc = getSharedPreferences("UserName", 0);
				SharedPreferences.Editor edit = spc.edit();
				edit.putString("username", loginVo.getUserId());
				edit.putString("password", loginVo.getPassword());
				edit.commit();
				
				UserVo userVo = userVoList.get(0);
				/**
				 * put all the user information into the shared preferences		
				 */
				SharedPreferences spc1 = getSharedPreferences("UserDetails", 0);
				SharedPreferences.Editor edits = spc1.edit();
				edits.putString("firstName", userVo.getFirst_name());
				edits.putString("lastName", userVo.getLast_name());
				edits.putString("UserId", userVo.getUser_Id());
				edits.commit();				
				/**
				 * Put credentials in to data base
				 */
				CreateSqliteHelper csh = new CreateSqliteHelper(getApplicationContext());
				SQLiteDatabase db = csh.getWritableDatabase();	
				ContentValues cv = new ContentValues();
				cv.put("ID", 1);
				cv.put("OPEN", "ON");
				db.insert("LOGIN", null, cv);
				db.close();
				/**
				 * Start near me activity
				 */
				Intent mainIntent = new Intent(getApplicationContext(),NearMeActivity.class);
				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(mainIntent);
				finish();
			} 
			else {
				isLogin =true;
				if(!ValidationUtil.isNullOrEmpty(message) && message.equalsIgnoreCase("Your account is inactive, Please contact TangoTab Administrator."))
				{
					showDialog(Inactive_User);
				} 
				else
				{
					showDialog(Invalid_User);	
				}
			}
			
		}

		
	}
	
	private void doSignIn()
	{
		if(!ValidationUtil.isNullOrEmpty(username.getText().toString()) && !ValidationUtil.isNullOrEmpty(password.getText().toString()))
		{
			if(checkInternetConnection())
			{
				executeLogin();			
				
			}
			else{
				
				showDialog(Dialog_id);
			}
		}
		else
		{
			
			showDialog(I_User);
		}
		
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case Invalid_User:
			AlertDialog.Builder ab1 = new AlertDialog.Builder(LogInActivity.this);
			ab1.setTitle("TangoTab");
			ab1.setMessage(message);
			ab1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					isLogin =true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab1.create();
		case I_User:
			AlertDialog.Builder ab2 = new AlertDialog.Builder(LogInActivity.this);
			ab2.setTitle("TangoTab");
			ab2.setMessage("Please provide login credentials");
			ab2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) 
				{
					isLogin =true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab2.create();
		case Dialog_id:
			AlertDialog.Builder ab3 = new AlertDialog.Builder(LogInActivity.this);
			ab3.setTitle("TangoTab");
			ab3.setMessage("We are unable to make an internet connection at this time.Some functionalities will be limited until a connection is made.");
			ab3.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					isLogin =true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}
				}
			});
			return ab3.create();
		case Inactive_User:
			AlertDialog.Builder ab5 = new AlertDialog.Builder(LogInActivity.this);
			ab5.setTitle("TangoTab");
			ab5.setMessage(message);
			ab5.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which)
				{
					isLogin =true;
					try{
						dialog.dismiss();
					}catch(Exception e)
					{
						Log.e("EXception:", "Exception occured before dismiss dilog.");
					}					
					Intent loginIntent = new Intent(LogInActivity.this,LogInActivity.class);
					startActivity(loginIntent);
				}
			});
			return ab5.create();
		}
		return mDialog;
		
	}
	
	/**
	 * Get the user and password from the user and call the service to execute.
	 * 
	 */
	private void executeLogin()
	{
		InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(signIn.getWindowToken(), 0);
		String newUser = username.getText().toString();
		String newPass = password.getText().toString().trim();
		
		//password encoding
		String encodedpass = Base64.encodeToString(newPass.getBytes(),Base64.DEFAULT);
		encodedpass.trim();			
		String enpass = encodedpass.substring(0,encodedpass.length() - 1);
		
		loginVo = new LoginVo();
		loginVo.setUserId(newUser);
		loginVo.setPassword(enpass);				
		new UserValidationAsyncTask().execute();
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
	 * Connect to SQLlite database and retrieve the data from login table.
	 * 
	 */
	private void doLoginFromDataBase()
	{
		/**
		 * Get the user credentials from data base.
		 */
		CreateSqliteHelper sqliteHelper = new CreateSqliteHelper(getApplicationContext());
		SQLiteDatabase dataBase = sqliteHelper.getReadableDatabase();
		Cursor cursor = dataBase.rawQuery("Select * from LOGIN", null);
		int rowCount = cursor.getCount();
		cursor.close();
		dataBase.close();		
		if(rowCount>0)
		{
			/**
			 * Check the Internet connection
			 */
			if (checkInternetConnection())
			{	
				String paswd1=null;
				String usrnm1 = null;
				SharedPreferences valid_spc = getSharedPreferences("UserName", 0);
				String usrnm = valid_spc.getString("username", usrnm1);
				String paswd = valid_spc.getString("password", paswd1);
				/**
				 * Create new LoginVo object and pass the user name and password
				 */
				loginVo = new LoginVo();
				loginVo.setUserId(usrnm);
				loginVo.setPassword(paswd);
				/**
				 * Service call for user validation		
				 */
				new UserValidationAsyncTask().execute();
			}
		}
	}
	/**
	 * Back button functionality
	 */
	@Override
	public void onBackPressed() {
		Intent homeActivity = new Intent(LogInActivity.this, FacebookLogin.class);
		homeActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(homeActivity);
		super.onBackPressed();
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		tracker.stopSession();
		if(mDialog != null)
		{
			try{
				mDialog.dismiss();
			}catch(Exception e)
			{
				Log.e("EXception:", "Exception occured before dismiss dilog.");
			}
			
		}
	}

}