package web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;
import util.Category;
import util.Util;
import util.Validator;

/**
 * Servlet implementation class UpdateProductServlet
 */
@WebServlet("/UpdateProduct")
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String productId = request.getParameter(Param.ID);

		if (!Validator.intValidator(productId)) {
			response.sendError(415);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		Product testProduct;
		try {
			testProduct = productDao.getProductById(Integer.parseInt(productId));
		} catch (DBException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
			response.sendError(500);
			return;
		}
		if (testProduct == null) {
			response.sendError(416);
			return;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.UPDATE_PRODUCT_JSP);
		request.setAttribute(Param.PRODUCT, testProduct);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String name = request.getParameter(Param.NAME);
		String price = request.getParameter(Param.PRICE);
		String description = request.getParameter(Param.DESCRIPTION);
		String imageLink = request.getParameter(Param.IMAGE_LINK);
		String category = request.getParameter(Param.CATEGORY);
		String id = request.getParameter(Param.ID);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		int productId = Integer.parseInt(id);
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		try {
			Product testProductById = productDao.getProductById(productId);
			Product testProductByName = productDao.getProductByName(name);

			if (testProductById == null || !Validator.intValidator(id)) {
				response.sendError(416);
				return;
			} else if (testProductByName != null) {
				errors.put(Param.NAME, "The name '" + name + "' is taken");
			}

		} catch (DBException e) {
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.ADD_PRODUCT_JSP);
			request.setAttribute(Param.ERRORS, errors);
			dispatcher.forward(request, response);
			return;
		}

		try {
			productDao.updateProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink,
					Category.valueOf(category), productId));
		} catch (DBException e) {
			// TODO Auto-generated catch block
			System.err.println("Update product exception: " + e);
			response.sendError(500);
			return;
		}

		response.sendRedirect(Page.PIZZA_PREFERITA);
	}

}
