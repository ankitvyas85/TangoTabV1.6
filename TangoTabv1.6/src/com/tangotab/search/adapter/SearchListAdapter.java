package com.tangotab.search.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.claimOffer.activity.ClaimOfferActivity;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * Adapter class to be used for displaying list of deals in layout.
 * 
 * <br> Class :SearchListAdapter
 * <br> Layout :dealslistitems.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class SearchListAdapter extends BaseAdapter 
{
	/*
	 * Meta Definitions
	 */
	private ImageLoader imageLoader;
	private Context context;
	private final List<DealsDetailVo> dealsDetailList;
	/*
	 * UI Widgets
	 */
	private LayoutInflater layoutInflater;
	private TextView businessname;
	private TextView dealname;
	private TextView date;
	private TextView nodealsavailable;	
	private LinearLayout llShowMore;
	
	/**
	 * Constructor for near me list adapter.
	 * @param context
	 * @param dealsDetailList
	 * @param locationSearch
	 */
	public SearchListAdapter(Context context,List<DealsDetailVo> dealsDetailList,boolean locationSearch,LinearLayout llShowMore) 
	{
		this.context=context;
		this.dealsDetailList = dealsDetailList;
		this.llShowMore =llShowMore;
		imageLoader = new ImageLoader(context.getApplicationContext());		
		layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
	}
	
	
	@Override
	public int getCount()
	{
		return dealsDetailList.size();
	}

	@Override
	public Object getItem(int arg0)
	{
		return null;
	}

	@Override
	public long getItemId(int arg0) 
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) 
		{
			if(convertView==null)
			{
				convertView=layoutInflater.inflate(R.layout.dealslistitems, null);
			}
			DealsDetailVo  dealsDetailVo= dealsDetailList.get(position);
			
			String noDeals = dealsDetailVo.getNoOfdeals();
			if (noDeals.contains("'"))
				noDeals = noDeals.replace("'", "");
			int no_deals =0;
			try
			{
			 no_deals = Integer.parseInt(noDeals);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			if(getCount()<no_deals)
				llShowMore.setVisibility(View.VISIBLE);
			if(getCount()<10)
				llShowMore.setVisibility(View.GONE);	
				
			String restDeal_Available_startDate = dealsDetailVo.getDealAvailableStartDate();
			String rest[] = restDeal_Available_startDate.split("-");
			String rest1;
			if (rest.length > 2)
				rest1 = rest[1] + "-" + rest[2] + "-" + rest[0];
			else
				rest1 = rest[1] + "-" + rest[0];
	
			// Refer to TextView from Layout
			businessname = (TextView) convertView.findViewById(R.id.businessname);
			businessname.setText(dealsDetailVo.getBusinessName());
			ImageView image = (ImageView) convertView.findViewById(R.id.image);
			imageLoader.DisplayImage(dealsDetailVo.getImageUrl(), image);	
			dealname = (TextView) convertView.findViewById(R.id.dealname);
			dealname.setText(dealsDetailVo.getDealName());
	
			date = (TextView) convertView.findViewById(R.id.date);
			date.setText(rest1 + ", " + dealsDetailVo.getStartTime() + " to "+ dealsDetailVo.getEndTime());
			nodealsavailable = (TextView) convertView.findViewById(R.id.nodealsavailable);
			String drivingDistance=dealsDetailVo.getDrivingDistance();
			/**
			 * Breaking nodealsavailable into two lines 
			 */
			StringBuilder dealsAvailable = new StringBuilder();
			dealsAvailable.append(dealsDetailVo.getNoDealsAvailable()).append(" offers available").append("\r\n").append( drivingDistance).append(" miles");
			nodealsavailable.setText(dealsAvailable.toString());
			/* 
			 * Click Handler for ContentView
			 */
			convertView.setOnClickListener(
					new OnClickListener() 
					{		
						@Override
						public void onClick(View v) 
						{
							int textPosition = position;
							DealsDetailVo dealsDetailVo = dealsDetailList.get(textPosition);
							Intent calimedIntent = new Intent(context,ClaimOfferActivity.class);
							calimedIntent.putExtra("from",  "search");
							calimedIntent.putExtra("selectDeal",  dealsDetailVo);
							context.startActivity(calimedIntent);
			             }
					});
			convertView.setTag(dealsDetailVo);
			return convertView;		
	}
	
	
}
