package org.sdhub.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mysql.fabric.xmlrpc.base.Data;

public class DateUtil {
	public final static DateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd");
	public final static DateFormat formater2 = new SimpleDateFormat("MM/dd/yyyy");
	public final static DateFormat formater3 = new SimpleDateFormat("yyyyMMdd");
	
	public static String[] getJiDuRegion(Integer year, Integer jidu)
	{
		String result[] = new String[2];
		
		String start = "";
		String end = "";
		
		if(jidu == 1)
		{
			start = "0100";
			end = "0332";
		}
		
		if(jidu == 2)
		{
			start = "0400";
			end = "0632";
		}
		
		if(jidu == 3)
		{
			start = "0700";
			end = "0932";
		}
		
		if(jidu == 4)
		{
			start = "1000";
			end = "1232";
		}
		
		result[0] = year.toString() + start;
		result[1] = year.toString() + end;
		
		return result;
	}
	
	public static Integer getJiDu(Date date)
	{
		int month = date.getMonth();
		if(month >=0 && month <= 2)
		{
			return 1;
		}
		if(month >=3 && month <= 5)
		{
			return 2;
		}
		if(month >=6 && month <= 8)
		{
			return 3;
		}
		if(month >=9 && month <= 11)
		{
			return 4;
		}
		return 1;
	}
	
	public static Integer getYear(Date date)
	{
		Integer year = 1900 + date.getYear();
		return year;
	}
	
	public static String getCurrentData_Format3()
	{
		Date currentDate = new Date();
		return formater3.format(currentDate);
	}
	
	public static String getCurrentData_Format2()
	{
		Date currentDate = new Date();
		return formater2.format(currentDate);
	}
	
	public static String getCurrentData_Format1()
	{
		Date currentDate = new Date();
		return formater1.format(currentDate);
	}
}
