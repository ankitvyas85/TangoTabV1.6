package com.tangotab.login.service;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.services.TangoTabBaseService;
import com.tangotab.login.dao.LoginDao;
import com.tangotab.login.vo.LoginVo;

/**
 * This class used for login authenticate service
 * 
 * @author dillip.lenka
 *
 */
public class LoginService extends TangoTabBaseService
{
	
	
	/**
	 * This method will check the authentication for login
	 * 
	 * @param loginUrl
	 * @return
	 */
	public String doSignIn(LoginVo loginVo) throws TangoTabException
	{
		String message =null;
		try 
		{
			LoginDao loginDao = new LoginDao();
			message = loginDao.doLogin(loginVo);
		}
		catch (TangoTabException e)
		{
			Log.e("Exception :", "IOException occured in login service respone ", e);
			throw new TangoTabException("LoginService", "doSignIn", e);
		}
		
		return message;
	}
}
