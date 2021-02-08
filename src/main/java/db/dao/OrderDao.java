package db.dao;

import java.util.List;
import java.util.Map;

import db.entity.Order;
import db.entity.Product;

public interface OrderDao {
	
	public List<Order> getAllOrders() throws Exception;
	
	public List<Order> getOrdersByUserId(int userId) throws Exception;
	
	public List<Order> getOrdersByStatus(String status) throws Exception;
	
	public int insertOrder(Order model, List<Product> products) throws Exception;

}
