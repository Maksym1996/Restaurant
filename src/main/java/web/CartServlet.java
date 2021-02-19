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

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		RequestDispatcher dispatcher;

		Cart cart = (Cart) session.getAttribute(Param.CART);

		if (cart == null) {
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			return;
		}
		List<Product> products = cart.getProducts();
		if (products.isEmpty()) {
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			return;
		}
		Map<String, Integer> params = Validator.cartValidator(request.getParameter(Param.CHANGE),
				request.getParameter(Param.ID));
		int id = params.get(Param.ID);
		int change = params.get(Param.CHANGE);

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(Param.COUNT);

		if (null == count) {
			count = new HashMap<>();
			session.setAttribute(Param.COUNT, count);
			for (Product p : products) {
				count.put(p.getId(), 1);
			}
		} else if (0 != id && 0 != change && null != count.get(id)) {
			int value = count.get(id) + change;
			if (value <= 0) {
				value = 1;
			} else if (value >= 20) {
				value = 20;
			}
			count.put(id, value);
		}

		// realize delete product from cart
		int deleteId = Validator.intValidatorReturnInt(request.getParameter(Param.DELETE_ID));
		for (Product p : products) {
			if (p.getId() == deleteId) {
				products.remove(p);
				count.remove(p.getId());
				break;
			}

		}
		if (products.isEmpty()) {
			session.removeAttribute(Param.COUNT);
			dispatcher = request.getRequestDispatcher(Page.EMPTY_CART);
			dispatcher.forward(request, response);
			return;
		}

		int orderSumm = 0;

		for (Product p : products) {
			if (count.get(p.getId()) == null) {
				count.put(p.getId(), 1);
			}
			orderSumm += p.getPrice() * count.get(p.getId());
		}

		dispatcher = request.getRequestDispatcher(Page.CART_JSP);
		request.setAttribute(Param.PRODUCTS_LIST, products);
		request.setAttribute(Param.ORDER_SUMM, orderSumm);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String firstName = request.getParameter(Param.FIRST_NAME);
		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		String address = request.getParameter(Param.ADDRESS);
		String summ = request.getParameter(Param.SUMM);

		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();
		User user = (User) session.getAttribute(Param.USER);
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
		if (!Validator.intValidator(summ)) {
			response.sendError(416);
			return;
		}

		Cart cart = (Cart) session.getAttribute(Param.CART);
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.CART_JSP);

			int orderSumm = 0;
			for (Product p : cart.getProducts()) {
				orderSumm += p.getPrice();
			}
			request.setAttribute(Param.PRODUCTS_LIST, cart.getProducts());
			request.setAttribute(Param.ORDER_SUMM, orderSumm);

			request.setAttribute(Param.ERRORS, errors);

			dispatcher.forward(request, response);
			return;
		}

		// take user
		int userId = 0;
		if (user != null) {
			userId = user.getId();
		} else {
			UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
			try {
				for (User u : userDao.getUsersForManager()) {
					if (u.getPhoneNumber().equals(phoneNumber)) {
						userId = u.getId();
					}
				}
				if (userId == 0) {
					User model = Util.createUser(firstName, phoneNumber);
					userId = userDao.insertUser(model);
				}
			} catch (Exception e) {
				// TODO add some logger 05.02.2021
				response.sendError(500);
				return;
			}
		}

		// create order
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(Dao.ORDER_VIEW);
		List<Product> products = cart.getProducts();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(Param.COUNT);

		try {
			orderDao.insertOrder(Util.createOrder(Status.NEW, address, userId, summ), products, count);
		} catch (Exception e) {
			// TODO add some logger 05.02.2021
			response.sendError(500);
			return;
		}

		session.removeAttribute(Param.COUNT);
		session.removeAttribute(Param.CART);
		response.sendRedirect(Page.LOGIN_PAGE);

	}
}
