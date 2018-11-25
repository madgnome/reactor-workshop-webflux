package io.spring.workshop.stockquotes;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class QuoteGenerator {

  private final MathContext mathContext = new MathContext(2);

  private final Random random = new Random();

  static final List<Quote> prices = Arrays.asList(
      new Quote("TEAM", 73.20),
      new Quote("MSFT", 65.11),
      new Quote("GOOG", 200.00),
      new Quote("SPOT", 129.41),
      new Quote("AMZN", 150.06)
  );

  public Flux<Quote> fetchQuoteStream(Duration period) {
    // TODO: We want to emit quotes with a specific period;
    // 1. For each tick
    // 2. Generate a list of quotes using `generateQuotes`
    // 3. "flatten" that List<Quote> into a Flux<Quote>
    // 4. Drop events on backpressure
    return Flux.<Quote>empty()
        .log("io.spring.workshop.stockquotes");
  }

  /*
   * Create quotes for all tickers at a single instant.
   */
  private List<Quote> generateQuotes(long interval) {
    final Instant instant = Instant.now();
    return prices
        .stream()
        .map(
            baseQuote -> {
              BigDecimal priceChange =
                  baseQuote
                      .getPrice()
                      .multiply(new BigDecimal(0.20 * this.random.nextDouble()), this.mathContext);
              Quote result =
                  new Quote(baseQuote.getTicker(), baseQuote.getPrice().add(priceChange));
              result.setInstant(instant);
              return result;
            })
        .collect(Collectors.toList());
  }
}
