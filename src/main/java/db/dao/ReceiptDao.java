package db.dao;

import java.util.List;

import db.entity.Receipt;
import exception.DBException;

public interface ReceiptDao {
	
	List<Receipt> getListOfReceipts() throws DBException;

}
