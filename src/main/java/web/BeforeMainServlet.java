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
public class BeforeMainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html;charset=UTF-8");
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");

		int currentPage = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
		String[] categories = request.getParameterValues("categories") != null
				? request.getParameterValues("categories")
				: new String[] {};
		String sortValue = request.getParameter("sortValue") != null ? request.getParameter("sortValue").toLowerCase()
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
			response.sendRedirect("SomeWrong.jsp");
		}
		try {
			partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, asc, skip,
					limitProductOnPage);
		} catch (Exception e) {
			// TODO add some logger 03.02.2021
			response.sendRedirect("SomeWrong.jsp");
		}

		HttpSession session = request.getSession(true);
		Cart cart = (Cart) session.getAttribute("cart");

		if (cart == null) {
			cart = new Cart();
			session.setAttribute("cart", cart);
		}

		List<Product> cartProducts = cart.getProducts();
		request.setAttribute("cartProducts", cartProducts);
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
					response.sendRedirect("SomeWrong.jsp");
				}
			}
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("Pizza Preferita.jsp");
		request.setAttribute("productsList", partListProducts);
		request.setAttribute("maxPages", Util.getMaxPages(productsCount, limitProductOnPage));
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("categories", categories);
		request.setAttribute("sortValue", sortValue);
		request.setAttribute("asc", asc);

		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
