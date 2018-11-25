package io.spring.workshop.stockquotes;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
//  We create a `@SpringBootTest`, starting an actual server on a `RANDOM_PORT`
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StockQuotesApplicationTests {

	// Spring Boot will create a `WebTestClient` for you,
	// already configure and ready to issue requests against "localhost:RANDOM_PORT"
	@Autowired
	private WebTestClient webTestClient;

	@Test
	public void fetchQuotesAsStream() {
		List<Quote> result = webTestClient
				.get().uri("/quotes/feed")
				.exchange()
				.expectStatus().isOk()
				.returnResult(Quote.class)
				.getResponseBody()
				.take(30)
				.collectList()
				.block();

		assertThat(result).allSatisfy(quote -> assertThat(quote.getPrice()).isPositive());
	}

}