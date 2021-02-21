package web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
import db.dao.UserDao;
import db.entity.Product;
import db.entity.User;
import util.Cart;
import util.Status;
import util.Util;
import util.Validator;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet("/Cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(CartServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		HttpSession session = request.getSession(true);
		RequestDispatcher dispatcher;

		Cart cart = (Cart) session.getAttribute(Param.CART);
		log.debug(Param.CART + ": " + cart);
		if (cart == null) {
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.EMPTY_CART);
			return;
		}
		List<Product> products = cart.getProducts();
		log.debug("Product list: " + cart);
		if (products.isEmpty()) {
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.EMPTY_CART);
			return;
		}
		Map<String, Integer> params = Validator.cartValidator(request.getParameter(Param.CHANGE),
				request.getParameter(Param.ID));
		int id = params.get(Param.ID);
		int change = params.get(Param.CHANGE);
		log.debug("Id: " + id);
		log.debug("Change: " + change);

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(Param.COUNT);

		if (count == null) {
			count = new HashMap<>();
			session.setAttribute(Param.COUNT, count);
			for (Product p : products) {
				count.put(p.getId(), 1);
				log.debug("Name " + p.getName() + " count = " + count.get(p.getId()));
			}
		} else if (id != 0 && change != 0 && count.get(id) != null) {
			int value = count.get(id) + change;
			log.debug("value  = " + value);
			if (value <= 0) {
				value = 1;
			} else if (value >= 20) {
				value = 20;
			}
			count.put(id, value);
		}

		// realize delete product from cart
		int deleteId = Validator.intValidatorReturnInt(request.getParameter(Param.DELETE_ID));
		log.debug("Delete Id: " + deleteId);
		for (Product p : products) {
			if (p.getId() == deleteId) {
				products.remove(p);
				count.remove(p.getId());
				log.debug("Remove from cart: " + p.getName());
				break;
			}

		}
		if (products.isEmpty()) {
			session.removeAttribute(Param.COUNT);
			log.debug("Remove " + Param.COUNT + " from session");
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.EMPTY_CART);
			return;
		}

		int orderSumm = 0;
		log.debug("Get order sum");
		for (Product p : products) {
			if (count.get(p.getId()) == null) {
				count.put(p.getId(), 1);
				log.debug("Remove from cart: " + p.getName());
			}
			orderSumm += p.getPrice() * count.get(p.getId());
		}
		log.debug("Order summ: " + orderSumm);
		dispatcher = request.getRequestDispatcher(Page.CART_JSP);
		request.setAttribute(Param.PRODUCTS_LIST, products);
		request.setAttribute(Param.ORDER_SUMM, orderSumm);
		log.info(Comment.FORWARD + Page.CART_JSP);
		log.debug(Comment.FORWARD_WITH_PARAMETR + products);
		log.debug(Comment.FORWARD_WITH_PARAMETR + "Order summ = " + orderSumm);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);

		String firstName = request.getParameter(Param.FIRST_NAME);
		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		String address = request.getParameter(Param.ADDRESS);

		log.debug(Param.FIRST_NAME + " " + firstName);
		log.debug(Param.PHONE_NUMBER + " " + phoneNumber);
		log.debug(Param.ADDRESS + " " + address);

		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();
		User user = (User) session.getAttribute(Param.USER);
		log.debug("User from session: " + user);
		if (user == null) {
			if (null == firstName || firstName.isEmpty()) {
				errors.put(Param.FIRST_NAME, "Provide your first name");
			}
			if (null == phoneNumber || phoneNumber.isEmpty()) {
				errors.put(Param.PHONE_NUMBER, "Provide your first name");
			} else if (!Pattern.matches(Validator.PHONE_NUMBER_PATTERN, phoneNumber)) {
				errors.put(Param.PHONE_NUMBER, "The entered phone number is incorrect");
			}
		}

		if (null == address || address.isEmpty()) {
			errors.put(Param.ADDRESS, "Indicate the address where the delivery will be");
		}

		Cart cart = (Cart) session.getAttribute(Param.CART);
		if (cart == null) {
			log.info(Comment.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.CART_JSP);

			int orderSumm = 0;
			for (Product p : cart.getProducts()) {
				orderSumm += p.getPrice();
			}
			request.setAttribute(Param.PRODUCTS_LIST, cart.getProducts());
			request.setAttribute(Param.ORDER_SUMM, orderSumm);
			request.setAttribute(Param.ERRORS, errors);
			log.info(Comment.FORWARD + Page.CART_JSP);
			log.debug(Comment.FORWARD_WITH_PARAMETR + "Order summ: " + orderSumm);
			log.debug(Comment.FORWARD_WITH_PARAMETR + cart.getProducts());
			log.debug(Comment.FORWARD_WITH_PARAMETR + errors);
			dispatcher.forward(request, response);
			return;
		}

		// take user
		int userId = 0;
		if (user != null) {
			userId = user.getId();
			log.debug("User id: " + userId);
		} else {
			UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
			try {
				user = userDao.getUserByNumber(phoneNumber);
				if (user == null) {
					user = Util.createUser(firstName, phoneNumber);
					userId = userDao.insertUser(user);
					log.debug("User id: " + userId);
				} else {
					userId = user.getId();
					log.debug("User id: " + userId);
				}
			} catch (Exception e) {
				log.error(Comment.DB_EXCEPTION + e.getMessage());
				log.info(Comment.REDIRECT + 500);
				response.sendError(500);
				return;
			}
		}

		// create order
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		log.debug("OrderDao " + orderDao);
		List<Product> products = cart.getProducts();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(Param.COUNT);
		log.debug("Count from session" + count);

		int orderSumm = 0;

		for (Product p : products) {
			if (count.get(p.getId()) == null) {
				count.put(p.getId(), 1);
			}
			orderSumm += p.getPrice() * count.get(p.getId());
		}

		try {
			orderDao.insertOrder(Util.createOrder(Status.NEW, address, userId, orderSumm), products, count);
		} catch (Exception e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		session.removeAttribute(Param.COUNT);
		log.debug("Remove " + Param.COUNT + " from session");
		session.removeAttribute(Param.CART);
		log.debug("Remove " + Param.CART + " from session");

		log.info(Comment.REDIRECT + Page.LOGIN_PAGE);
		response.sendRedirect(Page.LOGIN_PAGE);

	}
}
