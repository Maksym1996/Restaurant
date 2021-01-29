package db.dao;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.entity.Product;

public class SQLProductDao implements ProductDao {
	private static final String GET_ALL_PRODUCTS = "SELECT * FROM product";
	private static final String INSERT_PRODUCT = "INSERT INTO product VALUE" + "(DEFAULT, ?, ?, ?, ?, ?, ?,)";
	private static final String GET_PRODUCT = "SELECT * FROM product WHERE id = ?";
	private static final String GET_PRODUCTS_BY_CATEGORY = "SELECT * FROM WHERE product IN (?)";
	private static final String UPDATE_PRODUCT = "UPDATE product WHERE id = ? SET name=?," + 
			"price=?, description=?, count=?, image_link=?, category_id=?";
	
	private DataSource dataSource;

	public SQLProductDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Product> getAllProduct() {
		List<Product> allProduct = new ArrayList<>();
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;

		try {
			con = dataSource.getConnection();
			stat = con.createStatement();
			rs = stat.executeQuery(GET_ALL_PRODUCTS);

			while (rs.next()) {
				allProduct.add(extractionProduct(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		} finally {
			close(con, stat, rs);
		}

		return allProduct;
	}

	@Override
	public int insertProduct(Product model) {
		int productId = 0;
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(INSERT_PRODUCT);
			int k = 1;
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setInt(k++, model.getCount());
			prep.setString(k++, model.getImageLink());
			prep.setInt(k++, model.getCategoryId());

			if (prep.executeUpdate() > 0) {
				rs = prep.getGeneratedKeys();
				if (rs.next()) {
					productId = rs.getInt(1);
					model.setId(productId);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
		} finally {
			close(con, prep, rs);
		}
		return 0;
	}

	@Override
	public Product getProduct(int id) {
		Product model = new Product();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_PRODUCT);
			prep.setInt(1, id);
			rs = prep.executeQuery();
			
			if(rs.next()) {
				model = extractionProduct(rs);
			}
		}catch(SQLException e){
			//TODO LOGGER
		}finally {
			close(con, prep, rs);
		}
	
		return model;
	}

	@Override
	public List<Product> getProductByCategories(List<String> categories) {
		List<Product> productByCategories = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_PRODUCTS_BY_CATEGORY);
			Array categoriesArray = con.createArrayOf("VARCHAR", categories.toArray());
			prep.setArray(1, categoriesArray);
			rs = prep.executeQuery();

			while (rs.next()) {
				productByCategories.add(extractionProduct(rs));
			}

		} catch (SQLException e) {
			// TODO LOGGER
		} finally {
			close(con, prep, rs);
		}

		return productByCategories;
	}

	@Override
	public boolean updateProduct(Product model) {
		boolean result = false;
		Connection con = null;
		PreparedStatement prep = null;
		
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(UPDATE_PRODUCT);
			int k = 1;
			prep.setInt(k++, model.getId());
			prep.setString(k++, model.getName());
			prep.setInt(k++, model.getPrice());
			prep.setString(k++, model.getDescription());
			prep.setInt(k++, model.getCount());
			prep.setString(k++, model.getImageLink());
			prep.setInt(k++, model.getCategoryId());
			
			if(prep.executeUpdate() > 0) {
				result = true;
			}
		}catch(SQLException e) {
			//TODO logger
		}finally {
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
		product.setCategoryId(rs.getInt(k));

		return product;
	}

	private void close(AutoCloseable... autoCloseables) {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
			}
		}
	}

}
