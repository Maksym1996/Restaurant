package db.dao;

import java.util.List;

import db.entity.Product;

public interface ProductDao {

	Product getProduct(int id) throws Exception;

	List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip, int limit) throws Exception;

	int insertProduct(Product model) throws Exception; 

	boolean updateProduct(Product model) throws Exception;

	long getProductCount(String[] categories) throws Exception;
	
	boolean deleteProduct(int id) throws Exception;

}
