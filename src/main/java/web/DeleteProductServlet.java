package web;

import java.io.IOException;

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
import util.Validator;

/**
 * Servlet implementation class DeleteProductServlet
 */
@WebServlet("/DeleteProduct")
public class DeleteProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.DELETED_PRODUCT_JSP);
		dispatcher.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String stringProductId = request.getParameter(Param.ID);

		if (!Validator.intValidator(stringProductId)) {
			response.sendError(415);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		int productId = Integer.parseInt(stringProductId);

		try {
			if (null == productDao.getProductById(productId)) {
				response.sendError(416);
				return;
			}
		} catch (Exception e) {
			// TODO logger 14.02.2021
			response.sendError(500);
			return;
		}

		try {
			if (!productDao.deleteProductById(productId)) {
				throw new Exception();
			}
		} catch (Exception e) {
			// TODO logger 14.02.2021
			response.sendError(500);
		}

		response.sendRedirect(Page.DELETED_PRODUCT);
	}
}
