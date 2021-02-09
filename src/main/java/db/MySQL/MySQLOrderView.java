package db.MySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import db.dao.OrderDao;
import db.entity.Order;
import db.entity.Product;

public class MySQLOrderView implements OrderDao {
	private static final String GET_ALL_ORDERS = "SELECT * FROM orderView";
	private static final String GET_ORDERS_BY_USER_ID = "SELECT * FROM orderView WHERE user_id = ?";
	private static final String GET_ORDERS_BY_STATUS = "SELECT * FROM orderView WHERE status = ?";
	private static final String SET_NEW_ORDER = "INSERT INTO orderView(id, order_date, state, address, user_id) VALUES(DEFAULT, current_timestamp(), ?, ?, ?)";
	private static final String SET_PRODUCT_FOR_ORDER = "INSERT INTO orderView(order_id, product_id, count, price) VALUES(?, ?, ?, ?)";
	private static final String SET_STATE_AND_CLOSEDATE = "UPDATE orderView WHERE id = ? SET state=?, closing_date=curremt_timestamp() ";
	private static final String SET_STATE = "UPDATE orderView WHERE id = ? SET state=?";

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
	public int insertOrder(Order model, List<Product> products, Map<Integer, Integer> count) throws Exception {
		Connection con = null;

		PreparedStatement prep = null;
		ResultSet rs = null;
		int orderId = 0;
		try {

			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			prep = con.prepareStatement(SET_NEW_ORDER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
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
			for (Product p : products) {
				try (PreparedStatement setProdPrepSt = con.prepareStatement(SET_PRODUCT_FOR_ORDER)) {
					k = 1;
					setProdPrepSt.setInt(k++, orderId);
					setProdPrepSt.setInt(k++, p.getId());
					setProdPrepSt.setInt(k++, count.get(p.getId()));
					setProdPrepSt.setInt(k++, p.getPrice());
					setProdPrepSt.executeUpdate();
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

	@Override
	public boolean updateOrderState(int id, String status) throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			if ("DECLINE".equals(status) || "CLOSED".equals(status)) {
				prep = con.prepareStatement(SET_STATE_AND_CLOSEDATE);
			} else {
				prep = con.prepareStatement(SET_STATE);
			}
			prep.setInt(1, id);
			prep.setString(2, status);
			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			rollback(con);
			// TODO add some logger 03.02.2021
			throw new SQLException();
		} finally {
			close(con, prep);
		}

		return result;

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
		order.setOrderId(rs.getInt(k++));
		order.setProductId(rs.getInt(k++));
		order.setCount(rs.getInt(k++));
		order.setPrice(rs.getInt(k++));

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
