package db.dao;

import java.util.List;
import java.util.Map;

import db.entity.Order;
import db.entity.Product;

public interface OrderDao {
	
	List<Order> getAllOrders() throws Exception;
	
	List<Order> getOrdersByUserId(int userId) throws Exception;
	
	List<Order> getOrdersByStatus(String status) throws Exception;
	
	int insertOrder(Order model, List<Product> products, Map<Integer, Integer> count) throws Exception;

	boolean updateOrderState(int id, String status) throws Exception;
}
