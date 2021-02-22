package web;

import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;

import consts.ParamConst;

public class LanguageServletTest {
	private static final String PATH = "http://localhost:8080/restaurant-web/Pizza%20Preferita?";

	private LanguageServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private HttpSession session;

	@Before
	public void setUp() {
		servlet = new LanguageServlet();
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		session = mock(HttpSession.class);
	}

	@Test
	public void callDoPostThenReturnPath() throws Exception {

		when(request.getSession(true)).thenReturn(session);
		when(request.getParameter(ParamConst.LANG)).thenReturn("ru");
		when(request.getHeader(ParamConst.REFERER)).thenReturn(PATH);

		servlet.doPost(request, response);

		verify(response).sendRedirect(PATH);
	}
}
