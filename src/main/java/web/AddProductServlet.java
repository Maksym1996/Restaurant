package web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.dao.ProductDao;
import util.Category;
import util.Util;

/**
 * Servlet implementation class AddProductServlet
 */
@WebServlet("/AddProduct")
public class AddProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("AddProduct.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String pricePattern = "[1-9]{1}[0-9]*";

		String name = request.getParameter("name");
		String price = request.getParameter("price");
		String description = request.getParameter("description");
		String imageLink = request.getParameter("imageLink");
		String category = request.getParameter("category");

		Map<String, String> errors = new HashMap<>();
		
		if (name == null || name.isEmpty()) {
			errors.put("name", "Enter product name");
		}

		if (price == null || price.isEmpty()) {
			errors.put("price", "Enter product price");
		}else if(!Pattern.matches(pricePattern, price)) {
			errors.put("pricePattern", "The price must be an integer and not start from zero");
		}

		if (description == null || description.isEmpty()) {
			errors.put("description", "Enter product description");
		}

		if (imageLink == null || imageLink.isEmpty()) {
			errors.put("imageLink", "Enter product imageLink");
		}
		
		if (category == null || category.isEmpty()) {
			errors.put("category", "Enter product category");
		}
		Category categoryObject = Category.byTitle(category);
		if(categoryObject == null) {
			errors.put("category", "The '"+ category + "' is not a valid category");
		}
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("AddProduct.jsp");
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}
		
		
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		
		try {
			productDao.insertProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink, categoryObject));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			throw new IOException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IOException();
		}

		response.sendRedirect("Pizza Preferita");
	}

}
