package db.dao;

import java.util.List;
import java.util.Map;

import db.entity.OrderView;
import db.entity.Product;
import exception.DBException;

/**
 * The interface describing the general view of the data access methods for the
 * orderView entity
 */
public interface OrderViewDao {
	
	String getStatusByOrderId(int orderId) throws DBException;

	List<OrderView> getAllOrderViews() throws DBException;

	List<OrderView> getOrderViewsByUserId(int userId) throws DBException;

	List<OrderView> getOrdersByStatus(String status) throws DBException;

	int insertOrder(OrderView model, List<Product> products, Map<Integer, Integer> count) throws DBException;

	boolean updateStatusById(int id, String status) throws DBException;
}
