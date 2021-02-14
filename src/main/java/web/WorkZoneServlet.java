package web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
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

		HttpSession session = request.getSession(true);
		String role = (String) session.getAttribute("role");
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute("orderDao");

		List<OrderView> orderViewList = new ArrayList<>();
		String forwardPage;
		switch (role) {
		case "MANAGER":
			try {
				orderViewList = orderDao.getAllOrders();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IOException();
			}
			forwardPage = "Manager.jsp";
			break;
		case "COOK":
			try {
				orderViewList = orderDao.getOrdersByStatus("COOKING");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				throw new IOException();
			}
			forwardPage = "Cook.jsp";
			break;
		case "COURIER":
			try {
				orderViewList = orderDao.getOrdersByStatus("IN_DELIVERY");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IOException();
			}
			forwardPage = "Delivery.jsp";
			break;
		default:
			response.sendError(403);
			return;

		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");

		Set<Product> productList = new HashSet<>();
		Set<User> userList = new HashSet<>();
		Set<OrderView> orders = new LinkedHashSet<>();
		for (OrderView o : orderViewList) {
			try {
				orders.add(o);
				productList.add(productDao.getProduct(o.getProductId()));
				userList.add(userDao.getUser(o.getUserId()));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				throw new IOException();
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

		String status = request.getParameter("status");
		String id = request.getParameter("id");

		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute("orderDao");

		if (id != null && status != null && !"REJECTED".equals(status) && !"PERFORMED".equals(status)) {

			List<String> statusList = new ArrayList<>();
			statusList.add("NEW");
			statusList.add("COOKING");
			statusList.add("COOKED");
			statusList.add("IN_DELIVERY");
			statusList.add("DELIVERED_AND_PAID");
			statusList.add("PERFORMED");
			statusList.add("REJECTED");
			int indexStatus = statusList.indexOf(status);
			int orderId = Integer.parseInt(id);
			try {
				int currentIndexStatus = statusList.indexOf(orderDao.getStateByOrderId(orderId));
				if (indexStatus != currentIndexStatus) {
					response.sendError(400);
					return;
				}
				status = statusList.get(statusList.indexOf(status) + 1);
				orderDao.updateOrderState(orderId, status);
			} catch (Exception e2) {
				// TODO add logger 09.02
				throw new IOException();
			}
		}

		response.sendRedirect("WorkZone");
	}

}
