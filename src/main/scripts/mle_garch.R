garch_loglik<-function(para,x,mu){

  omega0=para[1]
  alpha=para[2]
  beta=para[3]

  loglik=0
  h=var(x)

  vol=c()
  for (i in 2:length(x)){
    h=omega0+alpha*(x[i-1]-mu)^2+beta*h
    loglik=loglik+dnorm(x[i],mu,sqrt(h),log=TRUE)
  }
  return(-loglik)
}

igarch_loglik<-function(para,x,mu){
  omega0=para[1]
  alpha=para[2]

  loglik=0
  h=var(x)

  vol=c()
  for (i in 2:length(x)){
    h=omega0+alpha*(x[i-1]-mu)^2+(1 - alpha)*h
    loglik=loglik+dnorm(x[i],mu,sqrt(h),log=TRUE)
  }
  return(-loglik)
}

para=c(0.2,0.8,0.2)

#mlef<-optim(para, garch_loglik, gr = NULL,method = c("Nelder-Mead"),hessian=FALSE,diff(ec$rate),0)