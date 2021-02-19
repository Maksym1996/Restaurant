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

import consts.Page;
import consts.Param;
import db.dao.OrderViewDao;
import db.dao.UserDao;
import db.entity.Product;
import db.entity.User;
import util.Cart;
import util.Status;
import util.Util;

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

		String changeId = request.getParameter(Param.ID);
		int change = Param.INC.equals(request.getParameter(Param.CHANGE)) ? 1
				: Param.DEC.equals(request.getParameter(Param.CHANGE)) ? -1 : 0;

		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute(Param.COUNT);

		if (count == null) {
			count = new HashMap<>();
			session.setAttribute(Param.COUNT, count);
			for (Product p : products) {
				count.put(p.getId(), 1);
			}
		} else if (changeId != null && change != 0) {
			int id = Integer.parseInt(changeId);
			if (count.get(id) != null) {

				int value = count.get(id) + change;
				if (value <= 0) {
					value = 1;
				} else if (value >= 20) {
					value = 20;
				}
				count.put(id, value);
			}
		}

		// realize delete product from cart
		String deleteId = request.getParameter(Param.DELETE_ID);
		if (deleteId != null) {
			for (Product p : products) {
				if (p.getId() == Integer.parseInt(deleteId)) {
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
		String phoneNumberRegex = "[0][0-9]{9}";

		String firstName = request.getParameter("firstName");
		String phoneNumber = request.getParameter("phoneNumber");
		String address = request.getParameter("address");
		String sum = request.getParameter("sum");

		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			if (firstName.isEmpty()) {
				errors.put("firstName", "Provide your first name");
			}
			if (phoneNumber.isEmpty()) {
				errors.put("phoneNumber", "Provide your first name");
			}
			if (!Pattern.matches(phoneNumberRegex, phoneNumber)) {
				errors.put("phoneNumberPattern", "The entered phone number is incorrect");
			}
		}

		if (address.isEmpty()) {
			errors.put("address", "Indicate the address where the delivery will be");
		}

		Cart cart = (Cart) session.getAttribute("cart");
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("Cart.jsp");

			int orderSumm = 0;
			for (Product p : cart.getProducts()) {
				orderSumm += p.getPrice();
			}
			request.setAttribute("productsList", cart.getProducts());
			request.setAttribute("orderSumm", orderSumm);

			request.setAttribute("errors", errors);

			dispatcher.forward(request, response);
			return;
		}

		// take user
		int userId = 0;
		if (user != null) {
			userId = user.getId();
		} else {
			UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
			try {
				for (User u : userDao.getUsersForManager()) {
					if (u.getPhoneNumber().equals(phoneNumber)) {
						userId = u.getId();
					}
				}
			} catch (Exception e1) {
				// TODO add some logger 08.02.2021
				throw new IOException();
			}
			if (userId == 0) {

				User model = Util.createUser(firstName, phoneNumber);

				try {
					userId = userDao.insertUser(model);
				} catch (Exception e) {
					// TODO add some logger 05.02.2021
					throw new IOException();
				}
			}
		}

		// create order
		OrderViewDao orderDao = (OrderViewDao) request.getServletContext().getAttribute("orderDao");
		List<Product> products = cart.getProducts();
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute("count");
		try {
			orderDao.insertOrder(Util.createOrder(Status.NEW, address, userId, sum), products, count);
		} catch (Exception e) {
			// TODO add some logger 05.02.2021
			throw new IOException();
		}

		session.removeAttribute("count");
		session.removeAttribute("cart");
		response.sendRedirect("Login page");

	}

}
