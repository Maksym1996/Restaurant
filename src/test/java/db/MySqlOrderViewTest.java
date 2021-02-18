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
import org.junit.BeforeClass;
import org.junit.Test;

import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.entity.Product;
import db.mysql.MySqlOrderView;
import db.mysql.MySqlProduct;
import util.SqlTestUtil;
import util.Status;
import util.Util;

public class MySqlOrderViewTest {
	private static DataSource dataSource;

	private OrderViewDao orderViewDao;
	
	@BeforeClass
	public static void setUpClass() {
		dataSource = SqlTestUtil.getDatasource();
	}

	@Before
	public void beforeTest() throws Exception {
		orderViewDao = new MySqlOrderView(dataSource);
		try (Connection con = dataSource.getConnection(); Statement statement = con.createStatement()) {
			statement.executeUpdate(SqlTestUtil.getSqlScript());
		}
	}

	@Test
	public void insertOrderView() throws Exception {
		ProductDao productDao = new MySqlProduct(dataSource);
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(1));
		products.add(productDao.getProduct(2));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 2);
		count.put(2, 1);
		orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 2, "500"), products, count);
		assertEquals(6, orderViewDao.getAllOrders().size());
	}

	@Test(expected = NullPointerException.class)
	public void insertOrderViewNullCount() throws Exception {
		ProductDao productDao = new MySqlProduct(dataSource);
		List<Product> products = new ArrayList<>();
		products.add(productDao.getProduct(1));
		products.add(productDao.getProduct(2));
		Map<Integer, Integer> count = new HashMap<>();
		count.put(1, 2);
		orderViewDao.insertOrder(Util.createOrder(Status.NEW, "Плеханово", 2, "500"), products, count);
	}

	@Test(expected = SQLException.class)
	public void insertOrderViewInvaliduserID() throws Exception {
		ProductDao productDao = new MySqlProduct(dataSource);
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
		assertEquals(Status.NEW.toString(), orderViewDao.getStateByOrderId(1));
	}

	@Test
	public void getStateByOrderIdEqTenExpSQLException() throws Exception {
		assertNull(orderViewDao.getStateByOrderId(10));
	}

	@Test
	public void getAllOrderViewResFour() throws Exception {
		// Thread.sleep(1000);
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
		// Thread.sleep(100);
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