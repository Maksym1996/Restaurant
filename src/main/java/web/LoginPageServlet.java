package web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.Dao;
import consts.Path;
import consts.Param;
import db.dao.ReceiptDao;
import db.dao.UserDao;
import db.entity.Receipt;
import db.entity.User;
import exception.DBException;
import util.Validator;

/**
 * Servlet that implements the functionality of authorizing and displaying a
 * custom page
 */
@WebServlet("/Login page")
public class LoginPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(LoginPageServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug(Log.BEGIN);

		HttpSession session = request.getSession(true);
		String forwardPage;

		String logout = request.getParameter(Param.LOG_OUT);
		LOG.trace("logout " + logout);

		if (Param.LOG_OUT.equals(logout)) {
			session.invalidate();
			LOG.trace("session invalidate");
			forwardPage = Path.LOGIN_PAGE_JSP;
		} else if (session == null || session.getAttribute(Param.USER) == null) {
			LOG.trace("session is null or user from session is null");
			forwardPage = Path.LOGIN_PAGE_JSP;
		} else {
			User user = (User) session.getAttribute(Param.USER);
			ReceiptDao receiptDao = (ReceiptDao) request.getServletContext().getAttribute(Dao.RECEIPT);
			List<Receipt> listOfReceipts = null;
			try {
				listOfReceipts = receiptDao.getListOfReceiptsByUserId(user.getId());
			} catch (DBException e) {
				LOG.error(Log.DB_EXCEPTION + e.getMessage());
				response.sendError(500);
				LOG.trace(Log.REDIRECT + 500);
				return;
			}
			request.setAttribute(Param.RECEIPTS_LIST, listOfReceipts);
			forwardPage = Path.ACCOUNT_JSP;
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher(forwardPage);
		dispatcher.forward(request, response);
		LOG.debug(Log.FORWARD + forwardPage);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.debug(Log.BEGIN);

		HttpSession session = request.getSession(true);

		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		LOG.trace("phoneNumber " + phoneNumber);

		String password = request.getParameter(Param.PASSWORD);
		LOG.trace("password " + password);

		Map<String, String> errors = Validator.authorizationValidator(phoneNumber, password);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);
		User user = null;
		try {
			user = userDao.getUserByNumberAndPass(phoneNumber, password);
			if (user == null) {
				errors.put(Param.NO_USER, "User with such data does not exist");
				LOG.trace("user == null");
			} else {
				session.setAttribute(Param.USER, user);
				LOG.trace("Set user to session" + user.toString());

				session.setAttribute(Param.ROLE, user.getRole());
				LOG.trace("Set role to session: " + user.getRole());
			}
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.debug(Log.REDIRECT + 500);

			return;
		}
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Path.LOGIN_PAGE_JSP);
			request.setAttribute(Param.ERRORS, errors);
			LOG.trace(Log.FORWARD + Path.LOGIN_PAGE_JSP);

			dispatcher.forward(request, response);
			LOG.debug(Log.FORWARD_WITH_PARAMETR + "errors " + errors);
			return;
		}
		response.sendRedirect(Path.LOGIN_PAGE);
		LOG.debug(Log.REDIRECT + Path.LOGIN_PAGE);
	}

}
