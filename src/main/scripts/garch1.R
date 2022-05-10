library(readxl)
library(rugarch)
library(tseries)
library(FinTS)

data = read_excel("eurczk.xlsx")
str(data)

data_train = data[1:(3074-258),]
data_test = data[(3074-258+1):3074, ]

Box.test(data_train$rate, type="Ljung-Box")

calculateArmaGarch <- function(data_train, data_test, arima, arma, garch) {
  s_data_train = diff(data_train$rate)
  s_data_test = diff(data_test$rate)
  arima_model = arima(s_data_train, order=arima)

  #testovanie stacionarity
  kpss.test(s_data_train)

  #vykreslenie rezidui
  #plot(arima_model$residuals, type="l")

  #testovanie heteroskedasticity v reziduach
  ArchTest(arima_model$residuals,lag=1)

  egarchrates.spec = ugarchspec(variance.model=list(model=garch,garchOrder=c(1,1)),
                                mean.model=list(armaOrder=arma),distribution.model = "std")
  fit = ugarchfit(data=c(s_data_train, s_data_test), spec=egarchrates.spec, out.sample=length(s_data_test), solver='hybrid')
  print(fit)

  modelfor = ugarchforecast(fit, data = NULL, n.ahead=1, n.roll = length(s_data_test), out.sample=length(s_data_test))
  print(modelfor)

  plot(arima_model$residuals,type="l", col="#43aed9", ylab="volatility", xlab="days", main="apARCH(1,1) model")
  legend("topright",
         legend = c("Conditional SD", "ARMA residuals"),col=c("red","#43aed9"),lty=1, cex=1)
  lines(drop(coredata(sigma(fit))),type="l",col="red")
  lines(drop(coredata(-sigma(fit))),type="l",col="red")
}

calculateArmaGarch(data_train, data_test, c(2,0,2), c(2,2), "apARCH")