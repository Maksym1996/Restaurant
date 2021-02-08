package db.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.dao.OrderDao;
import db.entity.Order;

public class MySQLOrderView implements OrderDao {
	private static final String GET_ALL_ORDERS = "SELECT * FROM orderView";
	private static final String GET_ORDERS_BY_USER_ID = "SELECT * FROM orderView WHERE user_id = ?";
	private static final String GET_ORDERS_BY_STATUS = "SELECT * FROM orderView WHERE status = ?";
	private static final String SET_NEW_ORDER = "INSERT INTO orderView(id, order_date, state, address, user_id) VALUES(DEFAULT, ?, ?, ?, ?)";
	private static final String SET_PRODUCT_FOR_ORDER = "INSERT INTO orderView(order_id, product_id, count, price) VALUES(?, ?, ?, ?)";
	private static final String SET_STATE_AND_CLOSEDATE = "UPDATE orderView SET state=?, closing_date=? WHERE id = ?";

	
	private final DataSource dataSource;

	public MySQLOrderView(DataSource dataSource) {
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
	public boolean updateOrder(int orderId, int productId, int count, int currentPrice) throws Exception {
		Connection con = null;
		PreparedStatement prep = null;
		boolean result = false;	
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(result);
			
			prep = con.prepareStatement(SET_PRODUCT_FOR_ORDER);
			int k = 1;
			prep.setInt(k++, orderId);
			prep.setInt(k++, productId);
			prep.setInt(k++, count);
			prep.setInt(k++, currentPrice);

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			rollback(con);
			// TODO some logger
			throw new SQLException();
		} finally {
			close(con, prep);
		}
		return result;
	
	}

	@Override
	public int insertOrder(Order model) throws Exception {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int orderId = 0;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			prep = con.prepareStatement(SET_NEW_ORDER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getOrderDate());
			prep.setString(k++, model.getStatus());
			prep.setString(k++, model.getAddress());
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
		order.setAddress(rs.getString(k++));
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