package provider;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.PageConst;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.ProviderException;
import util.Status;

public class DeliveryOrderPageProvider implements OrderPageProvider {

	private static final Logger log = LogManager.getLogger(DeliveryOrderPageProvider.class);

	private OrderViewDao orderDao;

	public DeliveryOrderPageProvider(OrderViewDao orderDao) {
		log.info("In constructor orderDao: " + orderDao);
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		log.info(Log.BEGIN);
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.IN_DELIVERY.name());
		} catch (Exception e) {
			log.error(Log.EXCEPTION + e.getMessage());
			throw new ProviderException(e);
		}
		log.info("return orderList and " + PageConst.DELIVERY_JSP);
		return new OrderPage(orderViewList, PageConst.DELIVERY_JSP);
	}

}