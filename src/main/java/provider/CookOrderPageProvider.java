package provider;

import java.util.List;

import consts.Page;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.ProviderException;
import util.Status;

public class CookOrderPageProvider implements OrderPageProvider {

	private OrderViewDao orderDao;
	
	public CookOrderPageProvider(OrderViewDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.COOKING.name());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ProviderException(e);
		}
		return new OrderPage(orderViewList, Page.COOK_JSP);
	}

}
