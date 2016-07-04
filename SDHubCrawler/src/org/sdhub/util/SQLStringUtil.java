package org.sdhub.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

public class SQLStringUtil {
	
	public static String buildInsertSQL(String table, HashMap<String, String> values)
	{
		
		
		StringBuilder sb = new StringBuilder();
		
		StringBuilder sb1 = new StringBuilder();
		
		StringBuilder sb2 = new StringBuilder();
		
		int length = values.size();
		int count = 0;
		
		sb.append("INSERT INTO ");
		sb.append(table);
		sb.append(" ");

		Set<String> keys = values.keySet();
		sb1.append("( ");
		sb2.append("( ");
		for(String key : keys)
		{
			count ++;
			sb1.append(key);
			
			sb2.append("'");
			sb2.append(StringEscapeUtils.escapeSql(values.get(key)));
			sb2.append("'");
			
			if(count < length)
			{
				sb1.append(", ");
				sb2.append(", ");
			}
		}
		
		sb1.append(") ");
		sb2.append(") ");
		
		sb.append(sb1.toString());
		sb.append(" VALUES ");
		sb.append(sb2.toString());
		
		return sb.toString();
	}
	
	//UPDATE 表名称 SET 列名称 = 新值 WHERE 列名称 = 某值
	//UPDATE Person SET Address = 'Zhongshan 23', City = 'Nanjing' WHERE LastName = 'Wilson'
	public static String buildUpdateSQL(String table, HashMap<String, String> values, String conditions)
	{
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		int length = values.size();
		int count = 0;
		
		sb.append("UPDATE ");
		sb.append(table);
		sb.append(" SET ");

		for(String key : values.keySet())
		{
			count ++;
			String temp = key + " = " + "'" + StringEscapeUtils.escapeSql(values.get(key)) + "'";
			sb1.append(temp);
			if(count < length)
			{
				sb1.append(", ");
			}
		}
		
		sb.append(sb1.toString());
		
		sb.append(" ");
		sb.append(conditions);
		
		return sb.toString();
	}
	
	public static String buildUpdateSQLByUID(String table, HashMap<String, String> values, String uid)
	{
		StringBuilder sb = new StringBuilder();
		StringBuilder sb1 = new StringBuilder();
		int length = values.size();
		int count = 0;
		
		sb.append("UPDATE ");
		sb.append(table);
		sb.append(" SET ");

		for(String key : values.keySet())
		{
			count ++;
			String temp = key + " = " + "'" + StringEscapeUtils.escapeSql(values.get(key)) + "'";
			sb1.append(temp);
			if(count < length)
			{
				sb1.append(", ");
			}
		}
		
		sb.append(sb1.toString());
		
		sb.append(" WHERE UID = '");
		sb.append(uid);
		sb.append("'");
		
		return sb.toString();
	}
	
	public static String buildRetriveSQL()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT ");
		
		return sb.toString();
	}
	
	public static String buildDeleteSQL()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT ");
		
		return sb.toString();
	}

}
