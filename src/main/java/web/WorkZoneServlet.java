package web;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.DaoConst;
import consts.PageConst;
import consts.Param;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import db.entity.UserWithPerformedOrders;
import db.entity.Product;
import db.entity.User;
import exception.DBException;
import provider.OrderPage;
import provider.OrderPageProvider;
import provider.OrderPageProviderContainer;
import util.Status;
import util.UserRole;
import util.Validator;

/**
 * Servlet that implements the functionality, display, and order tracking
 */
@WebServlet("/WorkZone")
public class WorkZoneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(WorkZoneServlet.class);

	private static final Comparator<UserWithPerformedOrders> USERS_BY_ORDERS_COUNT = Comparator
			.comparing(UserWithPerformedOrders::getCountOrders).reversed();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		HttpSession session = request.getSession(true);
		UserRole role = (UserRole) session.getAttribute(Param.ROLE);
		LOG.debug("Role from session" + role);

		OrderViewDao orderViewDao = (OrderViewDao) request.getServletContext().getAttribute(DaoConst.ORDER_VIEW);
		LOG.debug("orderViewDao");

		OrderPageProviderContainer pageProvidersContainer = new OrderPageProviderContainer(orderViewDao);
		LOG.debug("pageProvidersContainer");

		OrderPageProvider pageProvider = pageProvidersContainer.getProvider(role);
		LOG.debug("pageProvider");

		if (pageProvider == null) {
			response.sendError(403);
			LOG.info(Log.REDIRECT + 403);

			return;
		}

		OrderPage orderPage = pageProvider.getOrderPage();
		LOG.debug("orderPage" + orderPage);

		List<OrderView> orderViewList = orderPage.getOrderViewList();
		String forwardPage = orderPage.getForwardPage();
		LOG.debug("forwardPage: " + forwardPage);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		LOG.debug(DaoConst.PRODUCT);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(DaoConst.USER);
		LOG.debug(DaoConst.USER);

		Set<Product> productList = new HashSet<>();
		Set<User> userList = new HashSet<>();
		Set<OrderView> orders = new LinkedHashSet<>();
		for (OrderView o : orderViewList) {
			try {
				orders.add(o);
				productList.add(productDao.getProductById(o.getProductId()));
				userList.add(userDao.getUserById(o.getUserId()));

			} catch (DBException e) {
				response.sendError(500);
				LOG.error(Log.DB_EXCEPTION + e.getMessage());
				LOG.info(Log.REDIRECT + 500);
				return;
			}
		}

		List<UserWithPerformedOrders> usersWithPerformedOrders = null;
		try {
			usersWithPerformedOrders = userDao.getUserAndHimCountPerformedOrders();
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}

		List<UserWithPerformedOrders> sorterByCountUsersWithPerformedOrders = usersWithPerformedOrders.stream()
				.sorted(USERS_BY_ORDERS_COUNT).limit(2).collect(Collectors.toList());

		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		request.setAttribute(Param.ORDER_VIEW_LIST, orderViewList);
		LOG.debug("Order list: " + orderViewList);

		request.setAttribute(Param.USERS_WITH_PERFORMED_ORDERS, sorterByCountUsersWithPerformedOrders);

		request.setAttribute(Param.PRODUCTS_LIST, productList);
		LOG.debug("Product list" + productList);

		request.setAttribute(Param.USER_LIST, userList);
		LOG.debug("User list: " + userList);

		request.setAttribute(Param.ORDERS, orders);
		LOG.debug("Orders: " + orders);

		dispatcher.forward(request, response);
		LOG.info(Log.FORWARD + forwardPage);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(DaoConst.ORDER_VIEW);
		LOG.debug(DaoConst.ORDER_VIEW);

		String status = request.getParameter(Param.STATUS);
		LOG.debug("Status: " + status);

		String id = request.getParameter(Param.ID);
		LOG.debug("Id " + id);

		if (!Validator.workZoneValidator(status, id)) {
			response.sendError(400);
			LOG.debug("Request paraments is invalid");
			LOG.info(Log.REDIRECT + 400);

			return;
		}

		Status requestStatus = Status.valueOf(status);
		int orderId = Integer.parseInt(id);

		Status resultStatus;
		try {
			Status currentStatus = Status.valueOf(orderDao.getStatusByOrderId(orderId));
			LOG.debug("Current status " + currentStatus);
			if (!requestStatus.equals(Status.REJECTED) && requestStatus.equals(currentStatus)) {
				resultStatus = currentStatus.getNextStatuses().get(0);
				LOG.debug("Result status " + resultStatus);
			} else {
				resultStatus = currentStatus.getNextStatuses().get(1);
				LOG.debug("Result status " + resultStatus);
			}
			orderDao.updateStatusById(orderId, resultStatus.name());
			LOG.debug("Update status by id: " + orderId + " " + resultStatus.name());
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}
		response.sendRedirect(PageConst.WORK_ZONE);
		LOG.info(Log.REDIRECT + PageConst.WORK_ZONE);
	}
}
