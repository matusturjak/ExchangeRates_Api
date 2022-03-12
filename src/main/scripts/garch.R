library(readxl)
library(forecast)
library(lmtest)
library(tseries)
library(FinTS)
library(rugarch)
library(pracma)
library(Metrics)
library(MLmetrics)

data = read_excel("C:/Users/Matúš/Documents/prognostika/eurrub.xlsx")
str(data)

data_train = data[1:(3074-258),]
data_test = data[(3074-258+1):3074, ]

arima_model = auto.arima(data_train$rate)

egarchrates.spec = ugarchspec(variance.model=list(model="eGARCH",garchOrder=c(1,1)),
                              mean.model=list(armaOrder=c(4,4)),distribution.model = "norm")
fit = ugarchfit(data=diff(data_train$rate), spec=egarchrates.spec)
fit


plot(arima_model$residuals,type="l", col="#43aed9", ylab="volatility", xlab="days", main="EGARCH(1,1) model")
legend("topright",
       legend = c("Conditional SD", "ARMA residuals"),col=c("red","#43aed9"),lty=1, cex=1)
lines(drop(coredata(sigma(fit))),type="l",col="red")
lines(drop(coredata(-sigma(fit))),type="l",col="red")
lines(drop(coredata(fitted(fit))),type="l")