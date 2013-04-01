package com.tangotab.core.connectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import android.util.Log;
/**
 * This class will be used for creating connection manager for service call.
 * 
 * @author dillip.lenka
 *
 */
public class ConnectionManager
{
	/** FIELDS */
	private DefaultHttpClient httpclient=null;
	private HttpPost httppost=null;
	private HttpGet httpget=null;
	private HttpPut httpput=null;
	private InputStream inputStreamResponse=null;
	private String returnString=null;
	HttpResponse response=null;
	
	/**
	 * Method to get http client
	 * @param uri
	 */
	public void setupHttpGet(String uri) throws Exception
	{
		Log.v("Method invokation:", "Invoking method setupHttpGet() "+uri);
		try {
			this.httpclient = new DefaultHttpClient();
			this.httpget = new HttpGet(uri);
		} catch (Exception e)
		{
			Log.e("Exception :", "Exception occured at creating httpclient", e);
			throw new Exception(e.getLocalizedMessage());
		}
	}
	/**
	 * Method used for Initialize httpClient
	 * @param uri
	 */
	public void initializePutURL(String uri)
	{
		Log.v("Method invokation:", "Invoking method initializePutURL() "+uri);
		this.httpclient = new DefaultHttpClient();
		this.httpput = new HttpPut(uri);
		
	}
	
	/**
	 * Method will be used for get the response from url and entity parameters
	 * 
	 * @param uri
	 * @param content
	 */
	
	public void goPutIt(String uri, StringEntity content)
	{
		Log.v("Method invokation:", "Invoking method goPutIt() "+uri);
		try{
			this.httpput.setEntity(content);
			httpput.setHeader("Content-Type", "application/xml");
			
    		response = httpclient.execute(httpput);
    		Log.v("RESPONCE IS", "---"+response);
    		
		    } catch (Exception uee)
		    {
		    	Log.e("Exception :", "Exception occured when getting response ", uee);
		        uee.printStackTrace();
		    } 
		
	}
	/**
	 * Method to get response.
	 * @return
	 */
	public String getPutResponse()
	{
		Log.v("Method invokation:", "Invoking method getPutResponse() ");
		
		HttpEntity entity = response.getEntity();
        try 
         {
			inputStreamResponse = entity.getContent();
		}
        catch (IllegalStateException e)
		{	
        	Log.e("Exception :", "IllegalStateException occured in getPutResponse() method ", e);
			e.printStackTrace();
		} catch (IOException e)
		{	
			Log.e("Exception :", "IOException occured in getPutResponse() method ", e);
			e.printStackTrace();
		}
	    return convertStreamToString(inputStreamResponse);	     	
			
	}
	/**
	 * Method to be used for getting response.
	 * @return
	 */
	public InputSource makeGetRequestGetResponse()
	{
		Log.v("Method invokation:", "Invoking method makeGetRequestGetResponse() ");
		InputSource is = null;
		try {
			/** Perform the actual HTTP POST */
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			inputStreamResponse = entity.getContent();
			this.returnString = convertStreamToString(inputStreamResponse);
			
			/* Lets try to parse the HTTP Response here */
			is = new InputSource();
			is.setCharacterStream(new StringReader(this.returnString));

		} catch (Exception uee)
		{
			Log.e("Exception :", "Exception occured in makeGetRequestGetResponse() method ", uee);
			uee.printStackTrace();
		}
		return is;
	}

	/**
	 * This method will be used for initialize http client
	 * @param uri
	 */
	public void setupHttpPost(String uri)
	{
		Log.v("Method invokation:", "Invoking method setupHttpPost() "+"uri = "+uri);
		this.httpclient = new DefaultHttpClient();
		this.httppost = new HttpPost(uri);
		HttpProtocolParams.setUseExpectContinue(this.httpclient.getParams(),false);
	}

	/**
	 * 
	 * @param entity
	 */
	public void setHttpPostEntity(HttpEntity entity)
	{
		Log.v("Method invokation:", "Invoking method setHttpPostEntity() "+"entity = "+entity);
		this.httppost.setHeader("Content-Type", "text/xml");
		this.httppost.setEntity(entity);
		try {
			if (entity != null) 
			{
				Log.v("Request TO Server", EntityUtils.toString(entity));
				Log.v("REQUEST", entity.toString());
			}
		} catch (ClientProtocolException e)
		{
			Log.e("Exception :", "ClientProtocolException occured in setHttpPostEntity() method ", e);
			e.printStackTrace();
		} catch (IOException e)
		{
			Log.e("Exception :", "IOException occured in setHttpPostEntity() method ", e);
			e.printStackTrace();
		}
		
	}

	/**
	 * makeRequestGetResponse Just send the request, wait for response, convert
	 * to Input Source to return back
	 * @return
	 */
	public InputSource makeRequestGetResponse() 
	{
		Log.v("Method invokation:", "Invoking method makeRequestGetResponse() ");
		InputSource is = null;
		try {
			/** Perform the actual HTTP POST */
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			inputStreamResponse = entity.getContent();			
			this.returnString = convertStreamToString(inputStreamResponse);			
			Log.i("RESPONSE", this.returnString);			
			/* Lets try to parse the HTTP Response here */
			is = new InputSource();			
			is.setCharacterStream(new StringReader(this.returnString));
		} catch (Exception uee) {
			Log.e("Exception :", "IOException occured in makeRequestGetResponse() method ", uee);
			uee.printStackTrace();
		}
		return is;
	}
	/**
	 * makeRequestGetResponse Just send the request, wait for response, convert
	 * to Input Source to return back
	 */
	public InputSource makeRequestputResponse()
	{
		Log.v("Method invokation:", "Invoking method makeRequestputResponse() ");
		InputSource is = null;
		try {
			/** Perform the actual HTTP POST */
			HttpResponse response = httpclient.execute(httpput);
			HttpEntity entity = response.getEntity();
			inputStreamResponse = entity.getContent();
			this.returnString = convertStreamToString(inputStreamResponse);
			 Log.i("RESPONSE", this.returnString);
			/* Lets try to parse the HTTP Response here */
			is = new InputSource();
			is.setCharacterStream(new StringReader(this.returnString));
		} catch (Exception uee) {
			// Some Exception, Print the stack trace
			uee.printStackTrace();
		}
		return is;
	}	
	
	public void setupHttpPut(String uri) {
		//Log.v("POSTURL", uri);
		this.httpclient = new DefaultHttpClient();
		this.httpput = new HttpPut(uri);
		HttpProtocolParams.setUseExpectContinue(this.httpclient.getParams(),false);
	}	
	
	/*
	 * convertStreamToString This is used to convert IS to string
	 */
	public String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
}