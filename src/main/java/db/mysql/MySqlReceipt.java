package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import consts.Log;
import db.dao.ReceiptDao;
import db.entity.Order;
import db.entity.OrderContent;
import db.entity.Receipt;
import exception.DBException;
import util.Status;

public class MySqlReceipt extends AbstractMySqlDao implements ReceiptDao {

	private static final String SELECT_ALL_ORDERS = "SELECT o.id, o.order_date, o.closing_date, o.state, o.address, o.sum, u.first_name, u.last_name, u.phone_number FROM orders AS o JOIN user AS u ON o.user_id = u.id ";
	private static final String SELECT_CONTENT_BY_ORDER_ID = "SELECT ohp.count, ohp.price, p.name FROM order_has_product AS ohp JOIN product AS p ON ohp.product_id = p.id AND ohp.order_id = ?";

	private final DataSource dataSource;

	public MySqlReceipt(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * parametr can be null, user role or userId
	 */
	@Override
	public List<Receipt> getListOfReceipts(String parametr) throws DBException {
		log.debug(Log.START);

		List<Receipt> listOfReceipts = new ArrayList<>();
		Connection connect = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connect = dataSource.getConnection();
			statement = connect.createStatement();
			String sqlQuery = queryBuilder(parametr);
			log.debug("SQL_QUERY = " + sqlQuery);
			resultSet = statement.executeQuery(sqlQuery);
			while (resultSet.next()) {
				Order order = extractionOrder(resultSet);
				List<OrderContent> listOfOrderContent = getListOfOrderContent(order.getId());
				listOfReceipts.add(createReceipt(order, listOfOrderContent));
			}
		} catch (Exception e) {
			log.error(Log.DB_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, statement, resultSet);
		}
		log.debug(Log.FINISH_WITH + listOfReceipts.size());
		return listOfReceipts;
	}

	private List<OrderContent> getListOfOrderContent(int orderId) throws DBException {
		log.debug(Log.START);

		List<OrderContent> listOfOrderContent = new ArrayList<>();
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_CONTENT_BY_ORDER_ID);
			preparedStatement.setInt(1, orderId);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				listOfOrderContent.add(extractionOrderContent(resultSet));
			}
		} catch (Exception e) {
			log.error(Log.DB_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}

		log.debug(Log.FINISH_WITH + listOfOrderContent.size());
		return listOfOrderContent;

	}

	private Receipt createReceipt(Order order, List<OrderContent> orderContent) {
		log.debug(Log.START);
		Receipt receipt = new Receipt();

		receipt.setOrder(order);
		receipt.setOrderContent(orderContent);

		log.debug(Log.FINISH_WITH + receipt.toString());
		return receipt;
	}

	private Order extractionOrder(ResultSet resultSet) throws SQLException {
		log.debug(Log.START);

		Order order = new Order();
		int columnIndex = 1;

		order.setId(resultSet.getInt(columnIndex++));
		order.setOrderDate(resultSet.getString(columnIndex++));
		order.setClosingDate(resultSet.getString(columnIndex++));
		order.setStatus(Status.valueOf(resultSet.getString(columnIndex++)));
		order.setAddress(resultSet.getString(columnIndex++));
		order.setSum(resultSet.getString(columnIndex++));
		order.setUserFirstName(resultSet.getString(columnIndex++));
		order.setUserLastName(resultSet.getString(columnIndex++));
		order.setUserPhoneNumber(resultSet.getString(columnIndex));

		log.debug(Log.FINISH_WITH + order.toString());
		return order;
	}

	private OrderContent extractionOrderContent(ResultSet resultSet) throws SQLException {
		log.debug(Log.START);

		OrderContent orderContent = new OrderContent();
		int columnIndex = 1;

		orderContent.setProductCount(resultSet.getInt(columnIndex++));
		orderContent.setProductPrice(resultSet.getInt(columnIndex++));
		orderContent.setProductName(resultSet.getString(columnIndex));

		log.debug(Log.FINISH_WITH + orderContent.toString());
		return orderContent;

	}

	private String queryBuilder(String parametr) {
		StringBuilder query = new StringBuilder();
		query.append(SELECT_ALL_ORDERS);
		if (parametr != null) {

			switch (parametr) {
			case "COOK":
				query.append("WHERE state = '" + Status.COOKING.name() + "' ");
				break;
			case "DELIVERY":
				query.append("WHERE state = '" + Status.IN_DELIVERY.name() + "' ");
				break;
			case "MANAGER":
				query.append("WHERE state IN ('" + Status.NEW.name() + "', '" + Status.COOKED.name() + "', '"
						+ Status.DELIVERED_AND_PAID.name() + "', '" + Status.PERFORMED.name() + "', '" + Status.REJECTED
						+ "') ");
				break;
			default:
				query.append("WHERE o.user_id = " + parametr + " ");
				break;
			}
		}

		return query.append("ORDER BY o.state").toString();

	}

}
