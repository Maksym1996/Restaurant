package db.mysql;

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
import exception.DBException;
import util.Status;

public class MySqlOrderView implements OrderViewDao {
	private static final String SELECT_ORDER_VIEWS_FOR_MANAGER = "SELECT * FROM orderView WHERE state IN ('NEW', 'COOKED', 'DELIVERED_AND_PAID', 'PERFORMED', 'REJECTED') ORDER BY id DESC";
	private static final String SELECT_ORDER_VIEWS_BY_USER_ID = "SELECT * FROM orderView WHERE user_id = ? ORDER BY id DESC";
	private static final String SELECT_ORDER_VIEWS_BY_STATUS = "SELECT * FROM orderView WHERE state = ? ORDER BY id DESC";
	private static final String SELECT_STATUS_BY_ORDER_ID = "SELECT state FROM orders WHERE id = ?";
	private static final String INSERT_ORDER = "INSERT INTO orders(id, order_date, state, address, user_id, sum) VALUES(DEFAULT, current_timestamp(), ?, ?, ?, ?)";
	private static final String INSERT_PRODUCT_FOR_ORDER = "INSERT INTO order_has_product(order_id, product_id, count, price) VALUES(?, ?, ?, ?)";

	private final DataSource dataSource;

	public MySqlOrderView(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public String getStatusByOrderId(int orderId) throws DBException {
		String state = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_STATUS_BY_ORDER_ID);
			prep.setInt(1, orderId);
			rs = prep.executeQuery();
			if (rs.next()) {
				state = rs.getString(1);
			}

		} catch (SQLException e) {
			// TODO some logger
			System.err.println("GET order:" + e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return state;
	}

	@Override
	public List<OrderView> getAllOrderViews() throws DBException {
		List<OrderView> allOrders = new ArrayList<>();
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stat = con.createStatement();
			rs = stat.executeQuery(SELECT_ORDER_VIEWS_FOR_MANAGER);
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger
			System.err.println(e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, stat, rs);
		}

		return allOrders;
	}

	@Override
	public List<OrderView> getOrderViewsByUserId(int userId) throws DBException {
		List<OrderView> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_ORDER_VIEWS_BY_USER_ID);
			prep.setInt(1, userId);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger
			System.err.println(e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return allOrders;
	}

	@Override
	public List<OrderView> getOrdersByStatus(String status) throws DBException {
		List<OrderView> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_ORDER_VIEWS_BY_STATUS);
			prep.setString(1, status);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			// TODO some logger
			System.err.println(e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return allOrders;
	}

	@Override
	public int insertOrder(OrderView model, List<Product> products, Map<Integer, Integer> count) throws DBException {
		Connection con = null;

		PreparedStatement prep = null;
		ResultSet rs = null;
		int orderId = 0;
		try {

			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getStatus().name());
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
				try (PreparedStatement setProdPrepSt = con.prepareStatement(INSERT_PRODUCT_FOR_ORDER)) {
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
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return orderId;
	}

	@Override
	public boolean updateStatusById(int id, String status) throws DBException {
		String setState = "UPDATE orders SET state=? WHERE id = ?";
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
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
			System.err.println(e.getMessage());
			// TODO add some logger 03.02.2021
			throw new DBException(e);
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
		order.setStatus(Status.valueOf(rs.getString(k++)));
		order.setAddress(rs.getString(k++));
		order.setUserId(rs.getInt(k++));
		order.setSum(rs.getString(k++));
		order.setOrderId(rs.getInt(k++));
		order.setProductId(rs.getInt(k++));
		order.setCount(rs.getInt(k++));
		order.setPrice(rs.getInt(k));

		return order;
	}

	private void close(AutoCloseable... autoCloseables) throws DBException {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					// TODO add some logger 03.02.2021
					throw new DBException(e);
				}
			}
		}
	}

	private void rollback(Connection connect) throws DBException {
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new DBException(e);
		}
	}

}
