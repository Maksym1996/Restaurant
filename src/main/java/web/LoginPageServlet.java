package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import consts.Dao;
import consts.Params;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import db.entity.Product;
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

		if ("logout".equals(logout)) {
			session.invalidate();

			forwardPage = "Login page.jsp";
		} else if (session == null || session.getAttribute("user") == null) {
			forwardPage = "Login page.jsp";
		} else {
			User user = (User) session.getAttribute("user");
			session.setAttribute(Params.ROLE, user.getRole());
			ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
			OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
			Set<Product> productList = new HashSet<>();
			Set<OrderView> orders = new LinkedHashSet<>();
			List<OrderView> orderViewList = new ArrayList<>();
			try {
				orderViewList = orderDao.getOrderViewsByUserId(user.getId());
				for (OrderView o : orderViewList) {
				 	orders.add(o);
					
					productList.add(productDao.getProductById(o.getProductId()));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IOException();
			}
			request.setAttribute("orderViewList", orderViewList);
			request.setAttribute("productList", productList);
			request.setAttribute("orders", orders);

			forwardPage = "Account.jsp";
		}
	
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String phoneNumber = request.getParameter("phoneNumber");
		String password = request.getParameter("password");

		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
		User user = null;
		try {
			user = userDao.getUserByNumberAndPass(phoneNumber, password);
		} catch (Exception e) {
			// TODO add some logger 03.02.2021
			throw new IOException();
		}
		Map<String, String> errors = new HashMap<>();

		if (user == null || user.getId() == 0) {
			errors.put("errors", "Entered Phone number or Password is incorrectly");
			RequestDispatcher dispatcher = request.getRequestDispatcher("Login page.jsp");
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		session.setAttribute("role", user.getRole());
		response.sendRedirect("Login page");

	}

}
