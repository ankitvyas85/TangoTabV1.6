package com.tangotab.search.service;

import java.util.List;

import android.util.Log;

import com.tangotab.core.ex.TangoTabException;
import com.tangotab.nearMe.vo.DealsDetailVo;
import com.tangotab.search.dao.SearchDao;
import com.tangotab.search.vo.SearchVo;
/**
 * Class will be used for get list of deals from search criteria.
 * 
 * @author dillip.lenka
 *
 */
public class SearchService
{
	/**
	 * Get list of deals from search criteria..
	 * @param searchVo
	 * @return
	 * @throws TangoTabException
	 */
	public List<DealsDetailVo> getSearchList(SearchVo searchVo)throws TangoTabException
	{
		Log.v("Invoking method getSearchList() method with parameter ", "searchVo= "+searchVo.toString());
		List<DealsDetailVo> dealsList =null;
		try
		{
			SearchDao dao = new SearchDao();
			dealsList = dao.getSearchList(searchVo);
		}catch(Exception e)
		{
			Log.e("Exception occured ", "Exception occured in getSearchList() method ", e);
			throw new TangoTabException("SearchService", "getSearchList", e);
		}
		return dealsList;
	}
}
