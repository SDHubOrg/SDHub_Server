package org.sdhub;

import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.sdhub.constant.Config;
import org.sdhub.constant.OptNumbers;
import org.sdhub.database.InnerDBManager;
import org.sdhub.manager.JsonFileManager;
import org.sdhub.messagers.SMSMessager;
import org.sdhub.model.JsonTableModel;
import org.sdhub.util.MoneyUtil;
import org.sdhub.util.SQLStringUtil;
import org.sdhub.util.SymbolUtil;



public class MainAShareInfo_4_eastmoney {
	
	private static HashMap<String, String> titleToFields = new HashMap<String, String>();
	private static HashMap<String, String> fields = new HashMap<String, String>();
	
	private static String tableName = "AShareInfo_4_eastmoney";
	
	static {

		fields.put("CompName", "Char-128");
		fields.put("EngName", "Char-128");
		fields.put("FormerName", "Char-128");
		fields.put("Code_A", "Char-32");
		fields.put("Name_A", "Char-32");
		fields.put("Code_B", "Char-32");
		fields.put("Name_B", "Char-32");
		fields.put("Code_H", "Char-32");
		fields.put("Name_H", "Char-32");
		fields.put("Broad", "Char-128");
		fields.put("Industry", "Char-32");
		fields.put("Region", "Char-32");
		fields.put("Chairman", "Char-32");
		fields.put("President", "Char-32");
		fields.put("LegalPerson", "Char-32");
		fields.put("BdSecretary", "Char-32");
		fields.put("RegCapital", "Float-32");
		fields.put("RegAddress", "Char-256");
		fields.put("WorkAddress", "Char-256");

		titleToFields.put("CompName", "公司名称");//compNameString
		titleToFields.put("EngName", "英文名称");//engNameString
		titleToFields.put("FormerName", "曾用名");//formerNameString
		titleToFields.put("Code_A", "A股代码");//code_AString
		titleToFields.put("Name_A", "A股简称");//name_AString
		titleToFields.put("Code_B", "B股代码");// code_BString
		titleToFields.put("Name_B", "B股简称");//name_BString
		titleToFields.put("Code_H", "H股代码");//code_HString
		titleToFields.put("Name_H", "H股简称");//name_HString
		titleToFields.put("Broad", "证券类别");//broadString
		titleToFields.put("Industry", "所属行业");//industryString
		titleToFields.put("Region", "区域");//regionString
		titleToFields.put("Chairman", "董事长");//chairmanString
		titleToFields.put("President", "总经理");//presidentString
		titleToFields.put("LegalPerson", "法人代表");//legalPersonString
		titleToFields.put("BdSecretary", "董秘");//bdSecretaryString
		titleToFields.put("RegCapital", "注册资本\\(元\\)");//regCapitalString
		titleToFields.put("RegAddress", "注册地址");//regAddress
		titleToFields.put("WorkAddress", "办公地址");//workAddress
		
	}
	
	private static Logger logger = LogManager.getLogger(MainAShareInfo_4_eastmoney.class.getName());

	private static boolean succFlag = true;

	public static void main(String[] args) {

		
		InnerDBManager.init();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		ResultSet rs1 = null;
		
		List<String> symbolList = new ArrayList<String>();

		String deleteSql = "DELETE FROM " + tableName + " WHERE Code_A = ''{0}''";
		
		HashMap<String, HashMap<String, String>> aShareInfoMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, HashMap<String, String>> oldShareInfoMap = new HashMap<String, HashMap<String, String>>();
		
		JsonTableModel jtm = new JsonTableModel();
		
		jtm.init(tableName, fields);
		
		symbolList = MainAShareSymbolList.getSymbolList();
		aShareInfoMap = getAShareInfoList(symbolList);
		
		try{
			conn = InnerDBManager.innerDS.getConnection();
			InnerDBManager.printInnerDSStats();
			conn.setAutoCommit(false);

			String sql = "select * from " + tableName;
			
			stmt = conn.prepareStatement(sql);

			rs1 = stmt.executeQuery(sql);
			
			while(rs1.next())
			{
				HashMap<String, String> oldShareInfo = new HashMap<String, String>();
				oldShareInfo.put("CompName", rs1.getString("CompName"));
				oldShareInfo.put("EngName", rs1.getString("EngName"));
				oldShareInfo.put("FormerName", rs1.getString("FormerName"));
				oldShareInfo.put("Code_A", rs1.getString("Code_A"));
				oldShareInfo.put("Name_A", rs1.getString("Name_A"));
				oldShareInfo.put("Code_B", rs1.getString("Code_B"));
				oldShareInfo.put("Name_B", rs1.getString("Name_B"));
				oldShareInfo.put("Code_H", rs1.getString("Code_H"));
				oldShareInfo.put("Name_H", rs1.getString("Name_H"));
				oldShareInfo.put("Broad", rs1.getString("Broad"));
				oldShareInfo.put("Industry", rs1.getString("Industry"));
				oldShareInfo.put("Region", rs1.getString("Region"));
				oldShareInfo.put("Chairman", rs1.getString("Chairman"));
				oldShareInfo.put("President", rs1.getString("President"));
				oldShareInfo.put("LegalPerson", rs1.getString("LegalPerson"));
				oldShareInfo.put("BdSecretary", rs1.getString("BdSecretary"));
				oldShareInfo.put("RegCapital", rs1.getString("RegCapital"));
				oldShareInfo.put("RegAddress", rs1.getString("RegAddress"));
				oldShareInfo.put("WorkAddress", rs1.getString("WorkAddress"));
				
				oldShareInfo.put("UID", rs1.getString("UID"));
				
				oldShareInfoMap.put(oldShareInfo.get("Code_A"), oldShareInfo);
			}
			
			rs1.close();
			
			for(String symbolString : aShareInfoMap.keySet())
			{
				if(oldShareInfoMap.containsKey(symbolString))
				{
					if(!compareInfo(oldShareInfoMap.get(symbolString), aShareInfoMap.get(symbolString)))
					{
						HashMap<String, String> diffMap = diffInfo(oldShareInfoMap.get(symbolString), aShareInfoMap.get(symbolString));
						String updateSQL = SQLStringUtil.buildUpdateSQLByUID(tableName, 
																			diffMap, 
																			oldShareInfoMap.get(symbolString).get("UID"));
						System.out.println(updateSQL);
						//stmt.execute(updateSQL);
						stmt.addBatch(updateSQL);
						diffMap.put("UID", oldShareInfoMap.get(symbolString).get("UID"));// get UID
						jtm.addRecord(diffMap, diffMap.get("UID"), OptNumbers.UPDATE, -1);
					}
				}else{
					aShareInfoMap.get(symbolString).put("UID",UUID.randomUUID().toString()); // create new uuid
					System.out.println(SQLStringUtil.buildInsertSQL(tableName, aShareInfoMap.get(symbolString)));
					String insertValues = SQLStringUtil.buildInsertSQL(tableName, aShareInfoMap.get(symbolString));
					//stmt.execute(insertValues);
					stmt.addBatch(insertValues);
					jtm.addRecord(aShareInfoMap.get(symbolString), aShareInfoMap.get(symbolString).get("UID"), OptNumbers.NEW, -1);
				}
			}
			stmt.executeBatch();
			conn.commit();
			
			JsonFileManager jfManager = new JsonFileManager(Config.data_backup_path,Config.data_recent_path, tableName);

			jfManager.addFile(jtm);
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error("update " + tableName, e);
			succFlag = false;
		}finally {
			
			if(succFlag == false)
			{
				SMSMessager.sendErrorMessage(tableName + ": not complete perfect!!!!");
			}
			
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

	}
	
	public static HashMap<String, HashMap<String, String>> getAShareInfoList(List<String> symbolList)
	{
		HashMap<String, HashMap<String, String>> aShareInfoHashMap = new HashMap<String, HashMap<String, String>>();

		for(String symbolId6 : symbolList)
		{
/*			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			String symbolIdWithBroad = formatSymbolId(symbolId6);
	
			System.out.println(symbolIdWithBroad);
			
	        //http://f10.eastmoney.com/f10_v2/CompanySurvey.aspx?code=sz000002
	        String url = "http://f10.eastmoney.com/f10_v2/CompanySurvey.aspx?code={0}";
	
	        String finnalUrl = MessageFormat.format(url, symbolIdWithBroad);

	        Document doc;
	
	        try {
	        	
	            doc = Jsoup.connect(finnalUrl).timeout(8000).get();
	            
	            String title = doc.title();
	            if (!title.contains("F10资料")) {
	            	logger.error("fetch page error, page gone! fetch skip : " + finnalUrl );
	            	succFlag = false;
	                continue;
	            }
	            //标志位 如果这个字符串找不到说明页面不完全 “沪B2-20070217”
	            Elements flagElements = doc.getElementsMatchingOwnText("沪B2-20070217");
	            
	            if (null == flagElements) {
	            	logger.error("fetch page error, page format changed! can not find flagElements");
	            	succFlag = false;
	                continue;
	            }
	            
	            if (flagElements.isEmpty()) {
	            	logger.error("fetch page error, page format changed! can not find flagElements");
	            	succFlag = false;
	                continue;
	            }
	            
	            logger.info("Starting fetch : " + title);
	
	            Element table = doc.getElementById("Table0");
	
	            if (null == table) {
	            	logger.error("fetch page error, page format changed! can not find Table0");
	            	succFlag = false;
	                continue;
	            }
	
	            HashMap<String, String> shareInfo = new HashMap<String, String>();
	            
	            boolean infoCompleteFlag = true;
	            
	            for(String fieldString : titleToFields.keySet())
	            {
	            	//^status$
	                Elements elements = table.getElementsMatchingOwnText("^" + titleToFields.get(fieldString) + "$");
	                
	                if(null == elements){
	                	System.out.println(fieldString + " can not found");
	                	logger.error("fetch page error, page format changed! can not find element:" + fieldString);
	                	infoCompleteFlag = false;
	                	break;
	                }
	                
	                if (elements.isEmpty()) {
	                	System.out.println(fieldString + " is empty");
	                	logger.error("fetch page error, page format changed! can not find element:" + fieldString);
	                	infoCompleteFlag = false;
	                	break;
	                }else{
	                	
		                if(null == elements.get(0)){
		                	System.out.println(fieldString + " can not found");
		                	logger.error("fetch page error, page format changed! can not find element:" + fieldString);
		                	infoCompleteFlag = false;
		                	break;
		                }
	                	
		                if(null == elements.get(0).nextElementSibling()){
		                	System.out.println(fieldString + " can not found");
		                	logger.error("fetch page error, page format changed! can not find element: elements.get(0).nextElementSibling()" + fieldString);
		                	infoCompleteFlag = false;
		                	break;
		                }
		                
		                String fieldValueString = elements.get(0).nextElementSibling().text().toString();

	                	shareInfo.put(fieldString, fieldValueString);
	                	if("RegCapital".equals(fieldString))
	                	{
	                		shareInfo.put(fieldString, MoneyUtil.getMoneyString(shareInfo.get(fieldString)));
	                	}
	                	shareInfo.put("Code_A", symbolId6);
	                	System.out.println(titleToFields.get(fieldString) + ":" + shareInfo.get(fieldString));
	                }
	            }
	
	            if(false == infoCompleteFlag)
	            {
	            	succFlag = false;
	            	logger.error("skip symbol:" + symbolId6);
	                continue;
	            }
	            
	            aShareInfoHashMap.put(symbolId6, shareInfo);

	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            logger.error("can not open web page http://f10.eastmoney.com/f10_v2/CompanySurvey.aspx  " + e.getMessage());
	            succFlag = false;
	        }
		}
		return aShareInfoHashMap;
	}

	public static boolean compareInfo(HashMap<String, String> oldAShareInfo, HashMap<String, String> newAShareInfo)
	{
		for(String mainField: fields.keySet())
		{
			if(!oldAShareInfo.get(mainField).equals(newAShareInfo.get(mainField)))
			{
				return false;
			}
		}

		return true;
	}
	
	public static HashMap<String, String> diffInfo(HashMap<String, String> oldAShareInfo, HashMap<String, String> newAShareInfo)
	{
		HashMap<String, String> diffMap = new HashMap<String, String>();
		for(String mainField: fields.keySet())
		{
			if(!oldAShareInfo.get(mainField).equals(newAShareInfo.get(mainField)))
			{
				diffMap.put(mainField, newAShareInfo.get(mainField));
			}
		}

		return diffMap;
	}
	
	public static String formatSymbolId(String symbolId)
	{
		String symbolIdWithBroad = "";
		
		String symbolBroad = SymbolUtil.getSymbolExchMarket(symbolId);
		
		symbolIdWithBroad = symbolBroad + symbolId;
		
		return symbolIdWithBroad;
	}
	
}
