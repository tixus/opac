package de.tixus.eopac.domain;

import org.joda.time.DateTime;

public interface StockStatus {
	DateTime getAvailabilityDate();

	String getRemark();

	boolean isAvailable();
}
