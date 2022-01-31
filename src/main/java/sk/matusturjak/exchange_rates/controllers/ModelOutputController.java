package sk.matusturjak.exchange_rates.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.matusturjak.exchange_rates.service.ModelOutputService;

@RestController
@RequestMapping("/model")
public class ModelOutputController {

    private final ModelOutputService modelOutputService;

    public ModelOutputController(ModelOutputService modelOutputService) {
        this.modelOutputService = modelOutputService;
    }

    @GetMapping("/fitted")
    public ResponseEntity getFittedValues(@RequestParam("from") String from,
                                          @RequestParam("to") String to,
                                          @RequestParam("method") String method) {
        return new ResponseEntity<>(this.modelOutputService.getFitted(from, to, method), HttpStatus.OK);
    }


}
