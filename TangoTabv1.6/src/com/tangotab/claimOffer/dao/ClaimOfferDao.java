package com.tangotab.claimOffer.dao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.InputSource;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.signUp.xmlHandler.MessageHandler;
/**
 * Dao class for claim an offer from selected offers
 * 
 * @author dillip.lenka
 *
 */
public class ClaimOfferDao extends TangoTabBaseDao
{
	/**
	 * Claim the selected offer
	 * @param dealsDetailVo
	 * @return
	 */
	public String claimOffer(DealsDetailVo dealsDetailVo) throws TangoTabException
	{
		Log.v("Invoking method claimOffer() with parameter dealsDetailVo is ", dealsDetailVo.toString());
		String message=null;
		String offerUrl =getOfferUrl(dealsDetailVo);
		Log.v("offerUrl is", offerUrl);
		
		try {
			TangoTabBaseDao instance =TangoTabBaseDao.getInstance();
			ConnectionManager cManager = instance.getConManger();
			MessageHandler msgHandler = new MessageHandler();
			instance.getXmlReader().setContentHandler(msgHandler);
			cManager.setupHttpGet(offerUrl);
			InputSource m_is = cManager.makeGetRequestGetResponse();
			if(m_is!=null){
				instance.getXmlReader().parse(m_is);
				message = msgHandler.getMessage();
				Log.v("Response Message is", message);
			}
		} catch (IOException e) 
		{
			Log.e("Exception ", "IOException occuered in claimOffer method ", e);
			throw new TangoTabException("ClaimOfferDao", "claimOffer", e);
		} catch (Exception e)
		{
			Log.e("Exception ", "SAXException occuered in claimOffer method ", e);
			throw new TangoTabException("ClaimOfferDao", "claimOffer", e);
		}
		return message;
	}
	/**
	 * 
	 * @param dealsDetailVo
	 * @return
	 */
	private String getOfferUrl(DealsDetailVo dealsDetailVo)
	{
		Log.v("Invoking method getOfferUrl() with parameter dealsDetailVo is ", dealsDetailVo.toString());
		String offerUrl =null;
		if(!ValidationUtil.isNull(dealsDetailVo))
		{
			String mTimeStamp =null;
			String restDealStartDate =dealsDetailVo.getDealAvailableStartDate();
			 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		       
		        try{
		         Date dt = formatter.parse(restDealStartDate);
		         SimpleDateFormat formatter2 = new SimpleDateFormat("EEE, MMM dd yyyy");
		         mTimeStamp = formatter2.format(dt);
		         
		        }catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
				
		String mTimestamp = mTimeStamp+" "+dealsDetailVo.getEndTime();
		String mtime = mTimestamp.replace(" ", "%20");
		offerUrl= AppConstant.baseUrl+"/deals/insertdeal?emailId="+dealsDetailVo.getUserId()+"&timestamp="+mtime+"&dealId="+dealsDetailVo.getId()+"&orderedtimestamp=";
		Log.v("offerUrl is", offerUrl);
		}
		return 	offerUrl;
	}
}
