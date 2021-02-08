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
	public static final String EMPTY_CART = "EmptyCart.html";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		RequestDispatcher dispatcher;

		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null) {
			dispatcher = request.getRequestDispatcher(EMPTY_CART);
			dispatcher.forward(request, response);
			return;
		}
		List<Product> products = cart.getProducts();
		if (products.isEmpty()) {
			dispatcher = request.getRequestDispatcher(EMPTY_CART);
			dispatcher.forward(request, response);
			return;
		}
		
		
		String changeId = request.getParameter("id");
		int change = "inc".equals(request.getParameter("change")) ? 1
				: "dec".equals(request.getParameter("change")) ? -1 : 0;
		
		Map<Integer, Integer> count = (Map<Integer, Integer>) session.getAttribute("count");
		
		if (count == null) {
			count = new HashMap<>();
			session.setAttribute("count", count);
			for(Product p: products) {
				count.put(p.getId(), 1);
			}
		}
		
		if(changeId!=null && change!= 0) {
			int id = Integer.parseInt(changeId);
			int value = count.get(id)+change;
			if(value <= 0) {
				value = 1;
			} else if(value >= 20) {
				value = 20;
			}
			count.put(id, value);
		}
		
		// realize delete product from cart
				String deleteId = request.getParameter("deleteId");
				if (deleteId != null) {
					for (Product p : products) {
						if (p.getId() == Integer.parseInt(deleteId)) {
							products.remove(p);
							count.remove(p.getId());
							break;
						}
					}
					if (products.isEmpty()) {
						dispatcher = request.getRequestDispatcher(EMPTY_CART);
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

		String firstName = request.getParameter("firstName");
		String phoneNumber = request.getParameter("phoneNumber");
		String address = request.getParameter("address");

		HttpSession session = request.getSession(true);

		Map<String, String> errors = new HashMap<>();

		if (firstName.isEmpty()) {
			errors.put("firstName", "Provide your first name");
		}
		if (phoneNumber.isEmpty()) {
			errors.put("phoneNumber", "Provide your first name");
		}
		if (address.isEmpty()) {
			errors.put("address", "Indicate the address where the delivery will be");
		}

		if (!Pattern.matches(phoneNumberRegex, phoneNumber)) {
			errors.put("phoneNumberPattern", "The entered phone number is incorrect");
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
			User model = Util.createUser(firstName, phoneNumber);

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
		OrderDao orderDao = (OrderDao) request.getServletContext().getAttribute("orderDao");
		List<Product> products = cart.getProducts();
		try {
			orderDao.insertOrder(Util.createOrder("NEW", address, userId), products);
		} catch (Exception e) {
			// TODO add some logger 05.02.2021
			response.sendRedirect("SomeWrong.jsp");
			return;
		}

		session.removeAttribute("cart");
		response.sendRedirect("SuccessBuy.html");

	}

}
