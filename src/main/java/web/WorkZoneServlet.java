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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import consts.Dao;
import consts.Page;
import consts.Param;
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
import util.UserRole;
import util.Validator;

/**
 * Servlet that implements the functionality, display, and order tracking
 */
@WebServlet("/WorkZone")
public class WorkZoneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(WorkZoneServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		HttpSession session = request.getSession(true);
		UserRole role = (UserRole) session.getAttribute(Param.ROLE);
		log.debug("Role from session" + role);

		OrderViewDao orderViewDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		log.debug("orderViewDao");
		OrderPageProviderContainer pageProvidersContainer = new OrderPageProviderContainer(orderViewDao);
		log.debug("pageProvidersContainer");
		OrderPageProvider pageProvider = pageProvidersContainer.getProvider(role);
		log.debug("pageProvider");
		if (pageProvider == null) {
			log.info(Comment.REDIRECT + 403);
			response.sendError(403);
			return;
		}

		OrderPage orderPage = pageProvider.getOrderPage();
		log.debug("orderPage" + orderPage);

		List<OrderView> orderViewList = orderPage.getOrderViewList();
		String forwardPage = orderPage.getForwardPage();

		log.debug("forwardPage: " + forwardPage);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		log.debug(Dao.PRODUCT);
		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
		log.debug(Dao.USER);
		Set<Product> productList = new HashSet<>();
		Set<User> userList = new HashSet<>();
		Set<OrderView> orders = new LinkedHashSet<>();
		for (OrderView o : orderViewList) {
			try {
				orders.add(o);
				productList.add(productDao.getProductById(o.getProductId()));
				userList.add(userDao.getUserById(o.getUserId()));

			} catch (DBException e) {
				log.error(Comment.DB_EXCEPTION + e.getMessage());
				log.info(Comment.REDIRECT + 500);
				response.sendError(500);
				return;
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		request.setAttribute(Param.ORDER_VIEW_LIST, orderViewList);
		request.setAttribute(Param.PRODUCTS_LIST, productList);
		request.setAttribute(Param.USER_LIST, userList);
		request.setAttribute(Param.ORDERS, orders);
		log.info(Comment.FORWARD + forwardPage);
		log.debug("Order list: " + orderViewList);
		log.debug("Product list" + productList);
		log.debug("User list: " + userList);
		log.debug("Orders: " + orders);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		log.debug(Dao.ORDER_VIEW);

		String status = request.getParameter(Param.STATUS);
		String id = request.getParameter(Param.ID);
		log.debug("Status: " + status);
		log.debug("Id " + id);

		if (!Validator.workZoneValidator(status, id)) {
			log.debug("Request paraments is invalid");
			log.info(Comment.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		Status requestStatus = Status.valueOf(status);
		int orderId = Integer.parseInt(id);

		Status resultStatus;
		try {
			Status currentStatus = Status.valueOf(orderDao.getStatusByOrderId(orderId));
			log.debug("Current status " + currentStatus);
			if (!requestStatus.equals(Status.REJECTED) && requestStatus.equals(currentStatus)) {
				resultStatus = currentStatus.getNextStatuses().get(0);
				log.debug("Result status " + resultStatus);
			} else {
				resultStatus = currentStatus.getNextStatuses().get(1);
				log.debug("Result status " + resultStatus);
			}
			orderDao.updateStatusById(orderId, resultStatus.name());
			log.debug("Update status by id: " + orderId + " " + resultStatus.name());
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		log.info(Comment.REDIRECT + Page.WORK_ZONE);
		response.sendRedirect(Page.WORK_ZONE);
	}
}
