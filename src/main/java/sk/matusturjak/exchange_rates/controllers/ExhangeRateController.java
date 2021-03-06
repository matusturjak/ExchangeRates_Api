package sk.matusturjak.exchange_rates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.matusturjak.exchange_rates.service.ExchangeRateService;

@RestController
@RequestMapping("/rates")
public class ExhangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("/{from}-{to}/{start_at}/{end_at}")
    public ResponseEntity getRates(@PathVariable("from") String from, @PathVariable("to") String to,
                                   @PathVariable("start_at") String start_at, @PathVariable("end_at") String end_at) {
        return new ResponseEntity<>(this.exchangeRateService.getRates(from, to, start_at, end_at), HttpStatus.OK);
    }

    @GetMapping("/{from}-{to}")
    public ResponseEntity getAllRates(@PathVariable("from") String from, @PathVariable("to") String to) {
        return new ResponseEntity<>(this.exchangeRateService.getAllRates(from, to), HttpStatus.OK);
    }

    @GetMapping("/{from}-{to}/{count}")
    public ResponseEntity getLastRates(@PathVariable("from") String from, @PathVariable("to") String to, @PathVariable("count") Integer count) {
        return new ResponseEntity<>(this.exchangeRateService.getLastRates(from, to, count), HttpStatus.OK);
    }

}
