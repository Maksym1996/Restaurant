package web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import consts.Param;

import java.io.IOException;

/**
 * Servlet that implements interface language switching
 *
 */
@WebServlet("/language")
public class LanguageServlet extends HttpServlet {

	private static final long serialVersionUID = 2689567392392305656L;

	private static final Logger log = LogManager.getLogger(LanguageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		String lang = request.getParameter(Param.LANG);
		String path = request.getHeader(Param.REFERER);
		log.debug("Lang " + lang);
		log.debug("Path " + lang);

		HttpSession session = request.getSession(true);
		session.setAttribute(Param.LANG, lang);
		log.debug("Set lang in session " + lang);
		log.info(Comment.REDIRECT + path);
		response.sendRedirect(path);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		log.info("doGet()");
		doGet(request, response);
	}

}
