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
		String categories = "&categories=";
		String[] category = request.getParameterValues("categories");

		long pageSize = 2;
		List<Product> partListProducts = new ArrayList<>();

		if (category == null) {
			List<Product> temp = productDao.getAllProduct();
			temp.stream().skip(pageSize * (currentPage - 1)).limit(pageSize).forEach(partListProducts::add);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("Pizza Preferita.jsp?page=" + currentPage + categories + 0);
			request.setAttribute("productsList", partListProducts);
			request.setAttribute("maxPages", Util.getMaxPages(temp, pageSize));
			dispatcher.forward(request, response);

		} else {
			StringBuilder sb = new StringBuilder();
			for (String s : category) {
				sb.append(categories).append(s);
			}
			List<Product> temp = new ArrayList<>();
			for (String s : category) {
				temp.addAll(productDao.getProductByCategories(s));
			}
			temp.stream().skip(pageSize * (currentPage - 1)).limit(pageSize).forEach(partListProducts::add);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("Pizza Preferita.jsp?page=" + currentPage + sb.toString());
			request.setAttribute("productsList", partListProducts);
			request.setAttribute("maxPages", Util.getMaxPages(temp, pageSize));
			dispatcher.forward(request, response);

		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
