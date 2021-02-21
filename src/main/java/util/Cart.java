package util;

/**
 * The class for getting and setting the list of products for a user's shopping cart
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import db.entity.Product;

public class Cart implements Serializable {

	private static final long serialVersionUID = -3564439771722286373L;

	private List<Product> products = new ArrayList<>();

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}