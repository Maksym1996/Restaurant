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

public class DeleteProductServletTest {

	private DeleteProductServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private ServletContext context;

	@Before
	public void setUp() {
		servlet = new DeleteProductServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		context = mock(ServletContext.class);
		productDao = mock(ProductDao.class);
	}

	@Test
	public void callDoPostInvalidIDThenRetrunError400() throws Exception {
		servlet.doPost(request, response);

		verify(response).sendError(400);
	}

	@Test
	public void callDoGetThenRetrunDeletedPageJSP() throws Exception {
		when(request.getRequestDispatcher(Path.DELETED_PRODUCT_JSP)).thenReturn(dispatcher);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
		;
	}


	@Test
	public void callDoPostValidIDThenRetrunError500() throws Exception {
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostValidIDThenRetrunDeleteProduct() throws Exception {
		Product product = mock(Product.class);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product);
		when(product.getId()).thenReturn(1);

		servlet.doPost(request, response);

		verify(response).sendRedirect(Path.DELETE_PRODUCT);
	}
	
	@Test
	public void callDoPostThenRetrunDeleteProduct() throws Exception {
		Product product = mock(Product.class);
		when(request.getParameter(Param.ID)).thenReturn("20");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(20)).thenReturn(product);
		when(product.getId()).thenReturn(20);

		servlet.doPost(request, response);

		verify(response).sendRedirect(Path.DELETE_PRODUCT);
	}
	
	@Test
	public void callDoPostThenRetrunError404() throws Exception {
		when(request.getParameter(Param.ID)).thenReturn("20");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(Dao.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(20)).thenReturn(null);

		servlet.doPost(request, response);

		verify(response).sendError(404);
	}
	

}
