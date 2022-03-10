package sk.matusturjak.exchange_rates.service.impl;

import org.springframework.stereotype.Service;
import sk.matusturjak.exchange_rates.model.ModelOutput;
import sk.matusturjak.exchange_rates.repository.ModelOutputRepository;
import sk.matusturjak.exchange_rates.service.ModelOutputService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ModelOutputServiceImpl implements ModelOutputService {

    private ModelOutputRepository modelOutputRepository;

    public ModelOutputServiceImpl(ModelOutputRepository modelOutputRepository) {
        this.modelOutputRepository = modelOutputRepository;
    }

    @Override
    public List<Double> getResiduals(String from, String to, String method) {
        ModelOutput residuals = this.modelOutputRepository.getModelOutput(from, to, method);
        List<String> arr = residuals != null ? Arrays.asList(residuals.getResiduals().split(",")) : new LinkedList<>();

        return arr.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    @Override
    public List<Double> getSigma(String from, String to, String method) {
        ModelOutput sigma = this.modelOutputRepository.getSigma(from, to, method);
        List<String> arr = sigma != null ? Arrays.asList(sigma.getSigma().split(",")) : new LinkedList<>();

        return arr.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    @Override
    public List<Double> getFitted(String from, String to, String method) {
        ModelOutput modelOutput = this.modelOutputRepository.getModelOutput(from, to, method);
        List<String> arr = modelOutput != null ? Arrays.asList(modelOutput.getFitted().split(",")) : new LinkedList<>();

        return arr.stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    @Override
    public List<Double> getLatestFitted(String from, String to, String method, int numOfFitted) {
        ModelOutput modelOutput = this.modelOutputRepository.getModelOutput(from, to, method);
        List<String> arr = modelOutput != null ? Arrays.asList(modelOutput.getFitted().split(",")) : new LinkedList<>();

        return arr.isEmpty() ? new LinkedList<>()
                : arr.subList(arr.size() - numOfFitted, arr.size()).stream().map(Double::parseDouble).collect(Collectors.toList());
    }

    @Override
    public List<Double> getResiduals(String from, String to, String method, Integer fromI, Integer toI) {
        if (fromI == null) {
            fromI = 0;
        }
        List<Double> residuals = this.getResiduals(from, to, method);
        if (toI == null) {
            toI = residuals.size();
        } else {
            toI++; // we want inclusive not exclusive
        }
        return fromI > toI ? new LinkedList<>() : residuals.subList(fromI, toI);
    }

    @Override
    public List<Double> getSigma(String from, String to, Integer fromI, Integer toI, String method) {
        if (fromI == null) {
            fromI = 0;
        }
        List<Double> sigmas = this.getSigma(from, to, method);
        if (toI == null) {
            toI = sigmas.size();
        } else {
            toI++; // we want inclusive not exclusive
        }
        return fromI > toI ? new LinkedList<>() : sigmas.subList(fromI, toI);
    }

    @Override
    public List<Double> getFitted(String from, String to, String method, Integer fromI, Integer toI) {
        if (fromI == null) {
            fromI = 0;
        }
        List<Double> fitted = this.getFitted(from, to, method);
        if (toI == null) {
            toI = fitted.size();
        } else {
            toI++;
        }
        return fromI > toI ? new LinkedList<>() : fitted.subList(fromI, toI);
    }

    @Override
    public ModelOutput findModelOutput(String from, String to, String method) {
        return this.modelOutputRepository.getModelOutput(from, to, method);
    }

    @Override
    public void addModelOutput(ModelOutput modelOutput) {
        this.modelOutputRepository.save(modelOutput);
    }

    @Override
    public void updateModelOutput(ModelOutput modelOutput) {
        this.modelOutputRepository.updateModelOutput(
                modelOutput.getFromCurr(),
                modelOutput.getToCurr(),
                modelOutput.getMethod(),
                modelOutput.getResiduals(),
                modelOutput.getSigma()
        );
    }
}
