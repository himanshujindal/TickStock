package com.tickstock.stocks.accessor.webservicex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.xml.sax.SAXException;

import android.util.Log;

import com.tickstock.stocks.accessor.InvalidBackendResponse;
import com.tickstock.stocks.accessor.StockQuoteAccessor;
import com.tickstock.stocks.model.StockQuote;
import com.tickstock.stocks.newsparser.newsParser;
import com.tickstock.stocks.blackscholes.*;

public class StockQuoteAccessorImpl implements StockQuoteAccessor {
	private static final String TAG = "MyApp";

	public StockQuote[] getStockQuote(String[] symbols) throws InvalidBackendResponse {
		Log.d(TAG, "getWeeklyStockQuote(String[]): START");
		List stockQuotes = new ArrayList();
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 30000);
		for(int i=0;i<symbols.length;i++){
			stockQuotes.add(getStockQuote(symbols[i], httpClient));
		}
		Log.d(TAG, "getWeeklyStockQuote: END");
		return (StockQuote[])stockQuotes.toArray(new StockQuote[0]);
	}
	private StockQuote getStockQuote(String symbol, HttpClient httpClient) throws InvalidBackendResponse {
		try {
			Log.d(TAG, "getWeeklyStockQuote(String[], HttpClient) : START");
			String a = "snl1c6p2vj1et8rr6r7r5j4";
			HttpUriRequest httpUriRequest = new HttpGet("http://finance.yahoo.com/d/quotes.csv?s="+symbol+ "&f="+a);
			Log.d(TAG, "getWeeklyStockQuote(String[], HttpClient) : Calling URL:"+httpUriRequest.toString());
			HttpResponse httpResponse = httpClient.execute(httpUriRequest);
			BufferedReader br = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
			Log.d(TAG,"getWeeklyStockQuote, BR: "+br.toString());
			
			return parsePriceQuoteFromWebServiceResponse(br);
		} catch(InvalidBackendResponse e){
			throw e;
		}catch(Throwable e){
			e.printStackTrace();
			throw new InvalidBackendResponse(-1, e.getMessage());
		}
	}

	private StockQuote parsePriceQuoteFromWebServiceResponse(BufferedReader br) throws ParserConfigurationException, SAXException, IOException, InvalidBackendResponse {
		
		Log.d(TAG,"parseweeklyPriceQuoteFromWebServiceResponse: START");
		Log.d(TAG,"parseweeklyPriceQuoteFromWebServiceResponse"+br.toString());
		StockQuote stockQuote = new StockQuote();
        String strLine;
		String delimit = ",";
		//fileSystem = new POIFSFileSystem (inputStream);
		Log.d(TAG,"parseweeklyPriceQuoteFromWebServiceResponse: Processing Buffered reader");	
		while ((strLine = br.readLine()) != null)   {
				Log.d(TAG, "String Read= "+strLine);
				String [] strArray =strLine.split(delimit);
				stockQuote.setSymbol(parse_string(strArray[0]));
				stockQuote.setCompanyName(parse_string(strArray[1]));
				stockQuote.setLastTradedPrice(Double.parseDouble(strArray[2]));
				stockQuote.setchange_abs(parse_double(strArray[3]));
				stockQuote.setVolume(Long.parseLong(strArray[5]));
				stockQuote.setmarket_cap(parse_string(strArray[6]));
				stockQuote.seteps(parse_double(strArray[7]));
				stockQuote.seteps_est_cur(parse_double(strArray[8]));
				stockQuote.seteps_est_nxt(parse_double(strArray[9]));
				stockQuote.setyr_target(parse_double(strArray[10]));
				stockQuote.setpeRatio(parse_double(strArray[11]));
				stockQuote.setpegRatio(parse_double(strArray[12]));
				stockQuote.setebitda(parse_string(strArray[13]));
				String volatility = Double.toString(new BlackSchole().getVol(stockQuote.getSymbol(), stockQuote.getLastTradedPrice()));
				volatility = volatility.substring(0, 4);
				stockQuote.setvolatility(Double.parseDouble(volatility));
				
				String sentiment = Double.toString(new newsParser().GetGoodSentiment(stockQuote.getSymbol()));
				sentiment = sentiment.substring(0, 4);
				
				
				stockQuote.setsentiment_good(Double.parseDouble(sentiment));
				Log.d(TAG, "Stored Stock Quote");
				Log.d(TAG, "Symbol:"+stockQuote.getSymbol());
				Log.d(TAG, "Name:"+stockQuote.getCompanyName());
				Log.d(TAG, "Last Traded Price:"+Double.toString(stockQuote.getLastTradedPrice()));
				Log.d(TAG, "Change Absolute:"+Double.toString(stockQuote.getchange_abs()));
				Log.d(TAG, "Volume:"+Long.toString(stockQuote.getVolume()));
				Log.d(TAG, "Market Cap:"+stockQuote.getmarket_cap());
				Log.d(TAG, "EPS:"+Double.toString(stockQuote.geteps()));
				Log.d(TAG, "EPS Estimate Current Year:"+Double.toString(stockQuote.geteps_est_cur()));
				Log.d(TAG, "EPS Estimate Next Year:"+Double.toString(stockQuote.geteps_est_nxt()));
				Log.d(TAG, "1 Year Target:"+Double.toString(stockQuote.getyr_target()));
				Log.d(TAG, "PE Ratio:"+Double.toString(stockQuote.getpeRatio()));
				Log.d(TAG, "PEG Ratio:"+Double.toString(stockQuote.getpegRatiot()));
				Log.d(TAG, "EBITDA:"+stockQuote.getebitdat());
				Log.d(TAG, "The good sentiment is :"+ Double.toString(stockQuote.getsentiment_good()));
			}
		Log.d(TAG,"parseweeklyPriceQuoteFromWebServiceResponse: Reader Read");
		Log.d(TAG,"parseweeklyPriceQuoteFromWebServiceResponse: EXIT");
		return stockQuote;
	}
	
	static private double parse_double(String str){
		double ret_d = 0;
		str.trim();
		if(str.contains("N/A"))
		{
			return ret_d;
		}
		if(str.contains("\"")==true)
		{
			str = str.substring(1, str.length()-2);
		}
		ret_d = Double.parseDouble(str);
		return ret_d;
	}
	
	static private String parse_string(String str){
		String ret_string;
		Log.d(TAG, "parse_string called with string:"+str);
		if(str.contains("\""))
		{
			ret_string=str.substring(1, str.length()-1);
		}
		else
			ret_string = str;
		return ret_string;
	}
}
