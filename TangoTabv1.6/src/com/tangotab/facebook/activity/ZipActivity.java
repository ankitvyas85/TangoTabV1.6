package com.tangotab.facebook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.Session;
import com.tangotab.R;
import com.tangotab.core.utils.ValidationUtil;

/**
 * Get ZipCode from face book login and update the user ZipCode.
 * 
 * <br> Class :ZipActivity
 * <br> layout:showoffersnear.xml
 * 
 * @author dillip.lenka
 *
 */
public class ZipActivity extends Activity 
{
	private Vibrator myVib;
	
	/**
	 * Execution will start here.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showoffersnear);
		/**
		 * UI widgets
		 */
		Button createNewAccount = (Button) findViewById(R.id.New_Acc);
		final EditText homeZip = (EditText) findViewById(R.id.home_zip);
		final EditText workZip = (EditText) findViewById(R.id.work_zip);
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		
		/**
		 * On click listener for create new Account
		 */
		createNewAccount.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				myVib.vibrate(50);
				Intent resultIntent = new Intent();
				String homeZipCode = homeZip.getText().toString();
				String workZipCode = workZip.getText().toString();
				if (ValidationUtil.isNullOrEmpty(homeZipCode)) {
					homeZipCode = workZipCode;
				}
				resultIntent.putExtra("Zip_Code", homeZipCode);
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});

	}
	/**
	 * Back button functionality added.
	 */
	@Override
	public void onBackPressed() {
		Session session = Session.getActiveSession();
        if (!ValidationUtil.isNull(session) && !session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
		super.onBackPressed();
	}
}
