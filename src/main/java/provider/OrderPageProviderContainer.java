package provider;

import java.util.HashMap;
import java.util.Map;

import db.dao.OrderViewDao;
import util.UserRole;

public class OrderPageProviderContainer {

	private Map<String, OrderPageProvider> pageProviders;

	public OrderPageProviderContainer(OrderViewDao orderViewDao) {
		pageProviders = new HashMap<>();
		pageProviders.put(UserRole.MANAGER.name(), new ManagerOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.COOK.name(), new CookOrderPageProvider(orderViewDao));
		pageProviders.put(UserRole.DELIVERY.name(), new DeliveryOrderPageProvider(orderViewDao));
	}

	public OrderPageProvider getProvider(String role) {
		return pageProviders.get(role);
	}

}
