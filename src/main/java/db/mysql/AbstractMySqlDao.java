package db.mysql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import exception.DBException;

/**
 * Abstract class for general method and fields
 *
 */
public abstract class AbstractMySqlDao {

	protected Logger log = LogManager.getLogger(this.getClass());

	protected void close(AutoCloseable... resourcesToClose) throws DBException {
		log.debug(Log.START);
		for (AutoCloseable resourceToClose : resourcesToClose) {
			if (resourceToClose == null) {
				continue;
			}
			try {
				resourceToClose.close();
				log.trace(Log.CLOSED + resourcesToClose);
			} catch (Exception e) {
				log.error(Log.EXCEPTION + e.getMessage());
				throw new DBException(e);
			}
		}
		log.debug(Log.FINISH);
	}

	protected void rollback(Connection connect) throws DBException {
		log.debug(Log.START);
		if (connect == null) {
			return;
		}
		try {
			connect.rollback();
			log.trace(Log.ROLLBACK + connect);
		} catch (SQLException e) {
			log.error(Log.SQL_EXCEPTION + e.getMessage());
			throw new DBException(e);
		}
		log.debug(Log.FINISH);
	}

}
