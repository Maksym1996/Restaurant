package provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import db.dao.OrderViewDao;
import util.UserRole;

public class OrderPageProviderContainer {

	private static final Logger log = LogManager.getLogger(OrderPageProviderContainer.class);

	private Map<UserRole, OrderPageProvider> pageProviders;

	public OrderPageProviderContainer(OrderViewDao orderViewDao) {
		log.info("In construtor");
		pageProviders = new HashMap<>();
		pageProviders.put(UserRole.MANAGER, new ManagerOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.COOK, new CookOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.DELIVERY, new DeliveryOrderPageProvider(orderViewDao));
	}

	public OrderPageProvider getProvider(UserRole role) {
		log.info(Comment.BEGIN);
		log.debug("role" + role);
		log.info("Return");
		return pageProviders.get(role);
	}

}
