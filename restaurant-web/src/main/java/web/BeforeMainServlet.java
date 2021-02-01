package web;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.dao.ProductDao;
import db.entity.Product;
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
		String[] categories = request.getParameterValues("categories") != null ? request.getParameterValues("categories") : new String[] {};
		String sortValue = request.getParameter("sortValue") != null ? request.getParameter("sortValue").toLowerCase()
				: "id";
		String desc = request.getParameter("desc");
		
		int limitProductOnPage = 2;
		List<Product> partListProducts;
		long productsCount;

		int skip = limitProductOnPage * (currentPage - 1);

		productsCount = productDao.getProductCount(categories);
		partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, desc, skip, limitProductOnPage);
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("Pizza Preferita.jsp");
		request.setAttribute("productsList", partListProducts);
		request.setAttribute("maxPages", Util.getMaxPages(productsCount, limitProductOnPage));
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("categories", categories);
		request.setAttribute("sortValue", sortValue);
		request.setAttribute("desc", desc);

		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
