package util;

import java.io.OutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import db.entity.Order;
import db.entity.OrderContent;
import db.entity.Receipt;
import exception.DBException;

public class CreateFile {

	private CreateFile() {
		// nothing
	}

	private static final Font TITLE_FONT = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
	private static final Font TABLE_FORT = FontFactory.getFont("FreeSans.ttf", "Cp1251", true);

	private static final String SPACE = " ";
	private static final String SELLER_LABEL = "Maksym Ltd. ";
	private static final String ORDER_NUMBER = "Заказ номер ";
	private static final int COLUMN_COUNT = 3;
	private static final String LABEL = "Pizza Preferita";
	private static final String ORDER_SUM = "Сумма заказа: ";
	private static final String GRN = "грн";
	private static final String CUSTOMER = "Покупатель: ";
	private static final String SELLER = "Продавец: ";
	private static final String NAME = "Название";
	private static final String QUANTITY = "Количество(шт)";
	private static final String PRICE_GRN = "Цена(грн за шт.)";
	private static final String OPEN_DATE = "Заказ получен: ";
	private static final String CLOSE_DATE = "Заказ закрыт: ";

	public static void writePdfFile(Receipt receipt, OutputStream outStream) throws DBException {
		Order order = receipt.getOrder();
		List<OrderContent> orderContent = receipt.getOrderContent();
		try {
			Document document = new Document();
			PdfWriter.getInstance(document, outStream);
			document.open();
			addContent(document, order, orderContent);
			document.close();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	private static void addContent(Document document, Order order, List<OrderContent> listOrderContent)
			throws DocumentException {
		Paragraph paragraphTitle = new Paragraph(LABEL, TITLE_FONT);
		paragraphTitle.setAlignment(Element.TITLE);
		addEmptyLine(paragraphTitle, 1);
		document.add(paragraphTitle);

		Paragraph paragraphOrderOpenDate = new Paragraph(OPEN_DATE + order.getOrderDate(), TABLE_FORT);
		paragraphOrderOpenDate.setAlignment(Element.ALIGN_LEFT);
		document.add(paragraphOrderOpenDate);

		Paragraph paragraphOrderCloseData = new Paragraph(CLOSE_DATE + order.getClosingDate(), TABLE_FORT);
		paragraphOrderCloseData.setAlignment(Element.ALIGN_LEFT);
		addEmptyLine(paragraphOrderCloseData, 2);
		document.add(paragraphOrderCloseData);

		Paragraph paragraphOrderNumber = new Paragraph(ORDER_NUMBER + order.getId(), TABLE_FORT);
		paragraphOrderNumber.setAlignment(Element.ALIGN_CENTER);
		document.add(paragraphOrderNumber);

		Paragraph orderProducts = new Paragraph(SPACE);
		orderProducts.setAlignment(Element.ALIGN_CENTER);
		createTable(orderProducts, listOrderContent);
		document.add(orderProducts);

		Paragraph paragraphSumm = new Paragraph(ORDER_SUM + order.getSum() + GRN, TABLE_FORT);
		paragraphSumm.setAlignment(Element.ALIGN_LEFT);
		addEmptyLine(paragraphSumm, 1);
		document.add(paragraphSumm);

		Paragraph paragraphCustomer = new Paragraph(
				CUSTOMER + order.getUserFirstName() + SPACE + order.getUserLastName(), TABLE_FORT);
		paragraphCustomer.setAlignment(Element.ALIGN_LEFT);
		document.add(paragraphCustomer);

		Paragraph paragraphSeller = new Paragraph(SELLER + SELLER_LABEL, TABLE_FORT);
		paragraphSeller.setAlignment(Element.ALIGN_LEFT);
		document.add(paragraphSeller);

		document.newPage();
	}

	private static void createTable(Paragraph orderProducts, List<OrderContent> listOrderContent) {
		PdfPTable table = new PdfPTable(COLUMN_COUNT);
		PdfPCell cell = new PdfPCell(new Phrase(NAME, TABLE_FORT));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(QUANTITY, TABLE_FORT));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase(PRICE_GRN, TABLE_FORT));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.setHeaderRows(1);

		for (OrderContent orderContetn : listOrderContent) {
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductName()), TABLE_FORT));
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductCount()), TABLE_FORT));
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductPrice()), TABLE_FORT));
		}
		orderProducts.add(table);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(SPACE));
		}
	}

//	private static final String LONG_SPACE = "    ";
//	private static final String LS = System.lineSeparator();
//	public static File orderReport(Receipt receipt) throws IOException {
//		File reportFile = new File("report.pdf");
//		try (Writer writer = new PrintWriter(reportFile, "UTF-8")) {
//
//			Order order = receipt.getOrder();
//
//			writer.write(LONG_SPACE + "Заказ №" + order.getId());
//			writer.write(LS);
//
//			writer.write(SPACE + "Состав заказа:");
//			writer.write(LS);
//
//			for (OrderContent orderContent : receipt.getOrderContent()) {
//				writer.write(SPACE + orderContent.getProductName());
//				writer.write(SPACE + orderContent.getProductCount() + "шт. *");
//				writer.write(SPACE + orderContent.getProductPrice() + "грн.");
//				writer.write(LS);
//			}
//			writer.write(LS);
//			writer.write("Сумма заказа: " + order.getSum() + "грн.");
//			writer.write(LS);
//			writer.write(LS);
//			writer.write("Дата оформления: " + order.getOrderDate());
//			writer.write(LS);
//			writer.write("Дата закрытия: " + order.getClosingDate());
//			writer.write(LS);
//			writer.write("Заказчик: " + order.getUserFirstName() + SPACE + order.getUserLastName());
//			writer.write(LS);
//			writer.write("Продавец: " + SELLER);
//			writer.write(LS);
//		}
//
//		return reportFile;
//	}

}
