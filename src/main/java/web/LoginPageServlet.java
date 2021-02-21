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
import util.Validator;

/**
 * Servlet implementation class LoginPage
 */
@WebServlet("/Login page")
public class LoginPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(LoginPageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		HttpSession session = request.getSession(true);
		String forwardPage;
		String logout = request.getParameter(Param.LOG_OUT);
		log.debug("logout " + logout);

		if (Param.LOG_OUT.equals(logout)) {
			session.invalidate();
			log.debug("session invalidate");
			forwardPage = Page.LOGIN_PAGE_JSP;
		} else if (session == null || session.getAttribute(Param.USER) == null) {
			log.debug("session is null or user from session is null");
			forwardPage = Page.LOGIN_PAGE_JSP;
		} else {
			User user = (User) session.getAttribute(Param.USER);
			session.setAttribute(Param.ROLE, user.getRole());
			log.debug("Set role = " + user.getRole() + " to session");
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
				log.error(Comment.DB_EXCEPTION + e.getMessage());
				log.info(Comment.REDIRECT + 500);
				response.sendError(500);
				return;
			}
			request.setAttribute(Param.ORDER_VIEW_LIST, orderViewList);
			request.setAttribute(Param.PRODUCTS_LIST, productList);
			request.setAttribute(Param.ORDERS, orders);

			log.debug(Comment.FORWARD_WITH_PARAMETR + "orderViewList " + orderViewList);
			log.debug(Comment.FORWARD_WITH_PARAMETR + "productList " + productList);
			log.debug(Comment.FORWARD_WITH_PARAMETR + "orders " + orders);

			forwardPage = Page.ACCOUNT_JSP;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		log.info(Comment.FORWARD + forwardPage);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		HttpSession session = request.getSession(true);

		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		log.debug("phoneNumber " + phoneNumber);
		String password = request.getParameter(Param.PASSWORD);
		log.debug("password " + password);

		Map<String, String> errors = Validator.authorizationValidator(phoneNumber, password);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
		User user = null;
		try {
			user = userDao.getUserByNumberAndPass(phoneNumber, password);
			log.debug("getUserByNumberAndPass: " + user);
			if (user == null) {
				errors.put(Param.NO_USER, "User with such data does not exist");
			} else {
				session.setAttribute(Param.USER, user);
				log.debug("Set user to session");
				session.setAttribute(Param.ROLE, user.getRole());
				log.debug("Set role to session: " + user.getRole());
			}
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.LOGIN_PAGE_JSP);
			request.setAttribute(Param.ERRORS, errors);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.LOGIN_PAGE_JSP);
			log.debug(Comment.FORWARD_WITH_PARAMETR + "errors " + errors);
			return;
		}
		log.info(Comment.REDIRECT + Page.LOGIN_PAGE);
		response.sendRedirect(Page.LOGIN_PAGE);
	}

}
