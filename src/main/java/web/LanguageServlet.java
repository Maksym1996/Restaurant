package web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.Param;

import java.io.IOException;

/**
 * Servlet that implements interface language switching
 *
 */
@WebServlet("/language")
public class LanguageServlet extends HttpServlet {

	private static final long serialVersionUID = 2689567392392305656L;

	private static final Logger LOG = LogManager.getLogger(LanguageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		String lang = request.getParameter(Param.LANG);
		LOG.debug("Lang " + lang);
		
		String path = request.getHeader(Param.REFERER);
		LOG.debug("Path " + lang);

		HttpSession session = request.getSession(true);
		session.setAttribute(Param.LANG, lang);
		LOG.debug("Set lang in session " + lang);
	
		response.sendRedirect(path);
		LOG.info(Log.REDIRECT + path);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);
		LOG.info("doGet()");

		doGet(request, response);
	}

}
