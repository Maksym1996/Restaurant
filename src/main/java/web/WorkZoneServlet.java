package web;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.OrderViewDao;
import db.dao.ReceiptDao;
import db.dao.UserDao;
import db.entity.UserWithPerformedOrders;
import db.entity.Receipt;
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

		ReceiptDao receiptDao = (ReceiptDao) request.getServletContext().getAttribute(Dao.RECEIPT);
		LOG.debug("orderViewDao");

		OrderPageProviderContainer pageProvidersContainer = new OrderPageProviderContainer(receiptDao);
		LOG.debug("pageProvidersContainer");

		OrderPageProvider pageProvider = pageProvidersContainer.getProvider(role);
		LOG.debug("pageProvider");

		if (pageProvider == null) {
			response.sendError(403);
			LOG.info(Log.REDIRECT + 403);

			return;
		}
		OrderPage orderPage = pageProvider.getOrderPage();
		LOG.trace("orderPage" + orderPage);
		String forwardPage = orderPage.getForwardPage();
		LOG.debug("forwardPage: " + forwardPage);

		List<Receipt> listOfReceipts = orderPage.getOrderViewList();

		// Display users with most count Performed orders
		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
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

		request.setAttribute(Param.RECEIPTS_LIST, listOfReceipts);
		request.setAttribute(Param.USERS_WITH_PERFORMED_ORDERS, sorterByCountUsersWithPerformedOrders);

		dispatcher.forward(request, response);
		LOG.info(Log.FORWARD + forwardPage);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		LOG.debug(Dao.ORDER_VIEW);

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
		response.sendRedirect(Page.WORK_ZONE);
		LOG.info(Log.REDIRECT + Page.WORK_ZONE);
	}
}
