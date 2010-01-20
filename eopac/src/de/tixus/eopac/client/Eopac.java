package de.tixus.eopac.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Eopac implements EntryPoint {
	// private static final String JSON_URL =
	// "https://www.buecherhallen.de/alswww2.dll/APS_QUICK_SEARCH?Style=Portal2&SubStyle=Advanced&Theme=&Lang=GER&ResponseEncoding=utf-8&Style=Portal2&BrowseAsHloc=69";
	// private static final String JSON_URL =
	// "http://localhost:8888/eopac/json?q=ABC+DEF";
	private static final String JSON_URL = "http://localhost/blog/index.html";

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Make call to remote server.
	 */
	public native static void getJson(int requestId, String url, Eopac handler) /*-{
		var callback = "callback" + requestId;

		// [1] Create a script element.
		var script = document.createElement("script");
		script.setAttribute("src", url+callback);
		script.setAttribute("type", "text/javascript");

		// [2] Define the callback function on the window object.
		window[callback] = function(jsonObj) {
		// [3]
		handler.@de.tixus.eopac.client.Eopac::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(jsonObj);
		window[callback + "done"] = true;
		}

		// [4] JSON download has 1-second timeout.
		setTimeout(function() {
		if (!window[callback + "done"]) {
		handler.@de.tixus.eopac.client.Eopac::handleJsonResponse(Lcom/google/gwt/core/client/JavaScriptObject;)(null);
		}

		// [5] Cleanup. Remove script and callback elements.
		document.body.removeChild(script);
		delete window[callback];
		delete window[callback + "done"];
		}, 1000);

		// [6] Attach the script element to the document body.
		document.body.appendChild(script);
	}-*/;

	private final HorizontalPanel addPanel = new HorizontalPanel();
	private final Button addStockButton = new Button("Add");
	private final Label errorMsgLabel = new Label();
	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private final HTML html = new HTML("Placeholder");
	private final Isbn13Validator isbn13Validator = new Isbn13Validator();
	private final int jsonRequestId = 0;
	private final Label lastUpdatedLabel = new Label();
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final TextBox newSymbolTextBox = new TextBox();

	private final ArrayList<String> stocks = new ArrayList<String>();
	private final FlexTable stocksFlexTable = new FlexTable();

	/**
	 * Handle the response to the request for stock data from a remote server.
	 */
	public void handleJsonResponse(final JavaScriptObject jso) {
		if (jso == null) {
			displayError("Couldn't retrieve JSON");
			return;
		}
		html.setHTML(jso.toString());
	}

	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {
		// Create table for stock data.
		stocksFlexTable.setText(0, 0, "ISBN");
		stocksFlexTable.setText(0, 1, "Title");
		stocksFlexTable.setText(0, 2, "Avail");
		stocksFlexTable.setText(0, 3, "Remove");
		stocksFlexTable.setBorderWidth(1);

		// Assemble Add Stock panel.
		addPanel.add(newSymbolTextBox);
		addPanel.add(addStockButton);

		// Assemble Main panel.
		mainPanel.add(stocksFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("mainContainer").add(mainPanel);
		RootPanel.get("outputContainer").add(html);

		newSymbolTextBox.setText("978-3-446-41957-5");
		newSymbolTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		addStockButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				addStock();
			}
		});

		// Listen for keyboard events in the input box.
		newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(final KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					addStock();
				}
			}
		});

	}

	/**
	 * Add stock to FlexTable. Executed when the user clicks the addStockButton
	 * or presses enter in the newSymbolTextBox.
	 */
	private void addStock() {
		final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		newSymbolTextBox.setFocus(true);

		// Don't add the stock if it's invalid or already in the table.
		if (!isbn13Validator.isValid(symbol)) {
			Window.alert("ISBN invalid. (Ex: 978-3-446-41957-5)");
			return;
		}
		if (stocks.contains(symbol)) {
			Window.alert("ISBN already contained.");
			return;
		}

		// Add the stock to the table.
		final int row = stocksFlexTable.getRowCount();
		stocks.add(symbol);
		stocksFlexTable.setText(row, 0, symbol);

		newSymbolTextBox.setText("");

		// Add a button to remove this stock from the table.
		final Button removeStockButton = new Button("x");
		removeStockButton.addClickHandler(new ClickHandler() {
			public void onClick(final ClickEvent event) {
				final int removedIndex = stocks.indexOf(symbol);
				stocks.remove(removedIndex);
				stocksFlexTable.removeRow(removedIndex + 1);
			}
		});
		stocksFlexTable.setWidget(row, 3, removeStockButton);

		requestBookData(symbol);
	}

	private void displayError(final String msg) {
		Window.alert(msg);
	}

	private void getHtml(final String url) {
		// Send request to server and catch any errors.
		final RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				url);

		try {
			final Request request = builder.sendRequest(null,
					new RequestCallback() {
						public void onError(final Request request,
								final Throwable exception) {
							displayError("Couldn't retrieve JSON");
						}

						public void onResponseReceived(final Request request,
								final Response response) {
							if (200 == response.getStatusCode()) {
								html.setHTML(response.getText());
							} else {
								displayError("Couldn't retrieve JSON ("
										+ response.getStatusText() + ")");
							}
						}
					});
		} catch (final RequestException e) {
			displayError("Couldn't retrieve JSON");
		}
	}

	private void requestBookData(final String isbn) {

		// Append the name of the callback function to the JSON URL.
		final String url = URL.encode(JSON_URL) + "?callback=";
		// final String url = URL.encode(JSON_URL);

		// Send request to server by replacing RequestBuilder code with a call
		// to a JSNI method.
		// getJson(jsonRequestId++, url, this);
		getHtml(url);
	}
}