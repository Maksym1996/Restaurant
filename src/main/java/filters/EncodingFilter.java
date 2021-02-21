package filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;

/**
 * 
 * Servlet filter for setting the required encoding
 */
@WebFilter("/EncodingFilter")
public class EncodingFilter implements Filter {
	private static final Logger log = LogManager.getLogger(EncodingFilter.class);

	private String encoding = "utf-8";

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		log.info(Comment.BEGIN);
		request.setCharacterEncoding(encoding);
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		log.info(Comment.BEGIN);
		String encodingParam = fConfig.getInitParameter("encoding");
		if (encodingParam != null) {
			encoding = encodingParam;
		}
	}

	@Override
	public void destroy() {
		// nothing do
	}

}
