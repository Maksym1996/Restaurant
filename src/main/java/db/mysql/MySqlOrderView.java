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

import consts.Comment;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import db.entity.Product;
import exception.DBException;
import util.Status;

public class MySqlOrderView extends AbstractMySqlDao implements OrderViewDao {
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
		log.info(Comment.BEGIN);
		String state = null;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("orderId = " + orderId);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_STATUS_BY_ORDER_ID);
			prep.setInt(1, orderId);
			rs = prep.executeQuery();
			if (rs.next()) {
				state = rs.getString(1);
			}
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + state);
		return state;
	}

	@Override
	public List<OrderView> getAllOrderViews() throws DBException {
		log.info(Comment.BEGIN);
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
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, stat, rs);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public List<OrderView> getOrderViewsByUserId(int userId) throws DBException {
		log.info(Comment.BEGIN);
		List<OrderView> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("userId = " + userId);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_ORDER_VIEWS_BY_USER_ID);
			prep.setInt(1, userId);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public List<OrderView> getOrdersByStatus(String status) throws DBException {
		log.info(Comment.BEGIN);
		List<OrderView> allOrders = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		log.debug("status = " + status);
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_ORDER_VIEWS_BY_STATUS);
			prep.setString(1, status);
			rs = prep.executeQuery();
			while (rs.next()) {
				allOrders.add(extractionOrder(rs));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public int insertOrder(OrderView model, List<Product> products, Map<Integer, Integer> count) throws DBException {
		log.info(Comment.BEGIN);
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int orderId = 0;
		log.debug("model = " + model.toString());
		log.debug("products = " + products.toArray());
		log.debug("count = " + count.size());
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
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			rollback(con);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		log.debug(Comment.RETURN + orderId);
		return orderId;
	}

	@Override
	public boolean updateStatusById(int id, String status) throws DBException {
		log.info(Comment.BEGIN);
		String setState = "UPDATE orders SET state=? WHERE id = ?";
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;
		log.debug("id = " + id);
		log.debug("status = " + status);
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			if (Status.REJECTED.name().equals(status) || Status.PERFORMED.name().equals(status)) {
				setState = "UPDATE orders SET state=?, closing_date = current_timestamp() WHERE id = ? ";
			}
			prep = con.prepareStatement(setState);
			prep.setString(1, status);
			prep.setInt(2, id);
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
		log.debug(Comment.EXTRACTION + order.toString());
		return order;
	}

}
