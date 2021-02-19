package web;

import static org.mockito.Mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;

import consts.Page;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.UserDao;

public class CartServletTest {
	private static final String path = Page.WORK_ZONE;

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
}
