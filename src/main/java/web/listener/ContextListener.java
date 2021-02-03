package web.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import db.dao.UserDao;
import db.dao.ProductDao;
import db.dao.SQLProductDao;
import db.dao.SQLUserDao;
import exception.ContextInitException;

@WebListener
public class ContextListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent event) {
		initDao(event);
    }
	
	private void initDao(ServletContextEvent event) {
		DataSource dataSource = getDataSource();
				
		UserDao userDao = new SQLUserDao(dataSource);
		ProductDao productDao = new SQLProductDao(dataSource);
		
		ServletContext servletContext = event.getServletContext();
		servletContext.setAttribute("userDao", userDao);
		servletContext.setAttribute("productDao", productDao);
	}
	
	private DataSource getDataSource() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:comp/env");
			return (DataSource) envContext.lookup("jdbc/restaurant");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			throw new ContextInitException("Failed to create datasource", e);
		}
	}

    public void contextDestroyed(ServletContextEvent event) {
    	
    }
}
