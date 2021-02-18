package provider;

import java.util.List;

import consts.ForwardPages;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.ProviderException;
import util.Status;

public class DeliveryOrderPageProvider implements OrderPageProvider {

	private OrderViewDao orderDao;
	
	public DeliveryOrderPageProvider(OrderViewDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.IN_DELIVERY.name());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ProviderException(e);
		}
		return new OrderPage(orderViewList, ForwardPages.DELIVERY_JSP);
	}

}