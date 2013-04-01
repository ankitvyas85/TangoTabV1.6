package com.tangotab.core.constant;

import java.util.List;

import com.tangotab.myOffers.Vo.OffersDetailsVo;
import com.tangotab.nearMe.vo.DealsDetailVo;

/**
 * This class will contain all the constant informations which will be used
 * through out the application.
 * 
 * @author dillip.lenka
 * 
 */
public class AppConstant {

	public static String IS_SETTINGSCHANGED = "10";

	public static String productionServer = "http://services.tangotab.com/services";

	public static String prodServer = "http://www.tangotab.com/tangotabservices/services";
	public static String qaServer = "http://qa.tangotab.com/tangotabservices/services";
	public static String develServer = "http://ec2-54-245-22-11.us-west-2.compute.amazonaws.com/tangotabservices/services";
	public static String stageServer = "http://Stage.tangotab.com/tangotabservices/services";

	//public static String debugFlurryAgentID = "FSSR4W2S8FGCY52YMX98";
	//public static String productionFlurryAgentID = "W47ZC2NDG4KQM56287CS";

	//public static String debugGoogleAnalyticsID = "UA-32403979-2";
	//public static String productionGoogleAnalyticsID = "GA-3461-2529-1";

	public static String baseUrl = prodServer;
	//public static String FlurryAgentID = productionFlurryAgentID;
	//public static String GoogleAnalyticsID = productionGoogleAnalyticsID;

	public static double locationLat = 0.0;
	public static double locationLong = 0.0;
	
	public static double dev_lat=0.0;
	public static double  dev_lang=0.0;
	
	public static final String NEAR_TO_RESTRURANT = "403.0";
	public static final String PACKAGE_NAME = "com.tangotab";
	public static final String WELCOME_MESSAGE = "TangoTab welcomes you";
	public static final String ALARM_ACTION_NAME = "com.bytefoundry.broadcast.ALARM";
	public static final String INSTLL_VERSION = "1.6";
	public static boolean map_flag=false;
	public static boolean flagFormMaping = false;
	public static List<DealsDetailVo> dealsList;
	public static List<OffersDetailsVo> offersList;
	
	public static final String APP_ID = "262980363802171";
	
	public final static String GA_REG_KEY="UA-37866823-1";//UA-4500699-13
	public static final String LOGIN_PAGE ="/SignIn";
	public static final String SIGN_UP_PAGE="/SignUp";
	public static final String FORGOT_PASSWORD_PAGE ="/Forgetpassword";
	public static final String SEARCH_PAGE="/Search";
	public static final String SETTING_PAGE="/Settings";
	public static final String NEARME_PAGE ="/NearMe";
	public static final String MYOFFER_PAGE ="/MyOffers";
	public static final String CLAIM_OFFER_PAGE ="/ClaimNow";
	public static final String DEALS_DETAIL_PAGE ="/CheckIn";
	public static final String FACEBOOK_LOGIN ="/FaceBookLogin";
	
	

	
}
