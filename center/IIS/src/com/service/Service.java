package com.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.db.DBManager;

public class Service {

	public String login(String username, String password) {

		// ��ȡSql��ѯ���
		String logSql = "select id from user where username ='" + username
				+ "' and password ='" + password +"'";

		// ��ȡDB����
		DBManager sql = DBManager.createInstance();
		sql.connectDB();

		// ����DB����
		try {
			ResultSet rs = sql.executeQuery(logSql);
			if (rs.next()) {
				Integer id = rs.getInt("id");
				sql.closeDB();
				return id.toString();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		sql.closeDB();
		return "NULL";
	}

	public Boolean register(String username, String password) {

		// ��ȡSql��ѯ���
		String regSql = "insert into user (username,password) values('"
				+ username + "','" + password + "') ";
		
		
		// ��ȡDB����
		DBManager sql = DBManager.createInstance();
		sql.connectDB();
		
		int ret = sql.executeUpdate(regSql);
		if (ret != 0) {
			sql.closeDB();
			return true;
		}
		sql.closeDB();

		return false;
	}
	public String getRecent(String userId)  // ��ȡ�û����һ�����ݼ�¼ʱ��
	{
		String sql = "select time from data where user_id = "+userId+" order by time desc limit 1";
		System.out.println(sql);
		DBManager db = DBManager.createInstance();
		db.connectDB();
		
		ResultSet ret = db.executeQuery(sql);
		try {
			ret.next();
			return ret.getString("time");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.print("�ռ�¼");
		}
		return "NULL";
	}
	
	public void add(String id, String JsonArrayStr) 
	{
		DBManager db = DBManager.createInstance();
		try {
			JSONArray jsonarray = new JSONArray(JsonArrayStr);
			for(int i = 0; i < jsonarray.length(); i++) // ���� JsonArray 
			{
				JSONObject json = jsonarray.getJSONObject(i);
				String item = "user_id";
				String values = id;
				Iterator it = json.keys();
				while(it.hasNext())  //����Json 
				{
					String key = it.next().toString();
					if(null!=key && !key.equals("id"))
					{
						item =  item + "," + key ;
						values = values + ",\'"+json.getString(key)+"\'";
					}	
				}
				String sql = "insert into data(" + item + ") values("+ values+")";
				System.out.println("SQL = " +sql);
				db.executeUpdate(sql);	
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getRecentData(String userId, String time) {
		// ��ȡSql��ѯ���
		String Sql;
		if(time.equals("NULL"))
			Sql = "select module_name,send,value,time from data where user_id = " + userId;
		else
			Sql = "select module_name,send,value,time from data where user_id = "+
		 userId + "vand time > " + time + "order by time desc";
		System.out.println("pull" + Sql);
				// ��ȡDB����
		DBManager sql = DBManager.createInstance();
		sql.connectDB();
				// ����DB����
		ResultSet rs = sql.executeQuery(Sql);
		JsonHandler handler = new JsonHandler();
		String ret;
		try {
			ret = handler.handle(rs).toString();
			sql.closeDB();
			return ret;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "[]";
	}
	
	public Boolean execUpdate(String sql) {
		System.out.print("exect Sql " + sql);
		DBManager db = DBManager.createInstance();
		db.connectDB();
		
		int ret = db.executeUpdate(sql);
		return ret == 1;
	}
}
