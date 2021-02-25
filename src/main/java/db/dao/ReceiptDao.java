package db.dao;

import java.util.List;

import db.entity.Receipt;
import exception.DBException;

public interface ReceiptDao {
	
	List<Receipt> getListOfReceipts(String userRole) throws DBException;
	
	List<Receipt> getListOfReceiptsByUserId(int userId) throws DBException;
	
	Receipt getReceipt(int orderId, String userPhoneNumber) throws DBException;

}
