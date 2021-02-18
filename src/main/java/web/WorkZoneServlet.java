package web;

import java.io.IOException;
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

import consts.Dao;
import consts.ForwardPages;
import consts.Params;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import db.entity.Product;
import db.entity.User;
import exception.DBException;
import provider.OrderPage;
import provider.OrderPageProvider;
import provider.OrderPageProviderContainer;
import util.Status;
import util.Validator;

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
		String role = (String) session.getAttribute(Params.ROLE);

		OrderViewDao orderViewDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		OrderPageProviderContainer pageProvidersContainer = new OrderPageProviderContainer(orderViewDao);
		OrderPageProvider pageProvider = pageProvidersContainer.getProvider(role);
		if (pageProvider == null) {
			response.sendError(403);
			return;
		}

		OrderPage orderPage = pageProvider.getOrderPage();

		List<OrderView> orderViewList = orderPage.getOrderViewList();
		String forwardPage = orderPage.getForwardPage();

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);

		Set<Product> productList = new HashSet<>();
		Set<User> userList = new HashSet<>();
		Set<OrderView> orders = new LinkedHashSet<>();
		for (OrderView o : orderViewList) {
			try {
				orders.add(o);
				productList.add(productDao.getProductById(o.getProductId()));
				userList.add(userDao.getUserById(o.getUserId()));

			} catch (DBException e) {
				// TODO Auto-generated catch block
				throw new IOException();
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		request.setAttribute(Params.ORDER_VIEW_LIST, orderViewList);
		request.setAttribute(Params.PRODUCTS_LIST, productList);
		request.setAttribute(Params.USER_LIST, userList);
		request.setAttribute(Params.ORDERS, orders);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);

		String status = request.getParameter(Params.STATUS);
		String id = request.getParameter(Params.ID);
		if (!Validator.workZoneValidator(status, id)) {
			response.sendError(400);
			return;
		}

		Status requestStatus = Status.valueOf(status);
		int orderId = Integer.parseInt(id);

		Status resultStatus;
		try {
			Status currentStatus = Status.valueOf(orderDao.getStatusByOrderId(orderId));
			if (!requestStatus.equals(Status.REJECTED) && requestStatus.equals(currentStatus)) {
				resultStatus = currentStatus.getNextStatuses().get(0);
			} else {
				resultStatus = currentStatus.getNextStatuses().get(1);
			}
			orderDao.updateStatusById(orderId, resultStatus.name());
		} catch (DBException e) {
			System.err.println("GetState:" + e);
			response.sendError(500);
			return;
		}
		response.sendRedirect(ForwardPages.WORK_ZONE);
	}
}
