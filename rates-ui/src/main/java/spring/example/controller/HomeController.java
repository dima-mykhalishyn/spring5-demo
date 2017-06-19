package spring.example.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import spring.example.dto.ExchangeRate;

import java.time.LocalDate;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

/**
 * Home Controller
 *
 * @author dmihalishin@gmail.com
 */
@RestController
public class HomeController {

	private final String RATES_SERVICE = "rates.service";

	private final String url;

	private final EurekaClient eurekaClient;

	@Autowired
	public HomeController(final EurekaClient eurekaClient) {
		final LocalDate date = LocalDate.ofYearDay(2000, 1);
		this.url = String.format("/rates/history/%d/%d/%d", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		this.eurekaClient = eurekaClient;
	}

	@GetMapping(path = "/rates/history", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	@ResponseBody
	public Flux<ExchangeRate> fetchRatesStream() {
		final InstanceInfo ratesService = this.eurekaClient.getNextServerFromEureka(RATES_SERVICE, false);
		return WebClient.create(ratesService.getHomePageUrl())
				.get()
				.uri(url)
				.accept(APPLICATION_STREAM_JSON)
				.retrieve()
				.bodyToFlux(ExchangeRate.class)
				.share()
				.log();
	}

}
