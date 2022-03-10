package sk.matusturjak.exchange_rates.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.matusturjak.exchange_rates.service.PredictionService;

@RestController
@RequestMapping("/prediction")
public class PredictionsController {

    private final PredictionService predictionService;

    public PredictionsController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity getPredictions(@RequestParam("from") String from, @RequestParam("to") String to,
                                         @RequestParam("ahead") Integer numberOfPredictions) {
        return new ResponseEntity<>(this.predictionService.getPredictions(from, to, numberOfPredictions), HttpStatus.OK);
    }
}
