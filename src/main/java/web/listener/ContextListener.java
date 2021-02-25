package web.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import consts.Dao;
import db.dao.UserDao;
import db.mysql.MySqlOrderView;
import db.mysql.MySqlProduct;
import db.mysql.MySqlReceipt;
import db.mysql.MySqlUser;
import db.dao.OrderViewDao;
import db.dao.ProductDao;
import db.dao.ReceiptDao;
import exception.ContextInitException;

@WebListener
public class ContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		initDao(event);
	}

	private void initDao(ServletContextEvent event) {
		DataSource dataSource = getDataSource();

		UserDao userDao = new MySqlUser(dataSource);
		ProductDao productDao = new MySqlProduct(dataSource);
		OrderViewDao orderDao = new MySqlOrderView(dataSource);
		ReceiptDao receiptDao = new MySqlReceipt(dataSource);

		ServletContext servletContext = event.getServletContext();
		servletContext.setAttribute(Dao.USER, userDao);
		servletContext.setAttribute(Dao.PRODUCT, productDao);
		servletContext.setAttribute(Dao.ORDER_VIEW, orderDao);
		servletContext.setAttribute(Dao.RECEIPT, receiptDao);

	}

	private DataSource getDataSource() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:comp/env");
			return (DataSource) envContext.lookup("jdbc/restaurant");
		} catch (NamingException e) {
			throw new ContextInitException("Failed to create datasource", e);
		}
	}

}
