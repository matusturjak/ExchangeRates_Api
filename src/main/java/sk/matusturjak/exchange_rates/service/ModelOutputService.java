package sk.matusturjak.exchange_rates.service;

import sk.matusturjak.exchange_rates.model.ModelOutput;

import java.util.List;

public interface ModelOutputService {
    List<Double> getResiduals(String from, String to, String method);
    List<Double> getSigma(String from, String to, String method);
    List<Double> getFitted(String from, String to, String method);
    List<Double> getLatestFitted(String from, String to, int predictions, int numOfFitted);

    List<Double> getResiduals(String from, String to, String method, Integer fromI, Integer toI);
    List<Double> getSigma(String from, String to, Integer fromI, Integer toI, String method);
    List<Double> getFitted(String from, String to, String method, Integer fromI, Integer toI);

    ModelOutput findModelOutput(String from, String to, String method);
    void addModelOutput(ModelOutput modelOutput);
    void updateModelOutput(ModelOutput modelOutput);
}
