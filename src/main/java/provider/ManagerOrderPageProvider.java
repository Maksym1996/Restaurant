package provider;

import java.util.List;

import consts.Page;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.ProviderException;

public class ManagerOrderPageProvider implements OrderPageProvider {

	private OrderViewDao orderDao;
	
	public ManagerOrderPageProvider(OrderViewDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getAllOrderViews();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new ProviderException(e);
		}
		return new OrderPage(orderViewList, Page.MANAGER_JSP);
	}

}
