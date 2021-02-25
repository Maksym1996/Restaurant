package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import consts.Path;
import db.entity.Order;
import db.entity.OrderContent;
import db.entity.Receipt;
import exception.DBException;

public class CreateFile {

	private CreateFile() {
		// nothing
	}

	private static final String SPACE = " ";
	private static final String SELLER = "Maksym Ltd. ";
	

	private static Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 24, Font.BOLD);
	private static final String ORDER_NUMBER = "Order number # ";
	private static final int COLUMN_COUNT = 3;

	public static File createPdfFile(Receipt receipt) throws DBException {
		Order order = receipt.getOrder();
		List<OrderContent> orderContent = receipt.getOrderContent();
		File reportFile = new File(Path.REPORT_PDF);
		try {
			Document document = new Document();
			OutputStream outStream = new FileOutputStream(reportFile);
			PdfWriter.getInstance(document, outStream);
			document.open();
			addContent(document, order, orderContent);
			document.close();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return reportFile;
	}

	private static void addContent(Document document, Order order, List<OrderContent> listOrderContent)
			throws DocumentException {
		Paragraph paragraphTitle = new Paragraph("Pizza Preferita");
		paragraphTitle.setAlignment(Element.ALIGN_RIGHT);
		paragraphTitle.setFont(titleFont);
		addEmptyLine(paragraphTitle, 3);
		document.add(paragraphTitle);

		Paragraph paragraphOrderNumber = new Paragraph(ORDER_NUMBER + order.getId());
		paragraphOrderNumber.setAlignment(Element.ALIGN_CENTER);
		addEmptyLine(paragraphOrderNumber, 1);
		document.add(paragraphOrderNumber);
		System.out.println(paragraphOrderNumber.toString());
		
		Paragraph orderProducts = new Paragraph("");
		orderProducts.setAlignment(Element.ALIGN_CENTER);
		createTable(orderProducts, listOrderContent);
		document.add(orderProducts);
		System.out.println(orderProducts.toString());
		
		Paragraph paragraphSumm = new Paragraph("Summ of order: " + order.getSum() + "uah");
		paragraphSumm.setAlignment(Element.ALIGN_LEFT);
		addEmptyLine(paragraphSumm, 1);
		document.add(paragraphSumm);
		System.out.println(paragraphSumm.toString());
		
		Paragraph paragraphCustomer = new Paragraph(
				"Customer: " + order.getUserFirstName() + SPACE + order.getUserLastName());
		paragraphCustomer.setAlignment(Element.ALIGN_LEFT);
		document.add(paragraphCustomer);
		System.out.println(paragraphCustomer.toString());
		
		Paragraph paragraphSeller = new Paragraph("Seller: " + SELLER);
		paragraphSeller.setAlignment(Element.ALIGN_LEFT);
		document.add(paragraphSeller);
		System.out.println(paragraphSeller.toString());
		// Start a new page
		document.newPage();
	}

	private static void createTable(Paragraph orderProducts, List<OrderContent> listOrderContent) {
		PdfPTable table = new PdfPTable(COLUMN_COUNT);
		PdfPCell cell = new PdfPCell(new Phrase("Name of product"));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Количество(шт)"));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		cell = new PdfPCell(new Phrase("Цена(грн)"));
		cell.setVerticalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.setHeaderRows(1);

		for (OrderContent orderContetn : listOrderContent) {
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductName())));
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductCount())));
			table.addCell(new Phrase(String.valueOf(orderContetn.getProductPrice())));
		}
		orderProducts.add(table);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
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
