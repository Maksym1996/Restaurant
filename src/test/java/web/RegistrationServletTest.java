package web;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import consts.Dao;
import consts.Page;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.Product;
import db.entity.User;
import exception.DBException;
import util.Cart;

public class RegistrationServletTest {
	private RegistrationServlet servlet;
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
		servlet = new RegistrationServlet();
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
	public void callDoGetReturnForwardRegistrationJSP() throws Exception {
		when(request.getRequestDispatcher(Page.REGISTRATION_JSP)).thenReturn(dispatcher);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetReturnError500() throws Exception {
		when(request.getRequestDispatcher(Page.REGISTRATION_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.USER)).thenReturn(userDao);
		when(userDao.getUsersByRegistered("true")).thenThrow(new DBException(null));
		servlet.doPost(request, response);

		verify(response).sendError(500);
	}
}
