package web;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.OrderDao;
import db.dao.UserDao;
import db.entity.Product;
import db.entity.User;
import util.Cart;
import util.Util;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet("/Cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String emptyCart = "EmptyCart.html";
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		RequestDispatcher dispatcher;
		
		Cart cart = (Cart) session.getAttribute("cart");
		
		if(cart == null) {
			dispatcher = request.getRequestDispatcher(emptyCart);
			dispatcher.forward(request, response);
			return;
		}
		List<Product> products = cart.getProducts();
		if (products.isEmpty()) {
			dispatcher = request.getRequestDispatcher(emptyCart);
			dispatcher.forward(request, response);
			return;
		}

		
		// realize delete product from cart
		String productId = request.getParameter("productId");
		if (productId != null) {
			for (Product p : products) {
				if (p.getId() == Integer.parseInt(productId)) {
					products.remove(p);
					break;
				}
			}
			if (products.isEmpty()) {
				dispatcher = request.getRequestDispatcher(emptyCart);
				dispatcher.forward(request, response);
				return;
			}
		}

		int orderSumm = 0;
		for (Product p : products) {
			orderSumm += p.getPrice();
		}

		dispatcher = request.getRequestDispatcher("Cart.jsp");
		request.setAttribute("productsList", products);
		request.setAttribute("orderSumm", orderSumm);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String phoneNumberRegex = "[0][0-9]{9}";
		String porchRegex = "[0-9]+";
		String apartmentRegex = "[0-9]+";

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String phoneNumber = request.getParameter("phoneNumber");
		String street = request.getParameter("street");
		String house = request.getParameter("house");
		String apartment = request.getParameter("apartment");
		String porch = request.getParameter("porch");
		
		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();

		if (firstName.isEmpty()) {
			errors.put("firstName", "Provide your first name");
		}
		if (lastName.isEmpty()) {
			errors.put("lastName", "Provide your last name");
		}
		if (phoneNumber.isEmpty()) {
			errors.put("phoneNumber", "Provide your first name");
		}
		if (street.isEmpty()) {
			errors.put("street", "Indicate the street where the delivery will be");
		}
		if (house.isEmpty()) {
			errors.put("house", "Indicate the house where the delivery will be");
		}

		if (!Pattern.matches(phoneNumberRegex, phoneNumber)) {
			errors.put("phoneNumberPattern", "The entered phone number is incorrect");
		}
		if (!apartment.isEmpty() && !Pattern.matches(apartmentRegex, apartment)) {
			errors.put("apartmentPattern", "Сannot contain any characters unless numbers");
		}
		if (!porch.isEmpty() && !Pattern.matches(porchRegex, porch)) {
			errors.put("porchPattern", "Сannot contain any characters unless numbers");
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
		User user = (User) session.getAttribute("user");
		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");
		if (user == null) {
			User model = Util.createUser(firstName, lastName, phoneNumber);

			try {
				userId = userDao.insertUser(model);
			} catch (Exception e) {
				response.sendRedirect("SomeWrong.jsp");
				// TODO add some logger 05.02.2021
				return;
			}
		} else {
			userId = user.getId();
		}

		// create order
		int orderId = 0;
		OrderDao orderDao = (OrderDao) request.getServletContext().getAttribute("orderDao");
		Date date = new Date();
		SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.KOREA);
		String currentDate = formatForDateNow.format(date);
		try {
			orderId = orderDao
					.insertOrder(Util.createOrder(currentDate, null, "NEW", street, house, apartment, porch, userId));
		} catch (Exception e) {

			// TODO add some logger 05.02.2021

			if (user == null) {
				try {
					userDao.deleteUser(userId);
				} catch (Exception e1) { 
					// TODO add some logger 05.02.2021 
					response.sendRedirect("SomeWrong.jsp");
					return;
				}
			}

			response.sendRedirect("SomeWrong.jsp");
			return;
		}

		// create receipt
		List<Product> products = cart.getProducts();

		// Receipt was be here
		session.removeAttribute("cart");
		response.sendRedirect("SuccessBuy.html");

	}

}
