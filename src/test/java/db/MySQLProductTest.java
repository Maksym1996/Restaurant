package db;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import db.dao.ProductDao;
import db.entity.Product;
import db.mysql.MySqlProduct;
import util.Category;
import util.SqlTestUtil;
import util.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.sql.DataSource;

public class MySQLProductTest {

	private static DataSource dataSource;

	@BeforeClass
	public static void setUpClass() {
		dataSource = SqlTestUtil.getDatasource();
	}

	private ProductDao productDao;

	@Before
	public void beforeTest() throws SQLException, ClassNotFoundException {
		productDao = new MySqlProduct(dataSource);
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
			statement.executeUpdate(SqlTestUtil.getSqlScript());
		}
	}

	@Test
	public void insertProductReturnId() throws Exception {
		assertEquals(7, productDao
				.insertProduct(Util.createProduct("Prod", 100, "description", "imageLink", Category.BURGER, 0)));
	}

	@Test(expected = SQLException.class)
	public void insertInvalidProduct() throws Exception {
		Product product = new Product();
		product.setCategory(Category.BURGER);
		productDao.insertProduct(product);
	}

	@Test
	public void getProduct() throws Exception {
		Product productFromDB = productDao.getProduct(2);

		Product product = new Product();
		product.setId(2);
		product.setName("Маргарита");
		product.setPrice(250);
		product.setDescription("");
		product.setImageLink("");
		product.setCategory(Category.BURGER);

		assertEquals(productFromDB.hashCode(), product.hashCode());
		assertEquals(productFromDB, (product));

	}

	@Test
	public void getNullProduct() throws Exception {
		Product productFromDB = productDao.getProduct(0);
		assertNull(productFromDB.getCategory());
	}

	@Test
	public void updateProduct() throws Exception {
		Product product = new Product();
		product.setId(2);
		product.setName("Маргари");
		product.setPrice(250);
		product.setDescription("");
		product.setImageLink("");
		product.setCategory(Category.BURGER);

		assertTrue(productDao.updateProduct(product));
	}

	@Test(expected = SQLException.class)
	public void updateProductDublicateName() throws Exception {
		Product product = new Product();
		product.setId(2);
		product.setName("Гамбургер");
		product.setPrice(250);
		product.setDescription("");
		product.setImageLink("");
		product.setCategory(Category.BURGER);
		productDao.updateProduct(product);
	}

	@Test
	public void getProductCountByCategory() throws Exception {
		long actual = productDao.getProductCount(new String[] { Category.BURGER.toString() });
		long expected = 2;
		assertEquals(expected, actual);
	}

	@Test
	public void getProductCountByInvalidCategory() throws Exception {
		long actual = productDao.getProductCount(new String[] { "Milk" });
		long expected = 0;
		assertEquals(expected, actual);
	}

	@Test
	public void getProductCountByEmptyCategory() throws Exception {
		long actual = productDao.getProductCount(new String[] { "" });
		long expected = 0;
		assertEquals(expected, actual);
	}

	@Test
	public void getProductByCategoriesOnPage() throws Exception {
		List<Product> products = productDao.getProductByCategoriesOnPage(new String[] {}, "name", "true", 2, 8);
		long expected = 4;
		assertEquals(expected, products.size());
	}

	@Test
	public void getProductByCategoriesOnPageInvalidArguments() throws Exception {
		List<Product> products = productDao.getProductByCategoriesOnPage(new String[] {}, "price", "false", -1, -2);
		long expected = 6;
		assertEquals(expected, products.size());
	}

	@Test
	public void getProductByCategoryOnPage() throws Exception {
		List<Product> products = productDao.getProductByCategoriesOnPage(new String[] { Category.BURGER.toString() },
				"category", "false", 0, 10);
		long expected = 2;
		assertEquals(expected, products.size());
	}

	@Test
	public void getProductByCategoryOnPageByPrice() throws Exception {
		List<Product> products = productDao.getProductByCategoriesOnPage(new String[] { Category.PIZZA.toString() },
				"description", "true", 0, 10);
		long expected = 2;
		assertEquals(expected, products.size());
	}

	@Test
	public void deleteProduct() throws Exception {
		assertTrue(productDao.deleteProduct(2));
	}

	@Test
	public void deleteNonExistentProduct() throws Exception {
		assertFalse(productDao.deleteProduct(9));
	}

}
