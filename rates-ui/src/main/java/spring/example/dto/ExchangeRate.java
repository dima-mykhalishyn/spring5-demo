package spring.example.dto;

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
}
