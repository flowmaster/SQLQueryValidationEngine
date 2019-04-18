package com.jsqlAss.common;


import core.jsqlAss.ValidationConstants;

public class Util {
	
	public static boolean conjucative(String lObjString ,String contains1,String contains2)
	{
		boolean isConjucative = false;
		String [] parseRes =  lObjString.split(ValidationConstants.SPACE);
		for (int index = 0;index<parseRes.length ; index++)
		{
			if(parseRes[index].trim().equalsIgnoreCase(contains1.trim()) && parseRes[index+1].trim().equalsIgnoreCase(contains2.trim()))
			{
				isConjucative = true;
				break;
			}
		}
		return isConjucative ;
	}
	
	public static String solo(String lObj)
	{
		return ValidationConstants.SPACE+lObj+ValidationConstants.SPACE;
	}

}
