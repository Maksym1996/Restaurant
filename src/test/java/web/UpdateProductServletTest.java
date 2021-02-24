package web;

import static org.mockito.Mockito.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import consts.DaoConst;
import consts.PageConst;
import consts.Param;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;

public class UpdateProductServletTest {
	private UpdateProductServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private RequestDispatcher dispatcher;
	private ProductDao productDao;
	private ServletContext context;

	@Before
	public void setUp() {
		servlet = new UpdateProductServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		dispatcher = mock(RequestDispatcher.class);
		productDao = mock(ProductDao.class);
		context = mock(ServletContext.class);
		productDao = mock(ProductDao.class);
	}

	@Test
	public void callDoGetWithoutParamReturnError400() throws Exception {
		
		
		servlet.doGet(request, response);

		verify(response).sendError(400);
	}

	@Test
	public void callDoGetWithParamReturnError500() throws Exception {
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenThrow(new DBException(null));

		servlet.doGet(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoGetWithParamReturnForwardUpdateProductJSP() throws Exception {
		Product product = mock(Product.class);
		when(request.getRequestDispatcher(PageConst.UPDATE_PRODUCT_JSP)).thenReturn(dispatcher);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product);

		servlet.doGet(request, response);

		verify(dispatcher).forward(request, response);
	}
	
	@Test
	public void callDoPostWithoutParamReturnError400() throws Exception {
		
		
		servlet.doPost(request, response);

		verify(response).sendError(400);
	}

	@Test
	public void callDoPostWithIDReturnError500() throws Exception {
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostWithIDReturnError404() throws Exception {
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(null);

		servlet.doPost(request, response);

		verify(response).sendError(404);
	}

	@Test
	public void callDoPostWithIdAndNameReturnForwardUpadateProductJSP() throws Exception {
		Product product1 = mock(Product.class);
		Product product2 = mock(Product.class);
		when(request.getRequestDispatcher(PageConst.UPDATE_PRODUCT_JSP)).thenReturn(dispatcher);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.NAME)).thenReturn("Паперони");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product1);
		when(productDao.getProductByName("Паперони")).thenReturn(product2);

		servlet.doPost(request, response);

		verify(dispatcher).forward(request, response);
		;
	}

	@Test
	public void callDoPostWithIdAndNameReturnError500() throws Exception {
		Product product1 = mock(Product.class);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.NAME)).thenReturn("Паперони");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product1);
		when(productDao.getProductByName("Паперони")).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}

	@Test
	public void callDoPostWithValidParamentsReturnRedirectPizzaPreferita() throws Exception {
		Product product1 = mock(Product.class);
		Product product2 = mock(Product.class);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.NAME)).thenReturn("Паперони");
		when(request.getParameter(Param.PRICE)).thenReturn("10");
		when(request.getParameter(Param.DESCRIPTION)).thenReturn("Паперони");
		when(request.getParameter(Param.IMAGE_LINK)).thenReturn("Картинка");
		when(request.getParameter(Param.CATEGORY)).thenReturn("Pizza");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product1);
		when(productDao.getProductByName("Паперони")).thenReturn(product2);
		when(productDao.updateProduct(product2)).thenReturn(true);

		servlet.doPost(request, response);

		verify(response).sendRedirect(PageConst.PIZZA_PREFERITA);
	}

	@Test
	public void callDoPostWithValidParamentsReturnError500() throws Exception {
		Product product1 = mock(Product.class);
		Product product2 = mock(Product.class);
		when(request.getParameter(Param.ID)).thenReturn("1");
		when(request.getParameter(Param.NAME)).thenReturn("Паперони");
		when(request.getParameter(Param.PRICE)).thenReturn("10");
		when(request.getParameter(Param.DESCRIPTION)).thenReturn("Паперони");
		when(request.getParameter(Param.IMAGE_LINK)).thenReturn("Картинка");
		when(request.getParameter(Param.CATEGORY)).thenReturn("Pizza");
		when(request.getServletContext()).thenReturn(context);
		when(context.getAttribute(DaoConst.PRODUCT)).thenReturn(productDao);
		when(productDao.getProductById(1)).thenReturn(product1);
		when(productDao.getProductByName("Паперони")).thenReturn(product2);
		when(productDao.updateProduct(any())).thenThrow(new DBException(null));

		servlet.doPost(request, response);

		verify(response).sendError(500);
	}
}
