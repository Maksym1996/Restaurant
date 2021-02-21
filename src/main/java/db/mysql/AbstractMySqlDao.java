package db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Comment;
import exception.DBException;
 
public abstract class AbstractMySqlDao {
	
	protected Logger log = LogManager.getLogger(this.getClass());
	
	protected void close(AutoCloseable... resourcesToClose) throws DBException {
		for (AutoCloseable resourceToClose : resourcesToClose) {
			if (resourceToClose == null) {
				continue;
			}
			try {
				resourceToClose.close();
			} catch (Exception e) {
				log.error(Comment.EXCEPTION + e.getMessage());
				throw new DBException(e);
			}
		}
	}

	protected void rollback(Connection connect) throws DBException {
		if (connect == null) {
			return;
		}
		try {
			connect.rollback();
		} catch (SQLException e) {
			log.error(Comment.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		}
	}

}
