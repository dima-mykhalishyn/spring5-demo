package spring.example.route;

import spring.example.logic.ExchangeRate;
import spring.example.logic.RatesProducer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Tests for {@link RatesRouter}
 *
 * @author dmihalishin@gmail.com
 */
public class RatesRouterTest {

	private WebTestClient testClient;

	@Before
	public void createTestClient() {
		final RatesProducer producer = new RatesProducer("http://api.fixer.io");
		this.testClient = WebTestClient.bindToRouterFunction(new RatesRouter().route(producer))
				.configureClient()
				.baseUrl("http://localhost/")
				.responseTimeout(Duration.ofMinutes(1))
				.build();
	}

	@Test
	public void getLatestRate() {
		this.testClient.get()
				.uri("/rates/latest")
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBody(ExchangeRate.class).returnResult();
	}

	@Test
	public void rate4xx() {
		this.testClient.get()
				.uri("/rates/-1")
				.exchange()
				.expectStatus().is4xxClientError();
	}

	@Test
	public void ratesHistory() throws Exception {
		final LocalDate date = LocalDate.now().minusDays(3);
		final String url = String.format("/rates/history/%d/%d/%d",date.getYear(), date.getMonthValue(), date.getDayOfMonth());
		this.testClient.get()
				.uri(url)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_STREAM_JSON)
				.expectBodyList(ExchangeRate.class).hasSize(3).returnResult();
	}

}