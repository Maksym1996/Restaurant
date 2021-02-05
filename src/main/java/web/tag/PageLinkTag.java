package web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class PageLinkTag extends SimpleTagSupport {

	private int pageNumber;
	private String[] categories;
	private String sortValue;
	private String asc;
    private String productId;
	
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}

	public void setSortValue(String sortValue) {
		this.sortValue = sortValue;
	}

	public void setAsc(String asc) {
		this.asc = asc;
	}
	
	public void setProductId(String productId) {
		this.productId = productId;
	}

	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		StringBuilder link = new StringBuilder();
		link.append("/restaurant-web/Pizza Preferita").append("?page=").append(pageNumber);
		if (categories != null) {
			for (String category : categories) {
				link.append("&categories=").append(category);
			}
		}
		if (sortValue != null) {
			link.append("&sortValue=").append(sortValue);
		}
		if (asc != null) {
			link.append("&asc=").append(asc);
		}
		if(productId != null) {
			link.append("&productId=").append(productId);
		}
		out.print(link.toString());
	}
}
