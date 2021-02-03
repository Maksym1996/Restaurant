package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import db.entity.Product;

public class SQLProductDao implements ProductDao {
	private static final String INSERT_PRODUCT = "INSERT INTO product VALUE" + "(DEFAULT, ?, ?, ?, ?, ?, ?,)";
	private static final String GET_PRODUCT = "SELECT * FROM product WHERE id = ?";
	private static final String UPDATE_PRODUCT = "UPDATE product WHERE id = ? SET name=?,"
			+ "price=?, description=?, count=?, image_link=?, category_id=?";

	private DataSource dataSource;

	public SQLProductDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Product> getProductByCategoriesOnPage(String[] categories, String sortValue, String desc, int skip,
			int limit) throws Exception {
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
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return productByCategories;
	}

	@Override
	public long getProductCount(String[] categories) throws Exception {
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
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}
		return res;
	}

	@Override
	public int insertProduct(Product model) throws Exception {
		int productId = 0;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(INSERT_PRODUCT);
			int k = 1;
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setInt(k++, model.getCount());
			prep.setString(k++, model.getImageLink());
			prep.setString(k++, model.getCategory());

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
			// TODO add some logger 03.02.2021
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}
		return 0;
	}

	@Override
	public Product getProduct(int id) throws Exception {
		Product model = new Product();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_PRODUCT);
			prep.setInt(1, id);
			rs = prep.executeQuery();

			if (rs.next()) {
				model = extractionProduct(rs);
			}
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return model;
	}

	@Override
	public boolean updateProduct(Product model) throws Exception {
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;

		try {
			con = dataSource.getConnection();
			con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			prep = con.prepareStatement(UPDATE_PRODUCT);
			int k = 1;
			prep.setInt(k++, model.getId());
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setInt(k++, model.getCount());
			prep.setString(k++, model.getImageLink());
			prep.setString(k++, model.getCategory());

			if (prep.executeUpdate() > 0) {
				result = true;
			}
			con.commit();
		} catch (SQLException e) {
			rollback(con);
			// TODO add some logger 03.02.2021
			throw new SQLException();
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
		product.setCount(rs.getInt(k++));
		product.setImageLink(rs.getString(k++));
		product.setCategory(rs.getString(k));

		return product;
	}

	private void close(AutoCloseable... autoCloseables) throws Exception {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					// TODO add some logger 03.02.2021
					throw new Exception();
				}
			}
		}
	}

	private void rollback(Connection connect) throws SQLException {
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new SQLException();
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

		if ("false".equals(desc)) {
			result.append("DESC ");
		}

		return result.append("LIMIT ? OFFSET ?").toString();

	}

}
