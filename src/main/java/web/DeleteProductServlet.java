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

import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
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

	private static final Logger LOG = LogManager.getLogger(DeleteProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);
		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.DELETED_PRODUCT_JSP);
		LOG.info(CommentConst.FORWARD + PageConst.DELETED_PRODUCT_JSP);
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);
		String stringProductId = request.getParameter(ParamConst.ID);
		LOG.debug("stringProductId " + stringProductId);

		if (!Validator.intValidator(stringProductId)) {
			LOG.debug("stringProductId is invalid");
			LOG.info(CommentConst.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		int productId = Integer.parseInt(stringProductId);

		try {
			Product testProduct = productDao.getProductById(productId);
			LOG.debug("getProductById(): " + testProduct);
			if (testProduct == null) {
				LOG.info(CommentConst.REDIRECT + 404);
				response.sendError(404);
				return;
			}
			productDao.deleteProductById(productId);
			LOG.debug("deleteProductById");
		} catch (DBException e) {
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		LOG.info(CommentConst.REDIRECT + PageConst.DELETE_PRODUCT);
		response.sendRedirect(PageConst.DELETE_PRODUCT);
	}
}
