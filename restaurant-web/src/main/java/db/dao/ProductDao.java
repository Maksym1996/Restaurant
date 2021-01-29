package db.dao;

import java.util.List;

import db.entity.Product;

public interface ProductDao {
	
	Product getProduct(int id);
	
	List<Product> getAllProduct();
	
	List<Product> getProductByCategories(List<String> categories);
	
	int insertProduct(Product model);
	
	boolean updateProduct(Product model);

}
