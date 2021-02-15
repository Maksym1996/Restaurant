package web;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.dao.ProductDao;
import db.entity.Product;

/**
 * Servlet implementation class UpdateProductServlet
 */
@WebServlet("/UpdateProduct")
public class UpdateProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String patternId = "[1-9]{1} [0-9]*";
    	
    	String productId = request.getParameter("id");
    	
    	if(productId.isEmpty()) {
    		response.sendError(400);
    		return;
    	}
    	
    	if(!Pattern.matches(patternId, productId)) {
    		response.sendError(415);
    		return;
    	}
    	ProductDao productDao = (ProductDao)request.getServletContext().getAttribute("productDao");
    	Product testProduct;
    	try {
    		testProduct = productDao.getProduct(Integer.parseInt(productId));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			throw new IOException();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new IOException();
		}
    	if(testProduct == null) {
    		response.sendError(416);
    		return;
    	}
    	
    	
    	
    	RequestDispatcher dispatcher = request.getRequestDispatcher("UpdateProduct.jsp");
    	request.setAttribute("product", testProduct);
    	dispatcher.forward(request, response);
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.sendRedirect("Pizza Preferita");
	}

}
