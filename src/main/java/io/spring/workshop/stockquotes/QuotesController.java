package io.spring.workshop.stockquotes;

import static java.time.Duration.ofMillis;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
public class QuotesController {

  private final Flux<Quote> quoteStream;

  public QuotesController(QuoteGenerator quoteGenerator) {
    quoteStream = quoteGenerator.fetchQuoteStream(ofMillis(1000)).share();
  }

  @CrossOrigin
  @GetMapping(path = "/quotes/feed", produces = TEXT_EVENT_STREAM_VALUE)
  @ResponseBody
  public Flux<Quote> quotesStream() {
    return quoteStream;
  }
}
