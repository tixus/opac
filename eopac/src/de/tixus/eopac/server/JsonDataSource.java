/**
 * 
 */
package de.tixus.eopac.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author TSP
 * 
 */
public class JsonDataSource extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final double MAX_PRICE = 100.0; // $100.00
	private static final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

	@Override
	protected void doGet(final HttpServletRequest req,
			final HttpServletResponse resp) throws ServletException,
			IOException {

		final Random rnd = new Random();

		final PrintWriter out = resp.getWriter();
		out.println('[');
		final String[] stockSymbols = req.getParameter("q").split(" ");
		for (final String stockSymbol : stockSymbols) {

			final double price = rnd.nextDouble() * MAX_PRICE;
			final double change = price * MAX_PRICE_CHANGE
					* (rnd.nextDouble() * 2f - 1f);

			out.println("  {");
			out.print("    \"symbol\": \"");
			out.print(stockSymbol);
			out.println("\",");
			out.print("    \"price\": ");
			out.print(price);
			out.println(',');
			out.print("    \"change\": ");
			out.println(change);
			out.println("  },");
		}
		out.println(']');
		out.flush();
	}
}
