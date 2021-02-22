package web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;
import util.Cart;
import util.Util;
import util.Validator;

/**
 * Servlet that implements the main page of the application, including sorting,
 * filtering, and paging
 */

@WebServlet("/Pizza Preferita")
public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger log = LogManager.getLogger(MainPageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		log.info(CommentConst.BEGIN);
		ProductDao productDao = (ProductDao) request.getServletContext().getAttribute(DaoConst.PRODUCT);
		Map<String, String> params = Validator.mainPageValidator(request.getParameter(ParamConst.PAGE),
				request.getParameter(ParamConst.PRODUCT_ID), request.getParameter(ParamConst.SORT_VALUE));

		int currentPage = Integer.parseInt(params.get(ParamConst.PAGE));
		log.debug("currentPage " + currentPage);
		int productId = Integer.parseInt(params.get(ParamConst.PRODUCT_ID));
		log.debug("productId " + productId);
		String[] categories = request.getParameterValues(ParamConst.CATEGORIES) != null
				? request.getParameterValues(ParamConst.CATEGORIES)
				: new String[] {};
		log.debug("categories " + categories);
		String sortValue = params.get(ParamConst.SORT_VALUE);
		log.debug("sortValue " + sortValue);
		String asc = request.getParameter(ParamConst.ASC);
		log.debug("asc " + asc);

		int limitProductOnPage = 3;
		log.debug("limitProductOnPage " + limitProductOnPage);
		List<Product> partListProducts = null;
		long productsCount = 0;

		int skip = limitProductOnPage * (currentPage - 1);
		log.debug("skip " + skip);

		try {
			productsCount = productDao.getProductsCount(categories);
			partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, asc, skip,
					limitProductOnPage);
		} catch (DBException e) {
			log.error(CommentConst.DB_EXCEPTION + e.getMessage());
			log.info(CommentConst.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		HttpSession session = request.getSession(true);
		Cart cart = (Cart) session.getAttribute(ParamConst.CART);
		log.debug("Cart from session  " + cart);
		if (cart == null) {
			cart = new Cart();
			session.setAttribute(ParamConst.CART, cart);
		}

		List<Product> cartProducts = cart.getProducts();
		if (productId != 0) {
			boolean contain = false;
			for (Product p : cartProducts) {
				if (p.getId() == productId) {
					contain = true;
					break;
				}
			}
			if (!contain) {
				try {
					cartProducts.add(productDao.getProductById(productId));
				} catch (DBException e) {
					log.error(CommentConst.DB_EXCEPTION + e.getMessage());
					log.info(CommentConst.REDIRECT + 500);
					response.sendError(500);
					return;
				}
			}
		}

		int maxPages = Util.getMaxPages(productsCount, limitProductOnPage);

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.PIZZA_PREFERITA_JSP);
		request.setAttribute(ParamConst.PRODUCTS_LIST, partListProducts);
		request.setAttribute(ParamConst.MAX_PAGES, maxPages);
		request.setAttribute(ParamConst.CURRENT_PAGE, currentPage);
		request.setAttribute(ParamConst.CATEGORIES, categories);
		request.setAttribute(ParamConst.SORT_VALUE, sortValue);
		request.setAttribute(ParamConst.ASC, asc);
		log.info(CommentConst.FORWARD + PageConst.PIZZA_PREFERITA_JSP);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "partListProducts " + partListProducts);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "maxPages " + maxPages);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "currentPage " + currentPage);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "categories " + categories);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "sortValue " + sortValue);
		log.debug(CommentConst.FORWARD_WITH_PARAMETR + "asc " + asc);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(CommentConst.BEGIN);
		log.info("doGet()");
		doGet(request, response);
	}

}
