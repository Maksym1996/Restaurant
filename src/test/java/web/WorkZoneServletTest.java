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
import consts.ForwardPages;
import consts.Params;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;
import db.entity.OrderView;
import exception.DBException;
import provider.OrderPage;
import util.Status;
import util.UserRole;

public class WorkZoneServletTest {

	private static final String path = ForwardPages.WORK_ZONE;

	private WorkZoneServlet servlet;
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
		servlet = new WorkZoneServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		productDao = mock(ProductDao.class);
		context = mock(ServletContext.class);
		session = mock(HttpSession.class);
		orderViewDao = mock(OrderViewDao.class);
		productDao = mock(ProductDao.class);
		userDao = mock(UserDao.class);
	}

	@Test
	public void callDoGetThenReturnError403() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Params.ROLE)).thenReturn(UserRole.ADMIN.name());

		servlet.doGet(request, response);

		verify(response).sendError(403);
	}

	@Test
	public void callDoGetThenReturnManagerPage() throws Exception {

		when(request.getRequestDispatcher(ForwardPages.MANAGER_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(context.getAttribute(Dao.USER)).thenReturn(userDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Params.ROLE)).thenReturn(UserRole.MANAGER.name());

		servlet.doGet(request, response);
		verify(request, times(1)).getRequestDispatcher(ForwardPages.MANAGER_JSP);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetThenReturnDeliveryPage() throws Exception {

		when(request.getRequestDispatcher(ForwardPages.DELIVERY_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(context.getAttribute(Dao.USER)).thenReturn(userDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Params.ROLE)).thenReturn(UserRole.DELIVERY.name());

		servlet.doGet(request, response);
		verify(request, times(1)).getRequestDispatcher(ForwardPages.DELIVERY_JSP);
		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoGetThenReturnCookPage() throws Exception {

		when(request.getRequestDispatcher(ForwardPages.COOK_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(context.getAttribute(Dao.USER)).thenReturn(userDao);
		when(request.getSession(true)).thenReturn(session);
		when(session.getAttribute(Params.ROLE)).thenReturn(UserRole.COOK.name());

		servlet.doGet(request, response);
		verify(request, times(1)).getRequestDispatcher(ForwardPages.COOK_JSP);
		verify(dispatcher).forward(request, response);	
	}
	
	@Test
	public void callDoPostWithoutParamThenReturnError400() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);

		servlet.doPost(request, response);

		verify(response).sendError(400);
	}
	
	@Test
	public void callDoPostWithParamThenReturnRedirectPath() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(request.getParameter("id")).thenReturn("2");
		when(request.getParameter("status")).thenReturn(Status.NEW.name());
		when(orderViewDao.getStatusByOrderId(2)).thenReturn(Status.NEW.name());
		
		servlet.doPost(request, response);

		verify(response).sendRedirect(path);;
	}

	
	@Test
	public void callDoPostWithParamStatusRejectThenReturnRedirectPath() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(request.getParameter("id")).thenReturn("2");
		when(request.getParameter("status")).thenReturn(Status.REJECTED.name());
		when(orderViewDao.getStatusByOrderId(2)).thenReturn(Status.NEW.name());
		
		servlet.doPost(request, response);

		verify(response).sendRedirect(path);
	}
	
	@Test
	public void callDoPostThenReturnError500() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(request.getParameter("id")).thenReturn("2");
		when(request.getParameter("status")).thenReturn(Status.REJECTED.name());
		when(orderViewDao.getStatusByOrderId(2)).thenReturn(Status.NEW.name());
		when(orderViewDao.updateStatusById(2, Status.REJECTED.name())).thenThrow(new DBException(null));
		
		servlet.doPost(request, response);

		verify(response).sendError(500);
	}
	
	@Test
	public void callDoPostThenReturnRedirect() throws Exception {

		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.ORDER_VIEW)).thenReturn(orderViewDao);
		when(request.getParameter("id")).thenReturn("2");
		when(request.getParameter("status")).thenReturn(Status.PERFORMED.name());
		when(orderViewDao.getStatusByOrderId(2)).thenReturn(Status.NEW.name());
		
		servlet.doPost(request, response);

		verify(response).sendRedirect(path);;
	}
}
