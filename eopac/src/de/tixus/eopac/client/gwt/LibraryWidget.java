/**
 * 
 */
package de.tixus.eopac.client.gwt;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author TSP
 *
 */
public class LibraryWidget extends Composite {

	private static LibraryWidgetUiBinder uiBinder = GWT
			.create(LibraryWidgetUiBinder.class);

	interface LibraryWidgetUiBinder extends UiBinder<Widget, LibraryWidget> {
	}

	@UiField
	Button button;

	public LibraryWidget(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

		// Can access @UiField after calling createAndBindUi
		button.setText(firstName);
	}

	@UiHandler("button")
	void onClick(ClickEvent e) {
		Window.alert("Hello!");
	}

}
