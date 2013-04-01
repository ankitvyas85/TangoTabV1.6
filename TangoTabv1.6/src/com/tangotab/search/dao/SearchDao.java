package com.tangotab.search.dao;

import java.io.IOException;
import java.util.List;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tangotab.core.connectionManager.ConnectionManager;
import com.tangotab.core.constant.AppConstant;
import com.tangotab.core.dao.TangoTabBaseDao;
import com.tangotab.core.ex.TangoTabException;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.nearMe.xmlHandler.DealDetailHandler;
import com.tangotab.search.vo.SearchVo;
/**
 * Get list of Deals from the search criteria.
 * 
 * @author dillip.lenka
 *
 */
public class SearchDao extends TangoTabBaseDao
{
	/**
	 * Get list of deals form search criteria.
	 * 
	 * @param searchVo
	 * @return
	 */
	public List<DealsDetailVo> getSearchList(SearchVo searchVo)throws TangoTabException
	{
		Log.v("Invoking getSearchList() method  with parameter", "searchVo = "+searchVo.toString());
		List<DealsDetailVo> dealsList =null;		
		
		try {
			TangoTabBaseDao instance = TangoTabBaseDao.getInstance();
			ConnectionManager cManager = instance.getConManger();
			DealDetailHandler searchHandler = new DealDetailHandler();
			instance.getXmlReader().setContentHandler(searchHandler);
			String searchUrl =getSearchUrl(searchVo);
			if(ValidationUtil.isNullOrEmpty(searchUrl))
				return null;
			Log.v("Search URL: ", searchUrl);		
			cManager.setupHttpGet(searchUrl);			
			InputSource m_is = cManager.makeGetRequestGetResponse();
				if(m_is!=null)
				{
					instance.getXmlReader().parse(m_is);
					dealsList = searchHandler.getDealsList();
				}
			}catch (IOException e)
			{
				Log.e("Exception occured ", "IOException occured in getSearchList() method ", e);
				throw new TangoTabException("SearchDao", "getSearchList", e);
			} catch (Exception e)
			{
				Log.e("Exception occured ", "SAXException occured in getDealsList() method ", e);
				throw new TangoTabException("SearchDao", "getSearchList", e);
			}
			return dealsList;
	}
	/**
	 * generate search URL from searchVo Object.
	 * @param searchVo
	 * @return
	 */
	private String getSearchUrl(SearchVo searchVo)
	{
		Log.v("Invoking getSearchUrl() method with parameter searchVo= ", searchVo.toString());
		String searchUrl =null;
		if(ValidationUtil.isNullOrEmpty(searchVo.getType()))
		{
			String address = searchVo.getAddress();
			String restName = searchVo.getRestName();
			String addressText = address.replace(" ", "%20");
			searchUrl =  AppConstant.baseUrl+"/deals/search?restName="+ TangoTabBaseDao.encodeURI(restName)
					+ "&address="+ TangoTabBaseDao.encodeURI(addressText)+ "&pageIndex="+searchVo.getPageIndex()+ "&noOfdeals=0&searchingradius="
					+ searchVo.getDistance()+ "&coordinate="+ searchVo.getLocLat()+ ","+ searchVo.getLocLaong()
					+ "&userId=" + searchVo.getUserId();
			
			Log.v("searchUrl is", searchUrl);
		}
		else
		{
			String city = searchVo.getAddress();
			String cityText = null;
			if(!ValidationUtil.isNullOrEmpty(city))
			{
				cityText = city.replace(" ", "%20");
				searchUrl =  AppConstant.baseUrl+"/deals/search?type="+ searchVo.getType()
						+ "&city="+ TangoTabBaseDao.encodeURI(cityText)+ "&zipcode="+searchVo.getZipCode()+ "&coordinate="+ searchVo.getLocLat()+ ","+ searchVo.getLocLaong()+ "&searchingradius="
						+ searchVo.getDistance()+"&pageIndex="+searchVo.getPageIndex()+"&noOfdeals=0"+"&restName="+searchVo.getRestName()+"&address="+searchVo.getAddress()+"&version="+searchVo.getVersionName()+"&userId="+searchVo.getUserId();
				Log.v("searchUrl is", searchUrl);		
			}
			
			
		}
		
		return searchUrl;
	}
		
}
