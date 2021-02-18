package web;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.ProductDao;
import db.entity.Product;
import util.Cart;
import util.Util;

/**
 * Servlet implementation class BeforeMain
 */

@WebServlet("/Pizza Preferita")
public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String CATEGORIES = "categories";
	private static final String SORT_VALUE = "sortValue";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");

		int currentPage = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
		String[] categories = request.getParameterValues(CATEGORIES) != null ? request.getParameterValues(CATEGORIES)
				: new String[] {};
		String sortValue = request.getParameter(SORT_VALUE) != null ? request.getParameter(SORT_VALUE).toLowerCase()
				: "id";
		String asc = request.getParameter("asc");
		int productId = request.getParameter("productId") != null ? Integer.parseInt(request.getParameter("productId"))
				: 0;

		int limitProductOnPage = 2;
		List<Product> partListProducts = null;
		long productsCount = 0;

		int skip = limitProductOnPage * (currentPage - 1);

		try {
			productsCount = productDao.getProductCount(categories);
		} catch (Exception e) {
			// TODO add some logger 03.02.2021
			System.err.println(e);
			response.sendError(500);
			return;
		}
		try {
			partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, asc, skip,
					limitProductOnPage);
		} catch (Exception e) {
			System.err.println(e);
			// TODO add some logger 03.02.2021
			throw new IOException();
		}

		HttpSession session = request.getSession(true);
		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}

		List<Product> cartProducts = cart.getProducts();
		if (productId != 0) {
			boolean contain = false;
			for (Product p : cartProducts) {
				if (p.getId() == productId) {
					contain = true;
					break;
				}
			}
			if (!contain) {
				try {
					cartProducts.add(productDao.getProduct(productId));
				} catch (Exception e) {
					// TODO add some logger 04.02.2021
					throw new IOException();
				}
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("Pizza Preferita.jsp");
		request.setAttribute("productsList", partListProducts);
		request.setAttribute("maxPages", Util.getMaxPages(productsCount, limitProductOnPage));
		request.setAttribute("currentPage", currentPage);
		request.setAttribute(CATEGORIES, categories);
		request.setAttribute(SORT_VALUE, sortValue);
		request.setAttribute("asc", asc);

		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}