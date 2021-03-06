package web;

import static org.mockito.Mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import consts.Dao;
import consts.Path;
import consts.Param;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;

public class AddProductServletTest {
	private AddProductServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private ServletContext context;

	@Before
	public void setUp() {
		servlet = new AddProductServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		productDao = mock(ProductDao.class);
		context = mock(ServletContext.class);
		productDao = mock(ProductDao.class);
	}

	@Test
	public void callDoGetReturnAddProductJSP() throws Exception {
		when(request.getRequestDispatcher(Path.ADD_PRODUCT_JSP)).thenReturn(dispatcher);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoPostWithoutParamReturnAddProductJSP() throws Exception {
		Product product = mock(Product.class);
		when(request.getRequestDispatcher(Path.ADD_PRODUCT_JSP)).thenReturn(dispatcher);
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductByName(any())).thenReturn(product);

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
	}

	@Test
	public void callDoPostWithoutParamReturnError500() throws Exception {
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductByName(any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostWithParamReturnAddProductJSP() throws Exception {
		when(request.getParameter(Param.NAME)).thenReturn("Bulka");
		when(request.getParameter(Param.PRICE)).thenReturn("100");
		when(request.getParameter(Param.DESCRIPTION)).thenReturn("Bulka");
		when(request.getParameter(Param.IMAGE_LINK)).thenReturn("Bulka");
		when(request.getParameter(Param.CATEGORY)).thenReturn("Burger");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductByName(any())).thenReturn(null);

		servlet.doPost(request, response);

		verify(response).sendRedirect(Path.PIZZA_PREFERITA);

	}

	@Test
	public void callDoPostWithParamReturnError500() throws Exception {
		when(request.getParameter(Param.NAME)).thenReturn("Bulka");
		when(request.getParameter(Param.PRICE)).thenReturn("100");
		when(request.getParameter(Param.DESCRIPTION)).thenReturn("Bulka");
		when(request.getParameter(Param.IMAGE_LINK)).thenReturn("Bulka");
		when(request.getParameter(Param.CATEGORY)).thenReturn("Burger");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductByName(any())).thenReturn(null);
		when(productDao.insertProduct(any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}
}
