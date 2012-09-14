package com.tickstock.stocks.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.tickstock.stocks.accessor.InvalidBackendResponse;
import com.tickstock.stocks.activity.R;
import com.tickstock.stocks.handler.StockQuoteHandler;
import com.tickstock.stocks.model.StockQuote;
import com.tickstock.stocks.service.StockQuoteService;

public class GetStockQuote extends Activity implements OnClickListener, OnKeyListener, OnCheckedChangeListener{
    private  final int ADD_STOCK_TO_PORTFOLIO_EVENT = 1;  //static removed from these three lines
    private  final int GET_QUOTE_EVENT = 2;
    private static final DateFormat dateFormat =new SimpleDateFormat("h:mmaa 'on' EEE, d MMM yyyy");
	private double total_volatility = 0;
    private double net_volatility = 0;
    private final static String TAG = "MyApp";
    
	private TextView lastTradedValue, changeValue_abs, stockNameValue, stockSymbolValue, 
	volumeValue, marketCapValue, epsValue, targetPriceValue, peRatioValue, epsest_curValue, epsest_nxtValue, 
	screenRefreshTimeValue, pegRatioValue, ebitdaValue, volatilityValue, outlookValue, netVolatility;
    private static List<String> SYMBOLS = new ArrayList<String>();
	private AutoCompleteTextView symbolWidget, myPortfolioWidget;
	private ImageView changeImageView1;
	private Button getQuoteButton;
	private Button addSymbolButton;
	private Button refreshPortfolioButton;
	private TabHost tabHost;
	private Button addSymbolErrorCloseButton;
	private Button getQuoteErrorCloseButton;
	
	private RadioGroup chartradio;
	
	private Map<String, Double> volatilityMap;
	private Map<String, TableRow> symbols ;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /* InvalidBackendResponse invalidBackendResponse = new InvalidBackendResponse(-2, "I m here");
        displayErrorMessage(invalidBackendResponse, 3); */
        
        stockSymbolValue = (TextView) findViewById(R.id.stockSymbolValue);
        stockNameValue = (TextView) findViewById(R.id.stockNameValue);
        lastTradedValue = (TextView) findViewById(R.id.lastTradedValue);
        changeImageView1 = (ImageView) findViewById(R.id.changeImage1);
        changeValue_abs = (TextView) findViewById(R.id.changeValue_abs);
        volumeValue = (TextView) findViewById(R.id.volumeValue);
        marketCapValue = (TextView) findViewById(R.id.marketCapValue);
        epsValue = (TextView) findViewById(R.id.epsValue);
        epsest_curValue = (TextView) findViewById(R.id.eps_currValue);
        epsest_nxtValue = (TextView) findViewById(R.id.eps_nxtValue);
        targetPriceValue = (TextView) findViewById(R.id.targetPriceValue);
        peRatioValue = (TextView) findViewById(R.id.peRatioValue);
        pegRatioValue = (TextView) findViewById(R.id.pegRatioValue);
        ebitdaValue = (TextView) findViewById(R.id.ebitdaValue);
        volatilityValue = (TextView) findViewById(R.id.volatilityValue);
        outlookValue = (TextView) findViewById(R.id.marketSentiValue);
        
        netVolatility = (TextView) findViewById(R.id.netvolatility);
        
        screenRefreshTimeValue = (TextView) findViewById(R.id.screenRefreshTimeValue);
        
        getQuoteButton = (Button)findViewById(R.id.SubmitSymbols);
        getQuoteButton.setOnClickListener(this);
        
        addSymbolButton = (Button) findViewById(R.id.myPortfolioSubmitSymbol);
        addSymbolButton.setOnClickListener(this);
        
        refreshPortfolioButton = (Button)findViewById(R.id.myPortfolioRefreshPortfolio);
        refreshPortfolioButton.setOnClickListener(this);
        
        myPortfolioWidget = (AutoCompleteTextView) findViewById(R.id.myPortfolioSymbol);
        myPortfolioWidget.setOnKeyListener(this);
        
        symbolWidget = (AutoCompleteTextView) findViewById(R.id.symbol);
        symbolWidget.setOnKeyListener(this);
        
        tabHost = (TabHost)findViewById(R.id.tabhost); 
          
        tabHost.setup(); 
        TabHost.TabSpec fav=tabHost.newTabSpec("favQuoteTab");  
		fav.setContent(R.id.rlFavQuote);  
		fav.setIndicator("My Portfolio", getResources().getDrawable(R.drawable.portfolio));  
        tabHost.addTab(fav);  
        TabHost.TabSpec spec=tabHost.newTabSpec("getQuoteTab");  
        spec.setContent(R.id.rlGetQuote);  
        spec.setIndicator("Get Quote", getResources().getDrawable(R.drawable.quote));  
        tabHost.addTab(spec);  
        tabHost.setCurrentTab(0); 
        
        addSymbolErrorCloseButton = (Button) findViewById(R.id.addSymbolErrorClose);
        addSymbolErrorCloseButton.setOnClickListener(this);
        
        getQuoteErrorCloseButton = (Button) findViewById(R.id.getQuoteErrorClose);
        getQuoteErrorCloseButton.setOnClickListener(this);


        chartradio = (RadioGroup) findViewById(R.id.ChartRadio);
        chartradio.setOnCheckedChangeListener(this);
        chartradio.setOnClickListener(this);
        volatilityMap = new HashMap<String, Double>();
        symbols = new HashMap<String, TableRow>();
        SharedPreferences preferences = super.getPreferences(MODE_PRIVATE);
        Log.d(TAG, "Retreiving preferences");
        Map map = preferences.getAll();
        Set<String> set = map.keySet();
        Iterator<String> iterator = set.iterator();
        while(iterator.hasNext()){
        	String symbol = iterator.next();
        	Log.d(TAG, "Symbol present in preference:"+symbol);
        	StockQuote stockQuote = new StockQuote();
        	stockQuote.setSymbol(symbol);
        	symbols.put(symbol, addStockToPortfolioList(stockQuote));
        }
        refreshSymbols();
    }

    /*
	private void myput(Map<String, TableRow> symbols, String symbol, TableRow addStockToPortfolioList) {
		symbols.put(symbol, addStockToPortfolioList);
		Object vol = addStockToPortfolioList.getVirtualChildAt(6).getTag(4);
		Log.d(TAG, "VAlue of vol is "+vol );
		
	}
	*/

	private void refreshSymbols() {
		if(!symbols.isEmpty()){
        	showProcessingImage(R.id.myPortfolioLoadingImageView);
        	getQuotes((String[])symbols.keySet().toArray(new String[0]), ADD_STOCK_TO_PORTFOLIO_EVENT);
        }
	}

	public void onClick(View view) {
		Log.d(TAG, "Got clicked");
		String symbol = null;
		int event = 0;
		if(view == getQuoteButton){
			symbol = getSearchedKeyWord(R.id.symbol);
			if(symbol == null || symbol.trim().length()==0){
				return;
			}
			cleanupPreviousGetQuoteResults();
			showProcessingImage(R.id.loadingImageView);
			
			event = GET_QUOTE_EVENT;
		}
		else if(view == addSymbolButton){
			Log.d(TAG,"button=addsymbolbutton");
			symbol = getSearchedKeyWord(R.id.myPortfolioSymbol);
			if(symbol == null || symbol.trim().length()==0){
				return;
			}
			if(symbols.containsKey(symbol)){
				InvalidBackendResponse invalidBackendResponse = new InvalidBackendResponse(-2, "Duplicate Entry attempted");
				updateStock(invalidBackendResponse, ADD_STOCK_TO_PORTFOLIO_EVENT);
				return;
			}
			Log.d(TAG,"Creating new StockQuote and new table with symbol:"+symbol);
			StockQuote stockQuote = new StockQuote();
			stockQuote.setSymbol(symbol);
			symbols.put(symbol, addStockToPortfolioList(stockQuote ));
			showProcessingImage(R.id.myPortfolioLoadingImageView);
			event = ADD_STOCK_TO_PORTFOLIO_EVENT;

		}
		else if(view == refreshPortfolioButton){
			refreshSymbols();
			return;
		}
		else if(view == addSymbolErrorCloseButton){
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlAddSymbolError);
			relativeLayout.setVisibility(View.GONE);
			return;
		}
		else if(view == getQuoteErrorCloseButton){
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlgetQuoteError);
			relativeLayout.setVisibility(View.GONE);
			return;
		}
		else if(view == chartradio)
		{
			int checkedRadioButton = chartradio.getCheckedRadioButtonId();
	        
	        int radioButtonSelected = 0;
	         
	        switch (checkedRadioButton) {
	          case R.id.WeeklyRadio : radioButtonSelected = 2;
	                           	              break;
	          case R.id.YearlyRadio : radioButtonSelected = 3;
	          							break;
	          
	          case R.id.DailyRadio  : radioButtonSelected = 1;
	        		                      break;
	        }
	        
	        symbol = getSearchedKeyWord(R.id.symbol);
			if(symbol == null || symbol.trim().length()==0){
				return;
			}
	        String imgUrl = "";
	        if(radioButtonSelected==2)
	        {
	        	
	        	imgUrl = "http://ichart.yahoo.com/v?s="+symbol;
	        	Drawable image = getDrawableImage(imgUrl);
	    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
	    		imgView.setImageDrawable(image);
	        }
	        else if(radioButtonSelected==3)
	        {
	        	imgUrl = "http://ichart.finance.yahoo.com/c/bb/m/"+symbol;
	        	Drawable image = getDrawableImage(imgUrl);
	    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
	    		imgView.setImageDrawable(image);
	        }
	        else if(radioButtonSelected==1)
	        {
	        	imgUrl = "http://ichart.yahoo.com/t?s="+symbol;
	        	Drawable image = getDrawableImage(imgUrl);
	    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
	    		imgView.setImageDrawable(image);
	        }
	        return;
		}

		getQuotes(new String[]{symbol}, event);
	}
	
	public GetStockQuote() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
	 */
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		 
		int checkedRadioButton = chartradio.getCheckedRadioButtonId();
        
        int radioButtonSelected = 0;
         
        switch (checkedRadioButton) {
          case R.id.WeeklyRadio : radioButtonSelected = 2;
                           	              break;
          case R.id.YearlyRadio : radioButtonSelected = 3;
          							break;
          
          case R.id.DailyRadio  : radioButtonSelected = 1;
        		                      break;
        }
        
        String symbol = getSearchedKeyWord(R.id.symbol);
		if(symbol == null || symbol.trim().length()==0){
			return;
		}
        String imgUrl = "";
        if(radioButtonSelected==2)
        {
        	
        	imgUrl = "http://ichart.yahoo.com/v?s="+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }
        else if(radioButtonSelected==3)
        {
        	imgUrl = "http://ichart.finance.yahoo.com/c/bb/m/"+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }
        else if(radioButtonSelected==1)
        {
        	imgUrl = "http://ichart.yahoo.com/t?s="+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }

	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "OnStop Called");
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.clear();
		Iterator<String> symbolSetIterator = symbols.keySet().iterator();
		while(symbolSetIterator.hasNext()){
			editor.putString(symbolSetIterator.next(), null);
			Log.d(TAG, "Storing the preference for symbol:"+symbolSetIterator.toString());
		}
		editor.commit();
	}

	private void getQuotes(String[] symbols, int event) {
		Log.d(TAG, "getQuotes Called with :"+symbols.toString());
		StockQuoteHandler handler = new StockQuoteHandler(this, event);
		StockQuoteService stockQuoteService = new StockQuoteService(symbols, event, handler);
		
		Thread thread = new Thread(stockQuoteService);
		thread.start();
	}

	private String getSearchedKeyWord(int editTextId) {
		String symbol;
		EditText symbolWidget = (EditText) findViewById(editTextId);
		symbol = symbolWidget.getText().toString();
		return symbol;
	}

	private void cleanupPreviousGetQuoteResults() {
		stockSymbolValue.setText("");
		stockNameValue.setText("");
		lastTradedValue.setText("");
		changeValue_abs.setText("");
        volumeValue.setText("");
        marketCapValue.setText("");
        epsValue.setText("");
        epsest_curValue.setText("");
        epsest_nxtValue.setText("");
        targetPriceValue.setText("");
        peRatioValue.setText("");
        pegRatioValue.setText("");
        ebitdaValue.setText("");
        volatilityValue.setText(""); 
        outlookValue.setText("");
        
	}

	private void showProcessingImage(int imageViewId) {
		ImageView imageView = (ImageView) findViewById(imageViewId);
		imageView.setImageResource(R.drawable.loading);
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360, 13, 13);
		rotateAnimation.setDuration(1000);
		rotateAnimation.setRepeatCount(Animation.INFINITE);
		imageView.startAnimation(rotateAnimation);
	}
	
	public void updateStock(Object object, int event){
		if(object.getClass().isArray()){
			StockQuote[] stockQuotes = (StockQuote[])object;
			Log.d(TAG, "updateStock called with event:"+event);
			if(event == ADD_STOCK_TO_PORTFOLIO_EVENT){
				for(int i=0;i<stockQuotes.length;i++){
					Log.d(TAG, "updateStockInformationInPortfolio for symbol:"+stockQuotes[i].getSymbol());
					updateStockInformationInPortfolio(stockQuotes[i], symbols.get(stockQuotes[i].getSymbol()));
				}
				stopAnimation(R.id.myPortfolioLoadingImageView);
	        	screenRefreshTimeValue.setText("Last updated at: "+dateFormat.format(new Date()));
			}else if(event == GET_QUOTE_EVENT){
				for(int i=0;i<stockQuotes.length;i++){
					displayStockInfo(stockQuotes[i]);
				}
				stopAnimation(R.id.loadingImageView);
			}else{
				InvalidBackendResponse backendResponse = new InvalidBackendResponse(-2, "Program Error:Invalid Event Code returned");
				displayErrorMessage(backendResponse, 3);
				return;
			}
			
			for(int i=0;i<stockQuotes.length;i++){
				addToDropDownList(stockQuotes[i].getSymbol());
			}
			
		}
		else{
			InvalidBackendResponse backendResponse = (InvalidBackendResponse)object;
			displayErrorMessage(backendResponse, event);
			if(event == ADD_STOCK_TO_PORTFOLIO_EVENT){
				stopAnimation(R.id.myPortfolioLoadingImageView);
			}else if(event == GET_QUOTE_EVENT){
				stopAnimation(R.id.loadingImageView);
			}
		}
	}

	private void stopAnimation(int imageViewId) {
		ImageView imageView = (ImageView) findViewById(imageViewId);
		imageView.clearAnimation();
		imageView.setImageResource(0);
	}

	private TableRow addStockToPortfolioList(StockQuote stockQuote) {
		Log.d(TAG, "Adding stock to portfolio list"+stockQuote.getSymbol());
		final TableLayout tableLayout = (TableLayout) findViewById(R.id.myPortfolioTable);
		final TableRow tableRow = new TableRow(this);

		Log.d(TAG, "Adding symbol text");
		final TextView myPortfolioSymbolLabel = new TextView(this);
		myPortfolioSymbolLabel.setId(1);
		myPortfolioSymbolLabel.setPadding(2, 5, 2, 5);
		final String symbol = stockQuote.getSymbol();
		myPortfolioSymbolLabel.setText(symbol, TextView.BufferType.SPANNABLE);
		Spannable str = (Spannable) myPortfolioSymbolLabel.getText();
		str.setSpan(new UnderlineSpan(), 0, symbol.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		myPortfolioSymbolLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
            	tabHost.setCurrentTab(1);
            	symbolWidget.setText(myPortfolioSymbolLabel.getText());
                getQuoteButton.performClick();
            }
        });
		tableRow.addView(myPortfolioSymbolLabel);
		
		Log.d(TAG, "Adding Price text");
		TextView myPortfolioSymbolValue = new TextView(this);
		myPortfolioSymbolValue.setId(2);
		myPortfolioSymbolValue.setPadding(2, 5, 2, 5);
		myPortfolioSymbolValue.setTextColor(Color.YELLOW);
		//myPortfolioSymbolValue.setText(Double.toString(stockQuote.getLastTradedPrice()));
		tableRow.addView(myPortfolioSymbolValue);
		
		Log.d(TAG, "Adding Image Field");
		ImageView myPortfolioSymbolChangeImage = new ImageView(this);
		myPortfolioSymbolChangeImage.setId(3);
		myPortfolioSymbolChangeImage.setPadding(2, 8, 0, 2);
		tableRow.addView(myPortfolioSymbolChangeImage);
		
		TextView myPortfolioSymbolChangeValue = new TextView(this);
		myPortfolioSymbolChangeValue.setId(4);
		myPortfolioSymbolChangeValue.setPadding(0, 5, 2, 5);
		tableRow.addView(myPortfolioSymbolChangeValue);
		
		Log.d(TAG, "Adding Outlook Field");
		TextView myPortfolioOutlookValue = new TextView(this);
		myPortfolioOutlookValue.setId(5);
		myPortfolioOutlookValue.setPadding(2, 5, 2, 5);
		//myPortfolioEPSestValue.setText(Double.toString(stockQuote.geteps_est_cur()));
		tableRow.addView(myPortfolioOutlookValue);
		
		Log.d(TAG, "Adding Volatility Field");
		TextView myPortfolioVolatilityValue = new TextView(this);
		myPortfolioVolatilityValue.setId(6);
		myPortfolioVolatilityValue.setPadding(2, 5, 2, 5);
		//myPortfolioEPSestValue.setText(Double.toString(stockQuote.geteps_est_cur()));
		tableRow.addView(myPortfolioVolatilityValue);
		
		Log.d(TAG, "Adding Remove View Symbol");
		ImageView removeSymbolView = new ImageView(this);
		removeSymbolView.setPadding(2, 5, 2, 5);
		removeSymbolView.setImageResource(R.drawable.close_button);
		removeSymbolView.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	total_volatility-=volatilityMap.get(symbol);
	            	volatilityMap.remove(symbol);
	            	symbols.remove(symbol);
	            	net_volatility = total_volatility/symbols.size();
	        		netVolatility.setText("Implied volatilty as per BlackScholes Model is:"+Double.toString(net_volatility).substring(0, 4)+"%");
	                tableRow.setVisibility(View.GONE);
	                tableLayout.removeView(tableRow);
	                assignRowColor(tableLayout);
	            }           
	        });
		tableRow.addView(removeSymbolView);
		tableLayout.addView(tableRow);
        assignRowColor(tableLayout);		
		
        Log.d(TAG, "Created new table "+tableRow);
		myPortfolioWidget.setText("");
		return tableRow;
		
	}
	
	private void updateStockInformationInPortfolio(StockQuote stockQuote, TableRow tableRow){
		if(tableRow==null){
			Log.d(TAG, "table row is null for stockquote:"+ stockQuote.getSymbol());
			return;
		}
		Log.d(TAG, "updateStockInformationInPortfolio called with:"+stockQuote.getSymbol());
		Log.d(TAG, "TableRow:"+tableRow.getId());
		Log.d(TAG, "Stock Info: Value, Change, PER, Eps: "+ Double.toString(stockQuote.getLastTradedPrice())+", "+Double.toString(stockQuote.getchange_abs())+", "+Double.toString(stockQuote.getpeRatio())+", "+ Double.toString(stockQuote.geteps_est_cur()));
		TextView myPortfolioSymbolValue = (TextView) tableRow.findViewById(2);
		TextView myPortfolioSymbolChangeValue = (TextView)tableRow.findViewById(4);
		ImageView myPortfolioSymbolChangeImage = (ImageView) tableRow.findViewById(3);
		/*
		TextView myPortfolioPERatioValue = (TextView) tableRow.findViewById(5);
		TextView myPortfolioEpsValue = (TextView) tableRow.findViewById(6);
		*/
		TextView myPortfolioMarketOutlook = (TextView) tableRow.findViewById(5);
		myPortfolioSymbolValue.setText(stockQuote.getLastTradedPrice()+"");
		setChangeInformation(stockQuote, myPortfolioSymbolChangeValue, myPortfolioSymbolChangeImage);
		if(stockQuote.getsentiment_good()<30)
		{
			myPortfolioMarketOutlook.setTextColor(Color.RED);
		}
		else if(stockQuote.getsentiment_good()<70)
		{
			myPortfolioMarketOutlook.setTextColor(Color.YELLOW);
		}
		else
		{
			myPortfolioMarketOutlook.setTextColor(Color.GREEN);
		}
		
		myPortfolioMarketOutlook.setText(Double.toString(stockQuote.getsentiment_good())+" %");
		
		TextView myPortfolioVolatility = (TextView) tableRow.findViewById(6);
		myPortfolioVolatility.setText(Double.toString(stockQuote.getvolatility())+" %");
		
		volatilityMap.put(stockQuote.getSymbol(), stockQuote.getvolatility());
		total_volatility = total_volatility+stockQuote.getvolatility();
		net_volatility = total_volatility/symbols.size();
		netVolatility.setText("Implied volatilty as per BlackScholes Model is:"+Double.toString(net_volatility).substring(0, 4)+"%");
		netVolatility.setTextColor(Color.WHITE);
		if(stockQuote.getvolatility()<30)
		{
			myPortfolioVolatility.setTextColor(Color.GREEN);
		}
		else if(stockQuote.getsentiment_good()<70)
		{
			myPortfolioVolatility.setTextColor(Color.YELLOW);
		}
		else
		{
			myPortfolioVolatility.setTextColor(Color.RED);
		}
	}

	private void addToDropDownList(String symbol) {
		if(!SYMBOLS.contains(symbol)){
			SYMBOLS.add(symbol);
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_dropdown_item_1line, SYMBOLS);
		    symbolWidget.setAdapter(adapter);
		    
		}
	}
	
	public void displayErrorMessage(InvalidBackendResponse backendResponse, int event) {
		if(event == ADD_STOCK_TO_PORTFOLIO_EVENT){
			TextView errorTextView = (TextView) findViewById(R.id.addSymbolErrorText);
			errorTextView.setText(backendResponse.toString());
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlAddSymbolError);
			relativeLayout.setVisibility(View.VISIBLE);
		}else{
			TextView errorTextView = (TextView) findViewById(R.id.getQuoteErrorText);
			errorTextView.setText(backendResponse.toString());
			RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rlgetQuoteError);
			relativeLayout.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void displayStockInfo(StockQuote stockQuote){
        
        try{
        	String updatedOn;
        	updatedOn = dateFormat.format(stockQuote.getUpdatedTime());
        }
        catch(Throwable e){
        	System.out.println("Exception setting updatedOn:"+e);
        }
		
		stockSymbolValue.setText(stockQuote.getSymbol());
		stockNameValue.setText(stockQuote.getCompanyName());
		lastTradedValue.setText(String.valueOf(stockQuote.getLastTradedPrice()));
		setChangeInformation(stockQuote, changeValue_abs, changeImageView1);
        volumeValue.setText(stockQuote.getVolume()+"");
        marketCapValue.setText(stockQuote.getmarket_cap()+"");
        epsValue.setText(stockQuote.geteps()+"");
        epsest_curValue.setText(stockQuote.geteps_est_cur()+"");
        epsest_nxtValue.setText(stockQuote.geteps_est_nxt()+"");
        targetPriceValue.setText(stockQuote.getyr_target()+"");
        peRatioValue.setText(stockQuote.getpeRatio()+"");
        pegRatioValue.setText(stockQuote.getpegRatiot()+"");
        ebitdaValue.setText(stockQuote.getebitdat()+"");
        volatilityValue.setText(Double.toString(stockQuote.getvolatility())+" %");
        outlookValue.setText(Double.toString(stockQuote.getsentiment_good())+" %");
        
        int checkedRadioButton = chartradio.getCheckedRadioButtonId();
        
        int radioButtonSelected = 0;
         
        switch (checkedRadioButton) {
          case R.id.WeeklyRadio : radioButtonSelected = 2;
                           	              break;
          case R.id.YearlyRadio : radioButtonSelected = 3;
          
          case R.id.DailyRadio  : radioButtonSelected = 1;
        		                      break;
        }
        
        String symbol = getSearchedKeyWord(R.id.symbol);
		if(symbol == null || symbol.trim().length()==0){
			return;
		}
        String imgUrl = "";
        if(radioButtonSelected==2)
        {
        	
        	imgUrl = "http://ichart.yahoo.com/v?s="+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }
        else if(radioButtonSelected==3)
        {
        	imgUrl = "http://ichart.finance.yahoo.com/c/bb/m/"+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }
        else if(radioButtonSelected==1)
        {
        	imgUrl = "http://ichart.yahoo.com/t?s="+symbol;
        	Drawable image = getDrawableImage(imgUrl);
    		ImageView imgView = (ImageView)findViewById(R.id.chartImageView);
    		imgView.setImageDrawable(image);
        }
	}

	private void setChangeInformation(StockQuote stockQuote, TextView textView, ImageView imageView) {
		String change = String.valueOf(Math.abs(stockQuote.getchange_abs()));
		textView.setText("    "+change);
        
		if(stockQuote.getchange_abs()>0){
			textView.setTextColor(Color.GREEN);
			imageView.setImageResource(R.drawable.green);
		}else if(stockQuote.getchange_abs()<0){
			textView.setTextColor(Color.RED);
			imageView.setImageResource(R.drawable.red);
		}else{
			textView.setTextColor(Color.WHITE);
		}
	}

	public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
		if(keyCode==KeyEvent.KEYCODE_ENTER){
			if(arg0 == symbolWidget){
				return getQuoteButton.performClick();
			}
			else if(arg0 == myPortfolioWidget){
				return addSymbolButton.performClick();
			} 
			else{
				throw new RuntimeException("Unknown Key Click event");
			}
		}
		else{
			return false;
		}
	}

	private void assignRowColor(final TableLayout tableLayout) {
		int childCount = tableLayout.getChildCount();
		for(int i=1;i<childCount;i++){
			TableRow iRow = (TableRow) tableLayout.getChildAt(i);
			if(i%2==0){
				iRow.setBackgroundColor(Color.parseColor("#333333"));
			}
			else{
				iRow.setBackgroundColor(Color.parseColor("#222222"));
			}
		}
	}
	
	private Drawable getDrawableImage(String url) {
		try {
			InputStream is = (InputStream) this.fetch(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object fetch(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	

}