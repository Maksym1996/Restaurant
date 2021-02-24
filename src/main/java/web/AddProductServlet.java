package web;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.DaoConst;
import consts.PageConst;
import consts.Param;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;
import util.Category;
import util.Util;
import util.Validator;

/**
 * Servlet implementing product and creating additions
 */
@WebServlet("/AddProduct")
public class AddProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(AddProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.ADD_PRODUCT_JSP);
		dispatcher.forward(request, response);
		
		LOG.info(Log.FORWARD + PageConst.ADD_PRODUCT_JSP);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		String name = request.getParameter(Param.NAME);
		String price = request.getParameter(Param.PRICE);
		String description = request.getParameter(Param.DESCRIPTION);
		String imageLink = request.getParameter(Param.IMAGE_LINK);
		String category = request.getParameter(Param.CATEGORY);

		LOG.debug(Param.NAME + name);
		LOG.debug(Param.PRICE + price);
		LOG.debug(Param.DESCRIPTION + description);
		LOG.debug(Param.IMAGE_LINK + imageLink);
		LOG.debug(Param.CATEGORY + category);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);

		LOG.debug(DaoConst.PRODUCT + " " + productDao);

		try {
			Product testProductByName = productDao.getProductByName(name);

			if (testProductByName != null) {
				errors.put(Param.NAME, "The name '" + name + "' is taken");
				LOG.debug("Test product by name = NOT NULL");
			}
			LOG.debug("Test product by name = NULL");
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);
			
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.ADD_PRODUCT_JSP);
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			LOG.debug("Erorrs is not empty");
			LOG.info(Log.FORWARD + PageConst.ADD_PRODUCT_JSP);
			LOG.debug(Log.FORWARD_WITH_PARAMETR + errors);
			
			return;
		}

		Product productModelToInsert = Util.createProduct(name, Integer.parseInt(price), description, imageLink,
				Category.byTitle(category), 0);

		try {
			productDao.insertProduct(productModelToInsert);
			LOG.debug("Insert product");
		} catch (Exception e) {
			response.sendError(500);
			LOG.error(Log.EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}

		response.sendRedirect(PageConst.PIZZA_PREFERITA);
		
		LOG.info(Log.REDIRECT + PageConst.PIZZA_PREFERITA);
	}
}
