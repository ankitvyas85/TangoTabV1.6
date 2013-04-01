package com.tangotab.core.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to  validating fields.
 *
 * @author Dillip.Lenka
 *
 */
public class ValidationUtil
{

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(String value) {
		return (value == null);
	}

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(Object value) {
		return (value == null);
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(String value) {
		return (value == null || value.length()<=0);
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(List value) {
		return (value == null || value.isEmpty());
	}

	/**
	 * Checks if is null or empty.
	 *
	 * @param value the value
	 * @return true, if is null or empty
	 */
	public static boolean isNullOrEmpty(Map value) {
		return (value == null || value.isEmpty());
	}

	/**
	 * Checks if is positive.
	 *
	 * @param field to be validated.
	 * @return true if given number is positive.
	 */
	public static boolean isPositive(final int field) {
		return field > 0;
	}

	/**
	 * Checks if is positive.
	 *
	 * @param field to be validated.
	 * @return true if given number is positive.
	 */
	public static boolean isPositive(final long field) {
		return field > 0;
	}

	/**
	 * Checks if is null or false.
	 *
	 * @param value the value
	 * @return true, if is null or false
	 */
	public static boolean isNullOrFalse(Boolean value) {
		return (value == null || value == false);
	}

	/**
	 * Checks if is null.
	 *
	 * @param value the value
	 * @return true, if is null
	 */
	public static boolean isNull(Number value) {
		return (value == null);
	}

	/**
	 * Checks if is integer.
	 *
	 * @param input the input
	 * @return true, if is integer
	 */
	public static boolean isInteger(String input)
	{
		if(isNullOrEmpty(input))
			return false;
		try {
			Long.parseLong(input.trim());
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * Check for null and set value.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String checkForNullAndSetValue(String value) {
		String returnValue = "";
		if (ValidationUtil.isNullOrEmpty(value)) {
			returnValue = "0";
		} else {
			returnValue = value;
		}
		return returnValue;
	}
	
	/**
	 * This method will do the email validation 
	 * @param emailstring
	 * @return
	 */
	public static boolean eMailValidation(String emailstring)
	{
		if(ValidationUtil.isNullOrEmpty(emailstring))
			return false;
		Pattern emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
				+ "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
				+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
		Matcher emailMatcher = emailPattern.matcher(emailstring);
		return emailMatcher.matches();
	}
}
