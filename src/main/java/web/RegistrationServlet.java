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
import consts.CommentConst;
import consts.DaoConst;
import consts.PageConst;
import consts.ParamConst;
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
		LOG.info(CommentConst.BEGIN);

		RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.REGISTRATION_JSP);
		request.setAttribute("SITE_KEY", CaptchaConst.SITE_KEY);
		LOG.debug("Set in request SITE KEY to reCaptcha");

		dispatcher.forward(request, response);
		LOG.info(CommentConst.FORWARD + PageConst.REGISTRATION_JSP);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(CommentConst.BEGIN);

		String firstName = request.getParameter(ParamConst.FIRST_NAME);
		LOG.debug("firstName" + firstName);

		String lastName = request.getParameter(ParamConst.LAST_NAME);
		LOG.debug("lastName" + lastName);

		String email = request.getParameter(ParamConst.EMAIL);
		LOG.debug("email" + email);

		String phoneNumber = request.getParameter(ParamConst.PHONE_NUMBER);
		LOG.debug("phoneNumber" + phoneNumber);

		String password = request.getParameter(ParamConst.PASSWORD);
		LOG.debug("password" + password);

		String confirmPassword = request.getParameter(ParamConst.CONFIRM_PASSWORD);
		LOG.debug("confirmPassword" + confirmPassword);

		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		LOG.debug("Get g-recaptcha-response");

		Map<String, String> errors = Validator.registrationValidator(firstName, lastName, email, phoneNumber, password,
				confirmPassword);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(DaoConst.USER);

		List<User> allRegistredUsers = null;
		try {
			allRegistredUsers = userDao.getUsersByRegistered("true");
			LOG.debug("getUsersByRegistered('true')");
		} catch (DBException e) {
			response.sendError(500);
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);

			return;
		}
		for (User user : allRegistredUsers) {
			if (user.getEmail().equals(email)) {
				errors.put(ParamConst.EMAIL, "An account with such email already exists!");
				LOG.debug("such email already exists");
			}
			if (user.getPhoneNumber().equals(phoneNumber)) {
				errors.put(ParamConst.PHONE_NUMBER, "An account with such phone number already exist!");
				LOG.debug("such phone number already exist");
			}
		}

		// Verify CAPTCHA.
		if (!VerifyCaptcha.verify(gRecaptchaResponse, request)) {
			LOG.debug("reCaptcha is not Valid");
			errors.put("captchaResponse", "Captcha invalid!");
		}
		
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(PageConst.REGISTRATION_JSP);
			request.setAttribute(ParamConst.ERRORS, errors);
			LOG.info(CommentConst.FORWARD + PageConst.REGISTRATION_JSP);

			dispatcher.forward(request, response);
			LOG.debug(CommentConst.FORWARD_WITH_PARAMETR + errors);
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
			LOG.error(CommentConst.DB_EXCEPTION + e.getMessage());
			LOG.info(CommentConst.REDIRECT + 500);

			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute(ParamConst.USER, userToInsert);
		LOG.debug("Set user in session " + userToInsert);

		response.sendRedirect(PageConst.PIZZA_PREFERITA);
		LOG.info(CommentConst.REDIRECT + PageConst.PIZZA_PREFERITA);
	}
}
