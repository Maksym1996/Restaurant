package provider;

import java.util.List;

import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.DBException;
import util.Status;

public class CourierOrderPageProvider implements OrderPageProvider {

	private OrderViewDao orderDao;
	
	public CourierOrderPageProvider(OrderViewDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.IN_DELIVERY.name());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new DBException(e);
		}
		return new OrderPage(orderViewList, "Delivery.jsp");
	}

}