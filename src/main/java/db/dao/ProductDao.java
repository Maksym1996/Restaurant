package db.dao;

import java.util.List;

import db.entity.Product;
import exception.DBException;

public interface ProductDao {

	Product getProductById(int id) throws DBException;

	Product getProductByName(String name) throws DBException;

	List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip, int limit)
			throws DBException;

	long getProductCount(String[] categories) throws DBException;

	int insertProduct(Product model) throws DBException;

	boolean updateProduct(Product model) throws DBException;

	boolean deleteProductById(int id) throws DBException;

}
