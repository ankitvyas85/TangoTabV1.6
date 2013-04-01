package com.tangotab.signUp.vo;

import com.tangotab.core.vo.TangoTabBaseVo;
/**
 * User detail information class.
 * 
 * @author dillip.lenka
 *
 */
public class UserVo extends TangoTabBaseVo
{
	private String first_name;
	private String last_name;
	private String zip_code;
	private String mobile_phone;
	private String user_Id;
	private String email;
	private String password;
	private String promoText;
	
	/**
	 * Overloaded constructor
	 * @param first_name
	 * @param last_name
	 * @param zip_code
	 * @param mobile_phone
	 * @param user_Id
	 * @param email
	 * @param password
	 * @param promoText
	 */
	public UserVo(String first_name, String last_name, String zip_code,String mobile_phone,String user_Id,String email,String password,String promoText)
	{
		this.first_name = first_name;
		this.last_name = last_name;
		this.zip_code = zip_code;
		this.mobile_phone = mobile_phone;
		this.user_Id = user_Id;
		this.email = email;
		this.password = password;
		this.promoText = promoText;
	}
	/**
	 * Default constructor
	 */
	public UserVo(){
		
	}

	/**
	 * @return the first_name
	 */
	public String getFirst_name() {
		return first_name;
	}

	/**
	 * @param first_name the first_name to set
	 */
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	/**
	 * @return the last_name
	 */
	public String getLast_name() {
		return last_name;
	}

	/**
	 * @param last_name the last_name to set
	 */
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	/**
	 * @return the zip_code
	 */
	public String getZip_code() {
		return zip_code;
	}

	/**
	 * @param zip_code the zip_code to set
	 */
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

	/**
	 * @return the mobile_phone
	 */
	public String getMobile_phone() {
		return mobile_phone;
	}

	/**
	 * @param mobile_phone the mobile_phone to set
	 */
	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	/**
	 * @return the user_Id
	 */
	public String getUser_Id() {
		return user_Id;
	}

	/**
	 * @param user_Id the user_Id to set
	 */
	public void setUser_Id(String user_Id) {
		this.user_Id = user_Id;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the promoText
	 */
	public String getPromoText() {
		return promoText;
	}

	/**
	 * @param promoText the promoText to set
	 */
	public void setPromoText(String promoText) {
		this.promoText = promoText;
	}

	@Override
	public String toString() {
		return "UserVo [first_name=" + first_name + ", last_name=" + last_name
				+ ", zip_code=" + zip_code + ", mobile_phone=" + mobile_phone
				+ ", user_Id=" + user_Id + ", email=" + email + ", password="
				+ password + ", promoText=" + promoText + ", getFirst_name()="
				+ getFirst_name() + ", getLast_name()=" + getLast_name()
				+ ", getZip_code()=" + getZip_code() + ", getMobile_phone()="
				+ getMobile_phone() + ", getUser_Id()=" + getUser_Id()
				+ ", getEmail()=" + getEmail() + ", getPassword()="
				+ getPassword() + ", getPromoText()=" + getPromoText()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	
}
