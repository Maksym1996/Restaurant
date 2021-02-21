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

import consts.Comment;
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
 * Servlet implementing product and creating additions
 */
@WebServlet("/AddProduct")
public class AddProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	private static final Logger log = LogManager.getLogger(AddProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.ADD_PRODUCT_JSP);
		log.info(Comment.FORWARD + Page.ADD_PRODUCT_JSP);
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);

		String name = request.getParameter(Param.NAME);
		String price = request.getParameter(Param.PRICE);
		String description = request.getParameter(Param.DESCRIPTION);
		String imageLink = request.getParameter(Param.IMAGE_LINK);
		String category = request.getParameter(Param.CATEGORY);

		log.debug(Param.NAME + name);
		log.debug(Param.PRICE + price);
		log.debug(Param.DESCRIPTION + description);
		log.debug(Param.IMAGE_LINK + imageLink);
		log.debug(Param.CATEGORY + category);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		log.debug(Dao.PRODUCT + " " + productDao);

		try {
			Product testProductByName = productDao.getProductByName(name);

			if (testProductByName != null) {
				errors.put(Param.NAME, "The name '" + name + "' is taken");
				log.debug("Test product by name = NOT NULL");
			}
			log.debug("Test product by name = NULL");
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.ADD_PRODUCT_JSP);
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			log.debug("Erorrs is not empty");
			log.info(Comment.FORWARD + Page.ADD_PRODUCT_JSP);
			log.debug(Comment.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		try {
			productDao.insertProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink,
					Category.byTitle(category), 0));
			log.debug("Insert product");
		} catch (Exception e) {
			log.error(Comment.EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		log.info(Comment.REDIRECT + Page.PIZZA_PREFERITA);
		response.sendRedirect(Page.PIZZA_PREFERITA);
	}
}
