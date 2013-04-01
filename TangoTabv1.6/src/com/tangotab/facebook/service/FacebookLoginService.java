package com.tangotab.facebook.service;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.facebook.dao.FacebookLoginDao;
import com.tangotab.facebook.vo.FaceBookVo;
/**
 * Service class to logging into face book and get authentication for login into Tangotab application.
 * 
 * @author Dillip.Lenka
 *
 */
public class FacebookLoginService
{
	
	/**
	 * method will check whether the user already exists or not.
	 * 
	 * @param userName
	 * @param facebookId
	 * @return
	 * @throws TangoTabException
	 */
	
	public Map<String, String> checkForUser(FaceBookVo faceBookVo) throws TangoTabException
	{
		Log.v("Invoking the Method checkForUser() with faceBookVo :",faceBookVo.toString());
		Map<String, String> response = new HashMap<String, String>();
		try
		{
			FacebookLoginDao facebookLoginDao = new FacebookLoginDao();
			response = facebookLoginDao.checkForUser(faceBookVo);
		} 
		catch (TangoTabException e)
		{
			Log.e("Exception ", "Exception occured at checkForUser() method",e);
			throw new TangoTabException("FacebookLoginService", "checkForUser", e);
		}
		return response;
	}
	
	/**
	 * Method will allow to sign up into tangotab application by using face book login credentials.
	 * 
	 * @param faceBookVo
	 * @throws TangoTabException
	 */
	public void signUpToTangoTab(FaceBookVo faceBookVo) throws TangoTabException
	{
		Log.v("signUpToTangoTab","Invoking the Method signUpToTangoTab()");
		String message = null;
		try 
		{
			FacebookLoginDao facebookLoginDao = new FacebookLoginDao();
			facebookLoginDao.signUpToTangoTab(faceBookVo);
		} 
		catch (TangoTabException e) {
			Log.e("Exception ", "Exception occured at signUpToTangoTab() method",e);
			throw new TangoTabException("FacebookLoginService", "signUpToTangoTab", e);
		}
	}
	/**
	 * Method will be update the zip code for the given user.
	 * 
	 * @param faceBookVo
	 * @throws TangoTabException
	 */
	public void updateZipForUser(FaceBookVo faceBookVo) throws TangoTabException
	{
		Log.v("updateZipForUser","Invoking the Method updateZipForUser()");
		String message = null;
		try
		{
			FacebookLoginDao facebookLoginDao = new FacebookLoginDao();
			message = facebookLoginDao.updateZipForUser(faceBookVo);
			Log.v("updateZipForUser message is ", message);
		}
		catch (TangoTabException e) 
		{
			Log.e("Exception ", "Exception occured at updateZipForUser() method",e);
			throw new TangoTabException("FacebookLoginService", "updateZipForUser", e);
		}
	}
	
	
}