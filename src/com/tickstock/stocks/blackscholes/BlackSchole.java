package com.tickstock.stocks.blackscholes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import java.io.IOException;
 
// The Black and Scholes (1973) Stock option formula

public class BlackSchole {
	
	private static final String TAG = "MyApp";
 
  public double getVol(String sym, double s) throws IOException {	  	
	  	  //S = Stock Price
	  	  //X = Strike Price
	  	  //T = Years to Maturity
	  	  //r = Risk Free Rate
	  	  //v = Volatility    
	        //General variables
	  	  
	        double t = 1;
	        double r = .08;
	        double v  = .30;
	        Document doc = Jsoup.connect("http://finance.yahoo.com/q/op?s="+sym).timeout(1000000000).get();
	        Element td = doc.select("td.yfnc_h").get(1);
	        String s1 = td.select("a").text();
	        String s2 = s1.substring(sym.length()+9, s1.length()-3);
	        double strike_price = Double.parseDouble(s2);
	        System.out.println(strike_price);
	        double lastTradedValueForCall = LastTrade(s1,sym);
	        return 100*getVolatility('c',s,strike_price,t,r,v,lastTradedValueForCall);
	    }
	    
	    public static double getVolatility(char cYHOO, double csYHOO, double cxYHOO, double ctYHOO, double crYHOO, double cvYHOO, double lastTradedValueForCall) {
	  	
	  	  double o = new BlackSchole().BlackScholes(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO);
	  	 
	  	  if((o - lastTradedValueForCall) > 7){
	  		  cvYHOO = cvYHOO - .029;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((o - lastTradedValueForCall) > 6){
	  		  cvYHOO = cvYHOO - .027;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((o - lastTradedValueForCall) > 5){
	  		  cvYHOO = cvYHOO - .025;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((o - lastTradedValueForCall) > 4){
	  		  cvYHOO = cvYHOO - .023;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((o - lastTradedValueForCall) > 3){
	  		  cvYHOO = cvYHOO - .021;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((o - lastTradedValueForCall) > 2){
	  		  cvYHOO = cvYHOO - .019;
	  		  if(cvYHOO < .17) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	   
	  	  if((o - lastTradedValueForCall) > 1){
	  		  cvYHOO = cvYHOO - .017;
	  		  if(cvYHOO < .14) {
	  			  return cvYHOO;
	  		  }
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	  if((lastTradedValueForCall - o) > 1){
	  		  cvYHOO = cvYHOO + .015;
	  		  cvYHOO = getVolatility(cYHOO,csYHOO,cxYHOO,ctYHOO,crYHOO,cvYHOO,lastTradedValueForCall);
	  	  }
	  	return cvYHOO;
	   }
	    
	    public static Double LastTrade(String str, String symbol) throws IOException{
	  	 // System.out.println(str);
	  	  String vars = "yfs_l10_" + str.toLowerCase();
	  	 // System.out.println(vars);
	  	  Document doc = Jsoup.connect("http://finance.yahoo.com/q?s="+str).timeout(1000000000).get();
	  	  String lastTrade = null;
	        Elements span_list = doc.select("body span");
	  		for(int j = 0;j < span_list.size();j++) {
	           	  if(span_list.get(j).id().equals(vars)) {
	          		  lastTrade = span_list.get(j).text();
	          		  return Double.parseDouble(lastTrade);
	              } 	
	            }
	  		return Double.parseDouble(lastTrade);
	    }
	    
	  public double BlackScholes(char CallPutFlag, double S, double X, double T, double r, double v)
	  {
	  double d1, d2;
	  d1=(Math.log(S/X)+(r+v*v/2)*T)/(v*Math.sqrt(T));
	  d2=d1-v*Math.sqrt(T);
	  if (CallPutFlag=='c')
	  {
	  return S*CND(d1)-X*Math.exp(-r*T)*CND(d2);
	  }
	  else
	  {
	  return X*Math.exp(-r*T)*CND(-d2)-S*CND(-d1);
	  }
	  }

	  // The cumulative normal distribution function 
	  public double CND(double X)
	  {
	  double L, K, w ;
	  double a1 = 0.31938153, a2 = -0.356563782, a3 = 1.781477937, a4 = -1.821255978, a5 = 1.330274429;

	  L = Math.abs(X);
	  K = 1.0 / (1.0 + 0.2316419 * L);
	  w = 1.0 - 1.0 / Math.sqrt(2.0 * Math.PI) * Math.exp(-L *L / 2) * (a1 * K + a2 * K *K + a3 * Math.pow(K,3) + a4 * Math.pow(K,4) + a5 * Math.pow(K,5));

	  if (X < 0.0) 
	  {
	  w= 1.0 - w;
	  }
	  return w;
	  }
	  }     