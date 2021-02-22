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
import util.Validator;

/**
 * Servlet that implements the functionality of changing product parameters
 */
@WebServlet("/UpdateProduct")
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(UpdateProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);
		String productId = request.getParameter(ParamConst.ID);

		LOG.debug("Product Id = " + productId);

		if (!Validator.intValidator(productId)) {
			LOG.debug("ProductId is invalid");
			LOG.info(CommentConst.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		Product product;
		try {
			product = productDao.getProductById(Integer.parseInt(productId));
			LOG.debug("getProductById");
			LOG.debug("Product: " + product);
		} catch (DBException e) {
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.UPDATE_PRODUCT_JSP);
		request.setAttribute(ParamConst.PRODUCT, product);
		LOG.info(CommentConst.FORWARD + PageConst.UPDATE_PRODUCT_JSP);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + product);
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
		String id = request.getParameter(ParamConst.ID);

		LOG.debug("Name " + name);
		LOG.debug("Price " + price);
		LOG.debug("Description " + description);
		LOG.debug("Image Link " + imageLink);
		LOG.debug("Category " + category);
		LOG.debug("ID " + id);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		if (!Validator.intValidator(id)) {
			LOG.debug("Parametrs are invalid");
			LOG.info(CommentConst.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		int productId = Integer.parseInt(id);
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		Product productToUpdate;
		try {
			productToUpdate = productDao.getProductById(productId);
			LOG.debug("getProductBuId");
			boolean productWithIdDoesNotExist = productToUpdate == null;
			if (productWithIdDoesNotExist) {
				LOG.debug("productToUpdate " + productToUpdate);
				LOG.info(CommentConst.REDIRECT + 404);
				response.sendError(404);
				return;
			}
			Product productWithName = productDao.getProductByName(name);
			boolean otherProductWitNameDoesExist = productWithName != null
					&& productWithName.getId() != productToUpdate.getId();
			if (otherProductWitNameDoesExist) {
				errors.put(ParamConst.NAME, "The name '" + name + "' is taken");
			}

		} catch (DBException e) {
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.UPDATE_PRODUCT_JSP);
			request.setAttribute(ParamConst.ERRORS, errors);
			dispatcher.forward(request, response);
			LOG.info(CommentConst.FORWARD + PageConst.UPDATE_PRODUCT_JSP);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		try {
			productToUpdate.setName(name);
			productToUpdate.setPrice(Integer.parseInt(price));
			productToUpdate.setDescription(description);
			productToUpdate.setImageLink(imageLink);
			productToUpdate.setCategory(Category.byTitle(category));

			productDao.updateProduct(productToUpdate);
			LOG.debug("UpdateProduct");
		} catch (DBException e) {
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		LOG.info(CommentConst.REDIRECT + PageConst.PIZZA_PREFERITA);
		response.sendRedirect(PageConst.PIZZA_PREFERITA);
	}

}
