package web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import consts.Dao;
import consts.ForwardPages;
import consts.Params;
import db.dao.ProductDao;
import db.entity.Product;
import util.Cart;
import util.Util;
import util.Validator;

/**
 * Servlet implementation class BeforeMain
 */

@WebServlet("/Pizza Preferita")
public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		Map<String, String> params = Validator.mainPageValidator(request.getParameter(Params.PAGE),
				request.getParameter(Params.PRODUCT_ID), request.getParameter(Params.SORT_VALUE));

		int currentPage = Integer.parseInt(params.get(Params.PAGE));
		int productId = Integer.parseInt(params.get(Params.PRODUCT_ID));
		String[] categories = request.getParameterValues(Params.CATEGORIES) != null
				? request.getParameterValues(Params.CATEGORIES)
				: new String[] {};
		String sortValue = request.getParameter(Params.SORT_VALUE);
		String asc = request.getParameter(Params.ASC);

		int limitProductOnPage = 2;
		List<Product> partListProducts = null;
		long productsCount = 0;

		int skip = limitProductOnPage * (currentPage - 1);

		try {
			productsCount = productDao.getProductCount(categories);
			partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, asc, skip,
					limitProductOnPage);
		} catch (Exception e) {
			System.err.println(e);
			// TODO add some logger 03.02.2021
			response.sendError(500);return;
		}

		HttpSession session = request.getSession(true);
		Cart cart = (Cart) session.getAttribute(Params.CART);

		if (cart == null) {
			cart = new Cart();
			session.setAttribute(Params.CART, cart);
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
					response.sendError(500);
					return;
				}
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(ForwardPages.PIZZA_PREFERITA);
		request.setAttribute(Params.PRODUCTS_LIST, partListProducts);
		request.setAttribute(Params.MAX_PAGES, Util.getMaxPages(productsCount, limitProductOnPage));
		request.setAttribute(Params.CURRENT_PAGE, currentPage);
		request.setAttribute(Params.CATEGORIES, categories);
		request.setAttribute(Params.SORT_VALUE, sortValue);
		request.setAttribute(Params.ASC, asc);

		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
