package org.sdhub.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class StockDataModel {

	public static int SUSPENSION = 0;
	public static int TRADING = 1;
	
	public static DateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat formater2 = new SimpleDateFormat("MM/dd/yyyy");
	public static DateFormat formater3 = new SimpleDateFormat("yyyyMMdd");
	
	private String symbol; // 600358
	private Date   date;// 19950907
	private Double openPrc;
	private Double closePrc;
	private Double highPrc;
	private Double lowPrc;
	private Double volume;
	private Double amount;
	private Double preClosePrc;

	private Double af;
	private int status;

	public StockDataModel()
	{
		
		this.symbol = "";
		
		try {
			this.date = formater3.parse("19900101");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.openPrc = Double.NaN;
		this.closePrc = Double.NaN;
		this.highPrc = Double.NaN;
		this.lowPrc = Double.NaN;
		
		this.preClosePrc = Double.NaN;
		
		this.volume = Double.NaN;
		this.amount = Double.NaN;
		
		this.status = SUSPENSION;
		this.af = Double.NaN;
	}
	
	public Double getAf() {
		return af;
	}

	public void setAf(Double af) {
		this.af = af;
	}

	public void setAf(String afString) {
		Double af = Double.valueOf(afString);
		this.af = af;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getDate() {
		return formater3.format(this.date);
	}

	public void setDate(String dateString) {
		Date dateTemp = null;

		try {
			dateTemp = formater2.parse(dateString.trim());
			this.date = dateTemp;
			return;
		} catch (Exception e) {
			
		}
		
		try {
			dateTemp = formater1.parse(dateString.trim());
			this.date = dateTemp;
			return;
		} catch (ParseException e) {

		}
		
		try {
			dateTemp = formater3.parse(dateString.trim());
			this.date = dateTemp;
			return;
		} catch (ParseException e) {

		}
		
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public Double getOpenPrc() {
		return openPrc;
	}
	
	public void setOpenPrc(Double openPrc) {
		this.openPrc = openPrc;
	}
	
	public void setOpenPrc(String openPrcString) {
		Double openPrc = Double.valueOf(openPrcString);
		this.openPrc = openPrc;
	}
	
	public Double getClosePrc() {
		return closePrc;
	}
	
	public void setClosePrc(Double closePrc) {
		this.closePrc = closePrc;
	}
	
	public void setClosePrc(String closePrcString) {
		Double closePrc = Double.valueOf(closePrcString);
		this.closePrc = closePrc;
	}
	
	public Double getHighPrc() {
		return highPrc;
	}
	
	public void setHighPrc(Double highPrc) {
		this.highPrc = highPrc;
	}
	
	public void setHighPrc(String highPrcString) {
		Double highPrc = Double.valueOf(highPrcString);
		this.highPrc = highPrc;
	}
	
	public Double getLowPrc() {
		return lowPrc;
	}
	
	public void setLowPrc(Double lowPrc) {
		this.lowPrc = lowPrc;
	}
	
	public void setLowPrc(String lowPrcString) {
		Double lowPrc = Double.valueOf(lowPrcString);
		this.lowPrc = lowPrc;
	}
	
	public Double getVolume() {
		return volume;
	}
	
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	
	public void setVolume(String volumeString) {
		Double volume = Double.valueOf(volumeString);
		this.volume = volume;
	}

	public Double getPreClosePrc() {
		return preClosePrc;
	}

	public void setPreClosePrc(Double preClosePrc) {
		this.preClosePrc = preClosePrc;
	}
	
	public void setPreClosePrc(String preClosePrcString) {
		Double preClosePrc = Double.valueOf(preClosePrcString);
		this.preClosePrc = preClosePrc;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	public void setAmount(String amountString) {
		Double amount = Double.valueOf(amountString);
		this.amount = amount;
	}
	
}
