package provider;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import db.dao.ReceiptDao;
import util.UserRole;

public class OrderPageProviderContainer {

	private static final Logger log = LogManager.getLogger(OrderPageProviderContainer.class);

	private Map<UserRole, OrderPageProvider> pageProviders;

	public OrderPageProviderContainer(ReceiptDao receiptDao) {
		log.info("In construtor");
		pageProviders = new HashMap<>();
		pageProviders.put(UserRole.MANAGER, new ManagerOrderPageProvider(receiptDao));
		pageProviders.put(UserRole.COOK, new CookOrderPageProvider(receiptDao));
		pageProviders.put(UserRole.DELIVERY, new DeliveryOrderPageProvider(receiptDao));
	}

	public OrderPageProvider getProvider(UserRole role) {
		log.info(Log.BEGIN);
		log.debug("role" + role);
		log.info("Return");
		return pageProviders.get(role);
	}

}
