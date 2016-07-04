package org.sdhub;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sdhub.database.InnerDBManager;
import org.sdhub.messagers.SMSMessager;
import org.sdhub.util.SymbolUtil;

public class MainAShareSymbolList {

	private static Logger logger = LogManager.getLogger(MainAShareSymbolList.class.getName());
	
	public static void main(String[] args) {
		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement  stmt = null;
		ResultSet rs = null;
		List<String> oldSymbolList = new ArrayList<String>();
		
		List<String[]> stockList = new ArrayList<String[]>();
		
		List<String[]> diffStockList = new ArrayList<String[]>();
		
		boolean isIncluded;
		
		stockList = getStockList_Web_EastMoney();
		
		if(stockList.isEmpty())
		{
			logger.error("can not get symbol list from EastMoney");
			return;
		}
		
		try{
			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement("select symbol from AShareSymbolList");

			rs = stmt.executeQuery();
			conn.commit();
			while(rs.next())
			{
				oldSymbolList.add(rs.getString(1));
			}
			rs.close();
			
			for(String stock[] : stockList)
			{
				isIncluded = false;
				for(String symbol : oldSymbolList)
				{
					if(symbol.equals(stock[0]))
					{
						isIncluded = true;
					}
				}
				
				if(isIncluded == false)
				{
					diffStockList.add(stock);
				}
			}
			//symbol int(11) PK 
			//exch_market varchar(8) 
			//stock_name
			String insertSql = "INSERT INTO AShareSymbolList (symbol, exch_market, stock_name) VALUES (''{0}'', ''{1}'', ''{2}'')";
			for(String[] newStock: diffStockList)
			{
				System.out.println(MessageFormat.format(insertSql, 
						StringEscapeUtils.escapeSql(newStock[0]), 
						StringEscapeUtils.escapeSql(newStock[1]), 
						StringEscapeUtils.escapeSql(newStock[2])));
				stmt.execute(MessageFormat.format(insertSql, newStock[0],newStock[1],newStock[2]));
			}
			conn.commit();
			
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
		}finally {
            try {  
                if (rs != null){
                	if(!rs.isClosed())
                	{
                		rs.close(); 
                	}
                }
                if (stmt != null){
                    stmt.close(); 
                }
                if (conn != null){
                    conn.close();   
                }
            } catch (Exception e) {  
            	logger.error(e.getMessage());
            }  
		}

	}

	
	//String [0]  stock id
	//String [1]  exchange market
	//String [2]  stock name
	public static List<String[]> getStockList_Web_EastMoney() {
		List<String[]> stockListTemp = new ArrayList<String[]>();
		Document doc;
		try {
			
			doc = Jsoup.connect("http://quote.eastmoney.com/stocklist.html").timeout(8000).get();
			String title = doc.title();
			if (!title.equals("股票代码查询一览表 _ 股票行情 _ 东方财富网")) {
				logger.error("http://quote.eastmoney.com/stocklist.html has been changed !");
				return stockListTemp;
			}
			
			logger.info("Starting fetch : " + title);

			Element content = doc.getElementById("quotesearch");
			Elements links = content.getElementsByTag("li");

			for (Element link : links) {
				String linkText = link.text();
				String firstNum = linkText.substring(linkText.length() - 7,
						linkText.length() - 6);
				String stockId = linkText.substring(linkText.length() - 7,
						linkText.length() - 1);
				String stockName = linkText.substring(0, linkText.length() - 8);

				String arrayTemp[] = new String[3];
				
				
				
				if (SymbolUtil.isAStockId(stockId)) {
					arrayTemp[0] = stockId;
					// System.out.println(stockName);
					arrayTemp[2] = stockName;
					
					arrayTemp[1] = SymbolUtil.getSymbolExchMarket(stockId);
					
					stockListTemp.add(arrayTemp);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stockListTemp;
	}
	
	public static List<String> getSymbolList()
	{
		List<String> symbolList = new ArrayList<String>();
		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);
	
			stmt = conn.prepareStatement("select symbol from AShareSymbolList");

			rs = stmt.executeQuery();
	
			conn.commit();
			while(rs.next())
			{
				symbolList.add(rs.getString(1));
			}
		
		} catch (SQLException e) {
			logger.error("getSymbolList error!! ", e);
		}finally {

            try {  
                if (rs != null){
                    rs.close(); 
                }
                if (stmt != null){
                    stmt.close(); 
                }
                if (conn != null){
                    conn.close();   
                }
            } catch (Exception e) {  
            	e.printStackTrace();
            	logger.error(e);
            }  
		}
		
		return symbolList;
	}
	
}
