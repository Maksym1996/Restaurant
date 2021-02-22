package db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.CommentConst;
import exception.DBException;

/**
 * Abstract class for general method and fields
 *
 */
public abstract class AbstractMySqlDao {

	protected Logger log = LogManager.getLogger(this.getClass());

	protected void close(AutoCloseable... resourcesToClose) throws DBException {
		for (AutoCloseable resourceToClose : resourcesToClose) {
			if (resourceToClose == null) {
				continue;
			}
			try {
				resourceToClose.close();
				log.debug(CommentConst.CLOSED + resourcesToClose);
			} catch (Exception e) {
				log.error(CommentConst.EXCEPTION + e.getMessage());
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
			log.debug(CommentConst.ROLLBACK + connect);
		} catch (SQLException e) {
			log.error(CommentConst.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		}
	}

}
