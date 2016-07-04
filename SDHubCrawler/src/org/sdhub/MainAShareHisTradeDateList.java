package org.sdhub;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import org.sdhub.constant.OptNumbers;
import org.sdhub.database.InnerDBManager;
import org.sdhub.messagers.SMSMessager;

public class MainAShareHisTradeDateList {

	private static Logger logger = LogManager.getLogger(MainAShareHisTradeDateList.class.getName());
	
	private static String tableName = "AShareHisTradeDateList";
	
	public static void main(String[] args) {
		
		boolean succFlag = true;
		
		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement  stmt = null;
		ResultSet rs = null;
		
		List<String> oldDateList = new ArrayList<String>();
		
		List<String> newDateList = new ArrayList<String>();
		
		List<String> diffDateList = new ArrayList<String>();
		
		boolean isIncluded;
		
		newDateList = getTradingDateList_Web();
		
		if(newDateList.isEmpty())
		{
			logger.error("Fatal Error! can not get date list from sina");
			succFlag = false;
			if(succFlag == false)
			{
				SMSMessager.sendFatalMessage(tableName + ": not complete perfect!!!!");
			}
			return;
		}
		
		try{
			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement("select Date from " + tableName);

			rs = stmt.executeQuery();
			conn.commit();
			while(rs.next())
			{
				oldDateList.add(rs.getString(1));
			}
			rs.close();
			
			for(String newDate : newDateList)
			{
				isIncluded = false;
				for(String oldDate : oldDateList)
				{
					if(oldDate.equals(newDate))
					{
						isIncluded = true;
					}
				}
				
				if(isIncluded == false)
				{
					diffDateList.add(newDate);
				}
			}
			//symbol int(11) PK 
			//exch_market varchar(8) 
			//stock_name
			String insertSql = "INSERT INTO " + tableName + " (Date) VALUES (''{0}'')";
			for(String diffDate: diffDateList)
			{
				System.out.println(MessageFormat.format(insertSql, 
						StringEscapeUtils.escapeSql(diffDate)));
				stmt.addBatch(MessageFormat.format(insertSql, 
						StringEscapeUtils.escapeSql(diffDate)));
			}
			stmt.executeBatch();
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
	
	public static List<String> getTradingDateList_DB(){
		List<String> tradingDateList = new ArrayList<String>();
		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement  stmt = null;
		ResultSet rs = null;

		try{
			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);

			stmt = conn.prepareStatement("select Date from " + tableName + " ORDER BY Date");

			rs = stmt.executeQuery();
			conn.commit();
			while(rs.next())
			{
				tradingDateList.add(rs.getString(1));
			}
			rs.close();
			
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
		
		
		return tradingDateList;
	}
	
	// vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000001/type/S.phtml?year=1990&jidu=4
	public static List<String> getTradingDateList_Web() {
		List<String> tradingDateList = new ArrayList<String>();
		Integer startYear = 1990;
		Integer startJiDu = 1;
		for (startYear = 1990; startYear <= 2020; startYear++) {
			for (startJiDu = 1; startJiDu <= 4; startJiDu++) {
				String url = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/000001/type/S.phtml?year="
						+ startYear + "&jidu=" + startJiDu;

				Document doc;
				try {
					doc = Jsoup.connect(url).timeout(8000).get();
					String title = doc.title();
					if (!title.equals("上证综合指数(000001)_历史交易_新浪网")) {
						System.out.println("网页已发生变动，请重新修改代码！");
						return tradingDateList;
					}
					System.out.println("Starting fetch : " + title);

					Element table = doc.getElementById("FundHoldSharesTable");

					if (null == table) {
						System.out.println("End fetch : " + title);

						if (startYear <= 1990) {
							continue;
						} else {
							return tradingDateList;
						}
					}

					Elements dateHtmlList = table
							.getElementsMatchingOwnText("\\d\\d\\d\\d-\\d\\d-\\d\\d");

					List<String> datesTemp2 = new ArrayList<String>();

					for (Element dateHtml : dateHtmlList) {

						// Elements cols = row.children();
						//System.out.println(dateHtml.text());
						String dateTemp = dateHtml.text();
						String dateText = dateTemp.substring(0, 4) + dateTemp.substring(5, 7) + dateTemp.substring(8, 10);
						datesTemp2.add(dateText);
						//System.out.println(dateText);
					}

					int i = 0;
					for (i = datesTemp2.size() - 1; i >= 0; i--) {
						tradingDateList.add(datesTemp2.get(i));
					}

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return tradingDateList;
	}
}
