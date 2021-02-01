package db.dao;

import java.util.List;

import db.entity.Product;

public interface ProductDao {

	Product getProduct(int id);

	List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip, int limit);

	int insertProduct(Product model);

	boolean updateProduct(Product model);

	long getProductCount(String[] categories);

}
