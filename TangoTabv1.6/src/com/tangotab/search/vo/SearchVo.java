package com.tangotab.search.vo;

/**
 * All search criteria information class.
 * 
 * @author dillip.lenka
 *
 */
public class SearchVo
{
	private String address;
	private String restName;
	private String locLat;
	private String locLaong;
	private String userId;
	private String noOfDeals;
	private String distance;
	private String pageIndex;
	private String type;
	private String zipCode;
	private String cityName;
	private String versionName;
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the restName
	 */
	public String getRestName() {
		return restName;
	}
	/**
	 * @param restName the restName to set
	 */
	public void setRestName(String restName) {
		this.restName = restName;
	}
	/**
	 * @return the locLat
	 */
	public String getLocLat() {
		return locLat;
	}
	/**
	 * @param locLat the locLat to set
	 */
	public void setLocLat(String locLat) {
		this.locLat = locLat;
	}
	/**
	 * @return the locLaong
	 */
	public String getLocLaong() {
		return locLaong;
	}
	/**
	 * @param locLaong the locLaong to set
	 */
	public void setLocLaong(String locLaong) {
		this.locLaong = locLaong;
	}
	
	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the noOfDeals
	 */
	public String getNoOfDeals() {
		return noOfDeals;
	}
	/**
	 * @param noOfDeals the noOfDeals to set
	 */
	public void setNoOfDeals(String noOfDeals) {
		this.noOfDeals = noOfDeals;
	}
	
	/**
	 * @return the distance
	 */
	public String getDistance() {
		return distance;
	}
	/**
	 * @param distance the distance to set
	 */
	public void setDistance(String distance) {
		this.distance = distance;
	}
	/**
	 * @return the pageIndex
	 */
	public String getPageIndex() {
		return pageIndex;
	}
	/**
	 * @param pageIndex the pageIndex to set
	 */
	public void setPageIndex(String pageIndex) {
		this.pageIndex = pageIndex;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	
	/**
	 * @return the cityName
	 */
	public String getCityName() {
		return cityName;
	}
	/**
	 * @param cityName the cityName to set
	 */
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	/**
	 * @return the versionName
	 */
	public String getVersionName() {
		return versionName;
	}
	/**
	 * @param versionName the versionName to set
	 */
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SearchVo [address=" + address + ", restName=" + restName
				+ ", locLat=" + locLat + ", locLaong=" + locLaong + ", userId="
				+ userId + ", noOfDeals=" + noOfDeals + ", distance="
				+ distance + ", pageIndex=" + pageIndex + ", type=" + type
				+ ", zipCode=" + zipCode + ", cityName=" + cityName
				+ ", versionName=" + versionName + ", getAddress()="
				+ getAddress() + ", getRestName()=" + getRestName()
				+ ", getLocLat()=" + getLocLat() + ", getLocLaong()="
				+ getLocLaong() + ", getUserId()=" + getUserId()
				+ ", getNoOfDeals()=" + getNoOfDeals() + ", getDistance()="
				+ getDistance() + ", getPageIndex()=" + getPageIndex()
				+ ", getType()=" + getType() + ", getZipCode()=" + getZipCode()
				+ ", getCityName()=" + getCityName() + ", getVersionName()="
				+ getVersionName() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
			
}
