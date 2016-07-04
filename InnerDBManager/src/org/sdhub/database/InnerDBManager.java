package org.sdhub.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

import com.mysql.jdbc.Driver;

public class InnerDBManager {
	
	public static BasicDataSource innerDS;
	
    public static void init() {
    	
    	String url = "";
    	String username = "";
    	String password = "";
    	
    	Properties pps = new Properties();
    	try {
    		//TODO create your db.properties file in package org.sdhub.database
    		//url=jdbc:mysql://youripaddress/dbname?characterEncoding=UTF-8
    		//username=username_of_db
    		//password=password_of_db
			pps.load(InnerDBManager.class.getClass().getResourceAsStream("/org/sdhub/database/db.properties"));
			url = pps.getProperty("url");
			username = pps.getProperty("username");
			password = pps.getProperty("password");
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	System.out.println(url);
    	
        try {    

			if(null == innerDS)
			{
		        innerDS = new BasicDataSource();
		        innerDS.setUrl(url);
		        innerDS.setDriverClassName("com.mysql.jdbc.Driver");
		        innerDS.setUsername(username);
		        innerDS.setPassword(password);
		        return;
	    	}
			
			if(innerDS.isClosed())
			{
		        innerDS = new BasicDataSource();
		        innerDS.setUrl(url);
		        innerDS.setDriverClassName("com.mysql.jdbc.Driver");
		        innerDS.setUsername(username);
		        innerDS.setPassword(password);
		        return;
			}
        } catch (Exception e) {    
            e.printStackTrace();  
        }   
    }
    
    public static void printInnerDSStats() {
    	
    	if(innerDS != null)
    	{
    		if(!innerDS.isClosed())
    		{
		        System.out.println("NumActive: " + innerDS.getNumActive());
		        System.out.println("NumIdle: " + innerDS.getNumIdle());
    		}else
    		{
    			System.out.println("innerDS has been CLOSED!");
    		}
    	}else{
    		System.out.println("innerDS is NULL ! ! !");
    	}
    	
    }

    public static void shutdownInnerDS() throws SQLException {
        if(innerDS != null)
        {
        	if(!innerDS.isClosed())
        	{
        		innerDS.close();
        	}
        }
    }
}
