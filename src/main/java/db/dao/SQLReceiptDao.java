package db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import db.entity.Receipt;

public class SQLReceiptDao implements ReceiptDao{
	private static final String GET_ALL_RECEIPTS = "SELECT * FROM order_has_product";
	private static final String GET_RECEIPTS_BY_ORDER_ID = "SELECT * FROM order_has_product WHERE orderId = ?";
	private static final String SET_RECEIPT = "INSERT INTO order_has_product VALUE(?,?,?,?)";
	private final DataSource dataSource;

	public SQLReceiptDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean setReceipt(Receipt model) throws Exception {
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		boolean result = true;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(SET_RECEIPT, Statement.RETURN_GENERATED_KEYS);
			int k = 1;
			prep.setInt(k++, model.getOrderId());
			prep.setInt(k++, model.getProductId());
			prep.setInt(k++, model.getCount());
			prep.setInt(k++, model.getCurrentPrice());

			if (prep.executeUpdate() > 0) {
				rs = prep.getGeneratedKeys();
			}
			con.commit();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			rollback(con);
			// TODO some logger
			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}
		return result;
	}
	

	@Override
	public List<Receipt> getReceipt(int orderId) throws Exception {
		List<Receipt> allReceipts = new ArrayList<>();
		Connection con = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			prep = con.prepareStatement(GET_RECEIPTS_BY_ORDER_ID);
			prep.setInt(1, orderId);
			rs = prep.executeQuery();
			while (rs.next()) {
				allReceipts.add(extractionReceipt(rs));
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, prep, rs);
		}

		return allReceipts;
	}

	@Override
	public List<Receipt> getAllReceipt() throws Exception {
		List<Receipt> allReceipts = new ArrayList<>();
		Connection con = null;
		Statement stat = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			stat = con.createStatement();
			rs = stat.executeQuery(GET_ALL_RECEIPTS);
			while (rs.next()) {
				allReceipts.add(extractionReceipt(rs));
			}

		} catch (SQLException e) {
			// TODO some logger

			throw new SQLException();
		} finally {
			close(con, stat, rs);
		}

		return allReceipts;
	}
	
	private Receipt extractionReceipt(ResultSet rs) throws SQLException {
		Receipt receipt = new Receipt();
		int k = 1;
		receipt.setOrderId(rs.getInt(k++));
		receipt.setProductId(rs.getInt(k++));
		receipt.setCount(rs.getInt(k++));
		receipt.setCurrentPrice(rs.getInt(k++));

		return receipt;
	}

	private void close(AutoCloseable... autoCloseables) throws Exception {
		for (AutoCloseable ac : autoCloseables) {
			if (ac != null) {
				try {
					ac.close();
				} catch (Exception e) {
					// TODO add some logger 03.02.2021
					throw new Exception();
				}
			}
		}
	}

	private void rollback(Connection connect) throws SQLException {
		try {
			connect.rollback();
		} catch (SQLException e) {
			// TODO add some logger 03.02.2021
			throw new SQLException();
		}
	}

}
