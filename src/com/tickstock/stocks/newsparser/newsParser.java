

package com.tickstock.stocks.newsparser;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.util.Log;

import java.util.*; 
import java.util.Map.Entry;
import java.lang.String;

public class newsParser {

	private final static String TAG = "MyApp";
	public double GetGoodSentiment(String Symbol) throws IOException {
		Log.d(TAG, "Getting good sentiment for symbol:"+Symbol);
        String news_Content = null;
        
       	Map<String, Double> goodWords= new HashMap<String, Double>();
       	Map<String, Double> badWords = new HashMap<String, Double>();
       	
       	badWords.put("not", .4);
        badWords.put("Worries", .7);
        badWords.put("Low", .4);
        badWords.put("Muted", .8);
        badWords.put("Falters", .6);
        badWords.put("Loss", .6);
        badWords.put("Fumbles", .5);
        badWords.put("Decline", .4);
        badWords.put("Descend", .6);
        badWords.put("Mixed", .5);
        badWords.put("Ailing", .7);
        badWords.put("Declines", .6);
        badWords.put("Pessimistic", .7);
        badWords.put("Degrade", .6);
        badWords.put("Lower", .6);
        badWords.put("Avoid", .8);
        badWords.put("Conservative", .5);
        badWords.put("Downgrades", .8);
        badWords.put("Troubles", .6);
        badWords.put("Modest", .5);
        badWords.put("Flat", .5);
        badWords.put("Bear", .8);
        badWords.put("Bearish", .8);
        badWords.put("Disappointing", .8);
        badWords.put("Negative", .9);
        badWords.put("Loss", .9);
        badWords.put("Down Sharply", .9);
        badWords.put("Crash", .99);
        badWords.put("Not Good", .9);
        
        goodWords.put("Optimum", .7);
        goodWords.put("Gain", .7);
        goodWords.put("Bullish", .8);
        goodWords.put("Bull", .8);
        goodWords.put("Attractive", .7);
        goodWords.put("Favorite", .7);
        goodWords.put("Outperform", .8);
        goodWords.put("Soar", .7);
        goodWords.put("Raises Dividend", .9);
        goodWords.put("Higher", .6);
        goodWords.put("Upgrade", .6);
        goodWords.put("Optimistic", .7);
        goodWords.put("High", .7);
        goodWords.put("Surging", .9);
        goodWords.put("Beats", .9);
        goodWords.put("Boost", .9);
        goodWords.put("Best", 1.0);
        goodWords.put("Mixed", .5);
        goodWords.put("Beyond", .4);
        goodWords.put("Momentum", .4);
        goodWords.put("Raises", .6);
        goodWords.put("Raise", .6);
        goodWords.put("Good", .3);
        goodWords.put("Hike", .4);
        goodWords.put("Prime", .6);
        
        Log.d(TAG, "Created hash map with values for both and bad words");
        Set<Entry<String, Double>> setGood = goodWords.entrySet();
        Set<Entry<String, Double>> setBad = badWords.entrySet();
      
        
        Log.d(TAG, "Connecting to the database of news via Jsoup");
        Document doc = Jsoup.connect("http://www.nasdaq.com/symbol/"+Symbol+"/news-headlines").timeout(1000000000).get();
	    Elements div_list=doc.select("body div");
        Elements span_list = doc.select("body span");
        double goodcount = 0;
        double badcount = 0;
        for(int i = 0;i < div_list.size();i++)
        {
        	if(div_list.get(i).className().equals("headlines") )
        	{ 
        		for(int j = 0;j < span_list.size();j++) {
            	 if(span_list.get(j).className().equals("newstitle")) {
            		news_Content = span_list.get(j).text();
             		//  System.out.println(news_Content);
             		Iterator<Entry<String, Double>> itGood = setGood.iterator();
                    Iterator<Entry<String, Double>> itBad = setBad.iterator();
             		goodcount = goodcount + new newsParser().goodanalysis(news_Content,setGood,itGood);
             		badcount =  badcount + new newsParser().badanalysis(news_Content,setBad,itBad);
             	 } 	
        	   }	 
            }
       }
        Log.d(TAG, "Good Count is:"+goodcount);
        Log.d(TAG, "Bad Count is:"+badcount);
        /*
        Log.d(TAG, "Connecting to the database of news via Jsoup second time");
        doc = Jsoup.connect("http://www.nasdaq.com/symbol/"+Symbol+"/news-headlines?page=2").timeout(1000000000).get();
	    div_list=doc.select("body div");
        span_list = doc.select("body span");
        for(int i = 0;i < div_list.size();i++)
        {
        	if(div_list.get(i).className().equals("headlines") )
        	{ 
        		for(int j = 0;j < span_list.size();j++) {
            	 if(span_list.get(j).className().equals("newstitle")) {
            		news_Content = span_list.get(j).text();
             		//  System.out.println(news_Content);
             		Iterator<Entry<String, Double>> itGood = setGood.iterator();
                    Iterator<Entry<String, Double>> itBad = setBad.iterator();
             		goodcount = goodcount + new newsParser().goodanalysis(news_Content,setGood,itGood);
             		badcount =  badcount + new newsParser().badanalysis(news_Content,setBad,itBad);
             	 } 	
        	   }	 
            }
       }
        Log.d(TAG, "Good Count is:"+goodcount);
        Log.d(TAG, "Bad Count is:"+badcount);
        */
        double G = (goodcount)/(goodcount+badcount) * 100;
		//double B = (badcount)/(goodcount+badcount) * 100;
		//System.out.println("G " + G + "B : " + B );
		Log.d(TAG, "The good sentiment is +"+G);
		return G;
	}    
  
	private double goodanalysis(String news_Content, Set<Entry<String, Double>> setGood, Iterator<Entry<String, Double>> itGood) {
		// TODO Auto-generated method stub
		//Log.d(TAG,"Calculating good sentiment for string:"+news_Content);
		
		double good = 0;
		//System.out.println(news_Content);
		while (itGood.hasNext()) {
	       	 Entry<String, Double> mEntry = itGood.next();
	       	// System.out.println(mEntry.getKey() + " : " + mEntry.getValue());
	       	 String searchFor = mEntry.getKey();
	       	 	if(news_Content.matches("(?i).*"+searchFor+".*")==true)
	       	 	{
		            good += mEntry.getValue();
	       	 	}
	            //String base = "This is the method";
	       	 	//System.out.println("Search For:  " + searchFor) ;
	            /*
	       	 	int len = searchFor.length();
	            int result = 0;
	            if (len > 0) {  
	            int start = news_Content.indexOf(searchFor);
	      //      System.out.println("start" + start);
	            while (start != -1) {
	            result++;
	        //       System.out.println("result" + result);
	            start = news_Content.indexOf(searchFor, start+len);
	            }
	            }
	            Log.d(TAG,"Key:"+searchFor+" has occurences:"+result);
	          //  System.out.println("RESULT" + result);
	            good = good + (result * mEntry.getValue());
	           // System.out.println(good);
	            //return good;
	             * 
	             */
	   		}
		//Log.d(TAG,"Returning value of good:"+good);
		return good;
	    }
	
	private double badanalysis(String news_Content, Set<Entry<String,  Double>> setBad,Iterator<Entry<String, Double>> itBad  ) {
		// TODO Auto-generated method stub
		double bad = 0;
		//Log.d(TAG,"Calculating bad sentiment for string:"+news_Content);
		//System.out.println(news_Content);
	        while (itBad.hasNext()) {
	          	 Entry<String, Double> mEntry = itBad.next();
	          //	 System.out.println(mEntry.getKey() + " : " + mEntry.getValue());
	          	 String searchFor = mEntry.getKey();
	               //String base = "This is the method";
	      //    	System.out.println("Search For:  " + searchFor) ;
	               if(news_Content.matches("(?i).*"+searchFor+".*")==true)
		       	 	{
			            bad += mEntry.getValue();
		       	 	}
	               /*
	               int result = 0;
	               if (len > 0) {  
	        //    	   System.out.println("NEEWSA:" + news_Content);
	            	   
	               int start = news_Content.indexOf(searchFor);
	          //     System.out.println("start" + start);
	               while (start != -1) {
	               result++;
	            //   System.out.println("result" + result);
	               start = news_Content.indexOf(searchFor, start+len);
	               }
	               }
		            Log.d(TAG,"Key:"+searchFor+" has occurences:"+result);
	               bad = bad + (result * mEntry.getValue());
	             //  System.out.println(bad);
	              // return bad;
	               * 
	               */
	      	 }
			//Log.d(TAG,"Returning value of bad:"+bad);
			return bad;
	    }
	}
