package com.tickstock.stocks.handler;

import android.os.Handler;
import android.os.Message;

import com.tickstock.stocks.activity.GetStockQuote;

public class StockQuoteHandler extends Handler {
	private GetStockQuote getStockQuote;
	private int event;
	
	public StockQuoteHandler(GetStockQuote getStockQuote, int event) {
		super();
		this.getStockQuote = getStockQuote;
		this.event = event;
	}
     

	@Override
	public void handleMessage(Message msg) {
		getStockQuote.updateStock(msg.obj, event);
	}
}
