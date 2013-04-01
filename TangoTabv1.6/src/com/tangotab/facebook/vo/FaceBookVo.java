package com.tangotab.facebook.vo;

import com.tangotab.signUp.vo.UserVo;
/**
 * Class will be used for face book login.
 * 
 * @author Dillip.Lenka
 *
 */
public class FaceBookVo extends UserVo
{
	
	private String facebookid;

	/**
	 * @return the facebookid
	 */
	public String getFacebookid() {
		return facebookid;
	}

	/**
	 * @param facebookid the facebookid to set
	 */
	public void setFacebookid(String facebookid) {
		this.facebookid = facebookid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "FaceBookVo [facebookid=" + facebookid + ", getFacebookid()="
				+ getFacebookid() + ", getFirst_name()=" + getFirst_name()
				+ ", getLast_name()=" + getLast_name() + ", getZip_code()="
				+ getZip_code() + ", getMobile_phone()=" + getMobile_phone()
				+ ", getUser_Id()=" + getUser_Id() + ", getEmail()="
				+ getEmail() + ", getPassword()=" + getPassword()
				+ ", getPromoText()=" + getPromoText() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}


}
