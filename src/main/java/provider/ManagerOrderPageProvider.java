package provider;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Log;
import consts.Path;
import db.dao.ReceiptDao;
import db.entity.Receipt;
import exception.ProviderException;
import util.UserRole;

public class ManagerOrderPageProvider implements OrderPageProvider {

	private static final Logger log = LogManager.getLogger(ManagerOrderPageProvider.class);

	private ReceiptDao receiptDao;

	public ManagerOrderPageProvider(ReceiptDao receiptDao) {
		log.info("In constructor orderDao: " + receiptDao);
		this.receiptDao = receiptDao;
	}

	@Override
	public OrderPage getOrderPage() {
		log.info(Log.BEGIN);
		List<Receipt> receiptList;
		try {
			receiptList = receiptDao.getListOfReceipts(UserRole.MANAGER.name());
		} catch (Exception e) {
			log.error(Log.EXCEPTION + e.getMessage());
			throw new ProviderException(e);
		}
		log.info("return orderList and " + Path.MANAGER_JSP);
		return new OrderPage(receiptList, Path.MANAGER_JSP);
	}

}
