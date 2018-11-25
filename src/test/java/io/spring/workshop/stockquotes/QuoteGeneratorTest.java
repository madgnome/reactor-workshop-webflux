package io.spring.workshop.stockquotes;

import java.time.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class QuoteGeneratorTest {

  @Rule public MockitoRule rule = MockitoJUnit.rule();

  private QuoteGenerator quoteGenerator;

  @Before
  public void setUp() throws Exception {
    quoteGenerator = new QuoteGenerator();
  }

  @Test
  public void shouldEmitPricesEveryInterval() {
    StepVerifier.withVirtualTime(() -> quoteGenerator.fetchQuoteStream(Duration.ofSeconds(1)))
        .thenAwait(Duration.ofSeconds(1))
        .expectNextCount(QuoteGenerator.prices.size())
        .thenAwait(Duration.ofSeconds(1))
        .expectNextCount(QuoteGenerator.prices.size())
        .thenCancel()
        .verify(Duration.ofSeconds(1));
  }

  @Test
  public void shouldEmitQuoteForAtlassian() {
    StepVerifier.withVirtualTime(
            () ->
                quoteGenerator
                    .fetchQuoteStream(Duration.ofSeconds(1))
                    .filter(quote -> "TEAM".equals(quote.getTicker())))
        .thenAwait(Duration.ofSeconds(1))
        .expectNextCount(1)
        .thenCancel()
        .verify(Duration.ofSeconds(1));
  }

  @Test
  public void shoulDropEventInCaseOfBackpressure() {
    StepVerifier.withVirtualTime(
            () ->
                quoteGenerator
                    .fetchQuoteStream(Duration.ofMillis(10))
                    .flatMap(aLong -> Flux.just(aLong).delayElements(Duration.ofSeconds(1))))
        .thenAwait(Duration.ofSeconds(10))
        .expectNextCount(1000)
        .thenCancel()
        .verify(Duration.ofSeconds(10));
  }
}
