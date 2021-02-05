package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.entity.Order;

public class SQLOrderDao implements OrderDao {
	private static final String GET_ALL_ORDERS = "SELECT * FROM order";
	private static final String GET_ORDERS_BY_USER_ID = "SELECT * FROM order WHERE user_id = ?";
	private static final String GET_ORDERS_BY_STATUS = "SELECT * FROM order WHERE status = ?";
	private static final String SET_ORDER = "INSERT INTO order VALUE(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)";

	private final DataSource dataSource;

	public SQLOrderDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Order> getAllOrders() throws Exception {
		List<Order> allOrders = new ArrayList<>();
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stat = con.createStatement();
			rs = stat.executeQuery(GET_ALL_ORDERS);
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, stat, rs);
		}

		return allOrders;
	}

	@Override
	public List<Order> getOrdersByUserId(int userId) throws Exception {
		List<Order> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_ORDERS_BY_USER_ID);
			prep.setInt(1, userId);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return allOrders;
	}

	@Override
	public List<Order> getOrdersByStatus(String status) throws Exception {
		List<Order> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_ORDERS_BY_STATUS);
			prep.setString(1, status);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return allOrders;
	}

	@Override
	public int insertOrder(Order model) throws Exception {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int orderId = 0;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SET_ORDER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getOrderDate());
			prep.setString(k++, model.getClosingDate());
			prep.setString(k++, model.getStatus());
			prep.setString(k++, model.getStreet());
			prep.setString(k++, model.getHouse());
			prep.setString(k++, model.getApartment());
			prep.setString(k++, model.getPorch());
			prep.setInt(k++, model.getUserId());

			if (prep.executeUpdate() > 0) {
				rs = prep.getGeneratedKeys();
				if (rs.next()) {
					orderId = rs.getInt(1);
					model.setId(orderId);
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
		return orderId;
	}

	private Order extractionOrder(ResultSet rs) throws SQLException {
		Order order = new Order();
		int k = 1;
		order.setId(rs.getInt(k++));
		order.setOrderDate(rs.getString(k++));
		order.setClosingDate(rs.getString(k++));
		order.setStatus(rs.getString(k++));
		order.setStreet(rs.getString(k++));
		order.setHouse(rs.getString(k++));
		order.setApartment(rs.getString(k++));
		order.setPorch(rs.getString(k++));
		order.setUserId(rs.getInt(k++));

		return order;
	}

	private void close(AutoCloseable... autoCloseables) throws Exception {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					// TODO add some logger 03.02.2021
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
