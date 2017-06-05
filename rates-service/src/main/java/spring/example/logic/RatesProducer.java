package spring.example.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.stream.LongStream;

/**
 * Rates Producer
 *
 * @author dmihalishin@gmail.com
 */
@Component
public class RatesProducer {

	private static final Logger LOGGER = LoggerFactory.getLogger(RatesProducer.class);

	private final String fixerUrl;

	public RatesProducer(@Value("${fixer.url}") final String fixerUrl) {
		this.fixerUrl = fixerUrl;
	}

	public Flux<ExchangeRate> getExchangeRateHistoryForCurrency(final String currency,
																					final LocalDate startDate) {
		return Flux.fromStream(LongStream.range(0, LocalDate.now().toEpochDay() - startDate.toEpochDay()).boxed()).map(
				index -> {
					final LocalDate requestDate = startDate.plusDays(index);
					final ExchangeRate exchangeRate = this.getRate(currency, requestDate);
					LOGGER.info(exchangeRate.getDate());
					// set the request date => could be different in the response from fixer
					exchangeRate.setDate(requestDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
					return exchangeRate;
				}
		).zipWith(Flux.interval(Duration.ofSeconds( // simulate delay
				new Random().nextInt(9) + 1
		))).map(Tuple2::getT1).share().log();
	}

	public Mono<ExchangeRate> getMonoRate(final String currency, final LocalDate date) {
		final String parameter = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
		return WebClient.create(this.fixerUrl).get().uri(builder -> builder.replacePath("/" + parameter).queryParam("base", currency).build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToMono(ExchangeRate.class).cache().log();
	}

	private ExchangeRate getRate(final String currency, final LocalDate startDate) {
		final String parameter = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
		URI uri = URI.create(String.format(this.fixerUrl + "/%s?base=%s", parameter, currency));
		return new RestTemplate().getForObject(uri, ExchangeRate.class);
	}
}
