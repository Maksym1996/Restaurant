package web;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

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
import util.Status;

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

		List<OrderView> orderViewList;
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

		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute("orderDao");

		String idPattern = "[1-9]{1}[0-9]*";

		String status = request.getParameter("status");
		String requestId = request.getParameter("id");

		if (status == null || status.isEmpty() || requestId == null || requestId.isEmpty()) {
			response.sendError(416);
			return;
		}
		if (!Pattern.matches(idPattern, requestId)) {
			response.sendError(415);
			return;
		}
		// if illegalStateException or NumberFormatException will be redirect to
		// Wrong.jsp
		Status requestStatus = Status.valueOf(status);
		int orderId = Integer.parseInt(requestId);

		Status resultStatus;
		try {
			Status currentStatus = Status.valueOf(orderDao.getStateByOrderId(orderId));
			if (!requestStatus.equals(Status.REJECTED) && requestStatus.equals(currentStatus)) {
				resultStatus = currentStatus.getNextStatuses().get(0);
			} else {
				resultStatus = currentStatus.getNextStatuses().get(1);
			}
			orderDao.updateOrderState(orderId, resultStatus.name());
		} catch (Exception e) {
			System.err.println("GetState:" + e);
			response.sendError(500);
			return;
		}
		response.sendRedirect("WorkZone");
	}
}
