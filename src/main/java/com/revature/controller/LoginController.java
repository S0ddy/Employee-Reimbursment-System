package com.revature.controller;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.models.LoginDTO;
import com.revature.models.User;
import com.revature.services.LoginService;
import com.revature.services.UserService;



public class LoginController {
	
	private ObjectMapper om = new ObjectMapper();
	private LoginService ls = new LoginService();
	private UserService us = new UserService();

	public void login(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		
		if (req.getMethod().equals("POST")) {
			BufferedReader reader = req.getReader();

			StringBuilder sb = new StringBuilder();

			String line = reader.readLine();

			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}

			String body = new String(sb);

			LoginDTO lDTO = om.readValue(body, LoginDTO.class);

			if (ls.login(lDTO.username, lDTO.password)) {

				HttpSession ses = req.getSession();

				ses.setAttribute("user", lDTO);
				ses.setAttribute("loggedin", true);
				
				// Define user role
				String userRole = null;
				
				for (User user : us.getAllUsers()) {
					if (user.getUserName().equals(lDTO.username))
						userRole = user.getRole();
				}
				
				//Redirect to Employee/Manager page	
//				if (userRole.equals("Employee")) {
//					RequestDispatcher rs = req.getRequestDispatcher("employee.html");
//					rs.include(req, res);
//				} else if (userRole.equals("Manager")) {
//					RequestDispatcher rs = req.getRequestDispatcher("manager.html");
//					rs.include(req, res);
//				}
					

				res.getWriter().print(om.writeValueAsString(userRole));
				res.setStatus(200);

			} else {
				HttpSession ses = req.getSession(false);
				// Logout
				if (ses != null) {
					ses.invalidate(); // delete the session
				}
				res.setStatus(401);
				res.getWriter().print("Login Failed");
			}

		}
		
	}
	
}
