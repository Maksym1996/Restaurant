package provider;

import java.util.List;

import db.entity.OrderView;

public class OrderPage {
	private List<OrderView> orderViewList;
	private String forwardPage;
	
	public OrderPage(List<OrderView> orderViewList, String forwardPage) {
		super();
		this.orderViewList = orderViewList;
		this.forwardPage = forwardPage;
	}
	
	public List<OrderView> getOrderViewList() {
		return orderViewList;
	}
	
	public String getForwardPage() {
		return forwardPage;
	}
		
}
