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

import consts.Dao;
import consts.Page;
import consts.Param;
import db.dao.UserDao;
import db.entity.User;
import util.Util;
import util.Validator;
import util.VerifyCaptcha;

/**
 * Servlet implementation class Registrarion
 */
@WebServlet("/Registration")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String firstName = request.getParameter(Param.FIRST_NAME);
		String lastName = request.getParameter(Param.LAST_NAME);
		String email = request.getParameter(Param.EMAIL);
		String phoneNumber = request.getParameter(Param.PHONE_NUMBER);
		String password = request.getParameter(Param.PASSWORD);
		String confirmPassword = request.getParameter(Param.CONFIRM_PASSWORD);
		String gRecaptchaResponse = request.getParameter("g-recaptcha-response");

		Map<String, String> errors = Validator.registrationValidator(firstName, lastName, email, phoneNumber, password,
				confirmPassword);

		UserDao userDao = (UserDao) request.getServletContext().getAttribute(Dao.USER);

		List<User> allRegistredUsers = null;
		try {
			allRegistredUsers = userDao.getUsersByRegistered("true");
		} catch (Exception e1) {
			// TODO add some logger 03.02.2021
			response.sendError(500);
			return;
		}

		for (User user : allRegistredUsers) {
			if (user.getEmail().equals(email)) {
				errors.put(Param.EMAIL, "An account with such email already exists!");
			}
			if (user.getPhoneNumber().equals(phoneNumber)) {
				errors.put(Param.PHONE_NUMBER, "An account with such phone number already exist!");
			}
		}
		

        // Verify CAPTCHA.
       if(!VerifyCaptcha.verify(gRecaptchaResponse, request)) {

            errors.put("captchaResponse", "Captcha invalid!");
        }
		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher(Page.REGISTRATION_JSP);
			request.setAttribute(Param.ERRORS, errors);
			dispatcher.forward(request, response);
			return;
		}

		User model = Util.createUser(firstName, lastName, email, phoneNumber, password);
		User user = null;
		try {
			user = userDao.getUserByNumber(phoneNumber);
			if (user.getId() == 0) {
				user = userDao.getUserById(userDao.insertUser(model));
			} else {
				userDao.updateUser(model);
			}
		} catch (Exception e) {
			// TODO add some logger 03.02.2021
			System.err.println(e);
			response.sendError(500);
			return;
		}

		HttpSession session = request.getSession(true);
		session.setAttribute(Param.USER, user);
		response.sendRedirect(Page.PIZZA_PREFERITA);
	}
}
