package web;

import java.io.IOException;

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
import util.Validator;

/**
 * Servlet implementing product deleting
 */
@WebServlet("/DeleteProduct")
public class DeleteProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(DeleteProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.DELETED_PRODUCT_JSP);
		log.info(Comment.FORWARD + Page.DELETED_PRODUCT_JSP);
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		String stringProductId = request.getParameter(Param.ID);
		log.debug("stringProductId " + stringProductId);

		if (!Validator.intValidator(stringProductId)) {
			log.debug("stringProductId is invalid");
			log.info(Comment.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		int productId = Integer.parseInt(stringProductId);

		try {
			Product testProduct = productDao.getProductById(productId);
			log.debug("getProductById(): " + testProduct);
			if (testProduct == null) {
				log.info(Comment.REDIRECT + 404);
				response.sendError(404);
				return;
			}
			productDao.deleteProductById(productId);
			log.debug("deleteProductById");
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		log.info(Comment.REDIRECT + Page.DELETE_PRODUCT);
		response.sendRedirect(Page.DELETE_PRODUCT);
	}
}
