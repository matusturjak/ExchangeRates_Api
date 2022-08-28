# ExchangeRates_Api
Web application, the main functionality of which is the creation of predictions of hundreds of currency rates using autoregressive models.

Web application consist of two parts
- User interface
- Rest API

In both cases, the user has the opportunity to obtain information on exchange rates for the following currencies: EUR, CAD, CZK, HUF, AUD, HRK, CHF, PLN, BGN, TRY, USD, GBP, SEK, NOK. The user has the possibility to obtain historical data of exchange rates from 1.1. 2010 to the present.

Current exchange rate values are downloaded from the European Central Bank every working day at 23:00.

Current currency rate predictions are calculated in the application every day at 23:10. Since there are several hundred exchange rates in the application, the entire calculation takes approximately 30 minutes.

## User interface
User interface consists of 5 windows that provide the user with multiple functionalities.

### Predictions page
The Predictions page is the most important window of the entire application. In it, the user has the opportunity to obtain predictions of the selected exchange rate for one, three and five days ahead. This page also offers a graphic display of the estimated values of the exchange rate development. For the one-day prediction, which is calculated using the ARIMA model, there is also a graph showing the estimated volatility values of the residuals of the ARIMA model and the prediction of the volatility of the residuals one time period ahead. The user has the option to decide whether he wants to display a graph of the estimated volatility values of the residuals calculated using the GARCH or IGARCH model.

<img width="793" alt="prediction1" src="https://user-images.githubusercontent.com/69460412/187068199-35eda6f7-3c29-4a43-9434-143e71392f4f.png">
<img width="792" alt="prediction2" src="https://user-images.githubusercontent.com/69460412/187068200-397f97d6-7016-4bc8-b8ae-32aea53bf768.png">

### Actual Rates page
A page in which the current values of the registered exchange rates are displayed. All data are displayed in a transparent table where, in addition to the current values, there are also the values of the decrease/increase of a specific exchange rate.

<img width="793" alt="actual" src="https://user-images.githubusercontent.com/69460412/187068250-e7977afe-0e12-47aa-a94d-3a884b9279f6.png">

### Historical Rates page
A page displaying the historical values of the selected exchange rate for the specified period of time.

<img width="792" alt="history" src="https://user-images.githubusercontent.com/69460412/187068287-ecaa56a0-068a-4bc1-9459-107cac5d41bd.png">

### Rest API page
Page serves as quick documentation for the created Rest API. It shows examples of HTTP calls with their return values in JSON format.

<img width="378" alt="restapi" src="https://user-images.githubusercontent.com/69460412/187068399-a2418570-fff1-4401-bfa0-3d6a9589cfde.png">

## Rest API
Web application provides a total of 8 HTTP calls for the user, which in response to the request will send the user the required data in JSON format. The application provides the following HTTP calls.
- GET /prediction?from=EUR&to=CZK&ahead=1
- GET /latest/EUR 
- GET /latest?from=EUR&to=CZK 
- GET /latest/conversion?from=EUR&to=CZK&amount=15
- GET /rates/2019-11-27/2019-11-29?from=EUR&to=CZK
- GET /rates/latest?from=EUR&to=CZK&count=5
- GET /rates/all?from=EUR&to=CZK 
- GET /rates/2022-03-09?from=EUR&to=CZK

The mentioned HTTP calls with the currencies EUR and CZK served as an example. For each introduced HTTP call, the user has the opportunity to choose any currency that the application provides.

