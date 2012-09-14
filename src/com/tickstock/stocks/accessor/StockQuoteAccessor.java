package com.tickstock.stocks.accessor;

import com.tickstock.stocks.model.StockQuote;

public interface StockQuoteAccessor {
	public StockQuote[] getStockQuote(String[] symbols) throws InvalidBackendResponse;
}
