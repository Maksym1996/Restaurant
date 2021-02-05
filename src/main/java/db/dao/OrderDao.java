package db.dao;

import java.util.List;

import db.entity.Order;

public interface OrderDao {
	
	public List<Order> getAllOrders() throws Exception;
	
	public List<Order> getOrdersByUserId(int userId) throws Exception;
	
	public List<Order> getOrdersByStatus(String status) throws Exception;
	
	public int insertOrder(Order model) throws Exception;

}
