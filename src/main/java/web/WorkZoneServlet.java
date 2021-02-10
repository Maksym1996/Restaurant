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

import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
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

		if (status != null && !"DECLINE".equals(status) && !"CLOSED".equals(status)) {

			List<String> stat = new ArrayList<>();
			stat.add("NEW");
			stat.add("APPROVAL");
			stat.add("COOKING");
			stat.add("COOKED");
			stat.add("IN_DELIVERY");
			stat.add("DELIVERED_AND_PAID");
			stat.add("CLOSED");
			status = stat.get(stat.indexOf(status) + 1);
		}

		HttpSession session = request.getSession(true);
		String role = (String) session.getAttribute("role");
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute("orderDao");
		try {
			orderDao.updateOrderState(Integer.parseInt(id), status);
		} catch (Exception e2) {
			// TODO add logger 09.02
		}

		List<OrderView> orderViewList = new ArrayList<>();
		String forwardPage;
		switch (role) {
		case "MANAGER":
			try {
				orderViewList = orderDao.getAllOrders();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			forwardPage = "Manager.jsp";
			break;
		case "COOK":
			try {
				orderViewList = orderDao.getOrdersByStatus("APPROVAL");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			forwardPage = "Cook.jsp";
			break;
		case "DELIVERY":
			try {
				orderViewList = orderDao.getOrdersByStatus("COOKED");
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
		
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
		
		Set<Product> productList = new HashSet<>();
		Set<User> userList = new HashSet<>();
		Set<OrderView> orders = new HashSet<>();
	
		for (OrderView o : orderViewList) {
			try {
				orders.add(o);
				productList.add(productDao.getProduct(o.getProductId()));
				userList.add(userDao.getUser(o.getUserId()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		request.setAttribute("orderViewList", orderViewList);
		request.setAttribute("productList", productList);
		request.setAttribute("userList", userList);
		request.setAttribute("orders", orders);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
