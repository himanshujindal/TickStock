package com.tickstock.stocks.model;

import java.util.Date;

public class StockQuote {
	
	private String Symbol;
	private String CompanyName;
	
	private Date updatedTime;
	
	private double lastTradedPrice;
	private double change_abs;
	private long volume;
	private String market_cap;
	private double eps;
	private double yr_target;
	private double peRatio;
	private double eps_est_cur;
	private double eps_est_nxt;
	private double pegRatio;
	private String ebitda;
	private double sentiment_good;
	private double volatility;
	
	public void setsentiment_good (double temp) {
    	this.sentiment_good=temp;
    }
    public double getsentiment_good () {
    	return sentiment_good;
    }
	
    public void setvolatility (double temp) {
    	this.volatility=temp;
    }
    public double getvolatility () {
    	return volatility;
    }
    
	public void setchange_abs (double change) {
    	this.change_abs=change;
    }
    public double getchange_abs () {
    	return change_abs;
    }
    
    public void setyr_target (double temp) {
    	this.yr_target=temp;
    }
    public double getyr_target () {
    	return yr_target;
    }
    
    public void setmarket_cap (String strArray) {
    	this.market_cap=strArray;
    }
    public String getmarket_cap () {
    	return market_cap;
    }
    
    public void seteps (double temp) {
    	this.eps=temp;
    }
    public double geteps () {
    	return eps;
    }
    
    public void setPEGRatio(double pegRatio) {
    	this.pegRatio = pegRatio;
    }
	
	public double geteps_est_cur() {
		return eps_est_cur;
	}
	public void seteps_est_cur(double temp) {
		this.eps_est_cur = temp;
	}
	
	public double geteps_est_nxt() {
		return eps_est_nxt;
	}
	public void seteps_est_nxt(double temp) {
		this.eps_est_nxt = temp;
	}
	
	public double getpegRatiot() {
		return pegRatio;
	}
	public void setpegRatio(double temp) {
		this.pegRatio = temp;
	}
	
	public String getebitdat() {
		return ebitda;
	}
	public void setebitda(String strArray) {
		this.ebitda = strArray;
	}
	
	public double getpeRatio() {
		return peRatio;
	}
	public void setpeRatio(double priceToEarningRatio) {
		this.peRatio = priceToEarningRatio;
	}
	
	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	
	public void setLastTradedPrice(double strArray) {
		this.lastTradedPrice = strArray;
	}
	
	
	public void setVolume(long volume) {
		this.volume = volume;
	}
	
	public Date getUpdatedTime() {
		return updatedTime;
	}
	
	
	public double getLastTradedPrice() {
		return lastTradedPrice;
	}
	
	public long getVolume() {
		return volume;
	}
	
	public void setSymbol(String Symbol) {
		// TODO Auto-generated method stub
		this.Symbol = Symbol;		
	}

	public void setCompanyName(String CompanyName) {
		// TODO Auto-generated method stub
		this.CompanyName = CompanyName;
	}
	
	public String getSymbol() {
		// TODO Auto-generated method stub
		return Symbol;		
	}

	public String getCompanyName() {
		return CompanyName;
	}
}
