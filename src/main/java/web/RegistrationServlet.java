package web;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.UserDao;
import db.entity.User;
import util.Util;

/**
 * Servlet implementation class Registrarion
 */
@WebServlet("/Registration")
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher("Registration.jsp");
		dispatcher.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+(?:\\.[a-zA-Z0-9_!#$%&'*+/=?`{|}~^-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
		String phoneNumberRegex = "[0][0-9]{9}";
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{8,}";

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");

		Map<String, String> errors = new HashMap<>();

		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");

		List<User> allUsers = null;
		try {
			allUsers = userDao.getAllUsers();
		} catch (Exception e1) {
			// TODO add some logger 03.02.2021
			throw new IOException();
		}

		for (User user : allUsers) {
			if (user.getEmail().equals(email)) {
				errors.put("emailOrigin", "An account with such email already exists!");
			}
			if (user.getPhoneNumber().equals(phoneNumber)) {
				errors.put("phoneNumberOrigin", "An account with such phone number already exist!");
			}
		}

		if (firstName == null || firstName.isEmpty()) {
			errors.put("firstName", "Provide your first name");
		}
		if (lastName == null || lastName.isEmpty()) {
			errors.put("lastName", "Provide your last name");
		}
		if (email == null || email.isEmpty()) {
			errors.put("email", "Provide your email");
		}

		if (phoneNumber == null || phoneNumber.isEmpty()) {
			errors.put("phoneNumber", "Provide your first name");
		}
		if (password == null || password.isEmpty()) {
			errors.put("password", "Provide your password");
		} else if (!password.equals(confirmPassword)) {
			errors.put("confirmPasswordSame", "The passwords you entered are different");
		}
		if (confirmPassword == null || confirmPassword.isEmpty()) {
			errors.put("confirmPassword", "Confirm password");
		}

		if (!Pattern.matches(emailRegex, email)) {
			errors.put("emailPattern", "The entered email is incorrect");
		}
		if (!Pattern.matches(phoneNumberRegex, phoneNumber)) {
			errors.put("phoneNumberPattern", "The entered phone number is incorrect");
		}
		if (!Pattern.matches(passwordRegex, password)) {
			errors.put("passwordPattern",
					"The password must consist of at least 8 characters, at least one digit, one uppercase and lowercase letters of the Latin alphabet and one special character");
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("Registration.jsp");
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}

		User model = Util.createUser(firstName, lastName, email, phoneNumber, password);

		// TODO update user by phone number

		try {
			userDao.insertUser(model);
		} catch (Exception e) {
			// TODO add some logger 03.02.2021
			throw new IOException();
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("user", model);
		response.sendRedirect("Pizza Preferita");

	}

}
