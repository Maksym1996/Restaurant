package provider;

import java.util.List;

import db.entity.Receipt;

public class OrderPage {
	private List<Receipt> orderViewList;
	private String forwardPage;
	
	public OrderPage(List<Receipt> orderViewList, String forwardPage) {
		super();
		this.orderViewList = orderViewList;
		this.forwardPage = forwardPage;
	}
	
	public List<Receipt> getOrderViewList() {
		return orderViewList;
	}
	
	public String getForwardPage() {
		return forwardPage;
	}
		
}
