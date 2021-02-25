package web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import consts.Dao;
import consts.Log;
import consts.Param;
import db.dao.ReceiptDao;
import db.entity.Receipt;
import db.entity.User;
import exception.DBException;
import util.CreateFile;
import util.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Servlet that implements interface language switching
 *
 */
@WebServlet("/DownloadReport")
public class DownloadReport extends HttpServlet {

	private static final long serialVersionUID = 2689567392392305656L;

	private static final Logger LOG = LogManager.getLogger(DownloadReport.class);

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);

		String requestOrderId = request.getParameter(Param.ORDER_ID);
		LOG.trace(Param.ORDER_ID + ": " + requestOrderId);

		int orderId = Validator.intValidatorReturnInt(requestOrderId);
		LOG.trace(Param.ORDER_ID + " after validator: " + orderId);

		if (orderId == 0) {
			response.sendError(404);
			LOG.debug(Log.FINISH_WITH + 404);

			return;
		}

		HttpSession session = request.getSession();

		User user = (User) session.getAttribute(Param.USER);
		LOG.trace("User from session " + user.getPhoneNumber());

		ReceiptDao receiptDao = (ReceiptDao) request.getServletContext().getAttribute(Dao.RECEIPT);

		Receipt receipt = null;
		try {
			receipt = receiptDao.getReceipt(orderId, user.getPhoneNumber());
		} catch (DBException e) {
			LOG.error(Log.DB_EXCEPTION + e.getMessage());
			response.sendError(500);
			LOG.debug(Log.FINISH_WITH + 500);
		}

		if (receipt == null) {
			response.sendError(400);
			LOG.debug(Log.FINISH_WITH + 400);
			return;
		}

		String fileName = receipt.getOrder().getClosingDate();

		// response.setContentType("text/plain");
		response.setContentType("application/pdf");
		response.setCharacterEncoding("UTF-8");
		// response.setHeader("Content-disposition", "attachment; filename=" + fileName
		// + ".txt");
		response.setHeader("Content-disposition", "attachment; filename=" + fileName + ".pdf");

		// File reportFile = CreateFile.orderReport(receipt);
		File reportFile;
		try {
			reportFile = CreateFile.createPdfFile(receipt);
		} catch (Exception e) {
			LOG.error(Log.EXCEPTION + e);
			response.sendError(500);
			LOG.debug(Log.FINISH_WITH + 500);

			return;
		}

		try (OutputStream out = response.getOutputStream(); FileInputStream in = new FileInputStream(reportFile)) {

			byte[] buffer = new byte[4096];
			int length;

			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			out.flush();
		}
		LOG.debug(Log.FINISH);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOG.info(Log.BEGIN);
		LOG.info("doGet()");

		doGet(request, response);
	}

}
