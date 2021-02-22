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

import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import db.entity.User;
import exception.DBException;
import util.UserRole;

public class LoginPageServletTest {
	private LoginPageServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private UserDao userDao;
	private OrderViewDao orderViewDao;
	private ServletContext context;
	private HttpSession session;

	@Before
	public void setUp() {
		servlet = new LoginPageServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		context = mock(ServletContext.class);
		session = mock(HttpSession.class);
		orderViewDao = mock(OrderViewDao.class);
		productDao = mock(ProductDao.class);
		userDao = mock(UserDao.class);
	}

	@Test
	public void callDoGetWithLogoutThenReturnLoginPageJSP() throws Exception {

		when(request.getRequestDispatcher(PageConst.LOGIN_PAGE_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(request.getParameter(ParamConst.LOG_OUT)).thenReturn(ParamConst.LOG_OUT);
		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetWithoutLogoutThenReturnLoginPageJSP() throws Exception {

		when(request.getRequestDispatcher(PageConst.LOGIN_PAGE_JSP)).thenReturn(dispatcher);
		when(request.getSession()).thenReturn(session);
		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetThenReturnAccountJSP() throws Exception {

		User user = mock(User.class);

		when(request.getRequestDispatcher(PageConst.ACCOUNT_JSP)).thenReturn(dispatcher);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.USER)).thenReturn(user);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(context.getAttribute(DaoConst.ORDER_VIEW)).thenReturn(orderViewDao);
		when(user.getRole()).thenReturn(UserRole.CLIENT);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetWithNullOrderListThenReturnError500() throws Exception {

		User user = mock(User.class);
		@SuppressWarnings("unchecked")
		List<OrderView> orderViewList = mock(List.class);

		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(ParamConst.USER)).thenReturn(user);
		when(user.getRole()).thenReturn(UserRole.CLIENT);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(context.getAttribute(DaoConst.ORDER_VIEW)).thenReturn(orderViewDao);
		when(orderViewDao.getOrderViewsByUserId(0)).thenReturn(orderViewList);
		servlet.doGet(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostThenReturnLoginPageJSP() throws Exception {

		when(request.getRequestDispatcher(PageConst.LOGIN_PAGE_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoPostThenReturnError500() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);
		when(userDao.getUserByNumberAndPass(any(), any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostWithAllParamThenReturnLoginPage() throws Exception {

		when(request.getSession(true)).thenReturn(session);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.USER)).thenReturn(userDao);
		when(userDao.getUserByNumberAndPass(any(), any())).thenReturn(getUser());
		when(request.getParameter(ParamConst.PHONE_NUMBER)).thenReturn("0969055382");
		when(request.getParameter(ParamConst.PASSWORD)).thenReturn("Client9)");

		servlet.doPost(request, response);

		verify(response).sendRedirect(PageConst.LOGIN_PAGE);
	}

	private User getUser() {
		User user = new User();
		user.setId(1);
		user.setRole(UserRole.CLIENT);
		return user;
	}
}
