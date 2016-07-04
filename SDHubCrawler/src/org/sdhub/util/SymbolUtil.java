package org.sdhub.util;

public class SymbolUtil {
	
	public static final String MARKET_UNKNOW = "unknow";
	
	public static boolean isAStockId(Double id) {

		/*
		 * % 60 t1 = (CodeDouble>=600000); t2 = (CodeDouble<=609999);
		 * 
		 * % 00 t1 = (CodeDouble>=1); t2 = (CodeDouble<=3000);
		 * 
		 * % 30 t1 = (CodeDouble>=300001); t2 = (CodeDouble<=309999);
		 */

		if (id <= 309999 && id >= 300001) {
			return true;
		}

		if (id <= 3000 && id >= 1) {
			return true;
		}

		if (id <= 609999 && id >= 600000) {
			return true;
		}

		return false;
	}
	
	public static boolean isAStockId(Integer id) {

		if (id <= 309999 && id >= 300001) {
			return true;
		}

		if (id <= 3000 && id >= 1) {
			return true;
		}

		if (id <= 609999 && id >= 600000) {
			return true;
		}

		return false;
	}
	
	public static boolean isAStockId(String idString) {
		
		Double id = Double.valueOf(idString);

		if (id <= 309999 && id >= 300001) {
			return true;
		}

		if (id <= 3000 && id >= 1) {
			return true;
		}

		if (id <= 609999 && id >= 600000) {
			return true;
		}

		return false;
	}
	
	public static String getSymbolExchMarket(String idString)
	{
		Double id = Double.valueOf(idString);

		if (id <= 309999 && id >= 300001) {
			return "sz";
		}

		if (id <= 3000 && id >= 1) {
			return "sz";
		}

		if (id <= 609999 && id >= 600000) {
			return "sh";
		}

		return "unknow";
	}
	
	public static String getSymbolExchMarket(Double id)
	{

		if (id <= 309999 && id >= 300001) {
			return "sz";
		}

		if (id <= 3000 && id >= 1) {
			return "sz";
		}

		if (id <= 609999 && id >= 600000) {
			return "sh";
		}

		return "unknow";
	}
	
	public static String getSymbolExchMarket(Integer id)
	{

		if (id <= 309999 && id >= 300001) {
			return "sz";
		}

		if (id <= 3000 && id >= 1) {
			return "sz";
		}

		if (id <= 609999 && id >= 600000) {
			return "sh";
		}

		return "unknow";
	}
	
	public static String getYahooStyleId(String symbol)
	{
		Double id = Double.valueOf(symbol);

		if (id <= 309999 && id >= 300001) {
			return symbol + ".SZ";
		}

		if (id <= 3000 && id >= 1) {
			return symbol + ".SZ";
		}

		if (id <= 609999 && id >= 600000) {
			return symbol + ".SS";
		}

		return "unknow";
	}
	
}
