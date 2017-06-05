package spring.example.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Exchange Rate DTO
 *
 * @author dmihalishin@gmail.com
 */
public class ExchangeRate {

	private String date;

	private Map<String, Float> rates = new HashMap<>();

	public String getDate() {
		return date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public Map<String, Float> getRates() {
		return rates;
	}

	public void setRates(final Map<String, Float> rates) {
		this.rates = rates;
	}

	@Override
	public int hashCode() {
		return 17 * (this.date == null ? 1 : this.date.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		final ExchangeRate rate = (ExchangeRate) obj;
		return (this.date == null && rate.getDate() == null) || this.date.equals(rate.getDate());
	}
}
