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

	private static final Logger LOG = LogManager.getLogger(MainPageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		LOG.info(CommentConst.BEGIN);
		
		ProductDao productDao = (ProductDao)request.getServletContext().getAttribute(DaoConst.PRODUCT);
		Map<String, String> params = Validator.mainPageValidator(request.getParameter(ParamConst.PAGE),
				request.getParameter(ParamConst.PRODUCT_ID), request.getParameter(ParamConst.SORT_VALUE));

		int currentPage = Integer.parseInt(params.get(ParamConst.PAGE));
		LOG.debug("currentPage " + currentPage);
		
		int productId = Integer.parseInt(params.get(ParamConst.PRODUCT_ID));
		LOG.debug("productId " + productId);
		
		String[] categories = request.getParameterValues(ParamConst.CATEGORIES) != null
				? request.getParameterValues(ParamConst.CATEGORIES)
				: new String[] {};
		LOG.debug("categories " + categories);
		
		String sortValue = params.get(ParamConst.SORT_VALUE);
		LOG.debug("sortValue " + sortValue);
		
		String asc = request.getParameter(ParamConst.ASC);
		LOG.debug("asc " + asc);

		int limitProductOnPage = 3;
		LOG.debug("limitProductOnPage " + limitProductOnPage);
		
		List<Product> partListProducts = null;
		long productsCount = 0;

		int skip = limitProductOnPage * (currentPage - 1);
		LOG.debug("skip " + skip);

		try {
			productsCount = productDao.getProductsCount(categories);
			partListProducts = productDao.getProductByCategoriesOnPage(categories, sortValue, asc, skip,
					limitProductOnPage);
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);

			return;
		}

		HttpSession session = request.getSession(true);
		Cart cart = (Cart)session.getAttribute(ParamConst.CART);
		LOG.debug("Cart from session  " + cart);
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
					LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
					LOG.info(CommentConst.REDIRECT + 500);
					response.sendError(500);
					return;
				}
			}
		}

		int maxPages = Util.getMaxPages(productsCount, limitProductOnPage);

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.PIZZA_PREFERITA_JSP);
		request.setAttribute(ParamConst.PRODUCTS_LIST, partListProducts);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "partListProducts " + partListProducts);
		
		request.setAttribute(ParamConst.MAX_PAGES, maxPages);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "maxPages " + maxPages);
		
		request.setAttribute(ParamConst.CURRENT_PAGE, currentPage);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "currentPage " + currentPage);
		
		request.setAttribute(ParamConst.CATEGORIES, categories);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "categories " + categories);
		
		request.setAttribute(ParamConst.SORT_VALUE, sortValue);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "sortValue " + sortValue);
		
		request.setAttribute(ParamConst.ASC, asc);
		LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + "asc " + asc);
		
		dispatcher.forward(request, response);
		LOG.info(CommentConst.FORWARD + PageConst.PIZZA_PREFERITA_JSP);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);
		LOG.info("doGet()");
		doGet(request, response);
	}

}
