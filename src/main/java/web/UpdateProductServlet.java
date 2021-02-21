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
import util.Validator;

/**
 * Servlet that implements the functionality of changing product parameters
 */
@WebServlet("/UpdateProduct")
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(UpdateProductServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		String productId = request.getParameter(Param.ID);

		log.debug("Product Id = " + productId);

		if (!Validator.intValidator(productId)) {
			log.debug("ProductId is invalid");
			log.info(Comment.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		Product product;
		try {
			product = productDao.getProductById(Integer.parseInt(productId));
			log.debug("getProductById");
			log.debug("Product: " + product);
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.UPDATE_PRODUCT_JSP);
		request.setAttribute(Param.PRODUCT, product);
		log.info(Comment.FORWARD + Page.UPDATE_PRODUCT_JSP);
		log.debug(Comment.FORWARD_WITH_PARAMETR + product);
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
		String id = request.getParameter(Param.ID);

		log.debug("Name " + name);
		log.debug("Price " + price);
		log.debug("Description " + description);
		log.debug("Image Link " + imageLink);
		log.debug("Category " + category);
		log.debug("ID " + id);

		Map<String, String> errors = Validator.productValidator(name, price, description, imageLink, category);

		if (!Validator.intValidator(id)) {
			log.debug("Parametrs are invalid");
			log.info(Comment.REDIRECT + 400);
			response.sendError(400);
			return;
		}

		int productId = Integer.parseInt(id);
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(Dao.PRODUCT);
		Product productToUpdate;
		try {
			productToUpdate = productDao.getProductById(productId);
			log.debug("getProductBuId");
			boolean productWithIdDoesNotExist = productToUpdate == null;
			if (productWithIdDoesNotExist) {
				log.debug("productToUpdate " + productToUpdate);
				log.info(Comment.REDIRECT + 404);
				response.sendError(404);
				return;
			}
			Product productWithName = productDao.getProductByName(name);
			boolean otherProductWitNameDoesExist = productWithName != null
					&& productWithName.getId() != productToUpdate.getId();
			if (otherProductWitNameDoesExist) {
				errors.put(Param.NAME, "The name '" + name + "' is taken");
			}

		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.UPDATE_PRODUCT_JSP);
			request.setAttribute(Param.ERRORS, errors);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.UPDATE_PRODUCT_JSP);
			log.debug(Comment.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		try {
			productToUpdate.setName(name);
			productToUpdate.setPrice(Integer.parseInt(price));
			productToUpdate.setDescription(description);
			productToUpdate.setImageLink(imageLink);
			productToUpdate.setCategory(Category.byTitle(category));

			productDao.updateProduct(productToUpdate);
			log.debug("UpdateProduct");
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}
		log.info(Comment.REDIRECT + Page.PIZZA_PREFERITA);
		response.sendRedirect(Page.PIZZA_PREFERITA);
	}

}
