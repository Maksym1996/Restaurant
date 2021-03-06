package db.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import consts.Log;
import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;
import util.Category;

/**
 * The class implementing ProductDao for DBMS MySQL
 */
public class MySqlProduct extends AbstractMySqlDao implements ProductDao {
	private static final String INSERT_PRODUCT = "INSERT INTO product VALUES (DEFAULT, ?, ?, ?, ?, ?)";
	private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM product WHERE id = ?";
	private static final String SELECT_PRODUCT_BY_NAME = "SELECT * FROM product WHERE name = ?";
	private static final String UPDATE_PRODUCT_BY_ID = "UPDATE product SET name=?,"
			+ "price=?, description=?, image_link=?, category=?  WHERE id = ?";
	private static final String DELETE_PRODUCT_BY_ID = "DELETE FROM product WHERE id = ?";

	private final DataSource dataSource;

	public MySqlProduct(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip,
			int limit) throws DBException {
		log.debug(Log.START);
		List<Product> productsByCategories = new ArrayList<>();
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("categories = " + categories.length);
		log.trace("sortValue = " + sortValue);
		log.trace("desc = " + desc);
		log.trace("skip = " + skip);
		log.trace("limit = " + limit);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(protectSqlInjection(sortValue, categories, desc));

			int k = 1;
			for (String category : categories) {
				preparedStatement.setString(k++, category);
			}
			preparedStatement.setInt(k++, limit);
			preparedStatement.setInt(k, skip);

			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				productsByCategories.add(extractionProduct(resultSet));
			}

		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + productsByCategories.size());
		return productsByCategories;
	}

	@Override
	public long getProductsCount(String[] categories) throws DBException {
		log.debug(Log.START);
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		int countProducts = 0;
		log.trace("categories " + categories);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(countQuery(categories));
			int k = 1;
			for (String category : categories) {
				preparedStatement.setString(k++, category);
			}
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				countProducts = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + countProducts);
		return countProducts;
	}

	@Override
	public int insertProduct(Product product) throws DBException {
		log.debug(Log.START);
		int productId = 0;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("product = " + product.toString());
		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS);

			int k = 1;
			preparedStatement.setString(k++, product.getName());
			preparedStatement.setInt(k++, product.getPrice());
			preparedStatement.setString(k++, product.getDescription());
			preparedStatement.setString(k++, product.getImageLink());
			preparedStatement.setString(k, product.getCategory().toString());

			if (preparedStatement.executeUpdate() > 0) {
				resultSet = preparedStatement.getGeneratedKeys();
				if (resultSet.next()) {
					productId = resultSet.getInt(1);
					product.setId(productId);
				}
			}
			connect.commit();
			log.trace(Log.COMMIT);
		} catch (SQLException e) {
			log.error(Log.COMMIT + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + productId);
		return productId;
	}

	@Override
	public Product getProductById(int productId) throws DBException {
		log.debug(Log.BEGIN);
		Product product = null;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("id = " + productId);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_PRODUCT_BY_ID);
			preparedStatement.setInt(1, productId);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				product = extractionProduct(resultSet);
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + product);
		return product;
	}

	@Override
	public Product getProductByName(String name) throws DBException {
		log.debug(Log.START);
		Product product = null;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		log.trace("name " + name);
		try {
			connect = dataSource.getConnection();
			preparedStatement = connect.prepareStatement(SELECT_PRODUCT_BY_NAME);
			preparedStatement.setString(1, name);
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				product = extractionProduct(resultSet);
			}
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + product);
		return product;
	}

	@Override
	public boolean updateProduct(Product product) throws DBException {
		log.debug(Log.START);
		boolean result = false;
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		log.trace("Product = " + product.toString());
		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(UPDATE_PRODUCT_BY_ID);
			int k = 1;
			preparedStatement.setString(k++, product.getName());
			preparedStatement.setInt(k++, product.getPrice());
			preparedStatement.setString(k++, product.getDescription());
			preparedStatement.setString(k++, product.getImageLink());
			preparedStatement.setString(k++, product.getCategory().toString());
			preparedStatement.setInt(k, product.getId());

			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			connect.commit();
			log.trace(Log.COMMIT);
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement);
		}
		log.debug(Log.FINISH_WITH + result);
		return result;

	}

	@Override
	public boolean deleteProductById(int productId) throws DBException {
		log.debug(Log.START);
		log.trace("id = " + productId);
		boolean result = false;

		Connection connect = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			connect = dataSource.getConnection();
			connect.setAutoCommit(false);
			connect.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			preparedStatement = connect.prepareStatement(DELETE_PRODUCT_BY_ID);
			preparedStatement.setInt(1, productId);

			if (preparedStatement.executeUpdate() > 0) {
				result = true;
			}
			connect.commit();
			log.trace(Log.COMMIT);
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			rollback(connect);
			throw new DBException(e);
		} finally {
			close(connect, preparedStatement, resultSet);
		}
		log.debug(Log.FINISH_WITH + result);
		return result;
	}

	private Product extractionProduct(ResultSet resultSet) throws SQLException {
		log.debug(Log.START);
		Product product = new Product();
		int k = 1;
		product.setId(resultSet.getInt(k++));
		product.setName(resultSet.getString(k++));
		product.setPrice(resultSet.getInt(k++));
		product.setDescription(resultSet.getString(k++));
		product.setImageLink(resultSet.getString(k++));
		product.setCategory(Category.byTitle(resultSet.getString(k)));

		log.debug(Log.FINISH_WITH + product.toString());
		return product;
	}

	private String countQuery(String[] categories) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT count(id) FROM product ");
		if (categories != null && categories.length != 0) {
			String inSql = String.join(",", Collections.nCopies(categories.length, "?"));
			result.append("WHERE category IN (").append(inSql).append(") ");
		}
		return result.toString();

	}

	private String protectSqlInjection(String sortBy, String[] categories, String desc) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM product ");
		if (categories != null && categories.length != 0) {
			String inSql = String.join(",", Collections.nCopies(categories.length, "?"));
			result.append("WHERE category IN (").append(inSql).append(") ");
		}

		switch (sortBy) {
		case "name":
			result.append("ORDER BY name ");
			break;
		case "price":
			result.append("ORDER BY price ");
			break;
		case "category":
			result.append("ORDER BY category ");
			break;
		default:
			result.append("ORDER BY id ");
			break;
		}

		if (!"true".equals(desc)) {
			result.append("DESC ");
		} else {
			result.append("ASC ");
		}

		return result.append("LIMIT ? OFFSET ?").toString();

	}

}
