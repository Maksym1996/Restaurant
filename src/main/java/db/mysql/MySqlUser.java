package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import consts.Comment;
import db.dao.UserDao;
import db.entity.User;
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
	private static final String INSERT_USER = "INSERT INTO user VALUES (DEFAULT,?,?,?,?,?, DEFAULT, ?)";
	private static final String UPDATE_USER_BY_NUMBER = "UPDATE user SET first_name=?, last_name=?, password = ?, registered=?, email = ? WHERE phone_number = ?";

	private final DataSource dataSource;

	public MySqlUser(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<User> getUsersForManager() throws DBException {
		log.info(Comment.BEGIN);
		List<User> allUser = new ArrayList<>();
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stat = con.createStatement();
			rs = stat.executeQuery(SELECT_ALL_USERS);
			while (rs.next()) {
				allUser.add(extraction(rs));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, stat, rs);
		}
		log.debug(Comment.RETURN + allUser.size());
		return allUser;
	}

	@Override
	public List<User> getUsersByRegistered(String registered) throws DBException {
		log.info(Comment.BEGIN);
		List<User> registeredUser = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("registered = " + registered);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USERS_BY_REGISTERED);
			prep.setString(1, registered);
			rs = prep.executeQuery();
			while (rs.next()) {
				registeredUser.add(extraction(rs));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + registeredUser.size());
		return registeredUser;
	}

	@Override
	public int insertUser(User model) throws DBException {
		log.info(Comment.BEGIN);
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int userId = 0;
		log.debug("User = " + model.toString());
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getEmail());
			prep.setString(k++, model.getFirstName());
			prep.setString(k++, model.getLastName());
			prep.setString(k++, Util.stringToMD5(model.getPassword()));
			prep.setString(k++, model.getPhoneNumber());
			prep.setString(k++, model.getRegistered());

			if (prep.executeUpdate() > 0) {
				rs = prep.getGeneratedKeys();
				if (rs.next()) {
					userId = rs.getInt(1);
					model.setId(userId);
				}
			}
			con.commit();
			log.debug(Comment.COMMIT);
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			rollback(con);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + userId);
		return userId;
	}

	@Override
	public User getUserByNumberAndPass(String phoneNumber, String password) throws DBException {
		log.info(Comment.BEGIN);
		User model = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("Phone Numeber = " + phoneNumber);
		log.debug("Password = " + password);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USER_BY_NUMBER_AND_PASS);
			prep.setString(1, phoneNumber);
			prep.setString(2, Util.stringToMD5(password));
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + model);
		return model;
	}

	@Override
	public User getUserByNumber(String phoneNumber) throws DBException {
		log.info(Comment.BEGIN);
		User model = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("Phone Number = " + phoneNumber);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USER_BY_NUMBER);
			prep.setString(1, phoneNumber);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + model);
		return model;
	}

	@Override
	public boolean updateUser(User model) throws DBException {
		log.info(Comment.BEGIN);
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(UPDATE_USER_BY_NUMBER);
			int k = 1;
			prep.setString(k++, model.getFirstName());
			prep.setString(k++, model.getLastName());
			prep.setString(k++, Util.stringToMD5(model.getPassword()));
			prep.setString(k++, model.getRegistered());
			prep.setString(k++, model.getEmail());
			prep.setString(k++, model.getPhoneNumber());

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
			log.debug(Comment.COMMIT);
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			rollback(con);
			throw new DBException(e);
		} finally {
			close(con, prep);
		}
		log.debug(Comment.RETURN + result);
		return result;

	}

	@Override
	public User getUserById(int userId) throws DBException {
		log.info(Comment.BEGIN);
		User model = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("UserID = " + userId);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USER_BY_ID);
			prep.setInt(1, userId);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + model);
		return model;
	}

	private User extraction(ResultSet rs) throws SQLException {
		log.info(Comment.BEGIN);
		User user = new User();
		int k = 1;
		user.setId(rs.getInt(k++));
		user.setEmail(rs.getString(k++));
		user.setFirstName(rs.getString(k++));
		user.setLastName(rs.getString(k++));
		user.setPassword(rs.getString(k++));
		user.setPhoneNumber(rs.getString(k++));
		user.setRole(UserRole.valueOf(rs.getString(k++)));
		user.setRegistered(rs.getString(k));

		return user;
	}

}
