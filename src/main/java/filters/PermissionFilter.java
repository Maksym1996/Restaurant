package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class NoUserFilter
 */
@WebFilter("/PermissionFilter")
public class PermissionFilter implements Filter {

	private boolean active = false;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (active) {
			if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
				throw new ServletException("non-HTTP request or response");
			}
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			HttpSession session = httpServletRequest.getSession(true);
			if (session == null || session.getAttribute("role") == null) {
				httpServletResponse.sendError(401);
				return;
			} else if(!session.getAttribute("role").equals("ADMIN")) {
				httpServletResponse.sendError(403);
				return;
			}

		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		String act = config.getInitParameter("active");
		if (act != null) {
			active = (act.equalsIgnoreCase("TRUE"));
		}
	}

}
