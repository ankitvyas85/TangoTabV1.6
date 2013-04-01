package com.tangotab.facebook.dao;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.util.Log;
import android.util.Xml;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.facebook.vo.FaceBookVo;
import com.tangotab.login.xmlHandler.UserValidationXmlHandler;
import com.tangotab.signUp.xmlHandler.MessageHandler;

/**
 * Class to logging into faceBook and get authentication for login into TangoTab application.
 * 
 * @author Lakshmipathi.P
 * 
 */
public class FacebookLoginDao extends TangoTabBaseDao 
{
	/**
	 * Method will check whether the user already exists or not.
	 * 
	 * @param faceBookVo
	 * @return
	 * @throws TangoTabException
	 */
	public Map<String, String> checkForUser(FaceBookVo faceBookVo)throws TangoTabException 
	{

		Log.v("Invoking method checkForUser() with parameter faceBookVo :",faceBookVo.toString() );
		String message = null;
		Map<String, String> response = new HashMap<String, String>();
		String isActiveUrl = AppConstant.baseUrl + "/isactiveaccount?emailId="+ faceBookVo.getUser_Id() + "&facebookId=" + faceBookVo.getFacebookid();
		Log.v("isActiveUrl is", isActiveUrl);
		
		try 
		{
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			ConnectionManager cManager =new ConnectionManager();
			UserValidationXmlHandler userValidationXmlHandler = new UserValidationXmlHandler();
			instance.getXmlReader().setContentHandler(userValidationXmlHandler);
			cManager.setupHttpGet(isActiveUrl);
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = userValidationXmlHandler.getMessage();
				response.put("message", message);
				response.put("zipCode", userValidationXmlHandler.zip_code);
				response.put("userId", userValidationXmlHandler.user_Id);
				Log.v("FaceBook Response Message is", message);
		}
		} catch (IOException e)
		{
			Log.e("Exception ", "IOException occuered in checkForUser method ",	e);
			throw new TangoTabException("FacebookLoginDao", "checkForUser", e);
		}
		catch (Exception e) 
		{
			Log.e("Exception ",	"SAXException occuered in checkForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "checkForUser", e);
		}
		return response;
	}

	/**
	 * Sign up into TangoTap application using face book credentials.
	 * 
	 * @param faceBookVo
	 * @return
	 * @throws TangoTabException
	 */
	public String signUpToTangoTab(FaceBookVo faceBookVo)throws TangoTabException 
	{
		Log.v("Invoking method signUpToTangoTab() with parameter username and password :",faceBookVo + "," + faceBookVo.toString());
		String message = null;
		TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
		MessageHandler msgHandler = new MessageHandler();
		instance.getXmlReader().setContentHandler(msgHandler);
		ConnectionManager cManager =new ConnectionManager();
		cManager.initializePutURL(AppConstant.baseUrl + '/' + "signup");
		cManager.goPutIt(AppConstant.baseUrl + '/' + "signup",signupDetailsRequest(faceBookVo));
		String response = cManager.getPutResponse();
		InputSource m_is = new InputSource();
		m_is.setCharacterStream(new StringReader(response));
		try
		{
			if (m_is != null) {
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("FaceBook  Response Message is", message);
			}
		} 
		catch (IOException e)
		{
			Log.e("Exception ",	"IOException occuered in signUpToTangoTab method ", e);
			throw new TangoTabException("FacebookLoginDao", "signUpToTangoTab",e);
		} catch (SAXException e)
		{
			Log.e("Exception ","SAXException occuered in signUpToTangoTab method ", e);
			throw new TangoTabException("FacebookLoginDao", "signUpToTangoTab",e);
		}

		return message;
	}

	/**
	 * 
	 * @param userName
	 * @param postalZip
	 * @return
	 * @throws TangoTabException
	 */
	public String updateZipForUser(FaceBookVo faceBookVo)throws TangoTabException
	{
		Log.v("Invoking method updateZipForUser() with parameter faceBookVo :",faceBookVo.toString());
		String message = null;
		String updateZipUrl = AppConstant.baseUrl + "/updateZipCode?emailId="+ faceBookVo.getUser_Id() + "&zipCode=" + faceBookVo.getZip_code();
		Log.v("updateZipUrl is", updateZipUrl);
		
		try
		{
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			MessageHandler msgHandler = new MessageHandler();
			instance.getXmlReader().setContentHandler(msgHandler);
			ConnectionManager cManager =new ConnectionManager();
			instance.getXmlReader().setContentHandler(msgHandler);
			cManager.setupHttpGet(updateZipUrl);
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if (m_is != null)
			{
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("FaceBook Response Message is", message);
			}
		}
		catch (IOException e)
		{
			Log.e("Exception ","IOException occuered in updateZipForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateZipForUser",e);
		} catch (Exception e)
		{
			Log.e("Exception ","SAXException occuered in updateZipForUser method ", e);
			throw new TangoTabException("FacebookLoginDao", "updateZipForUser",	e);
		}
		return message;
	}
	/**
	 * Method will return a String entity in order to add all the parmeter.
	 * 
	 * @param faceBookVo
	 * @return
	 */
	public StringEntity signupDetailsRequest(FaceBookVo faceBookVo)
	{
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		StringEntity stringEntity = null;
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag(null, "signup");

			serializer.startTag(null, "email");
			serializer.text(faceBookVo.getUser_Id());
			serializer.endTag(null, "email");
			
			serializer.startTag(null, "password");
			serializer.text(faceBookVo.getPassword());
			serializer.endTag(null, "password");
			
			serializer.startTag(null, "firstname");
			serializer.text(faceBookVo.getFirst_name());
			serializer.endTag(null, "firstname");
			
			serializer.startTag(null, "lastname");
			serializer.text(faceBookVo.getLast_name());
			serializer.endTag(null, "lastname");
			
			serializer.startTag(null, "facebookid");
			serializer.text(faceBookVo.getFacebookid());
			serializer.endTag(null, "facebookid");
			
			serializer.startTag(null, "zipcode");
			serializer.text(faceBookVo.getZip_code());
			serializer.endTag(null, "zipcode");

			serializer.endTag(null, "signup");
			serializer.endDocument();
			stringEntity = new StringEntity(writer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringEntity;
	}
}