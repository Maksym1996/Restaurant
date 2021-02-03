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
		String porchRegex = "[1-9] {1} \\d*";
		String apartmentRegex = "[1-9]{1}\\d*";
		String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{8,}";

		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String phoneNumber = request.getParameter("phoneNumber");
		String password = request.getParameter("password");
		String confirmPassword = request.getParameter("confirmPassword");
		String street = request.getParameter("street");
		String house = request.getParameter("house");
		String apartment = request.getParameter("apartment");
		String porch = request.getParameter("porch");

		Map<String, String> errors = new HashMap<>();

		UserDao userDao = (UserDao) request.getServletContext().getAttribute("userDao");

		List<User> allUsers = null;
		try {
			allUsers = userDao.getAllUsers();
		} catch (Exception e1) {
			//TODO add some logger 03.02.2021
			response.sendRedirect("SomeWrong.jsp");
		}

		for (User user : allUsers) {
			if (email.equals(user.getEmail())) {
				errors.put("emailOrigin", "An account with such email already exists!");
			}
			if (phoneNumber.equals(user.getPhoneNumber())) {
				errors.put("phoneNumberOrigin", "An account with such phone number already exist!");
			}
		}

		if (firstName == null) {
			errors.put("firstName", "Provide your first name");
		}
		if (lastName == null) {
			errors.put("lastName", "Provide your last name");
		}
		if (email == null) {
			errors.put("email", "Provide your email");
		}
		if (phoneNumber == null) {
			errors.put("phoneNumber", "Provide your first name");
		}
		if (password == null) {
			errors.put("password", "Provide your password");
		}
		if (confirmPassword == null) {
			errors.put("confirmPassword", "Confirm password");
		}
		if (password != null && confirmPassword != null && !password.equals(confirmPassword)) {
			errors.put("confirmPasswordSame", "The passwords you entered are different");
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
		if (!Pattern.matches(apartmentRegex, apartment)) {
			errors.put("apartmentPattern", "Сannot contain any characters unless numbers");
		}
		if (!Pattern.matches(porchRegex, porch)) {
			errors.put("porchPattern", "Сannot contain any characters unless numbers");
		}

		if (!errors.isEmpty()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("Registration.jsp");
			request.setAttribute("errors", errors);
			dispatcher.forward(request, response);
			return;
		}

		User model = User.createUser(firstName, lastName, email, phoneNumber, confirmPassword, street, house,
				Integer.parseInt(apartment), Integer.parseInt(porch));

		try {
			userDao.insertUser(model);
		} catch (Exception e) {
			//TODO add some logger 03.02.2021
			response.sendRedirect("SomeWrong.jsp");
		}

		HttpSession session = request.getSession(true);
		session.setAttribute("user", model);
		response.sendRedirect("Pizza Preferita");

	}

}
