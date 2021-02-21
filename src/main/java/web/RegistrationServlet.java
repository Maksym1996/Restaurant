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

import consts.Comment;
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

	private static final Logger log = LogManager.getLogger(RegistrationServlet.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
		log.info(Comment.FORWARD + Page.REGISTRATION_JSP);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info(Comment.BEGIN);
		String firstName = request.getParameter(Param.FIRST_NAME);
		String lastName = request.getParameter(Param.LAST_NAME);
		String email = request.getParameter(Param.EMAIL);
		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		String password = request.getParameter(Param.PASSWORD);
		String confirmPassword = request.getParameter(Param.CONFIRM_PASSWORD);
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

		log.debug("firstName" + firstName);
		log.debug("lastName" + lastName);
		log.debug("email" + email);
		log.debug("phoneNumber" + phoneNumber);
		log.debug("password" + password);
		log.debug("confirmPassword" + confirmPassword);

		Map<String, String> errors = Validator.registrationValidator(firstName, lastName, email, phoneNumber, password,
				confirmPassword);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);

		List<User> allRegistredUsers = null;
		try {
			allRegistredUsers = userDao.getUsersByRegistered("true");
			log.debug("getUsersByRegistered('true')");
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		for (User user : allRegistredUsers) {
			if (user.getEmail().equals(email)) {
				errors.put(Param.EMAIL, "An account with such email already exists!");
				log.debug("such email already exists");
			}
			if (user.getPhoneNumber().equals(phoneNumber)) {
				errors.put(Param.PHONE_NUMBER, "An account with such phone number already exist!");
				log.debug("such phone number already exist");
			}
		}

		// Verify CAPTCHA.
		if (!VerifyCaptcha.verify(gRecaptchaResponse, request)) {
			log.debug("reCaptcha is not Valid");
			errors.put("captchaResponse", "Captcha invalid!");
		}
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
			request.setAttribute(Param.ERRORS, errors);
			dispatcher.forward(request, response);
			log.info(Comment.FORWARD + Page.REGISTRATION_JSP);
			log.debug(Comment.FORWARD_WITH_PARAMETR + errors);
			return;
		}

		User model = Util.createUser(firstName, lastName, email, phoneNumber, password);
		User user = null;
		try {
			user = userDao.getUserByNumber(phoneNumber);
			if (user == null) {
				int userId = userDao.insertUser(model);
				log.debug("userId" + userId);
				model.setId(userId);
			} else {
				userDao.updateUser(model);
				model.setId(user.getId());
				model.setRole(user.getRole());
			}
		} catch (DBException e) {
			log.error(Comment.DB_EXCEPTION + e.getMessage());
			log.info(Comment.REDIRECT + 500);
			response.sendError(500);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute(Param.USER, model);
		log.debug("Set user in session " + model);
		log.info(Comment.REDIRECT + Page.PIZZA_PREFERITA);
		response.sendRedirect(Page.PIZZA_PREFERITA);
	}
}
