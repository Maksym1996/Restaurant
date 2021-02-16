package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Before;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import db.dao.OrderViewDao;
import db.mysql.MySqlOrderView;

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
}
