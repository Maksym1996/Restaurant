package web;

import java.io.IOException;
import java.util.ArrayList;
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import db.entity.Product;
import db.entity.User;
import exception.DBException;
import util.Validator;

/**
 * Ыervlet that implements the functionality of authorizing and displaying a
 * custom page
 */
@WebServlet("/Login page")
public class LoginPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(LoginPageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		HttpSession session = request.getSession(true);
		String forwardPage;

		String logout = request.getParameter(ParamConst.LOG_OUT);
		LOG.debug("logout " + logout);

		if (ParamConst.LOG_OUT.equals(logout)) {
			session.invalidate();
			LOG.debug("session invalidate");
			forwardPage = PageConst.LOGIN_PAGE_JSP;
		} else if (session == null || session.getAttribute(ParamConst.USER) == null) {
			LOG.debug("session is null or user from session is null");
			forwardPage = PageConst.LOGIN_PAGE_JSP;
		} else {
			User user = (User) session.getAttribute(ParamConst.USER);
			LOG.debug("Get user from session -> " + user.toString());

			session.setAttribute(ParamConst.ROLE, user.getRole());
			LOG.debug("Set role = " + user.getRole() + " to session");

			ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
			OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(DaoConst.ORDER_VIEW);

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
				response.sendError(500);
				LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
				LOG.info(CommentConst.REDIRECT + 500);

				return;
			}
			request.setAttribute(ParamConst.ORDER_VIEW_LIST, orderViewList);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "orderViewList " + orderViewList);

			request.setAttribute(ParamConst.PRODUCTS_LIST, productList);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "productList " + productList);

			request.setAttribute(ParamConst.ORDERS, orders);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "orders " + orders);

			forwardPage = PageConst.ACCOUNT_JSP;
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		dispatcher.forward(request, response);
		LOG.info(CommentConst.FORWARD + forwardPage);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		HttpSession session = request.getSession(true);

		String phoneNumber = request.getParameter(ParamConst.PHONE_NUMBER);
		LOG.debug("phoneNumber " + phoneNumber);

		String password = request.getParameter(ParamConst.PASSWORD);
		LOG.debug("password " + password);

		Map<String, String> errors = Validator.authorizationValidator(phoneNumber, password);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(DaoConst.USER);
		User user = null;
		try {
			user = userDao.getUserByNumberAndPass(phoneNumber, password);
			LOG.debug("getUserByNumberAndPass: " + user);
			if (user == null) {
				errors.put(ParamConst.NO_USER, "User with such data does not exist");
			} else {
				session.setAttribute(ParamConst.USER, user);
				LOG.debug("Set user to session");

				session.setAttribute(ParamConst.ROLE, user.getRole());
				LOG.debug("Set role to session: " + user.getRole());
			}
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);

			return;
		}
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.LOGIN_PAGE_JSP);
			request.setAttribute(ParamConst.ERRORS, errors);
			LOG.info(CommentConst.FORWARD + PageConst.LOGIN_PAGE_JSP);

			dispatcher.forward(request, response);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "errors " + errors);
			return;
		}
		response.sendRedirect(PageConst.LOGIN_PAGE);
		LOG.info(CommentConst.REDIRECT + PageConst.LOGIN_PAGE);
	}

}
