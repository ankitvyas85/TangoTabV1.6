package com.tangotab.myOffers.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangotab.R;
import com.tangotab.core.utils.ImageLoader;
import com.tangotab.core.utils.ValidationUtil;
import com.tangotab.myOfferDetails.activity.MyoffersDetailActivity;
import com.tangotab.myOffers.Vo.OffersDetailsVo;
/**
 * Class will used to displaying list of offers .
 * 
 * <br> Class:MyOffersAdapter
 * <br> Layout:myofferslistitems.xml
 * 
 * @author Dillip.Lenka
 *
 */
public class MyOffersAdapter extends BaseAdapter
{
	/*
	 * Meta Definations
	 */
	private ImageLoader imageLoader;
	private Context context;
	private final List<OffersDetailsVo> offersList;
	
	/*
	 * UI Widgets
	 */
	private LayoutInflater layoutInflater;
	private TextView businessname;
	private TextView dealname;
	private TextView con_code;
	private TextView textViewtimeStamp;	
	private LinearLayout llShowMore;
	
	/**
	 * Constructor for nearmelist adapter.
	 * @param context
	 * @param dealsDetailList
	 * @param locationSearch
	 */
	public MyOffersAdapter(Context context,List<OffersDetailsVo> offersDetailsList,LinearLayout llShowMore) 
	{
		this.context=context;
		this.offersList = offersDetailsList;
		this.llShowMore = llShowMore;
		imageLoader = new ImageLoader(context.getApplicationContext());		
				
	}
	
	
	@Override
	public int getCount()
	{
		return offersList.size();
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
			layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView=layoutInflater.inflate(R.layout.myofferslistitems, null);	
			
			OffersDetailsVo  offersDetailsVo= offersList.get(position);
			ImageView image = (ImageView) convertView.findViewById(R.id.mydealimage);
			businessname = (TextView) convertView.findViewById(R.id.businessname);
			con_code = (TextView) convertView.findViewById(R.id.confirmationcode);
			dealname = (TextView) convertView.findViewById(R.id.dealName);
			textViewtimeStamp = (TextView) convertView.findViewById(R.id.timeStamp);
			
			if (!ValidationUtil.isNull(offersDetailsVo))
			{
				String currentDate = offersDetailsVo.getReserveTimeStamp();
				if(!ValidationUtil.isNullOrEmpty(currentDate))
				{
				String reserve[] = currentDate.split(" ");
				String rese[] = reserve[0].split("-");
				String totalTime;
				if (rese.length > 2) {
					totalTime = rese[1] + "-" + rese[2] + "-" + rese[0];
				} else
					totalTime = rese[1] + "-" + rese[0];
				textViewtimeStamp.setText(totalTime + ", " + offersDetailsVo. getStartTime() + " to "	+ offersDetailsVo.getEndTime());
				}
				try {
					if (getCount()< Integer.parseInt(offersDetailsVo.getNoOfDeals())){
						llShowMore.setVisibility(View.VISIBLE);
					}

					if (getCount() == Integer.parseInt(offersDetailsVo.getNoOfDeals()) || getCount()<10) {
						llShowMore.setVisibility(View.GONE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageLoader.DisplayImage(offersDetailsVo.getImageUrl(), image);
				businessname.setText(offersDetailsVo.getBusinessName());
				con_code.setText("Confirmation Code: " + offersDetailsVo.getConResId());
				dealname.setText(offersDetailsVo.getDealName());
				}
			else {
				Log.v("Offer detail object is null", "");
			}
				
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
							Bundle bundle = new Bundle();							
							OffersDetailsVo offersDetailsVo = offersList.get(textPosition);
							/**
							 * Start new activity
							 */
							Intent calimedIntent = new Intent(context,MyoffersDetailActivity.class);
							bundle.putParcelable("selectOffers", offersDetailsVo);
							calimedIntent.putExtras(bundle);
							context.startActivity(calimedIntent);
			             }
					});
			convertView.setTag(offersDetailsVo);
			return convertView;		
	}
	
	
	
}
