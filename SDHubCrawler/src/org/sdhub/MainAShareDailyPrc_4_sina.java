package org.sdhub;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.sdhub.constant.Config;
import org.sdhub.constant.OptNumbers;
import org.sdhub.database.InnerDBManager;
import org.sdhub.manager.JsonFileManager;
import org.sdhub.model.JsonTableModel;
import org.sdhub.model.StockDataModel;
import org.sdhub.util.DateUtil;
import org.sdhub.util.SymbolUtil;

import au.com.bytecode.opencsv.CSVReader;

public class MainAShareDailyPrc_4_sina {
	
	public static List<String> dateList;
	public static List<String> symbolList;
	
	private static HashMap<String, String> fields = new HashMap<String, String>();
	
	private static String tableName = "AShareDailyPrc_4_sina";
	
	private static Integer StartYear;
	private static Integer EndYear;
	
	
	static {

		fields.put("Code_A", "Char-32");
		fields.put("Date", "Char-16");
		fields.put("Open", "Float-32");
		fields.put("Close", "Float-32");
		fields.put("High", "Float-32");
		fields.put("Low", "Float-32");
		fields.put("Volume", "Float-32");
		fields.put("Amount", "Float-32");
		fields.put("AF", "Float-32");
	}
	
	
	public static void main(String[] args) {
		
		for(int index = 0; index < args.length; index++)
		{
			System.out.print(args[index] + "  ");
		}
		System.out.println("");
		
		dateList = MainAShareHisTradeDateList.getTradingDateList_DB();
		symbolList = new ArrayList<String>();
		symbolList = MainAShareSymbolList.getSymbolList();

		
		//symbolList.add("600001");
		

		
		if(args.length == 1)
		{
			if(args[0].equals("all"))
			{
				for (String symbolId : symbolList) {
					JsonTableModel jtm = new JsonTableModel();
					
					jtm.init(tableName, fields);

					for (int year = 1990; year <= 2025; year++) {

						for (int jidu = 1; jidu <= 4; jidu++) {

							List<StockDataModel> stockDataList = null;
							for(int retryCount = 1; retryCount <= 5; retryCount++)
							{
								try {
									stockDataList = getStockDailyPrcByJiDu(symbolId, year, jidu);
									break;
								} catch (IOException e) {
									e.printStackTrace();
									System.out.println("Retry Time :" + retryCount);
									if(retryCount == 5)
									{
										System.out.println("Fatal Error :" + year + "." + jidu +".");
									}
									continue;
								}
							}
							if(stockDataList == null)
							{
								continue;
							}else{

								for(StockDataModel stockDataModel: stockDataList)
								{
									HashMap<String, String> data = new HashMap<String, String>();
									
	/*								fields.put("Code_A", "Char-32");
									fields.put("Date", "Char-16");
									fields.put("Open", "Float-32");
									fields.put("Close", "Float-32");
									fields.put("High", "Float-32");
									fields.put("Low", "Float-32");
									fields.put("Volume", "Float-32");
									fields.put("Amount", "Float-32");
									fields.put("AF", "Float-32");*/
									
									data.put("Code_A", stockDataModel.getSymbol());
									data.put("Date", stockDataModel.getDate());
									data.put("Open", stockDataModel.getOpenPrc().toString());
									data.put("Close", stockDataModel.getClosePrc().toString());
									data.put("High", stockDataModel.getHighPrc().toString());
									data.put("Low", stockDataModel.getLowPrc().toString());
									data.put("Volume", stockDataModel.getVolume().toString());
									data.put("Amount", stockDataModel.getAmount().toString());
									data.put("AF", stockDataModel.getAf().toString());
									jtm.addRecord(data, UUID.randomUUID().toString(), OptNumbers.NEW, -1);
								}

							}
							
						}
					}
					
					String overlayFileName = symbolId + ".json";
					
					JsonFileManager jfManager = new JsonFileManager(Config.data_backup_path,Config.data_recent_path, tableName, overlayFileName);

					jfManager.addFile(jtm);
				}
			}else if(args[0].equals("daily"))
			{
				String currentDate_String = DateUtil.getCurrentData_Format3();
				Date currentDate = new Date();
				
				JsonTableModel jtm = new JsonTableModel();
				jtm.init(tableName, fields);
				
				for (String symbolId : symbolList) {

					List<StockDataModel> stockDataList = null;
					for(int retryCount = 1; retryCount <= 5; retryCount++)
					{
						try {
							stockDataList = getStockDailyPrcByJiDu(symbolId, DateUtil.getYear(currentDate), DateUtil.getJiDu(currentDate));
							break;
						} catch (IOException e) {
							e.printStackTrace();
							System.out.println("Retry Time :" + retryCount);
							if(retryCount == 5)
							{
								System.out.println("Fatal Error :" + DateUtil.getYear(currentDate) + "." + DateUtil.getJiDu(currentDate) +".");
							}
							continue;
						}
					}
					if(stockDataList == null)
					{
						continue;
					}else{
						if(stockDataList.isEmpty())
						{
							continue;
						}
						HashMap<String, String> data = new HashMap<String, String>();
						
						StockDataModel stockDataModel = stockDataList.get(stockDataList.size() - 1);
						
						if(!stockDataModel.getDate().equals(currentDate_String))
						{
							continue;
						}
						
/*						fields.put("Code_A", "Char-32");
						fields.put("Date", "Char-16");
						fields.put("Open", "Float-32");
						fields.put("Close", "Float-32");
						fields.put("High", "Float-32");
						fields.put("Low", "Float-32");
						fields.put("Volume", "Float-32");
						fields.put("Amount", "Float-32");
						fields.put("AF", "Float-32");*/
						
						data.put("Code_A", stockDataModel.getSymbol());
						data.put("Date", stockDataModel.getDate());
						data.put("Open", stockDataModel.getOpenPrc().toString());
						data.put("Close", stockDataModel.getClosePrc().toString());
						data.put("High", stockDataModel.getHighPrc().toString());
						data.put("Low", stockDataModel.getLowPrc().toString());
						data.put("Volume", stockDataModel.getVolume().toString());
						data.put("Amount", stockDataModel.getAmount().toString());
						data.put("AF", stockDataModel.getAf().toString());
						jtm.addRecord(data, UUID.randomUUID().toString(), OptNumbers.NEW, -1);
					}
				}
				JsonFileManager jfManager = new JsonFileManager(Config.data_backup_path,Config.data_recent_path, tableName);
				jfManager.addFile(jtm);
			}else
			{
				Integer year = Integer.valueOf(args[0]);
				
				
				
				for (String symbolId : symbolList) 
				{
					String overlayFileName = year.toString() + "_" + symbolId + ".json";
					
					for (int startJiDu = 1; startJiDu <= 4; startJiDu++) {
						
						List<StockDataModel> stockDataList = null;
						for(int retryCount = 1; retryCount <= 5; retryCount++)
						{
							try {
								stockDataList = getStockDailyPrcByJiDu(symbolId, year, startJiDu);
								break;
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("Retry Time :" + retryCount);
								if(retryCount == 5)
								{
									System.out.println("Fatal Error :" + year + "." + startJiDu +".");
								}
								continue;
							}
						}
						 
						if(stockDataList == null)
						{
							continue;
						}
						if(stockDataList.isEmpty())
						{
							continue;
						}
						JsonTableModel jtm = new JsonTableModel();
						jtm.init(tableName, fields);
						for(StockDataModel stockDataModel: stockDataList)
						{
							HashMap<String, String> data = new HashMap<String, String>();
							
/*							fields.put("Code_A", "Char-32");
							fields.put("Date", "Char-16");
							fields.put("Open", "Float-32");
							fields.put("Close", "Float-32");
							fields.put("High", "Float-32");
							fields.put("Low", "Float-32");
							fields.put("Volume", "Float-32");
							fields.put("Amount", "Float-32");
							fields.put("AF", "Float-32");*/
							
							data.put("Code_A", stockDataModel.getSymbol());
							data.put("Date", stockDataModel.getDate());
							data.put("Open", stockDataModel.getOpenPrc().toString());
							data.put("Close", stockDataModel.getClosePrc().toString());
							data.put("High", stockDataModel.getHighPrc().toString());
							data.put("Low", stockDataModel.getLowPrc().toString());
							data.put("Volume", stockDataModel.getVolume().toString());
							data.put("Amount", stockDataModel.getAmount().toString());
							data.put("AF", stockDataModel.getAf().toString());
							jtm.addRecord(data, UUID.randomUUID().toString(), OptNumbers.NEW, -1);
						}
						JsonFileManager jfManager = new JsonFileManager(Config.data_backup_path,Config.data_recent_path, tableName, overlayFileName);

						jfManager.addFile(jtm);
					}
				}
			}
		}else if(args.length == 2)
		{
			
			StartYear = Integer.valueOf(args[0]);
			EndYear = Integer.valueOf(args[1]);
			
			for (String symbolId : symbolList) {
				JsonTableModel jtm = new JsonTableModel();
				
				jtm.init(tableName, fields);

				for (int year = StartYear; year <= EndYear; year++) {

					for (int jidu = 1; jidu <= 4; jidu++) {

						List<StockDataModel> stockDataList = null;
						for(int retryCount = 1; retryCount <= 5; retryCount++)
						{
							try {
								stockDataList = getStockDailyPrcByJiDu(symbolId, year, jidu);
								break;
							} catch (IOException e) {
								e.printStackTrace();
								System.out.println("Retry Time :" + retryCount);
								if(retryCount == 5)
								{
									System.out.println("Fatal Error :" + year + "." + jidu +".");
								}
								continue;
							}
						}
						if(stockDataList == null)
						{
							continue;
						}else{

							for(StockDataModel stockDataModel: stockDataList)
							{
								HashMap<String, String> data = new HashMap<String, String>();
								
/*								fields.put("Code_A", "Char-32");
								fields.put("Date", "Char-16");
								fields.put("Open", "Float-32");
								fields.put("Close", "Float-32");
								fields.put("High", "Float-32");
								fields.put("Low", "Float-32");
								fields.put("Volume", "Float-32");
								fields.put("Amount", "Float-32");
								fields.put("AF", "Float-32");*/
								
								data.put("Code_A", stockDataModel.getSymbol());
								data.put("Date", stockDataModel.getDate());
								data.put("Open", stockDataModel.getOpenPrc().toString());
								data.put("Close", stockDataModel.getClosePrc().toString());
								data.put("High", stockDataModel.getHighPrc().toString());
								data.put("Low", stockDataModel.getLowPrc().toString());
								data.put("Volume", stockDataModel.getVolume().toString());
								data.put("Amount", stockDataModel.getAmount().toString());
								data.put("AF", stockDataModel.getAf().toString());
								jtm.addRecord(data, UUID.randomUUID().toString(), OptNumbers.NEW, -1);
							}

						}
						
					}
				}
				
				String overlayFileName = StartYear.toString() + "_" + EndYear.toString() + "_" + symbolId + ".json";
				
				JsonFileManager jfManager = new JsonFileManager(Config.data_backup_path,Config.data_recent_path, tableName, overlayFileName);

				jfManager.addFile(jtm);
			}
			
		}

	}


	public static List<StockDataModel> getStockDailyPrcByJiDu(String symbolId, Integer year, Integer jidu) throws IOException
	{
		System.out.println("" + symbolId + " : " + year + "-" + jidu);
		
		List<StockDataModel> rowList = new ArrayList<StockDataModel>();

		//http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/000562.phtml?year=2008&jidu=4
		String url = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_FuQuanMarketHistory/stockid/"
					+ symbolId
					+ ".phtml?year="
					+ year
					+ "&jidu=" + jidu;

		Document doc;
				
		try {
			rowList.clear();
			doc = Jsoup.connect(url).timeout(25000).get();
			String title = doc.title();
			if (title.equals("()复权历史交易_新浪财经_新浪网")) {
				System.out.println("symbolId 错误！");
				return null;
			}
			//System.out.println("Starting fetch : " + title);

			Element table = doc.getElementById("FundHoldSharesTable");

			if (null == table) {
				System.out.println("End fetch No valid Data!!: " + symbolId + ":" + year + "-" + jidu);
				System.out.println(title);
				throw new IOException();
			}

			Elements rows = table.select("tr");

			for (Element row : rows) {

				// 日期 开盘价 最高价 收盘价 最低价 交易量(股) 交易金额(元)
				Elements dateHtml = row
						.getElementsMatchingOwnText("\\d\\d\\d\\d-\\d\\d-\\d\\d");
				if (dateHtml.isEmpty()) {
					continue;
				}
				Elements cols = row.select("td");

				StockDataModel sd = new StockDataModel();

				//日期 开盘价 最高价 收盘价 最低价 交易量(股) 交易金额(元) 复权因子
				
				Date dateTemp = DateUtil.formater1.parse(cols.get(0).text().toString());
				
				String dateText = DateUtil.formater3.format(dateTemp);

				String openPrcString = cols.get(1).text();

				String highPrcString = cols.get(2).text();

				String closePrcString = cols.get(3).text();

				String lowPrcString = cols.get(4).text();

				String volumeString = cols.get(5).text();

				String amountString = cols.get(6).text();
				
				String afString = cols.get(7).text();

				sd.setSymbol(symbolId);
				sd.setDate(dateText);
				sd.setOpenPrc(openPrcString);
				sd.setHighPrc(highPrcString);
				sd.setClosePrc(closePrcString);
				sd.setLowPrc(lowPrcString);
				sd.setVolume(volumeString);
				sd.setAmount(amountString);
				sd.setAf(afString);
				sd.setStatus(StockDataModel.TRADING);
				rowList.add(sd);
			}
		}catch (ParseException e) {
			e.printStackTrace();
		}
		return rowList;
	}
	
	
	public static List<StockDataModel> getStockDailyPrcByJiDu_DB(String symbolId, Integer year, Integer jidu)
	{
		System.out.println("JIDU : " + symbolId + " " + year + " " + jidu);

		List<StockDataModel> rowList = new ArrayList<StockDataModel>();

		HashMap<String, StockDataModel> stockDataTempHashMap = new HashMap<String, StockDataModel>();

		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement  stmt = null;
		ResultSet rs = null;
				
		try {
			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);

			String startendDate[] = DateUtil.getJiDuRegion(year, jidu);

			String sqlTemp = "select * from {0} WHERE Date>''{1}'' AND Date<''{2}'' ORDER BY Date";
			String sqlString = MessageFormat.format(sqlTemp, 
					tableName, 
					startendDate[0], 
					startendDate[1]);
			System.out.println(sqlString);
			stmt = conn.prepareStatement(sqlString);
			rs = stmt.executeQuery();
			conn.commit();

			while (rs.next()) {

				StockDataModel sd = new StockDataModel();

				String dateText = rs.getString("Date");

				String openPrcString = rs.getString("Open");

				String highPrcString = rs.getString("High");

				String closePrcString = rs.getString("Close");

				String lowPrcString = rs.getString("Low");

				String volumeString = rs.getString("Volume");

				String amountString = rs.getString("Amount");
				
				String afString = rs.getString("AF");

				sd.setSymbol(symbolId);
				sd.setDate(dateText);
				sd.setOpenPrc(openPrcString);
				sd.setHighPrc(highPrcString);
				sd.setClosePrc(closePrcString);
				sd.setLowPrc(lowPrcString);
				sd.setVolume(volumeString);
				sd.setAmount(amountString);
				sd.setAf(afString);
				sd.setStatus(StockDataModel.TRADING);
				stockDataTempHashMap.put(sd.getDate(), sd);
				rowList.add(sd);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return rowList;
	}
	

	
}
