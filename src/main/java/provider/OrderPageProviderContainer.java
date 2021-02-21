package provider;

import java.util.HashMap;
import java.util.Map;

import db.dao.OrderViewDao;
import util.UserRole;

public class OrderPageProviderContainer {

	private Map<UserRole, OrderPageProvider> pageProviders;

	public OrderPageProviderContainer(OrderViewDao orderViewDao) {
		pageProviders = new HashMap<>();
		pageProviders.put(UserRole.MANAGER, new ManagerOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.COOK, new CookOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.DELIVERY, new DeliveryOrderPageProvider(orderViewDao));
	}

	public OrderPageProvider getProvider(UserRole role) {
		return pageProviders.get(role);
	}

}
