package db;

import org.junit.Before;
import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import db.dao.ProductDao;
import db.entity.Product;
import db.mysql.MySqlProduct;
import util.Category;
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

	private static final String JDBC_DRIVER = "org.h2.Driver";
	private static final String DB_URL = "jdbc:h2:~/test";
	private static final String USER = "youruser";
	private static final String PASS = "yourpassword";

	private static DataSource getDatasource() {
		HikariConfig config = new HikariConfig();
		config.setUsername(USER);
		config.setPassword(PASS);
		config.setJdbcUrl(DB_URL);
		DataSource ds = new HikariDataSource(config);
		return ds;
	}

	static ProductDao productDao = new MySqlProduct(getDatasource());

	@Before
	public void beforeTest() throws SQLException, ClassNotFoundException {
		Class.forName(JDBC_DRIVER);

		try (Connection con = getDatasource().getConnection(); Statement statement = con.createStatement()) {
			String sql = "DROP TABLE IF EXISTS product\n;" + "CREATE TABLE IF NOT EXISTS product (\r\n"
					+ "  id INTEGER NOT NULL AUTO_INCREMENT,\r\n" + "  name VARCHAR(45) NOT NULL,\r\n"
					+ "  price INTEGER NOT NULL,\r\n" + "  description VARCHAR(45) NOT NULL,\r\n"
					+ "  image_link TEXT NOT NULL,\r\n" + "  category VARCHAR(45) NOT NULL,\r\n"
					+ "  PRIMARY KEY (id),\r\n" + "  UNIQUE (id),\r\n" + "  UNIQUE (name));"
					+ "INSERT INTO product VALUES (1, 'Паперони', 320, 'Сыр, мясо и паперони', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Paperonni.jpg', 'Pizza');\r\n"
					+ "INSERT INTO product VALUES (2, 'Маргатира', 250, 'Сыр, помидоры', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Margarita.jpg', 'Pizza');\r\n"
					+ "\r\n"
					+ "INSERT INTO product VALUES (3, 'Кока-кола', 20, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Coca-Cola05.jpg', 'Drinks');\r\n"
					+ "INSERT INTO product VALUES (4, 'Крем-сода', 18, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/krem-soda.jpg', 'Drinks');\r\n"
					+ "\r\n"
					+ "INSERT INTO product VALUES (5, 'Чизбургер', 30, 'Булка, котлета, сыр', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/chizburger.jpg', 'Burger');\r\n"
					+ "INSERT INTO product VALUES (6, 'Гамбургер', 26, 'Булка, котлета', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/gamburger.jpg', 'Burger');";
			statement.executeUpdate(sql);
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
