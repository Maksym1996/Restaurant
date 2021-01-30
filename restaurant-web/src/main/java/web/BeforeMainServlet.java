package web;

import java.io.IOException;
import java.util.ArrayList;
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
		
		int currentPage = Integer.parseInt(request.getParameter("page") != null ? request.getParameter("page") : "1");
		String[] categories = request.getParameterValues("categories");

		int pageSize = 2;
		List<Product> partListProducts;
		long productsCount;

		int skip = pageSize * (currentPage - 1);
		int limit = pageSize;
		if (categories == null) {
			productsCount = productDao.getProductCount();
			partListProducts = productDao.getAllProduct(skip, limit);
		} else {
			productsCount = productDao.getProductCount(categories);
			partListProducts = productDao.getProductByCategories(categories, skip, limit);
		}
		
		RequestDispatcher dispatcher = request.getRequestDispatcher("Pizza Preferita.jsp");
		request.setAttribute("productsList", partListProducts);
		request.setAttribute("maxPages", Util.getMaxPages(productsCount, pageSize));
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("categories", categories);
		
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
