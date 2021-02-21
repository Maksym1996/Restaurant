package db;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.dao.UserDao;
import db.entity.User;
import db.mysql.MySqlUser;
import exception.DBException;
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
		int actual = userDao.getUsersForManager().size();
		assertEquals(expected, actual);
	}

	@Test
	public void getRegistredUsers() throws Exception {
		int expected = 5;

		int actual = userDao.getUsersByRegistered("true").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUnRegistredUsers() throws Exception {
		int expected = 1;
		Connection con = dataSource.getConnection();
		Statement statement = con.createStatement();
		statement.executeUpdate(
				"INSERT INTO user VALUES(6, 'maxkor@gmail.com', 'Ð�Ð´Ð¼Ð¸Ð½', 'Ð Ð°Ñ�Ð¿Ð¾Ñ€Ñ�Ð´Ð¸Ñ‚Ð¾Ð²Ð¸Ñ‡', '6bb19089370f5bb5478f7ec1b337f255', '0969055334', 'CLIENT', 'false')");

		int actual = userDao.getUsersByRegistered("false").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUnknownUsers() throws Exception {
		int expected = 0;
		int actual = userDao.getUsersByRegistered("Unknown").size();
		assertEquals(expected, actual);
	}

	@Test
	public void getUserByPhoneNumberNotNull() throws Exception {
		User actual = userDao.getUserByNumber("0969055386");
		assertNotNull(actual);

	}

	@Test
	public void getUserByInvalidNumber() throws Exception {
		assertNull(userDao.getUserByNumber("096905538699"));
	}

	@Test
	public void getUserByValidNumberEqualUsers() throws Exception {
		assertEquals("0969055386", userDao.getUserByNumber("0969055386").getPhoneNumber());
	}

	@Test
	public void getUserByIdEqualsTrue() throws Exception {
		User user = new User();
		user.setPhoneNumber("0969055386");

		assertTrue(user.equals(userDao.getUserById(1)));
	}

	@Test
	public void getUserByIdHashCodeTrue() throws Exception {
		User user = new User();
		user.setPhoneNumber("0969055386");

		assertEquals(user.hashCode(), userDao.getUserById(1).hashCode());
	}

	@Test
	public void insertUser() throws Exception {
		userDao.insertUser(Util.createUser("doctor", "no", "123", "qweryt", "admin"));
		assertEquals(6,userDao.getUsersForManager().size());
	}
	
	@Test(expected = DBException.class)
	public void insertDublicatUser() throws Exception {
		userDao.insertUser(Util.createUser("doctor", "no", "kordonets1996@ukr.net", "0969055386", "admin"));
	}

	@Test
	public void getUserByNumberAndPassword() throws Exception {
		User user = userDao.getUserByNumberAndPass("0969055386", "admin");
		assertNotNull(user.getEmail());
	}

	@Test
	public void getUserByNumberAndInvalidPassword() throws Exception {
		User user = userDao.getUserByNumberAndPass("0969055386", "client");
		assertNull(user);
	}

	@Test(expected = DBException.class)
	public void UpdateUserDublicatEmail() throws Exception {
		User user = userDao.getUserById(1);
		user.setEmail("maxkorodnets@gmail.com");
		userDao.updateUser(user);
	}

	@Test
	public void UpdateUserValid() throws Exception {
		User user = userDao.getUserById(1);
		user.setEmail("maxkordonets@gmail.com");
		assertTrue(userDao.updateUser(user));
	}

}
