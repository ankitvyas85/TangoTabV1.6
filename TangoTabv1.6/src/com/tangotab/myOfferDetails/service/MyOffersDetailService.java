package com.tangotab.myOfferDetails.service;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.myOfferDetails.dao.MyOffersDetailDao;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * Do check in for selected offer.
 * @author dillip.lenka
 *
 */
public class MyOffersDetailService
{
	/**
	 * do check in for selected offer.
	 * @param offersDetailsVo
	 * @return
	 * @throws TangoTabException
	 */
	public String checkIn(OffersDetailsVo offersDetailsVo)throws TangoTabException
	{
		Log.v("Invoking checkIn method", offersDetailsVo.toString());
		String checkinMessage =null;
		try
		{
			MyOffersDetailDao dao = new MyOffersDetailDao();
			checkinMessage= dao.doCheckIn(offersDetailsVo);
		}
		catch(Exception exe)
		{
			Log.e("Exception:", "Exception occured in chackin the offers ",exe);
			throw new TangoTabException("MyOffersDetailService", "checkIn", exe);
		}
		return checkinMessage;
	}
	
}
