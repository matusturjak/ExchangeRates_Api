package sk.matusturjak.exchange_rates.controllers.responses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.matusturjak.exchange_rates.model.Prediction;
import sk.matusturjak.exchange_rates.service.PredictionService;

import java.util.List;

@RestController
@RequestMapping("/prediction")
public class PredictionsController {

    @Autowired
    private PredictionService predictionService;

    @GetMapping("/{from}-{to}/{numberOfPredictions}")
    public ResponseEntity getPredictions(@PathVariable("from") String from, @PathVariable("to") String to,
                                         @PathVariable("numberOfPredictions") Integer numberOfPredictions) {
        List<Prediction> rates = this.predictionService.getPredictions(from, to, numberOfPredictions);
        return rates != null ?
                new ResponseEntity<>(rates,HttpStatus.OK) : new ResponseEntity<>("Something went wrong..",HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
