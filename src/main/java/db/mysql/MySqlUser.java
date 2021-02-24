package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import consts.Log;
import db.dao.UserDao;
import db.entity.User;
import db.entity.UserWithPerformedOrders;
import exception.DBException;
import util.UserRole;
import util.Util;

/**
 * The class implementing UserDao for DBMS MySQL
 *
 */
public class MySqlUser extends AbstractMySqlDao implements UserDao {
	private static final String SELECT_ALL_USERS = "SELECT * FROM user";
	private static final String SELECT_USERS_BY_REGISTERED = "SELECT * FROM user WHERE registered = ?";
	private static final String SELECT_USER_BY_NUMBER_AND_PASS = "SELECT * FROM user WHERE phone_number = ? AND password = ?";
	private static final String SELECT_USER_BY_ID = "SELECT * FROM user WHERE id = ?";
	private static final String SELECT_USER_BY_NUMBER = "SELECT * FROM user WHERE phone_number = ?";
	private static final String SELECT_PERFORMED_ORDERS = "SELECT u.phone_number, u.first_name, u.last_name, COUNT(o.id) FROM user AS u JOIN orders AS o ON u.id = o.user_id AND o.state = 'PERFORMED' GROUP BY u.phone_number";
	private static final String INSERT_USER = "INSERT INTO user VALUES (DEFAULT,?,?,?,?,?, DEFAULT, ?)";
	private static final String UPDATE_USER_BY_NUMBER = "UPDATE user SET first_name=?, last_name=?, password = ?, registered=?, email = ? WHERE phone_number = ?";

	private final DataSource dataSource;

	public MySqlUser(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<UserWithPerformedOrders> getUserAndHimCountPerformedOrders() throws DBException {
		log.debug(Log.START);
		List<UserWithPerformedOrders> usersWithPerformedOrders = new ArrayList<>();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SELECT_PERFORMED_ORDERS);
			while (resultSet.next()) {
				usersWithPerformedOrders.add(extractionUserWithPerformedOrder(resultSet));
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, statement, resultSet);
		}
		log.debug(Log.FINISH_WITH + usersWithPerformedOrders.size());
		return usersWithPerformedOrders;
	}

	private UserWithPerformedOrders extractionUserWithPerformedOrder(ResultSet resultSet) throws SQLException {
		UserWithPerformedOrders userWithPerformedOrder = new UserWithPerformedOrders();
		int k = 1;
		userWithPerformedOrder.setPhoneNumber(resultSet.getString(k++));
		userWithPerformedOrder.setFirstName(resultSet.getString(k++));
		userWithPerformedOrder.setLastName(resultSet.getString(k++));
		userWithPerformedOrder.setCountOrders(resultSet.getInt(k));
		log.trace(Log.EXTRACTION + userWithPerformedOrder.toString());
		return userWithPerformedOrder;
	}

	@Override
	public List<User> getUsersForManager() throws DBException {
		log.debug(Log.START);
		List<User> allUser = new ArrayList<>();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SELECT_ALL_USERS);
			while (resultSet.next()) {
				allUser.add(extraction(resultSet));
			}

		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, statement, resultSet);
		}
		log.debug(Log.FINISH_WITH + allUser.size());
		return allUser;
	}

	@Override
	public List<User> getUsersByRegistered(String registered) throws DBException {
		log.debug(Log.START);
		List<User> registeredUser = new ArrayList<>();
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("registered = " + registered);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_USERS_BY_REGISTERED);
			preparedStatement.setString(1, registered);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				registeredUser.add(extraction(resultSet));
			}

		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + registeredUser.size());
		return registeredUser;
	}

	@Override
	public int insertUser(User user) throws DBException {
		log.debug(Log.START);
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int userId = 0;
		log.trace("User = " + user.toString());
		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			preparedStatement.setString(k++, user.getEmail());
			preparedStatement.setString(k++, user.getFirstName());
			preparedStatement.setString(k++, user.getLastName());
			preparedStatement.setString(k++, Util.stringToMD5(user.getPassword()));
			preparedStatement.setString(k++, user.getPhoneNumber());
			preparedStatement.setString(k++, user.getRegistered());

			if (preparedStatement.executeUpdate() > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					userId = resultSet.getInt(1);
					user.setId(userId);
				}
			}
			connect.commit();
			log.trace(Log.COMMIT);
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + userId);
		return userId;
	}

	@Override
	public User getUserByNumberAndPass(String phoneNumber, String password) throws DBException {
		log.debug(Log.START);
		User user = null;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("Phone Numeber = " + phoneNumber);
		log.trace("Password = " + password);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_USER_BY_NUMBER_AND_PASS);
			preparedStatement.setString(1, phoneNumber);
			preparedStatement.setString(2, Util.stringToMD5(password));
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = extraction(resultSet);
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + user);
		return user;
	}

	@Override
	public User getUserByNumber(String phoneNumber) throws DBException {
		log.debug(Log.START);
		User user = null;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("Phone Number = " + phoneNumber);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_USER_BY_NUMBER);
			preparedStatement.setString(1, phoneNumber);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = extraction(resultSet);
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + user);
		return user;
	}

	@Override
	public boolean updateUser(User user) throws DBException {
		log.debug(Log.START);
		boolean result = false;
		Connection connect = null;
		PreparedStatement preparedStatement = null;

		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(UPDATE_USER_BY_NUMBER);
			int k = 1;
			preparedStatement.setString(k++, user.getFirstName());
			preparedStatement.setString(k++, user.getLastName());
			preparedStatement.setString(k++, Util.stringToMD5(user.getPassword()));
			preparedStatement.setString(k++, user.getRegistered());
			preparedStatement.setString(k++, user.getEmail());
			preparedStatement.setString(k++, user.getPhoneNumber());

			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			connect.commit();
			log.trace(Log.COMMIT);
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement);
		}
		log.debug(Log.FINISH_WITH + result);
		return result;

	}

	@Override
	public User getUserById(int userId) throws DBException {
		log.debug(Log.BEGIN);
		User user = null;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("UserID = " + userId);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_USER_BY_ID);
			preparedStatement.setInt(1, userId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				user = extraction(resultSet);
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + user);
		return user;
	}

	private User extraction(ResultSet resultSet) throws SQLException {
		log.debug(Log.START);
		User user = new User();
		int k = 1;
		user.setId(resultSet.getInt(k++));
		user.setEmail(resultSet.getString(k++));
		user.setFirstName(resultSet.getString(k++));
		user.setLastName(resultSet.getString(k++));
		user.setPassword(resultSet.getString(k++));
		user.setPhoneNumber(resultSet.getString(k++));
		user.setRole(UserRole.valueOf(resultSet.getString(k++)));
		user.setRegistered(resultSet.getString(k));
		
		log.debug(Log.FINISH_WITH + user.toString());
		return user;
	}

}
