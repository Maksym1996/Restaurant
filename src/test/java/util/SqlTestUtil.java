package util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.StringJoiner;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class SqlTestUtil {
	private static final String DB_URL = "jdbc:h2:mem:test";
	private static final String USER = "youruser";
	private static final String PASS = "yourpassword";

	public static DataSource getDatasource() {
		HikariConfig config = new HikariConfig();
		config.setUsername(USER);
		config.setPassword(PASS);
		config.setJdbcUrl(DB_URL);
		DataSource ds = new HikariDataSource(config);
		return ds;
	}

	public static String getSqlScript() {
		StringJoiner sql = new StringJoiner(System.lineSeparator());
		try (FileInputStream fis = new FileInputStream(new File("src/test/resources/db_create.sql"));
				Scanner scanner = new Scanner(fis)) {
			while (scanner.hasNext()) {
				sql.add(scanner.nextLine());
			}
		} catch (Exception e) {
			System.err.print(e);
		}
		return sql.toString();
	}

}
