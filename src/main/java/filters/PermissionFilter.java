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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import consts.Param;
import util.UserRole;

/**
 * Servlet Filter for differentiate access rights
 */
@WebFilter("/PermissionFilter")
public class PermissionFilter implements Filter {

	private static final Logger log = LogManager.getLogger(PermissionFilter.class);

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
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;

			HttpSession session = httpServletRequest.getSession();
			if (session == null || session.getAttribute(Param.ROLE) == null) {
				log.info(Comment.REDIRECT + 401);
				httpServletResponse.sendError(401);
				return;
			} else if (session.getAttribute(Param.ROLE) != UserRole.ADMIN) {
				log.info(Comment.REDIRECT + 403);
				httpServletResponse.sendError(403);
				return;
			}
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
