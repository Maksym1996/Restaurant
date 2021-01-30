package db.dao;

import java.util.List;

import db.entity.Product;

public interface ProductDao {
	
	Product getProduct(int id);
	
	List<Product> getAllProduct(int skip, int limit);
	
	List<Product> getProductByCategories(String[] categories, int skip, int limit);
	
	int insertProduct(Product model);
	
	boolean updateProduct(Product model);
	
	long getProductCount();
	
	long getProductCount(String[] categories);

}
