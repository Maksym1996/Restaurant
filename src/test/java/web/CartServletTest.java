package web;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

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
	
	List<Product> prods = new ArrayList<>();

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
	public void callDoGetNoThenReturnCartJSP() throws Exception {
		
		when(request.getRequestDispatcher(Page.CART_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Param.CART)).thenReturn(cart);
		when(session.getAttribute(Param.COUNT)).thenReturn(cart);
		when(cart.getProducts()).thenReturn(getListNotNull());

		servlet.doGet(request, response);
		
		verify(dispatcher).forward(request, response);
	}
}
