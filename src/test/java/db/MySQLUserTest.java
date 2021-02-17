package db;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import db.dao.UserDao;
import db.entity.User;
import db.mysql.MySqlUser;
import util.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class MySQLUserTest {
	private static final String JDBC_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:~/test;MODE=MySQL";
	private static final String USER = "youruser";
	private static final String PASS = "yourpassword";

	private static DataSource getDatasource() {
		HikariConfig config = new HikariConfig();
		config.setUsername(USER);
		config.setPassword(PASS);
		config.setJdbcUrl(DB_URL);
		DataSource ds = new HikariDataSource(config);
		return ds;
	}

	static UserDao userDao = new MySqlUser(getDatasource());

	@Before
	public void beforeTest() throws SQLException, ClassNotFoundException {
		Class.forName(JDBC_DRIVER);

		try (Connection con = getDatasource().getConnection(); Statement statement = con.createStatement()) {
			String sql = "DROP TABLE IF EXISTS user\n;" + "CREATE TABLE IF NOT EXISTS user(\r\n"
					+ "id INTEGER NOT NULL AUTO_INCREMENT,\r\n" + "  email VARCHAR(45),\r\n"
					+ "  first_name VARCHAR(45),\r\n" + "  last_name VARCHAR(45),\r\n" + "  password VARCHAR(45),\r\n"
					+ "  phone_number VARCHAR(10) NOT NULL,\r\n" + "  role VARCHAR(45) NOT NULL DEFAULT 'CLIENT',\r\n"
					+ "  registered VARCHAR(6) NOT NULL, \r\n" + "  PRIMARY KEY (`id`),\r\n" + "  UNIQUE (id),\r\n"
					+ "  UNIQUE (email),\r\n" + "  UNIQUE (phone_number));"
					+ "INSERT INTO user VALUES (1, 'kordonets1996@ukr.net', 'Максим', 'Кордонец', '36d2e385ff8453a66347bf048f11668c', '0969055386', 'MANAGER', 'true');\r\n"
					+ "INSERT INTO user VALUES (2, 'Povar@ukr.net', 'Повар', 'Куховаров', '6bb19089370f5bb5478f7ec1b337f255', '0969055385', 'COOK', 'true');\r\n"
					+ "INSERT INTO user VALUES (3, 'Courier@ukr.net', 'Курьер', 'Доставщиков', '6bb19089370f5bb5478f7ec1b337f255', '0969055384', 'COURIER', 'true');\r\n"
					+ "INSERT INTO user VALUES (4, 'kordonetsmax@gmail.com', 'Клиент', 'Посетитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055383', 'CLIENT', 'true');\r\n"
					+ "INSERT INTO user VALUES (5, 'maxkorodnets@gmail.com', 'Админ', 'Распорядитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055382', 'ADMIN', 'true');\r\n";
			statement.executeUpdate(sql);
		}
	}

	@Test
	public void getAllUserExpectFive() throws Exception {
		int expected = 5;
		int actual = userDao.getAllUsers().size();
		assertEquals(expected, actual);
	}

	@Test
	public void getRegistredUsers() throws Exception {
		int expected = 5;

		int actual = userDao.getRegisteredUsers("true").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUnRegistredUsers() throws Exception {
		int expected = 1;
		Connection con = getDatasource().getConnection();
		Statement statement = con.createStatement();
		statement.executeUpdate(
				"INSERT INTO user VALUES(6, 'maxkor@gmail.com', 'Админ', 'Распорядитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055334', 'CLIENT', 'false')");

		int actual = userDao.getRegisteredUsers("false").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUnknownUsers() throws Exception {
		int expected = 0;
		int actual = userDao.getRegisteredUsers("Unknown").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUserByPhoneNumberNotNull() throws Exception {
		User actual = userDao.getUser("0969055386");
		assertNotNull(actual);

	}

	@Test
	public void getUserByInvalidNumber() throws Exception {
		assertNull(userDao.getUser("096905538699").getPhoneNumber());
	}

	@Test
	public void getUserByValidNumberEqualUsers() throws Exception {
		assertEquals("0969055386", userDao.getUser("0969055386").getPhoneNumber());
	}

	@Test
	public void getUserByIdEqualsTrue() throws Exception {
		User user = new User();
		user.setPhoneNumber("0969055386");

		assertTrue(user.equals(userDao.getUser(1)));
	}

	@Test
	public void getUserByIdHashCodeTrue() throws Exception {
		User user = new User();
		user.setPhoneNumber("0969055386");

		assertEquals(user.hashCode(), userDao.getUser(1).hashCode());
	}

	@Test
	public void insertUser() throws Exception {
		int actual = userDao.insertUser(Util.createUser("doctor", "no", "123", "qweryt", "admin"));
		assertEquals(6, actual);
	}

	@Test
	public void getUserByNumberAndPassword() throws Exception {
		User user = userDao.getUser("kordonets1996@ukr.net", "admin");
		assertNotNull(user);
	}

	@Test
	public void getUserByNumberAndInvalidPassword() throws Exception {
		User user = userDao.getUser("kordonets1996@ukr.net", "client");
		assertNull(user.getPhoneNumber());
	}

	@Test(expected = NullPointerException.class)
	public void getUserByNullNumberAndNullPassword() throws Exception {
		userDao.getUser(null, null);
	}

	@Test(expected = SQLException.class)
	public void UpdateUserDublicatEmail() throws Exception {
		User user = userDao.getUser(1);
		user.setEmail("maxkorodnets@gmail.com");
		userDao.updateUser(user);
	}

	@Test
	public void UpdateUserValid() throws Exception {
		User user = userDao.getUser(1);
		user.setEmail("maxkordonets@gmail.com");
		assertTrue(userDao.updateUser(user));
	}

}
