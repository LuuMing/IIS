package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.service.Service;

public class dataPullLet extends HttpServlet {

	/**
	 * The doGet method of the Server let.
	 */

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
	}

	/**
	 * The doPost method of the Server let.
	 */

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 新建服务对象
		Service serv = new Service();
				
		// 接收注册信息
		String userId = request.getParameter("id");
		String time = request.getParameter("time");
		String ret;
				
		ret = serv.getRecentData(userId,time);
		System.out.println("pull" + ret);
		// 返回信息
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
				
		out.print(ret.toString());
		out.flush();
		out.close();
	}
}
