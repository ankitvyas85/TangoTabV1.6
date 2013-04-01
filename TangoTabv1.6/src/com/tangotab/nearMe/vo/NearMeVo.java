package com.tangotab.nearMe.vo;
/**
 * Class for Deal information.
 * 
 * @author dillip.lenka
 *
 */
public class NearMeVo
{
	private String cityName;
	private String zipCode;
	private double lattitude;
	private double longittude;
	private String setDistance;
	private String userId;
	private int pageIndex;
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
	 * @return the lattitude
	 */
	public double getLattitude() {
		return lattitude;
	}
	/**
	 * @param lattitude the lattitude to set
	 */
	public void setLattitude(double lattitude) {
		this.lattitude = lattitude;
	}
	/**
	 * @return the longittude
	 */
	public double getLongittude() {
		return longittude;
	}
	/**
	 * @param longittude the longittude to set
	 */
	public void setLongittude(double longittude) {
		this.longittude = longittude;
	}
	/**
	 * @return the setDistance
	 */
	public String getSetDistance() {
		return setDistance;
	}
	/**
	 * @param setDistance the setDistance to set
	 */
	public void setSetDistance(String setDistance) {
		this.setDistance = setDistance;
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
	
	
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	@Override
	public String toString() {
		return "NearMeVo [cityName=" + cityName + ", zipCode=" + zipCode
				+ ", lattitude=" + lattitude + ", longittude=" + longittude
				+ ", setDistance=" + setDistance + ", userId=" + userId
				+ ", pageIndex=" + pageIndex + ", getCityName()="
				+ getCityName() + ", getZipCode()=" + getZipCode()
				+ ", getLattitude()=" + getLattitude() + ", getLongittude()="
				+ getLongittude() + ", getSetDistance()=" + getSetDistance()
				+ ", getUserId()=" + getUserId() + ", getPageIndex()="
				+ getPageIndex() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
	
	
}
