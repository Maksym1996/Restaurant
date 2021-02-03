package web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.UserDao;
import db.entity.User;

/**
 * Servlet implementation class LoginPage
 */
@WebServlet("/Login page")
public class LoginPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		 HttpSession session = request.getSession();
	        String forwardPage;
	        String logout = request.getParameter("logout");

	        if("logout".equals(logout)) {

	           session.invalidate();

	            forwardPage = "Login page.jsp";
	        } else if (session == null || session.getAttribute("user") == null) {
	               forwardPage = "Login page.jsp";
	        } else {
	               User user = (User) session.getAttribute("user");
	               session.setAttribute("role", user.getRole());
	               forwardPage = "Account.jsp";

	           }

	        RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
	        dispatcher.forward(request, response);
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        UserDao userDao = (UserDao)request.getServletContext().getAttribute("userDao");
        User user = null;
		try {
			user = userDao.getUser(email, password);
		} catch (Exception e) {
			//TODO add some logger 03.02.2021
			response.sendRedirect("SomeWrong.jsp");
		}
        Map<String,String>errors = new HashMap<>();
        
        if(user == null || user.getId()==0) {
        	errors.put("errors", "Entered Email or Password is incorrectly");
        	RequestDispatcher dispatcher = request.getRequestDispatcher("Login page.jsp");
        	request.setAttribute("errors", errors);
        	dispatcher.forward(request, response);
        	return;
        }
        
        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());
        RequestDispatcher dispatcher = request.getRequestDispatcher("Account.jsp");
        dispatcher.forward(request, response);
        
   
	}

}
