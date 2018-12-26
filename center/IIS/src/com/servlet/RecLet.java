package com.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import com.service.Service;

public class RecLet extends HttpServlet {

	/**
	 * The doGet method of the Server let.
	 */

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// �½��������
		Service serv = new Service();
		
		// ����ע����Ϣ
		String userId = request.getParameter("id");
		String ret;
		
		System.out.println("let  "+ userId);
		ret = serv.getRecent(userId);
		System.out.println(ret);
		// ������Ϣ
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		out.print(ret.toString());
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the Server let.
	 */

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {



	}

}
