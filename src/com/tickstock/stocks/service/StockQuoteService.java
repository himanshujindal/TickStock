package com.tickstock.stocks.service;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.tickstock.stocks.accessor.InvalidBackendResponse;
import com.tickstock.stocks.accessor.StockQuoteAccessor;
import com.tickstock.stocks.accessor.webservicex.StockQuoteAccessorImpl;
import com.tickstock.stocks.model.StockQuote;

public class StockQuoteService implements Runnable {
	private String[] symbols;
	private Handler handler;
	private StockQuoteAccessor stockQuoteAccessor;
	
	public StockQuoteService(String[] symbols, int event, Handler handler){
		this.symbols = symbols;
		this.handler = handler;
		stockQuoteAccessor = new StockQuoteAccessorImpl();
	}

	public void run() {
		Looper.prepare();
        Message message = Message.obtain();
		try {
				StockQuote[] stockQuotes = stockQuoteAccessor.getStockQuote(symbols);
				message.obj = stockQuotes;
			
		} catch (InvalidBackendResponse e) {
			message.obj = e;
		}
		this.handler.sendMessage(message);
		Looper.loop();
	}
}
