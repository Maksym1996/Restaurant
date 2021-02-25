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

import consts.CaptchaConst;
import consts.Log;
import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.UserDao;
import db.entity.User;
import exception.DBException;
import util.Util;
import util.Validator;
import util.VerifyCaptcha;

/**
 * Servlet that implements the new user registration process
 */
@WebServlet("/Registration")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LogManager.getLogger(RegistrationServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
		request.setAttribute("SITE_KEY", CaptchaConst.SITE_KEY);
		LOG.debug("Set in request SITE KEY to reCaptcha");

		dispatcher.forward(request, response);
		LOG.info(Log.FORWARD + Page.REGISTRATION_JSP);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		String firstName = request.getParameter(Param.FIRST_NAME);
		LOG.debug("firstName" + firstName);

		String lastName = request.getParameter(Param.LAST_NAME);
		LOG.debug("lastName" + lastName);

		String email = request.getParameter(Param.EMAIL);
		LOG.debug("email" + email);

		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		LOG.debug("phoneNumber" + phoneNumber);

		String password = request.getParameter(Param.PASSWORD);
		LOG.debug("password" + password);

		String confirmPassword = request.getParameter(Param.CONFIRM_PASSWORD);
		LOG.debug("confirmPassword" + confirmPassword);

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		LOG.debug("Get g-recaptcha-response");

		Map<String, String> errors = Validator.registrationValidator(firstName, lastName, email, phoneNumber, password,
				confirmPassword);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);

		List<User> allRegistredUsers = null;
		try {
			allRegistredUsers = userDao.getUsersByRegistered("true");
			LOG.debug("getUsersByRegistered('true')");
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}
		for (User user : allRegistredUsers) {
			if (user.getEmail().equals(email)) {
				errors.put(Param.EMAIL, "An account with such email already exists!");
				LOG.debug("such email already exists");
			}
			if (user.getPhoneNumber().equals(phoneNumber)) {
				errors.put(Param.PHONE_NUMBER, "An account with such phone number already exist!");
				LOG.debug("such phone number already exist");
			}
		}

		// Verify CAPTCHA.
		if (!VerifyCaptcha.verify(gRecaptchaResponse, request)) {
			LOG.debug("reCaptcha is not Valid");
			errors.put("captchaResponse", "Captcha invalid!");
		}
		
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
			request.setAttribute(Param.ERRORS, errors);
			LOG.info(Log.FORWARD + Page.REGISTRATION_JSP);

			dispatcher.forward(request, response);
			LOG.debug(Log.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		User userToInsert = Util.createUser(firstName, lastName, email, phoneNumber, password);
		User testUser = null;
		try {
			testUser = userDao.getUserByNumber(phoneNumber);
			if (testUser == null) {
				int userId = userDao.insertUser(userToInsert);
				LOG.debug("userId" + userId);
				userToInsert.setId(userId);
			} else {
				userDao.updateUser(userToInsert);
				userToInsert.setId(testUser.getId());
				userToInsert.setRole(testUser.getRole());
			}
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			LOG.info(Log.REDIRECT + 500);

			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute(Param.USER, userToInsert);
		LOG.debug("Set user in session " + userToInsert);
		
		session.setAttribute(Param.USER, userToInsert.getRole());
		LOG.debug("Set role in session " + userToInsert.getRole());

		response.sendRedirect(Page.PIZZA_PREFERITA);
		LOG.info(Log.REDIRECT + Page.PIZZA_PREFERITA);
	}
}
