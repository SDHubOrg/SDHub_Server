package org.sdhub.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sdhub.MainAShareInfo_4_eastmoney;
import org.sdhub.constant.Config;
import org.sdhub.model.JsonTableModel;
import org.sdhub.model.TableIndexRecordModel;
import org.sdhub.util.DateUtil;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;

public class JsonFileManager {
	
	private static Logger logger = LogManager.getLogger(JsonFileManager.class.getName());
	
	private String tableName;
	private String recentDirPath;
	private String backupDirPath;
	private String overlayFileName;
	
	
	public JsonFileManager(String backupDirPath, String recentDirPath, String table)
	{
		this.tableName = table;
		this.recentDirPath = recentDirPath;
		this.backupDirPath = backupDirPath;
		this.overlayFileName = null;
	}
	
	public JsonFileManager(String backupDirPath, String recentDirPath, String table, String overlayFileName)
	{
		this.tableName = table;
		this.recentDirPath = recentDirPath;
		this.backupDirPath = backupDirPath;
		this.overlayFileName = overlayFileName;
	}
	
	public synchronized int addFile(JsonTableModel jtm)
	{
		int failFlag = 0;
		
		if(null == jtm)
		{
			logger.error("JsonFileManager.addFile : jtm is null!");
			return -1;
		}
		
		if(null == jtm.getRecords())
		{
			logger.error("JsonFileManager.addFile : jtm.getRecords is null!");
			return -1;
		}
		
		if(jtm.getRecords().isEmpty())
		{
			return failFlag;
		}
		//TODO
		if(jtm.getRecords().contains("UID"))
		{
			jtm.getRecords().remove("UID");
		}
		
		if(addFile(jtm, false) < 0)
		{
			failFlag = -1;
			logger.error("write backup file failure! table name : " + jtm.getTable());
		}
		
		if(addFile(jtm, true) < 0)
		{
			failFlag = -1;
			logger.error("write recent file failure! table name : " + jtm.getTable());
		}
		
		return failFlag;
	}

	private int addFile(JsonTableModel jtm, boolean isRecentFile)
	{
		String filePath = "";
		String dirPath = "";
		
		if(isRecentFile)
		{
			
			filePath = this.recentDirPath + "/" + tableName + "/" + "index.json";
			dirPath = this.recentDirPath + "/" + tableName + "/";
			
		}else{
			
			filePath = this.backupDirPath + "/" + tableName + "/" + "index.json";
			dirPath = this.backupDirPath + "/" + tableName + "/";
			
		}
		
		File tableIndex = new File(filePath);
		
		int lastSeqNo = -1;
		
		List<TableIndexRecordModel> tirmList = new ArrayList<TableIndexRecordModel>();
		
		TableIndexRecordModel tempTirm = new TableIndexRecordModel();
		
		TableIndexRecordModel lastTirm = new TableIndexRecordModel();
		
		TableIndexRecordModel newTirm = new TableIndexRecordModel();
		
		lastTirm.setLastSeqNo(-1);
		
		try {
			
			File tableDir = new File(dirPath);
			if(!tableDir.exists())
			{
				tableDir.mkdirs();
			}
			
			if(!tableIndex.exists())
			{	
				tableIndex.createNewFile();
				FileWriter fileWriter = new FileWriter(tableIndex);
				fileWriter.append("[]");
				fileWriter.flush();
				fileWriter.close();
			}
			
			JSONReader reader = null;
			try{
				reader = new JSONReader(new FileReader(tableIndex));
				reader.startArray();
				while(reader.hasNext()) {
					lastTirm = reader.readObject(TableIndexRecordModel.class);
					tirmList.add(lastTirm);
				}
				reader.endArray();
			}catch(JSONException e)
			{
				logger.error("read :" + tableName + "failure!", e);
				if(null != reader)
				{
					reader.close();
				}
			}
			if(null != reader)
			{
				reader.close();
			}
			
			jtm.resetSeqNo(lastTirm.getLastSeqNo());
			
			String fileName = null;
			if(this.overlayFileName == null)
			{
				fileName = createFileName(null);
			}else{
				fileName = this.overlayFileName;
			}
			
			//Start Write Json Table Model File
			String jtmFileName = dirPath + fileName;
			File newjtmFile = new File(jtmFileName);
			newjtmFile.createNewFile();
			JSONWriter jtmWriter = new JSONWriter(new FileWriter(newjtmFile));
			//jtmWriter.startObject();
			jtmWriter.writeValue(jtm);
			
			//jtmWriter.endObject();
			jtmWriter.close();
			
			if(jtm.recordSize() == 0)
			{
				newTirm.setFirstSeqNo(lastTirm.getLastSeqNo());
			}else {
				newTirm.setFirstSeqNo(lastTirm.getLastSeqNo() + 1);
			}
			
			newTirm.setLastSeqNo(lastTirm.getLastSeqNo() + jtm.recordSize());
			newTirm.setName(fileName);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis()); 
			newTirm.setTimestamp(timestamp.toString());
			
			tirmList.add(newTirm);
			
			if(isRecentFile)
			{
				//Delete Old table index record 
				if(tirmList.size() > Config.recent_size)
				{
					int oldSize = tirmList.size() - Config.recent_size;
					for(int i = 0; i < oldSize; i++)
					{
						tirmList.remove(0);
					}
				}
			}
			
			JSONWriter writer = new JSONWriter(new FileWriter(filePath));
			writer.startArray();
			for (TableIndexRecordModel tirmTemp : tirmList) {
				writer.writeValue(tirmTemp);
			}
			writer.endArray();
			writer.close();
			
			return 1;
		}catch (Exception e) {
			logger.error("write json file fail!", e);
			return -1;
		}

	}
	
	private synchronized String createFileName(String date)
	{
		StringBuilder sb = new StringBuilder("");
		
		String currentDate = "";
		
		if(null == date)
		{
			currentDate = DateUtil.formater3.format(new Date());
		}else{
			currentDate = date;
		}
		
		Long millis;
		
		millis = System.currentTimeMillis() % 86400000;
		
		sb.append(currentDate);
		sb.append("_");
		sb.append(millis.toString());
		sb.append(".json");
		
		return sb.toString();
	}
	
}
