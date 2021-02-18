package provider;

import java.util.HashMap;
import java.util.Map;

import db.dao.OrderViewDao;

public class OrderPageProviderContainer {
	
	private Map<String, OrderPageProvider> pageProviders;
	
	public OrderPageProviderContainer(OrderViewDao orderViewDao) {
		pageProviders = new HashMap<>();
		pageProviders.put("MANAGER", new ManagerOrderPageProvider(orderViewDao));
		pageProviders.put("COOK", new CookOrderPageProvider(orderViewDao));
		pageProviders.put("COURIER", new CourierOrderPageProvider(orderViewDao));
	}
	
	public OrderPageProvider getProvider(String role) {
		return pageProviders.get(role);
	}

}
