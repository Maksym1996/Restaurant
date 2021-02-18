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

import db.dao.ProductDao;
import db.entity.Product;
import exception.DBException;
import util.Category;

public class MySqlProduct implements ProductDao {
	private static final String INSERT_PRODUCT = "INSERT INTO product VALUES (DEFAULT, ?, ?, ?, ?, ?)";
	private static final String SELECT_PRODUCT_BY_ID = "SELECT * FROM product WHERE id = ?";
	private static final String UPDATE_PRODUCT_BY_ID = "UPDATE product SET name=?,"
			+ "price=?, description=?, image_link=?, category=?  WHERE id = ?";
	private static final String DELETE_PRODUCT_BY_ID = "DELETE FROM product WHERE id = ?";

	private DataSource dataSource;

	public MySqlProduct(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip,
			int limit) throws DBException {
		List<Product> productByCategories = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();

			prep = con.prepareStatement(protectSqlInjection(sortValue, categories, desc));

			int k = 1;
			for (String category : categories) {
				prep.setString(k++, category);
			}
			prep.setInt(k++, limit);
			prep.setInt(k++, skip);

			rs = prep.executeQuery();

			while (rs.next()) {
				productByCategories.add(extractionProduct(rs));
			}

		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			System.err.println(e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return productByCategories;
	}

	@Override
	public long getProductCount(String[] categories) throws DBException {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;

		int res = 0;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(countQuery(categories));
			int k = 1;
			for (String category : categories) {
				prep.setString(k++, category);
			}
			rs = prep.executeQuery();

			if (rs.next()) {
				res = rs.getInt(1);
			}

		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			System.err.println("Get Count: " + e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return res;
	}

	@Override
	public int insertProduct(Product model) throws DBException {
		int productId = 0;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(INSERT_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setString(k++, model.getImageLink());
			prep.setString(k++, model.getCategory().toString());

			if (prep.executeUpdate() > 0) {
				rs = prep.getGeneratedKeys();
				if (rs.next()) {
					productId = rs.getInt(1);
					model.setId(productId);
				}
			}
			con.commit();
		} catch (SQLException e) {
			rollback(con);
			System.err.println(e);
			// TODO add some logger 03.02.2021
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}
		return productId;
	}

	@Override
	public Product getProductById(int id) throws DBException {
		Product model = new Product();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SELECT_PRODUCT_BY_ID);
			prep.setInt(1, id);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extractionProduct(rs);
			}
		} catch (SQLException e) {
			System.err.println(e);
			// TODO add some logger 03.02.2021
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return model;
	}

	@Override
	public boolean updateProduct(Product model) throws DBException {
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(UPDATE_PRODUCT_BY_ID);
			int k = 1;
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setString(k++, model.getImageLink());
			prep.setString(k++, model.getCategory().toString());
			prep.setInt(k++, model.getId());

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			rollback(con);
			System.err.println(e);
			// TODO add some logger 03.02.2021
			throw new DBException(e);
		} finally {
			close(con, prep);
		}

		return result;

	}

	private Product extractionProduct(ResultSet rs) throws SQLException {
		Product product = new Product();
		int k = 1;
		product.setId(rs.getInt(k++));
		product.setName(rs.getString(k++));
		product.setPrice(rs.getInt(k++));
		product.setDescription(rs.getString(k++));
		product.setImageLink(rs.getString(k++));
		product.setCategory(Category.byTitle(rs.getString(k)));

		return product;
	}

	private void close(AutoCloseable... autoCloseables) throws DBException {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					System.err.println(e);
					// TODO add some logger 03.02.2021
					throw new DBException(e);
				}
			}
		}
	}

	private void rollback(Connection connect) throws DBException {
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			System.err.println(e);
			throw new DBException(e);
		}
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

	private String protectSqlInjection(String value, String[] categories, String desc) {
		StringBuilder result = new StringBuilder();
		result.append("SELECT * FROM product ");
		if (categories != null && categories.length != 0) {
			String inSql = String.join(",", Collections.nCopies(categories.length, "?"));
			result.append("WHERE category IN (").append(inSql).append(") ");
		}

		switch (value) {
		case "name":
			result.append("ORDER BY name ").toString();
			break;
		case "price":
			result.append("ORDER BY price ").toString();
			break;
		case "category":
			result.append("ORDER BY category ").toString();
			break;
		default:
			result.append("ORDER BY id ").toString();
			break;
		}

		if (!"true".equals(desc)) {
			result.append("DESC ");
		} else {
			result.append("ASC ");
		}

		return result.append("LIMIT ? OFFSET ?").toString();

	}

	@Override
	public boolean deleteProductById(int id) throws DBException {
		boolean result = false;

		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(DELETE_PRODUCT_BY_ID);
			prep.setInt(1, id);

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			// TODO add some logger 14.02.2021
			rollback(con);
			System.err.println(e);
			throw new DBException(e);
		} finally {
			close(con, prep, rs);
		}

		return result;

	}

}
