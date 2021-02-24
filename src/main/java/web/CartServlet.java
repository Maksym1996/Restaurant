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

import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
import db.dao.OrderViewDao;
import db.dao.UserDao;
import db.entity.Product;
import db.entity.User;
import util.Cart;
import util.Status;
import util.Util;
import util.Validator;

/**
 * Servlet that implements the management of the grocery cart and customer
 * orders
 */
@WebServlet("/Cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(CartServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		HttpSession session = request.getSession(true);
		RequestDispatcher dispatcher;

		Cart cart = (Cart) session.getAttribute(ParamConst.CART);
		LOG.debug(ParamConst.CART + ": " + cart);

		if (cart == null) {
			dispatcher = request.getRequestDispatcher(PageConst.EMPTY_CART);
			dispatcher.forward(request, response);
			LOG.info(CommentConst.FORWARD + PageConst.EMPTY_CART);

			return;
		}
		List<Product> products = cart.getProducts();
		LOG.debug("Product list: " + cart);

		if (products.isEmpty()) {
			dispatcher = request.getRequestDispatcher(PageConst.EMPTY_CART);
			dispatcher.forward(request, response);
			LOG.info(CommentConst.FORWARD + PageConst.EMPTY_CART);

			return;
		}
		Map<String, Integer> params = Validator.cartValidator(request.getParameter(ParamConst.CHANGE),
				request.getParameter(ParamConst.ID));
		int id = params.get(ParamConst.ID);
		LOG.debug("Id: " + id);

		int change = params.get(ParamConst.CHANGE);
		LOG.debug("Change: " + change);

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(ParamConst.COUNT);

		if (count == null) {
			count = new HashMap<>();
			session.setAttribute(ParamConst.COUNT, count);
			for (Product p : products) {
				count.put(p.getId(), 1);
				LOG.debug("Name " + p.getName() + " count = " + count.get(p.getId()));
			}
		} else if (id != 0 && change != 0 && count.get(id) != null) {
			int value = count.get(id) + change;
			LOG.debug("value  = " + value);
			if (value <= 0) {
				value = 1;
			} else if (value >= 20) {
				value = 20;
			}
			count.put(id, value);
		}

		// realize delete product from cart
		int deleteId = Validator.intValidatorReturnInt(request.getParameter(ParamConst.DELETE_ID));
		LOG.debug("Delete Id: " + deleteId);
		for (Product p : products) {
			if (p.getId() == deleteId) {
				products.remove(p);
				count.remove(p.getId());
				LOG.debug("Remove from cart: " + p.getName());
				break;
			}

		}
		if (products.isEmpty()) {
			session.removeAttribute(ParamConst.COUNT);
			LOG.debug("Removed " + ParamConst.COUNT + " attribute from session");
			dispatcher = request.getRequestDispatcher(PageConst.EMPTY_CART);
			dispatcher.forward(request, response);
			LOG.info(CommentConst.FORWARD + PageConst.EMPTY_CART);
			return;
		}

		int orderSumm = 0;
		LOG.debug("Get order sum");
		for (Product p : products) {
			if (count.get(p.getId()) == null) {
				count.put(p.getId(), 1);
				LOG.debug("Remove from cart: " + p.getName());
			}
			orderSumm += p.getPrice() * count.get(p.getId());
		}
		LOG.debug("Order summ: " + orderSumm);
		dispatcher = request.getRequestDispatcher(PageConst.CART_JSP);
		request.setAttribute(ParamConst.PRODUCTS_LIST, products);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + products);

		request.setAttribute(ParamConst.ORDER_SUMM, orderSumm);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "Order summ = " + orderSumm);

		dispatcher.forward(request, response);
		LOG.info(CommentConst.FORWARD + PageConst.CART_JSP);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		String firstName = request.getParameter(ParamConst.FIRST_NAME);
		LOG.debug(ParamConst.FIRST_NAME + " " + firstName);

		String phoneNumber = request.getParameter(ParamConst.PHONE_NUMBER);
		LOG.debug(ParamConst.PHONE_NUMBER + " " + phoneNumber);

		String address = request.getParameter(ParamConst.ADDRESS);
		LOG.debug(ParamConst.ADDRESS + " " + address);

		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();
		User user = (User) session.getAttribute(ParamConst.USER);
		LOG.debug("User from session: " + user);
		if (user == null) {
			if (null == firstName || firstName.isEmpty()) {
				errors.put(ParamConst.FIRST_NAME, "Provide your first name");
			}
			if (null == phoneNumber || phoneNumber.isEmpty()) {
				errors.put(ParamConst.PHONE_NUMBER, "Provide your first name");
			} else if (!Pattern.matches(Validator.PHONE_NUMBER_PATTERN, phoneNumber)) {
				errors.put(ParamConst.PHONE_NUMBER, "The entered phone number is incorrect");
			}
		}

		if (null == address || address.isEmpty()) {
			errors.put(ParamConst.ADDRESS, "Indicate the address where the delivery will be");
		}

		Cart cart = (Cart) session.getAttribute(ParamConst.CART);
		if (cart == null) {
			response.sendError(400);
			LOG.info(CommentConst.REDIRECT + 400);

			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.CART_JSP);

			int orderSumm = 0;
			for (Product p : cart.getProducts()) {
				orderSumm += p.getPrice();
			}
			request.setAttribute(ParamConst.PRODUCTS_LIST, cart.getProducts());
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + cart.getProducts());

			request.setAttribute(ParamConst.ORDER_SUMM, orderSumm);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "Order summ: " + orderSumm);

			request.setAttribute(ParamConst.ERRORS, errors);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + errors);

			dispatcher.forward(request, response);
			LOG.info(CommentConst.FORWARD + PageConst.CART_JSP);

			return;
		}

		// take user
		int userId = 0;
		if (user != null) {
			userId = user.getId();
			LOG.debug("UserId: " + userId);
		} else {
			UserDao userDao = (UserDao) request.getServletContext().getAttribute(DaoConst.USER);
			try {
				user = userDao.getUserByNumber(phoneNumber);
				if (user == null) {
					user = Util.createUser(firstName, phoneNumber);
					userId = userDao.insertUser(user);
					LOG.debug("User id: " + userId);
				} else {
					userId = user.getId();
					LOG.debug("User id: " + userId);
				}
			} catch (Exception e) {
				response.sendError(500);
				LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
				LOG.info(CommentConst.REDIRECT + 500);

				return;
			}
		}

		// create order
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute(DaoConst.ORDER_VIEW);
		LOG.debug("OrderDao " + orderDao);
		List<Product> products = cart.getProducts();

		@SuppressWarnings("unchecked")
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(ParamConst.COUNT);
		LOG.debug("Count from session" + count);

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
			response.sendError(500);
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);

			return;
		}

		session.removeAttribute(ParamConst.COUNT);
		LOG.debug("Remove " + ParamConst.COUNT + " from session");

		session.removeAttribute(ParamConst.CART);
		LOG.debug("Remove " + ParamConst.CART + " from session");

		response.sendRedirect(PageConst.LOGIN_PAGE);
		LOG.info(CommentConst.REDIRECT + PageConst.LOGIN_PAGE);

	}
}
