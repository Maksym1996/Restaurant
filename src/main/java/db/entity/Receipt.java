package db.entity;

import java.util.List;

public class Receipt {
	
	private Order order;
	private List<OrderContent> orderContent;
	
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public List<OrderContent> getOrderContent() {
		return orderContent;
	}
	public void setOrderContent(List<OrderContent> orderContent) {
		this.orderContent = orderContent;
	}

}
