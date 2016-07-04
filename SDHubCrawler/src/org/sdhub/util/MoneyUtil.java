package org.sdhub.util;

import java.text.DecimalFormat;

public class MoneyUtil {

	// get number from "1亿" "2百万" "4800万"
	public static Integer getMoneyInteger(String moneyString)
	{
		
		Integer result = Integer.MAX_VALUE;
		
		if(moneyString.contains("亿"))
		{
			int k = moneyString.indexOf("亿");
			String numberString = moneyString.substring(0, k);
			result = Integer.valueOf(numberString) * 100000000;
		}
		
		if(moneyString.contains("千万"))
		{
			int k = moneyString.indexOf("千万");
			String numberString = moneyString.substring(0, k);
			result = Integer.valueOf(numberString) * 10000000;
		}
		
		if(moneyString.contains("百万"))
		{
			int k = moneyString.indexOf("百万");
			String numberString = moneyString.substring(0, k);
			result = Integer.valueOf(numberString) * 1000000;
		}
		
		if(moneyString.contains("万"))
		{
			int k = moneyString.indexOf("万");
			String numberString = moneyString.substring(0, k);
			result = Integer.valueOf(numberString) * 10000;
		}
		
		return result;
		
	}
	
	// get number from "1.34亿" "25.4百万" "4800万"
	public static Double getMoneyDouble(String moneyString)
	{
		
		Double result = Double.NaN;
		
		if(moneyString.contains("亿"))
		{
			int k = moneyString.indexOf("亿");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 100000000;
		}
		
		if(moneyString.contains("千万"))
		{
			int k = moneyString.indexOf("千万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 10000000;
		}
		
		if(moneyString.contains("百万"))
		{
			int k = moneyString.indexOf("百万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 1000000;
		}
		
		if(moneyString.contains("万"))
		{
			int k = moneyString.indexOf("万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 10000;
		}
		
		return result;
	}
	
	// get number from "1.34亿" "25.4百万" "4800万"
	// return "180000000.5"
	public static String getMoneyString(String moneyString)
	{
		
		Double result = Double.NaN;
		
		if(moneyString.contains("亿"))
		{
			int k = moneyString.indexOf("亿");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 100000000;
		}
		
		if(moneyString.contains("千万"))
		{
			int k = moneyString.indexOf("千万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 10000000;
		}
		
		if(moneyString.contains("百万"))
		{
			int k = moneyString.indexOf("百万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 1000000;
		}
		
		if(moneyString.contains("万"))
		{
			int k = moneyString.indexOf("万");
			String numberString = moneyString.substring(0, k);
			result = Double.valueOf(numberString) * 10000;
		}
		
		DecimalFormat df = new DecimalFormat("0.00");
		
		return df.format(result);
	}
}
