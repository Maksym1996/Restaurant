package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.OrderDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.Order;
import db.entity.Product;
import db.entity.User;

/**
 * Servlet implementation class WorkZoneServlet
 */
@WebServlet("/WorkZone")
public class WorkZoneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String status = request.getParameter("status");
		String id = request.getParameter("id");
		
		List<String> stat = new ArrayList<>();
		stat.add("NEW");
		stat.add("APPROVAL");
		stat.add("COOKING");
		stat.add("COOKED");
		stat.add("IN_DELIVERY");
		stat.add("DELIVERED AND PAID");
		stat.add("CLOSED");

		if (!"DECLINE".equals(status) || !"CLOSED".equals(status)) {
			status = stat.get(stat.indexOf(status) + 1);
		}

		HttpSession session = request.getSession(true);
		String role = (String) session.getAttribute("role");
		OrderDao orderDao = (OrderDao) request.getServletContext().getAttribute("orderDao");
		try {
			orderDao.updateOrderState(Integer.parseInt(id), status);
		} catch (Exception e2) {
			//TODO add logger 09.02
		}
		
		
		List<Order> orderList = new ArrayList<>();
		String forwardPage;
		switch (role) {
		case "MANAGER":
			try {
				orderList = orderDao.getAllOrders();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardPage = "Manager.jsp";
			break;
		case "COOK":
			try {
				orderList = orderDao.getOrdersByStatus("APPROVAL");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			forwardPage = "Cook.jsp";
			break;
		case "DELIVERY":
			try {
				orderList = orderDao.getOrdersByStatus("COOKED");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardPage = "Delivery.jsp";
			break;
		case "CLIENT":
			response.sendError(403);
			return;
		default:
			response.sendError(401);
			return;
		}
		Set<Product> productList = new HashSet<>();
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		for (Order o : orderList) {
			try {
				productList.add(productDao.getProduct(o.getProductId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Set<User> userList = new HashSet<>();
		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
		for (Order o : orderList) {
			try {
				userList.add(userDao.getUser(o.getId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		request.setAttribute("orderList", orderList);
		request.setAttribute("productList", productList);
		request.setAttribute("userList", userList);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
