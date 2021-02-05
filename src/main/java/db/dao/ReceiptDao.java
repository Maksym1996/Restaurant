package db.dao;

import java.util.List;

import db.entity.Receipt;

public interface ReceiptDao {
	public boolean setReceipt(Receipt model) throws Exception;
	
	public List<Receipt> getReceipt(int orderId) throws Exception;
	
	public List<Receipt> getAllReceipt() throws Exception;
}
