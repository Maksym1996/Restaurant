package db.dao;

import java.util.List;
import java.util.Map;

import db.entity.OrderView;
import db.entity.Product;

public interface OrderViewDao {
	
	List<OrderView> getAllOrders() throws Exception;
	
	List<OrderView> getOrdersByUserId(int userId) throws Exception;
	
	List<OrderView> getOrdersByStatus(String status) throws Exception;
	
	int insertOrder(OrderView model, List<Product> products, Map<Integer, Integer> count) throws Exception;

	boolean updateOrderState(int id, String status) throws Exception;
}
