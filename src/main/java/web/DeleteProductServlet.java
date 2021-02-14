package web;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.dao.ProductDao;
import db.entity.Product;

/**
 * Servlet implementation class DeleteProductServlet
 */
@WebServlet("/DeleteProduct")
public class DeleteProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("Deleted.jsp");
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String patternId = "[1-9]{1}[0-9]*";

		String stringProductId = request.getParameter("id");

		if (stringProductId == null || stringProductId.isEmpty()) {
			response.sendError(400);
			return;
		}

		if (!Pattern.matches(patternId, stringProductId)) {
			response.sendError(415);
			return;
		}
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute("productDao");
		Product testProduct;
		int productId; 
		try {
			productId = Integer.parseInt(stringProductId);
			testProduct = productDao.getProduct(productId);
		} catch (NumberFormatException e) {
			// TODO logger 14.02.2021
			throw new IOException();
		} catch (Exception e) {
			// TODO logger 14.02.2021
			throw new IOException();
		}
		if (testProduct == null) {
			response.sendError(416);
			return;
		}
		boolean test = false;
		try {
			test = productDao.deleteProduct(productId);
		} catch (Exception e) {
			// TODO logger 14.02.2021
			throw new IOException();
		}
		if(!test) {
			//TODO logger 14.02.2021
			throw new IOException();
		}
		response.sendRedirect("DeleteProduct");
	}

}
