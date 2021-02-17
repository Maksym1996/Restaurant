package db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.entity.Product;
import db.mysql.MySqlOrderView;
import db.mysql.MySqlProduct;
import util.Status;
import util.Util;

public class MySqlOrderViewTest {
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

	static OrderViewDao orderViewDao = new MySqlOrderView(getDatasource());

	@Before
	public void beforeTest() throws Exception {
		Class.forName(JDBC_DRIVER);

		try (Connection con = getDatasource().getConnection(); Statement statement = con.createStatement()) {
			String sql = "DROP VIEW IF EXISTS orderView;" + 
					"DROP TABLE IF EXISTS product\n;CREATE TABLE IF NOT EXISTS product(\r\n" + 
					"id INTEGER NOT NULL AUTO_INCREMENT,\r\n" + 
					"name VARCHAR(45) NOT NULL,\r\n" + 
					"price INTEGER NOT NULL,\r\n" + 
					"description VARCHAR(45) NOT NULL,\r\n" + 
					"image_link TEXT NOT NULL,\r\n" + 
					"category VARCHAR(45) NOT NULL,\r\n" + 
					"PRIMARY KEY (id),\r\n" + 
					"UNIQUE (id),\r\n" + 
					"UNIQUE (name));\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"DROP TABLE IF EXISTS user\n;CREATE TABLE IF NOT EXISTS user(\r\n" + 
					"id INTEGER NOT NULL AUTO_INCREMENT,\r\n" + 
					"email VARCHAR(45),\r\n" + 
					"first_name VARCHAR(45),\r\n" + 
					"last_name VARCHAR(45),\r\n" + 
					"password VARCHAR(45),\r\n" + 
					"phone_number VARCHAR(10) NOT NULL,\r\n" + 
					"role VARCHAR(45) NOT NULL DEFAULT 'CLIENT',\r\n" + 
					"registered VARCHAR(6) NOT NULL,\r\n" + 
					"PRIMARY KEY (id),UNIQUE (id),\r\n" + 
					"UNIQUE (email),UNIQUE (phone_number));\r\n" + 
					"\r\n" + 
					"DROP TABLE IF EXISTS orders\n;CREATE TABLE IF NOT EXISTS orders (\r\n" + 
					"  id INTEGER NOT NULL AUTO_INCREMENT,\r\n" + 
					"  order_date VARCHAR(45) NULL,\r\n" + 
					"  closing_date VARCHAR(45) NULL,\r\n" + 
					"  state VARCHAR(45) NOT NULL,\r\n" + 
					"  address VARCHAR(80) NOT NULL,\r\n" + 
					"  user_id INTEGER NOT NULL,\r\n" + 
					"  sum VARCHAR(10) NOT NULL,\r\n" + 
					"  PRIMARY KEY (id),\r\n" + 
					"  CONSTRAINT user_id\r\n" + 
					"    FOREIGN KEY (user_id)\r\n" + 
					"    REFERENCES user (id)\r\n" + 
					"    ON DELETE CASCADE\r\n" + 
					"    ON UPDATE CASCADE);\r\n" + 
					"	\r\n" + 
					"DROP TABLE IF EXISTS order_has_product\n;CREATE TABLE IF NOT EXISTS order_has_product (\r\n" + 
					"  order_id INTEGER NOT NULL,\r\n" + 
					"  product_id INTEGER NOT NULL,\r\n" + 
					"  count INTEGER NOT NULL,\r\n" + 
					"  price INTEGER NOT NULL,\r\n" + 
					"  CONSTRAINT product_id\r\n" + 
					"    FOREIGN KEY (product_id)\r\n" + 
					"    REFERENCES product(id)\r\n" + 
					"    ON DELETE CASCADE\r\n" + 
					"    ON UPDATE CASCADE,\r\n" + 
					"  CONSTRAINT order_id\r\n" + 
					"    FOREIGN KEY (order_id)\r\n" + 
					"    REFERENCES orders(id)\r\n" + 
					"    ON DELETE CASCADE\r\n" + 
					"    ON UPDATE CASCADE);\r\n" + 
					"\r\n" + 
					"CREATE VIEW IF NOT EXISTS orderView\r\n" + 
					"AS SELECT * FROM orders o, order_has_product ohp\r\n" + 
					"WHERE o.id = ohp.order_id;\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"INSERT INTO product VALUES (1, 'Паперони', 320, 'Сыр, мясо и паперони', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Paperonni.jpg', 'Pizza');\r\n" + 
					"INSERT INTO product VALUES (2, 'Маргатира', 250, 'Сыр, помидоры', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Margarita.jpg', 'Pizza');\r\n" + 
					"\r\n" + 
					"INSERT INTO product VALUES (3, 'Кока-кола', 20, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/Coca-Cola05.jpg', 'Drinks');\r\n" + 
					"INSERT INTO product VALUES (4, 'Крем-сода', 18, 'Безалкогольный напиток, 0.5л', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/krem-soda.jpg', 'Drinks');\r\n" + 
					"\r\n" + 
					"INSERT INTO product VALUES (5, 'Чизбургер', 30, 'Булка, котлета, сыр', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/chizburger.jpg', 'Burger');\r\n" + 
					"INSERT INTO product VALUES (6, 'Гамбургер', 26, 'Булка, котлета', 'https://raw.githubusercontent.com/Maksym1996/Image_Restaurant/main/gamburger.jpg', 'Burger');\r\n" + 
					"	\r\n" + 
					"	\r\n" + 
					"	\r\n" + 
					"INSERT INTO user VALUES (1, 'kordonets1996@ukr.net', 'Максим', 'Кордонец', '36d2e385ff8453a66347bf048f11668c', '0969055386', 'MANAGER', 'true');\r\n" + 
					"INSERT INTO user VALUES (2, 'Povar@ukr.net', 'Повар', 'Куховаров', '6bb19089370f5bb5478f7ec1b337f255', '0969055385', 'COOK', 'true');\r\n" + 
					"INSERT INTO user VALUES (3, 'Courier@ukr.net', 'Курьер', 'Доставщиков', '6bb19089370f5bb5478f7ec1b337f255', '0969055384', 'COURIER', 'true');\r\n" + 
					"INSERT INTO user VALUES (4, 'kordonetsmax@gmail.com', 'Клиент', 'Посетитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055383', 'CLIENT', 'true');\r\n" + 
					"INSERT INTO user VALUES (5, 'maxkorodnets@gmail.com', 'Админ', 'Распорядитович', '6bb19089370f5bb5478f7ec1b337f255', '0969055382', 'ADMIN', 'true');					\r\n" + 
					"\r\n" + 
					"\r\n" + 
					"";
			statement.executeUpdate(sql);
		}
		ProductDao productDao = new MySqlProduct(getDatasource());
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(1));
		products.add(productDao.getProduct(2));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 2);
		count.put(2, 1);
		orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 2, "500"), products, count);
		orderViewDao.insertOrder(Util.createOrder(Status.REJECTED, "Плеханово", 4, "500"), products, count);
	}
	
	
	
	@Test
	public void insertOrderView() throws Exception {
		ProductDao productDao = new MySqlProduct(getDatasource());
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(1));
		products.add(productDao.getProduct(2));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 2);
		count.put(2, 1);
		assertEquals(3,orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 2, "500"), products, count));
	}
	
	
	
	@Test(expected = NullPointerException.class)
	public void insertOrderViewNullCount() throws Exception {
		ProductDao productDao = new MySqlProduct(getDatasource());
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(1));
		products.add(productDao.getProduct(2));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 2);
		orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 2, "500"), products, count);
	}
	
	@Test(expected = SQLException.class)
	public void insertOrderViewInvaliduserID() throws Exception {
		ProductDao productDao = new MySqlProduct(getDatasource());
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(3));
		products.add(productDao.getProduct(4));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(3, 2);
		count.put(4, 1);
		orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 9, "500"), products, count);
	}
	
	@Test
	public void getStateByOrderIdEqOneStateEqNEW() throws Exception {
		assertEquals(Status.NEW.toString(),orderViewDao.getStateByOrderId(1));
	}
	
	@Test
	public void getStateByOrderIdEqTenExpSQLException() throws Exception {
		assertNull(orderViewDao.getStateByOrderId(10));
	}
	
	@Test
	public void getAllOrderViewResFour() throws Exception {
		assertEquals(4, orderViewDao.getAllOrders().size());
	}
	
	@Test
	public void getOrderViewsByUserIdTwoExpectTwo() throws Exception {
		assertEquals(2, orderViewDao.getOrdersByUserId(2).size());
	}
	
	@Test
	public void getOrderViewsByInvalidUserIdExpectZero() throws Exception {
		assertEquals(0, orderViewDao.getOrdersByUserId(10).size());
	}
	
	@Test
	public void getOrderByStatusNEWExpTwo() throws Exception {
		assertEquals(2, orderViewDao.getOrdersByStatus(Status.NEW.toString()).size());
	}
	
	@Test
	public void getOrderByStatusInvalidExpZero() throws Exception {
		assertEquals(0, orderViewDao.getOrdersByStatus("CHO").size());
	}
	
	@Test
	public void UpdateOrderStatusCOOKEDExpTrue() throws Exception {
		assertTrue(orderViewDao.updateOrderState(1, Status.COOKED.toString()));
	}
	
	@Test
	public void UpdateOrderStatusCOOKEDInvalidOrderIDExpFalse() throws Exception {
		assertFalse(orderViewDao.updateOrderState(10, Status.COOKED.toString()));
	}
	
	@Test
	public void UpdateOrderStatusREJECTEDExpTrue() throws Exception {
		assertTrue(orderViewDao.updateOrderState(2, Status.REJECTED.toString()));
	}
	
	@Test
	public void UpdateOrderStatusPERFORMEDExpTrue() throws Exception {
		assertTrue(orderViewDao.updateOrderState(2, Status.PERFORMED.toString()));
	}
}
