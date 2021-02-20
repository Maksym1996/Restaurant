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

import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.Product;
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
	
	List<Product> prods = new ArrayList<>();

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
		
		when(request.getRequestDispatcher(Page.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(null);
		
		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetCartEmptyThenReturnEmptyCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetNotEmptyCartThenReturnEmptyCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.EMPTY_CART)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(cart.getProducts()).thenReturn(getList());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetCartWithNotNullProductThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetNotNullListThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(session.getAttribute(Param.COUNT)).thenReturn(getCount0());
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetCountZeroThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(session.getAttribute(Param.COUNT)).thenReturn(getCount0());
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.CHANGE)).thenReturn(Param.DEC);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoGetCountTwentyThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(session.getAttribute(Param.COUNT)).thenReturn(getCount20());
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.CHANGE)).thenReturn(Param.DEC);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoPostThenReturnError416() throws Exception {
		
		when(request.getSession(true)).thenReturn(session);

		servlet.doPost(request, response);
		
		verify(response).sendError(416);;
	}
	
	@Test
	public void callDoPostWithParamThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(new Cart());
		when(request.getParameter(Param.SUMM)).thenReturn("200");
		when(request.getParameter(Param.PHONE_NUMBER)).thenReturn("0123");

		servlet.doPost(request, response);
		
		verify(dispatcher).forward(request, response);;
	}
	
	@Test
	public void callDoPostWithValidParamThenReturnLoginPage() throws Exception {
		
		when(request.getSession(true)).thenReturn(session);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.USER)).thenReturn(userDao);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(session.getAttribute(Param.CART)).thenReturn(new Cart());
		when(request.getParameter(Param.SUMM)).thenReturn("200");
		when(request.getParameter(Param.PHONE_NUMBER)).thenReturn("0969055386");
		when(request.getParameter(Param.FIRST_NAME)).thenReturn("Maksym");
		when(request.getParameter(Param.ADDRESS)).thenReturn("Gagarina 6");

		servlet.doPost(request, response);
		
		verify(response).sendRedirect(Page.LOGIN_PAGE);
	}
	
	private List<Product> getList(){
		prods.add(new Product());
		return prods;
	}
	
	private List<Product> getListNotNull(){
		Product product = new Product();
		product.setId(1);
		prods.add(product);
		return prods;
	}
	
	private Map<Integer, Integer> getCount0(){
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 0);
		return count;
	}
	
	private Map<Integer, Integer> getCount20(){
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 30);
		return count;
	}
}
