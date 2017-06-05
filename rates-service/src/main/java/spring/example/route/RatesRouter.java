package spring.example.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import spring.example.logic.ExchangeRate;
import spring.example.logic.RatesProducer;

import java.time.LocalDate;
import java.util.function.Function;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * Rates Router
 *
 * @author dmihalishin@gmail.com
 */
@Configuration
public class RatesRouter {

	private static final String DATE_PATTERN = "{year:20[0-9]{2}}/{month:[0-9]{1,2}}/{day:[0-9]{1,2}}";

	private static final String CURRENCY = "currency";

	private static final String EURO = "EUR";

	@Bean
	public RouterFunction<ServerResponse> route(final RatesProducer ratesProducer) {
		final Function<ServerRequest, LocalDate> dateFunction = request -> LocalDate.of(
				Integer.valueOf(request.pathVariable("year")),
				Integer.valueOf(request.pathVariable("month")),
				Integer.valueOf(request.pathVariable("day"))
		);
		return RouterFunctions
				.route(GET("/rates/history/" + DATE_PATTERN).and(accept(APPLICATION_STREAM_JSON)),
						request -> {
							final LocalDate date = dateFunction.apply(request);
							return ServerResponse.ok().contentType(APPLICATION_STREAM_JSON).body(
									ratesProducer.getExchangeRateHistoryForCurrency(
											request.queryParam(CURRENCY).orElse(EURO), date
									), ExchangeRate.class
							);
						})
				.andRoute(GET("/rates/" + DATE_PATTERN).and(accept(APPLICATION_JSON)),
						request -> {
							final LocalDate date = dateFunction.apply(request);
							return getDateRate(request, ratesProducer, date);
						})
				.andRoute(
						GET("/rates/latest").and(accept(APPLICATION_JSON)),
						request -> getDateRate(request, ratesProducer, LocalDate.now()));
	}

	private Mono<ServerResponse> getDateRate(final ServerRequest request, final RatesProducer ratesProducer,
														  final LocalDate date) {
		return ServerResponse.ok().contentType(APPLICATION_JSON).body(
				ratesProducer.getMonoRate(request.queryParam(CURRENCY).orElse(EURO),
						date), ExchangeRate.class
		);
	}

}
