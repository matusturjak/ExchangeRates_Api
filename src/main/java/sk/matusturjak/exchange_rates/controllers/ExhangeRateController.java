package sk.matusturjak.exchange_rates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

@RestController
@RequestMapping("/rates")
public class ExhangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExhangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/{start_at}/{end_at}")
    public ResponseEntity getRates(@RequestParam("from") String from, @RequestParam("to") String to,
                                   @PathVariable("start_at") String start_at, @PathVariable("end_at") String end_at) {
        return new ResponseEntity<>(this.exchangeRateService.getRates(from, to, start_at, end_at), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity getAllRates(@RequestParam("from") String from, @RequestParam("to") String to) {
        return new ResponseEntity<>(this.exchangeRateService.getAllRates(from, to), HttpStatus.OK);
    }

    @GetMapping("/latest")
    public ResponseEntity getLastRates(@RequestParam("from") String from, @RequestParam("to") String to, @RequestParam("count") Integer count) {
        return new ResponseEntity<>(this.exchangeRateService.getLastRates(from, to, count), HttpStatus.OK);
    }

    @GetMapping("/{date}")
    public ResponseEntity getRatesByDate(@PathVariable("date") String date, @RequestParam("from") String from, @RequestParam("to") String to) {
        return new ResponseEntity<>(this.exchangeRateService.getRatesByDate(from, to, date), HttpStatus.OK);
    }

}
