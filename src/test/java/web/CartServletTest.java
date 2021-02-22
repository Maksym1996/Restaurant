package web;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.Product;
import exception.DBException;
import util.Cart;

public class CartServletTest {

	private CartServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private UserDao userDao;
	private OrderViewDao orderViewDao;
	private ServletContext context;
	private HttpSession session;
	private Cart cart;

	private List<Product> prods = new ArrayList<>();

	@Before
	public void setUp() {
		servlet = new CartServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		productDao = mock(ProductDao.class);
		context = mock(ServletContext.class);
		session = mock(HttpSession.class);
		orderViewDao = mock(OrderViewDao.class);
		productDao = mock(ProductDao.class);
		userDao = mock(UserDao.class);
		cart = mock(Cart.class);
	}

	@Test
	public void callDoGetCartNullThenReturnEmptyCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(null);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetCartEmptyThenReturnEmptyCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetNotEmptyCartThenReturnEmptyCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);
		when(cart.getProducts()).thenReturn(prods);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetCartWithNotNullProductThenReturnCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetNotNullListThenReturnCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);
		when(session.getAttribute(ParamConst.COUNT)).thenReturn(getCount0());
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetCountZeroThenReturnCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);
		when(session.getAttribute(ParamConst.COUNT)).thenReturn(getCount0());
		when(request.getParameter(ParamConst.ID)).thenReturn("1");
		when(request.getParameter(ParamConst.CHANGE)).thenReturn(ParamConst.DEC);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetCountTwentyThenReturnCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(cart);
		when(session.getAttribute(ParamConst.COUNT)).thenReturn(getCount20());
		when(request.getParameter(ParamConst.ID)).thenReturn("1");
		when(request.getParameter(ParamConst.CHANGE)).thenReturn(ParamConst.DEC);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoPostThenReturnError400() throws Exception {
		when(request.getSession(true)).thenReturn(session);

		servlet.doPost(request, response);

		verify(response).sendError(400);
	}

	@Test
	public void callDoPostWithParamThenReturnCartJSP() throws Exception {
		when(request.getRequestDispatcher(PageConst.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.CART)).thenReturn(new Cart());
		when(request.getParameter(ParamConst.SUMM)).thenReturn("200");
		when(request.getParameter(ParamConst.PHONE_NUMBER)).thenReturn("0123");

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoPostWithValidParamThenReturn500() throws Exception {
		when(request.getSession(true)).thenReturn(session);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);
		when(context.getAttribute(DaoConst.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(session.getAttribute(ParamConst.CART)).thenReturn(new Cart());
		when(request.getParameter(ParamConst.SUMM)).thenReturn("200");
		when(request.getParameter(ParamConst.PHONE_NUMBER)).thenReturn("0969055386");
		when(request.getParameter(ParamConst.FIRST_NAME)).thenReturn("Maksym");
		when(request.getParameter(ParamConst.ADDRESS)).thenReturn("Gagarina 6");
		when(orderViewDao.insertOrder(any(), any(), any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostWithValidParamThenReturnLoginPage() throws Exception {
		when(request.getSession(true)).thenReturn(session);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);
		when(context.getAttribute(DaoConst.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(session.getAttribute(ParamConst.CART)).thenReturn(new Cart());
		when(request.getParameter(ParamConst.SUMM)).thenReturn("200");
		when(request.getParameter(ParamConst.PHONE_NUMBER)).thenReturn("0969055386");
		when(request.getParameter(ParamConst.FIRST_NAME)).thenReturn("Maksym");
		when(request.getParameter(ParamConst.ADDRESS)).thenReturn("Gagarina 6");

		servlet.doPost(request, response);

		verify(response).sendRedirect(PageConst.LOGIN_PAGE);
	}

	@Test
	public void callDoPostExceptionUserDaoThenReturn500() throws Exception {
		when(request.getSession(true)).thenReturn(session);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(session.getAttribute(ParamConst.CART)).thenReturn(new Cart());
		when(request.getParameter(ParamConst.SUMM)).thenReturn("200");
		when(request.getParameter(ParamConst.PHONE_NUMBER)).thenReturn("0969055386");
		when(request.getParameter(ParamConst.FIRST_NAME)).thenReturn("Maksym");
		when(request.getParameter(ParamConst.ADDRESS)).thenReturn("Gagarina 6");
		when(userDao.insertUser(any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	private List<Product> getListNotNull() {
		Product product = new Product();
		product.setId(1);
		prods.add(product);
		return prods;
	}

	private Map<Integer, Integer> getCount0() {
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 0);
		return count;
	}

	private Map<Integer, Integer> getCount20() {
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 30);
		return count;
	}
}
