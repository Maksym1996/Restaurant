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

import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
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
		LOG.info(CommentConst.BEGIN);
		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.ADD_PRODUCT_JSP);
		LOG.info(CommentConst.FORWARD + PageConst.ADD_PRODUCT_JSP);
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		String name = request.getParameter(ParamConst.NAME);
		String price = request.getParameter(ParamConst.PRICE);
		String description = request.getParameter(ParamConst.DESCRIPTION);
		String imageLink = request.getParameter(ParamConst.IMAGE_LINK);
		String category = request.getParameter(ParamConst.CATEGORY);

		LOG.debug(ParamConst.NAME + name);
		LOG.debug(ParamConst.PRICE + price);
		LOG.debug(ParamConst.DESCRIPTION + description);
		LOG.debug(ParamConst.IMAGE_LINK + imageLink);
		LOG.debug(ParamConst.CATEGORY + category);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		LOG.debug(DaoConst.PRODUCT + " " + productDao);

		try {
			Product testProductByName = productDao.getProductByName(name);

			if (testProductByName != null) {
				errors.put(ParamConst.NAME, "The name '" + name + "' is taken");
				LOG.debug("Test product by name = NOT NULL");
			}
			LOG.debug("Test product by name = NULL");
		} catch (DBException e) {
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.ADD_PRODUCT_JSP);
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			LOG.debug("Erorrs is not empty");
			LOG.info(CommentConst.FORWARD + PageConst.ADD_PRODUCT_JSP);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		try {
			productDao.insertProduct(Util.createProduct(name, Integer.parseInt(price), description, imageLink,
					Category.byTitle(category), 0));
			LOG.debug("Insert product");
		} catch (Exception e) {
			LOG.error(CommentConst.EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		LOG.info(CommentConst.REDIRECT + PageConst.PIZZA_PREFERITA);
		response.sendRedirect(PageConst.PIZZA_PREFERITA);
	}
}
