package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.entity.User;

public class SQLUserDao implements UserDao {
	private static final String SALT = "234jsdflakj";
	private static final String SELECT_ALL_USERS = "SELECT * FROM user";
	private static final String INSERT_USER = "INSERT INTO user VALUE(DEFAULT,?,?,?, MD5(CONCAT(?,'" + SALT
			+ "')) ,?,?,?,?,?, DEFAULT, ?)";
	private static final String GET_USER = "SELECT * FROM user WHERE email = ? AND password = MD5(CONCAT(?,'" + SALT
			+ "'))";
	private static final String UPDATE_USER = "UPDATE user WHERE phone_number = ? SET first_name=?, last_name=?"
			+ "password=?, street=?, house=?, apartment=?, porch=?, registred=?";
	private final DataSource dataSource;

	public SQLUserDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<User> getAllUsers() throws Exception {
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

			throw new SQLException();
		} finally {
			close(con, stat, rs);
		}

		return allUser;
	}

	

	@Override
	public int insertUser(User model) throws Exception {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int userId = 0;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getEmail());
			prep.setString(k++, model.getFirstName());
			prep.setString(k++, model.getLastName());
			prep.setString(k++, model.getPassword());
			prep.setString(k++, model.getPhoneNumber());
			prep.setString(k++, model.getStreet());
			prep.setString(k++, model.getHouse());
			prep.setString(k++, model.getApartment());
			prep.setString(k++, model.getPorch());
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
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}
		return userId;
	}

	@Override
	public User getUser(String email, String password) throws Exception {
		User model = new User();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_USER);
			prep.setString(1, email);
			prep.setString(2, password);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extraction(rs);
			}
		} catch (SQLException e) {
			// TODO LOGGER
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}
		return model;
	}

	@Override
	public boolean updateUser(User model) throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(UPDATE_USER);
			int k = 1;
			prep.setInt(k++, model.getId());
			prep.setString(k++, model.getFirstName());
			prep.setString(k++, model.getLastName());
			prep.setString(k++, model.getPassword());
			prep.setString(k++, model.getPhoneNumber());
			prep.setString(k++, model.getStreet());
			prep.setString(k++, model.getHouse());
			prep.setString(k++, model.getApartment());
			prep.setString(k++, model.getPorch());
			prep.setString(k++, model.getRegistered());

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			rollback(con);
			// TODO logger
			throw new SQLException();
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
		user.setStreet(rs.getString(k++));
		user.setHouse(rs.getString(k++));
		user.setApartment(rs.getString(k++));
		user.setPorch(rs.getString(k++));
		user.setRole(rs.getString(k++));
		user.setRegistered(rs.getString(k++));

		return user;
	}

	private void close(AutoCloseable... ac) throws Exception {
		for (AutoCloseable a : ac) {
			if (a != null) {
				try {
					a.close();
				} catch (Exception e) {
					// TODO some logger
					throw new Exception();
				}
			}
		}
	}
	
	private void rollback(Connection connect) throws SQLException {
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new SQLException();
		}
	}

}
