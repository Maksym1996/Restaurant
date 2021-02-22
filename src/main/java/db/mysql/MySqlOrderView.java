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

/**
 * The class implementing OrderViewDao for DBMS MySQL
 *
 */
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
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.debug("orderId = " + orderId);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_STATUS_BY_ORDER_ID);
			preparedStatement.setInt(1, orderId);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				state = resultSet.getString(1);
			}
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Comment.RETURN + state);
		return state;
	}

	@Override
	public List<OrderView> getAllOrderViews() throws DBException {
		log.info(Comment.BEGIN);
		List<OrderView> allOrders = new ArrayList<>();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			resultSet = statement.executeQuery(SELECT_ORDER_VIEWS_FOR_MANAGER);
			while (resultSet.next()) {
				allOrders.add(extractionOrder(resultSet));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, statement, resultSet);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public List<OrderView> getOrderViewsByUserId(int userId) throws DBException {
		log.info(Comment.BEGIN);
		List<OrderView> allOrders = new ArrayList<>();
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.debug("userId = " + userId);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_ORDER_VIEWS_BY_USER_ID);
			preparedStatement.setInt(1, userId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				allOrders.add(extractionOrder(resultSet));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public List<OrderView> getOrdersByStatus(String status) throws DBException {
		log.info(Comment.BEGIN);
		List<OrderView> allOrders = new ArrayList<>();
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.debug("status = " + status);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_ORDER_VIEWS_BY_STATUS);
			preparedStatement.setString(1, status);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				allOrders.add(extractionOrder(resultSet));
			}

		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Comment.RETURN + allOrders.size());
		return allOrders;
	}

	@Override
	public int insertOrder(OrderView orderView, List<Product> products, Map<Integer, Integer> count) throws DBException {
		log.info(Comment.BEGIN);
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		int orderId = 0;
		log.debug("model = " + orderView);
		log.debug("products = " + products);
		log.debug("count = " + count);
		try {

			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(INSERT_ORDER, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			preparedStatement.setString(k++, orderView.getStatus().name());
			preparedStatement.setString(k++, orderView.getAddress());
			preparedStatement.setInt(k++, orderView.getUserId());
			preparedStatement.setString(k, orderView.getSum());

			if (preparedStatement.executeUpdate() > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					orderId = resultSet.getInt(1);
					orderView.setId(orderId);
				}
			}
			for (Product product : products) {
				try (PreparedStatement setProdPrepSt = connect.prepareStatement(INSERT_PRODUCT_FOR_ORDER)) {
					k = 1;
					setProdPrepSt.setInt(k++, orderId);
					setProdPrepSt.setInt(k++, product.getId());
					setProdPrepSt.setInt(k++, count.get(product.getId()));
					setProdPrepSt.setInt(k, product.getPrice());
					setProdPrepSt.executeUpdate();
				}

			}
			connect.commit();
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Comment.RETURN + orderId);
		return orderId;
	}

	@Override
	public boolean updateStatusById(int orderId, String status) throws DBException {
		log.info(Comment.BEGIN);
		String setState = "UPDATE orders SET state=? WHERE id = ?";
		boolean result = false;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		log.debug("id = " + orderId);
		log.debug("status = " + status);
		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			if (Status.REJECTED.name().equals(status) || Status.PERFORMED.name().equals(status)) {
				setState = "UPDATE orders SET state=?, closing_date = current_timestamp() WHERE id = ? ";
			}
			preparedStatement = connect.prepareStatement(setState);
			preparedStatement.setString(1, status);
			preparedStatement.setInt(2, orderId);
			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			connect.commit();
			log.debug(Comment.COMMIT);
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement);
		}
		log.debug(Comment.RETURN + result);
		return result;

	}

	private OrderView extractionOrder(ResultSet resultSet) throws SQLException {
		OrderView orderView = new OrderView();
		int k = 1;
		orderView.setId(resultSet.getInt(k++));
		orderView.setStatus(Status.valueOf(resultSet.getString(k++)));
		orderView.setAddress(resultSet.getString(k++));
		orderView.setUserId(resultSet.getInt(k++));
		orderView.setSum(resultSet.getString(k++));
		orderView.setOrderId(resultSet.getInt(k++));
		orderView.setProductId(resultSet.getInt(k++));
		orderView.setCount(resultSet.getInt(k++));
		orderView.setPrice(resultSet.getInt(k));
		log.debug(Comment.EXTRACTION + orderView.toString());
		return orderView;
	}

}
