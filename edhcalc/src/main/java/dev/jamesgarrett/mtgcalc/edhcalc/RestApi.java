package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class RestApi {
	private String cardName = "";
	private int responseCode;
	private URL url;
	private HttpURLConnection conn;
	
	RestApi(){
		
	}

	private void setupUrl() {
		String fullUrl = "https://api.scryfall.com/cards/named?exact=" + cardName;
		
		try {
			url = new URL(fullUrl);
		} 
		
		catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	
	public URL getUrl() {
		return url;
	}
	
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	public void startSearch() {
		setupUrl();
		
		// Setting up the connection
		try {
			conn = (HttpURLConnection) url.openConnection();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		try {
			conn.setRequestMethod("GET");
		}

		catch (ProtocolException e) {
			e.printStackTrace();
		}

		try {
			conn.connect();
			responseCode = conn.getResponseCode();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
