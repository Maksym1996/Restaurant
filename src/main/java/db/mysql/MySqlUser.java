package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.dao.UserDao;
import db.entity.User;
import exception.DBException;
import util.Util;

public class MySqlUser implements UserDao {
	private static final String SELECT_ALL_USERS = "SELECT * FROM user";
	private static final String SELECT_USERS_FOR_REGISTERED = "SELECT * FROM user WHERE registered = ?";
	private static final String INSERT_USER = "INSERT INTO user VALUES (DEFAULT,?,?,?,?,?, DEFAULT, ?)";
	private static final String SELECT_USER_BY_NUMBER_AND_PASS = "SELECT * FROM user WHERE phone_number = ? AND password = ?";
	private static final String SELECT_USER_BY_ID = "SELECT * FROM user WHERE id = ?";
	private static final String SELECT_USER_BY_NUMBER = "SELECT * FROM user WHERE phone_number = ?";
	private static final String UPDATE_USER_BY_NUMBER = "UPDATE user SET first_name=?, last_name=?, password = ?, registered=?, email = ? WHERE phone_number = ?";

	private final DataSource dataSource;

	public MySqlUser(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<User> getUsersForManager() throws DBException {
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
			// TODO some logger

			throw new DBException(e);
		} finally {
			close(con, stat, rs);
		}

		return allUser;
	}

	@Override
	public List<User> getUsersByRegistered(String registered) throws DBException {
		List<User> registredUser = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USERS_FOR_REGISTERED);
			prep.setString(1, registered);
			rs = prep.executeQuery();
			while (rs.next()) {
				registredUser.add(extraction(rs));
			}

		} catch (SQLException e) { 
			// TODO some logger

			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return registredUser;
	}

	@Override
	public int insertUser(User model) throws DBException {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int userId = 0;
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
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			rollback(con);
			// TODO some logger
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return userId;
	}

	@Override
	public User getUserByEmailAndPass(String phoneNumber, String password) throws DBException {
		User model = new User();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
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
			// TODO LOGGER
			System.err.println(e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return model;
	}

	@Override
	public User getUserByNumber(String phoneNumber) throws DBException {
		User model = new User();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USER_BY_NUMBER);
			prep.setString(1, phoneNumber);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			// TODO LOGGER
			System.err.println(e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return model;
	}

	@Override
	public boolean updateUser(User model) throws DBException {
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
		} catch (SQLException e) {
			rollback(con);
			// TODO logger
			System.err.println(e);
			throw new DBException(e);
		} finally {
			close(con, prep);
		}

		return result;

	}

	private User extraction(ResultSet rs) throws SQLException {
		User user = new User();
		int k = 1;
		user.setId(rs.getInt(k++));
		user.setEmail(rs.getString(k++));
		user.setFirstName(rs.getString(k++));
		user.setLastName(rs.getString(k++));
		user.setPassword(rs.getString(k++));
		user.setPhoneNumber(rs.getString(k++));
		user.setRole(rs.getString(k++));
		user.setRegistered(rs.getString(k));

		return user;
	}

	private void close(AutoCloseable... ac) throws DBException {
		for (AutoCloseable a : ac) {
			if (a != null) {
				try {
					a.close();
				} catch (Exception e) {
					// TODO some logger
					throw new DBException(e);
				}
			}
		}
	}

	private void rollback(Connection connect) throws DBException{
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new DBException(e);
		}
	}

	@Override
	public User getUserById(int userId) throws DBException {
		User model = new User();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_USER_BY_ID);
			prep.setInt(1, userId);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			// TODO LOGGER
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return model;
	}

}
