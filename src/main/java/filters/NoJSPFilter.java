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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;

/**
 * Servlet Filter to restrict access to *.jsp files
 */
@WebFilter("/NoJSPFilter")
public class NoJSPFilter implements Filter {

	private static final Logger log = LogManager.getLogger(NoJSPFilter.class);

	private boolean active = false;

	@Override
	public void destroy() {
		// nothing do
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info(Comment.BEGIN);
		if (active) {
			if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
				throw new ServletException("non-HTTP request or response");
			}
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			log.info(Comment.REDIRECT + 404);
			httpServletResponse.sendError(404);
			return;
		}
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		log.info(Comment.BEGIN);
		String act = config.getInitParameter("active");
		if (act != null) {
			active = (act.equalsIgnoreCase("TRUE"));
		}
	}

}
