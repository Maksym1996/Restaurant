package provider;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import consts.PageConst;
import db.dao.OrderViewDao;
import db.entity.OrderView;
import exception.ProviderException;
import util.Status;

public class CookOrderPageProvider implements OrderPageProvider {

	private static final Logger log = LogManager.getLogger(CookOrderPageProvider.class);

	private OrderViewDao orderDao;

	public CookOrderPageProvider(OrderViewDao orderDao) {
		log.info("In constructor orderDao: " + orderDao);
		this.orderDao = orderDao;
	}

	@Override
	public OrderPage getOrderPage() {
		log.info(CommentConst.BEGIN);
		List<OrderView> orderViewList;
		try {
			orderViewList = orderDao.getOrdersByStatus(Status.COOKING.name());
		} catch (Exception e) {
			log.error(CommentConst.EXCEPTION + e.getMessage());
			throw new ProviderException(e);
		}
		log.info("return orderList and " + PageConst.COOK_JSP);
		return new OrderPage(orderViewList, PageConst.COOK_JSP);
	}

}
