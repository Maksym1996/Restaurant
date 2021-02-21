package provider;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import consts.Page;
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
		log.info(Comment.BEGIN);
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.IN_DELIVERY.name());
		} catch (Exception e) {
			log.error(Comment.EXCEPTION + e.getMessage());
			throw new ProviderException(e);
		}
		log.info("return orderList and " + Page.DELIVERY_JSP);
		return new OrderPage(orderViewList, Page.DELIVERY_JSP);
	}

}