package web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import consts.ParamConst;

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
		LOG.info(CommentConst.BEGIN);

		String lang = request.getParameter(ParamConst.LANG);
		String path = request.getHeader(ParamConst.REFERER);

		LOG.debug("Lang " + lang);
		LOG.debug("Path " + lang);

		HttpSession session = request.getSession(true);
		session.setAttribute(ParamConst.LANG, lang);

		LOG.debug("Set lang in session " + lang);
		LOG.info(CommentConst.REDIRECT + path);

		response.sendRedirect(path);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);
		LOG.info("doGet()");

		doGet(request, response);
	}

}
