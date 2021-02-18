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
import db.entity.Product;
import util.Category;
import util.Util;

/**
 * Servlet implementation class UpdateProductServlet
 */
@WebServlet("/UpdateProduct")
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String patternId = "[1-9]{1}[0-9]*";

		String productId = request.getParameter("id");

		if (productId == null || productId.isEmpty()) {
			response.sendError(400);
			return;
		}

		if (!Pattern.matches(patternId, productId)) {
			response.sendError(415);
			return;
		}
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		Product testProduct;
		try {
			testProduct = productDao.getProductById(Integer.parseInt(productId));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
			throw new IOException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e);
			throw new IOException();
		}
		if (testProduct == null) {
			response.sendError(416);
			return;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher("UpdateProduct.jsp");
		request.setAttribute("product", testProduct);
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
		String id = request.getParameter("id");

		Map<String, String> errors = new HashMap<>();

		if (name == null || name.isEmpty()) {
			errors.put("name", "Enter product name");
		}

		if (price == null || price.isEmpty()) {
			errors.put("price", "Enter product price");
		} else if (!Pattern.matches(pricePattern, price)) {
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
		if (categoryObject == null) {
			errors.put("category", "The '" + category + "' is not a valid category");
		}

		int productId = 0;
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		Product product;
		try {
			product = productDao.getProductById(productId);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.err.println("Get Product: " + e1);
			response.sendError(500);
			return;
		}

		if (id == null || id.isEmpty()) {
			errors.put("id", "Product id was not found");
		} else if (!Pattern.matches(pricePattern, id)) {
			errors.put("idPattern", "The id must be an integer and not start from zero");
		} else if (product == null) {
			productId = Integer.parseInt(id);
			errors.put("idNone", "The product does not exist for the given id = " + productId + " !");
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("AddProduct.jsp");
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}
		productId = Integer.parseInt(id);
		try {
			productDao.updateProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink,
					categoryObject, productId));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.err.println("Update Product Number Format" + e);
			response.sendError(500);
			return;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Update product exception: " + e);
			response.sendError(500);
			return;
		}

		response.sendRedirect("Pizza Preferita");
	}

}
