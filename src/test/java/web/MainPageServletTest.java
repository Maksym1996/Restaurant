package web;

import org.junit.Before;
import org.junit.Test;

import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.ProductDao;
import exception.DBException;
import util.Cart;

import static org.mockito.Mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class MainPageServletTest {

	private static final String path = Page.PIZZA_PREFERITA_JSP;

	private MainPageServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private ServletContext context;
	private HttpSession session;
	Cart cart = mock(Cart.class);

	@Before
	public void setUp() {
		servlet = new MainPageServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		productDao = mock(ProductDao.class);
		context = mock(ServletContext.class);
		session = mock(HttpSession.class);
		cart = mock(Cart.class);
	}

	@Test
	public void whenCallDoGetThenServletReturnPage() throws Exception {

		when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(request.getSession(true)).thenReturn(session);

		servlet.doGet(request, response);

		verify(request, times(1)).getRequestDispatcher(path);
		verify(request, times(1)).getSession(true);
		verify(request, never()).getAttribute(Param.PAGE);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void whenCallDoGetWithParamThenServletReturnPage() throws Exception {

		when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(request.getParameter(Param.PAGE)).thenReturn("2");
		when(request.getParameter(Param.CATEGORIES)).thenReturn("Pizza");
		when(request.getParameter(Param.SORT_VALUE)).thenReturn("price");
		when(request.getParameter(Param.ASC)).thenReturn("true");
		when(request.getParameter(Param.PRODUCT_ID)).thenReturn("1");

		servlet.doGet(request, response);

		verify(request, times(1)).getRequestDispatcher(path);
		verify(request, times(1)).getSession(true);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void whenCallDoGetWithParamThenServletReturnError500() throws Exception {

		when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(request.getParameter(Param.PRODUCT_ID)).thenReturn("1");

		when(productDao.getProductById(1)).thenThrow(new DBException(new Throwable()));

		servlet.doGet(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void whenCallDoGetWithInvalidParamThenServletReturnPage() throws Exception {

		when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);

		when(request.getParameter(Param.PAGE)).thenReturn("ZERO");
		when(request.getParameter(Param.CATEGORIES)).thenReturn("To");
		when(request.getParameter(Param.SORT_VALUE)).thenReturn("Oport");
		when(request.getParameter(Param.ASC)).thenReturn("Trues");
		when(request.getParameter(Param.PRODUCT_ID)).thenReturn("TWO");

		servlet.doGet(request, response);

		verify(request, times(1)).getRequestDispatcher(path);
		verify(request, times(1)).getSession(true);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void whenCallGetProductCountThenServletReturnError500() throws Exception {

		when(request.getRequestDispatcher(path)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(request.getSession(true)).thenReturn(session);
		when(productDao.getProductsCount(any())).thenThrow(new DBException(new Throwable()));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

}
