package sk.matusturjak.exchange_rates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.service.LatestRateService;

@RestController
@RequestMapping("/latest")
public class LatestRateController {

    @Autowired
    private LatestRateService latestRateService;

    @GetMapping
    public ResponseEntity getAllLatest() {
        return new ResponseEntity<>(this.latestRateService.getAllLatestRates(), HttpStatus.OK);
    }

    @GetMapping("/{from}")
    public ResponseEntity getLatest(@PathVariable("from") String from) {
        return new ResponseEntity<>(this.latestRateService.getLatestRates(from), HttpStatus.OK);
    }

    @GetMapping("/{from}/{to}")
    public ResponseEntity getLatest(@PathVariable("from") String from, @PathVariable("to") String to) {
        LatestRate latestRate = this.latestRateService.getLatestRate(from, to);
        return latestRate != null ?
                new ResponseEntity<>(latestRate, HttpStatus.OK) : new ResponseEntity<>("Not founded..", HttpStatus.NOT_FOUND);
    }
}
