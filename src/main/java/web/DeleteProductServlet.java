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

import consts.Log;
import consts.DaoConst;
import consts.PageConst;
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

	private static final Logger LOG = LogManager.getLogger(DeleteProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.DELETED_PRODUCT_JSP);
		dispatcher.forward(request, response);

		LOG.info(Log.FORWARD + PageConst.DELETED_PRODUCT_JSP);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		String stringProductId = request.getParameter(Param.ID);
		LOG.debug("stringProductId " + stringProductId);

		if (!Validator.intValidator(stringProductId)) {
			response.sendError(400);
			LOG.debug("stringProductId is invalid");
			LOG.info(Log.REDIRECT + 400);

			return;
		}
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		int productId = Integer.parseInt(stringProductId);
		try {
			Product testProduct = productDao.getProductById(productId);
			LOG.debug("getProductById(): " + testProduct);

			if (testProduct == null) {
				response.sendError(404);
				LOG.info(Log.REDIRECT + 404);

				return;
			}
			productDao.deleteProductById(productId);
			LOG.debug("deleteProductById");
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}
		response.sendRedirect(PageConst.DELETE_PRODUCT);
		LOG.info(Log.REDIRECT + PageConst.DELETE_PRODUCT);
	}
}
