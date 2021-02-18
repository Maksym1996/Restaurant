package db;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.dao.UserDao;
import db.entity.User;
import db.mysql.MySqlUser;
import util.SqlTestUtil;
import util.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class MySQLUserTest {

	private static DataSource dataSource;

	private UserDao userDao;

	@BeforeClass
	public static void setUpClass() {
		dataSource = SqlTestUtil.getDatasource();
	}
	
	@Before
	public void beforeTest() throws SQLException, ClassNotFoundException {
		userDao = new MySqlUser(dataSource);
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
			statement.executeUpdate(SqlTestUtil.getSqlScript());
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
		Connection con = dataSource.getConnection();
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
		userDao.insertUser(Util.createUser("doctor", "no", "123", "qweryt", "admin"));
		assertEquals(6,userDao.getAllUsers().size());
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
