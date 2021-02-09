package sk.matusturjak.exchange_rates.controllers.responses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.matusturjak.exchange_rates.model.LatestRate;
import sk.matusturjak.exchange_rates.service.LatestRateService;

import java.util.List;

@RestController
@RequestMapping("/latest")
public class LatestRateController {

    @Autowired
    private LatestRateService latestRateService;

    @GetMapping
    public ResponseEntity getAllLatest() {
        List<LatestRate> rates = this.latestRateService.getAllLatestRates();
        return rates != null ?
                new ResponseEntity<>(rates, HttpStatus.OK) : new ResponseEntity<>("Something went wrong..", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/{from}")
    public ResponseEntity getLatest(@PathVariable("from") String from) {
        List<LatestRate> rates = this.latestRateService.getLatestRates(from);
        return rates != null ?
                new ResponseEntity<>(rates, HttpStatus.OK) : new ResponseEntity<>("Something went wrong..", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
