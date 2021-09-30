garch_loglik<-function(para,x,mu){
  # Parameters
  omega0=para[1]
  alpha=para[2]
  beta=para[3]
  # Volatility and loglik initialisation
  loglik=0
  h=var(x)
  # Start of the loop
  vol=c()
  for (i in 2:length(x)){
    h=omega0+alpha*(x[i-1]-mu)^2+beta*h
    loglik=loglik+dnorm(x[i],mu,sqrt(h),log=TRUE)
  }
  #print(para)
  return(-loglik)
}

para=c(0.2,0.8,0.2)

#mlef<-optim(para, garch_loglik, gr = NULL,method = c("Nelder-Mead"),hessian=FALSE,diff(ec$rate),0)