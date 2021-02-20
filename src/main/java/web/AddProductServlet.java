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
 * Servlet implementation class AddProductServlet
 */
@WebServlet("/AddProduct")
public class AddProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.ADD_PRODUCT_JSP);
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

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);

		try {
			Product testProductByName = productDao.getProductByName(name);

			if (testProductByName.getId() != 0) {
				errors.put(Param.NAME, "The name '" + name + "' is taken");
			}
		} catch (DBException e) {
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.ADD_PRODUCT_JSP);
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}

		try {
			productDao.insertProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink,
					Category.byTitle(category), 0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println(e);
			response.sendError(500);
			return;
		}

		response.sendRedirect(Page.PIZZA_PREFERITA);
	}
}
