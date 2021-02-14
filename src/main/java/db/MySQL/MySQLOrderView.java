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

import db.dao.OrderViewDao;
import db.entity.OrderView;
import db.entity.Product;

public class MySQLOrderView implements OrderViewDao {
	private static final String GET_ALL_ORDERS = "SELECT * FROM orderView WHERE state IN ('NEW', 'COOKED', 'DELIVERED_AND_PAID', 'PERFORMED', 'REJECTED') ORDER BY id DESC";
	private static final String GET_ORDERS_BY_USER_ID = "SELECT * FROM orderView WHERE user_id = ? ORDER BY id DESC";
	private static final String GET_ORDERS_BY_STATUS = "SELECT * FROM orderView WHERE state = ? ORDER BY id DESC";
	private static final String SET_NEW_ORDER = "INSERT INTO orderView(id, order_date, state, address, user_id, sum) VALUES(DEFAULT, current_timestamp(), ?, ?, ?, ?)";
	private static final String SET_PRODUCT_FOR_ORDER = "INSERT INTO orderView(order_id, product_id, count, price) VALUES(?, ?, ?, ?)";
	private static final String GET_STATUS_BY_ORDER_ID = "SELECT state FROM order WHERE id = ?";


	private final DataSource dataSource;

	public MySQLOrderView(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	@Override
	public String getStateByOrderId(int orderId) throws Exception {
		String state = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_STATUS_BY_ORDER_ID);
			prep.setInt(1, orderId);
			rs = prep.executeQuery();
			if (rs.next()) {
				state = rs.getString(1);
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return state;
	}
	
	

	@Override
	public List<OrderView> getAllOrders() throws Exception {
		List<OrderView> allOrders = new ArrayList<>();
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
	public List<OrderView> getOrdersByUserId(int userId) throws Exception {
		List<OrderView> allOrders = new ArrayList<>();
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
	public List<OrderView> getOrdersByStatus(String status) throws Exception {
		List<OrderView> allOrders = new ArrayList<>();
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
	public int insertOrder(OrderView model, List<Product> products, Map<Integer, Integer> count) throws Exception {
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
			prep.setString(k++, model.getSum());

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
		String setState = "UPDATE orders SET state=? WHERE id = ?";
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			if ("REJECTED".equals(status) || "PERFORMED".equals(status)) {
				setState = "UPDATE orders SET state=?, closing_date = current_timestamp() WHERE id = ? ";
			} 
			prep = con.prepareStatement(setState);
			prep.setString(1, status);
			prep.setInt(2, id);
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

	private OrderView extractionOrder(ResultSet rs) throws SQLException {
		OrderView order = new OrderView();
		int k = 1;
		order.setId(rs.getInt(k++));
		order.setOrderDate(rs.getString(k++));
		order.setClosingDate(rs.getString(k++));
		order.setStatus(rs.getString(k++));
		order.setAddress(rs.getString(k++));
		order.setUserId(rs.getInt(k++));
		order.setSum(rs.getString(k++));
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
