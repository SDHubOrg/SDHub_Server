package org.sdhub.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.sdhub.model.StockDataModel;

public class DataSpaceUtil {

	public static HashMap<String, List<StockDataModel>> answerAuthority(HashMap<String, List<StockDataModel>> dataSpace, int flag)
	{
		HashMap<String, List<StockDataModel>> dataSpaceAnswered = new HashMap<String, List<StockDataModel>>();
		
		//向前复权
		if(0 == flag)
		{
			for(String symbol:dataSpace.keySet())
			{
				List<StockDataModel> rowData = dataSpace.get(symbol);
				//System.out.println(rowData.size());
				List<StockDataModel> rowDataAnswered = new ArrayList<StockDataModel>();
				int i;
				//System.out.println(rowDataAnswered.size());
				
				double lastAf = Double.NaN;
				
				for(i = rowData.size() - 1; i >= 0; i--)
				{
					if(rowData.get(i) != null)
					{
						if(rowData.get(i).getStatus() == StockDataModel.TRADING)
						{
							lastAf = rowData.get(i).getAf();
							break;
						}
					}
				}
				
				
				//System.out.println(lastAf);
				for(i = 0; i < rowData.size(); i++)
				{
					
					StockDataModel sd = rowData.get(i);
					
					double af = sd.getAf();
					double temp = 0;
					//System.out.println(i);
					
					temp = sd.getOpenPrc() * af / lastAf;
					sd.setOpenPrc(temp);
					
					temp = sd.getHighPrc() * af / lastAf;
					sd.setHighPrc(temp);
					
					temp = sd.getLowPrc() * af / lastAf;
					sd.setLowPrc(temp);
					
					temp = sd.getClosePrc() * af / lastAf;
					sd.setClosePrc(temp);
					
					temp = sd.getVolume() / (af / lastAf);
					sd.setVolume(temp);
					
					rowDataAnswered.add(sd);
				}
				
				dataSpaceAnswered.put(symbol, rowDataAnswered);
			}
		}else if(1 == flag)
		{
			
			for(String symbol:dataSpace.keySet())
			{
				List<StockDataModel> rowData = dataSpace.get(symbol);
				List<StockDataModel> rowDataAnswered = new ArrayList<StockDataModel>(rowData.size());
				int i;

				for(i = rowData.size() - 1; i >= 0; i--)
				{
					StockDataModel sd = rowData.get(i);
					
					double af = sd.getAf();
					
					sd.setOpenPrc(sd.getOpenPrc() * af );
					sd.setHighPrc(sd.getHighPrc() * af );
					sd.setLowPrc(sd.getLowPrc() * af );
					sd.setClosePrc(sd.getClosePrc() * af );
					sd.setVolume(sd.getVolume() / af);
					
					rowDataAnswered.set(i, sd);
				}
				
				dataSpaceAnswered.put(symbol, rowDataAnswered);
			}
			
		}
		
		return dataSpaceAnswered;
	}
	
	
	
	public static HashMap<String, List<StockDataModel>> setSpaceStartDate(HashMap<String, List<StockDataModel>> dataSpace, String startDate)
	{
		for(String symbol : dataSpace.keySet())
		{
			List<StockDataModel>  rowList = dataSpace.get(symbol);
			
			rowList = setRowStartDate(rowList, startDate);
			
			dataSpace.put(symbol, rowList);
		}
		return dataSpace;
	}
	
	public static List<StockDataModel> setRowStartDate(List<StockDataModel> rowList, String startDate)
	{
		Iterator<StockDataModel> iterator = rowList.iterator();

		while(iterator.hasNext())
		{
			StockDataModel sd = iterator.next();
			if(startDate.equals(sd.getDate()))
			{
				break;
			}
			
			iterator.remove();
		}
		//System.out.println("rowList2: " + rowList.size());
		return rowList;
	}
	
}
