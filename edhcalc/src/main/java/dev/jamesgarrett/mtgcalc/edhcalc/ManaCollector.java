package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ManaCollector {
	
	private int wSymbol, uSymbol, bSymbol, rSymbol, gSymbol = 0;
	private int plains, islands, swamps, mountains, forests = 0;
	private int responseCode;
	private boolean specialCard;
	private ArrayList<String> missingCards = new ArrayList<String>();
	private HashMap<String, Integer> foundSymbols = new HashMap<String, Integer>();
	private Scanner scanner;
	private StringBuilder returnedString;
	private String cardName;
	private URL url;
	private HttpURLConnection conn;
	
	// List of the "generic mana" symbols that don't need to be counted
	private ArrayList<String> genericSymbols = new ArrayList<String>() {
		{
			add("{0}");
			add("{1}");
			add("{2}");
			add("{3}");
			add("{4}");
			add("{5}");
			add("{6}");
			add("{7}");
			add("{8}");
			add("{9}");
			add("{10}");
			add("{11}");
			add("{12}");
			add("{13}");
			add("{14}");
			add("{15}");
			add("{16}");
			add("{C}");
			add("{S}");
			add("{X}");
		}
	};
	
	ManaCollector(String cardName){
		this.cardName = cardName;
		
	}
	
	private void attemptConnection() {
		String fullUrl = "https://api.scryfall.com/cards/named?exact=" + cardName;
		
		try {
			url = new URL(fullUrl);
		} 
		
		catch(MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void processMana(ArrayList<String> symbols) {
		for (int i = 0; i < symbols.size(); i++) {
			
		}
	}
	
	private void normalCard(JSONObject card) {
		String manacost = card.get("mana_cost").toString();
		String replaced = manacost.replace("}", "} ");
		String [] splitSymbols = replaced.split(" ");
		
		// Checks to see if the card is a land
		if (manacost.equals("")) {
			return;
		}
		
		// If there's only one mana symbol
		else if (splitSymbols.length == 1) {	
			// We can ignore the symbol if it's a generic symbol
			if (genericSymbols.contains(splitSymbols[0])) {
				return;
			}
			
			// Otherwise...
			else {
				if (foundSymbols.containsKey(splitSymbols[0])) {
					int temp = foundSymbols.get(splitSymbols[0]);
					
					foundSymbols.replace(splitSymbols[0], temp++);
				}
				
				else {
					foundSymbols.put(splitSymbols[0], 1);
				}
			}
		}
		
		// If there's more than one mana symbol
		else {
			for (int i = 0; i < splitSymbols.length; i++) {
				String currentSymbol = splitSymbols[i];
				
				// Check to see if it's a generic symbol
				if (genericSymbols.contains(currentSymbol)) {
					continue;
				}
				
				// Otherwise...
				else {
					if (foundSymbols.containsKey(currentSymbol)) {
						int temp = foundSymbols.get(currentSymbol);
						
						foundSymbols.replace(currentSymbol, temp++);
					}
					
					else {
						foundSymbols.put(currentSymbol, 1);
					}
				}
			}
		}
		
		// # This mana is generic, colorless, or snow, which we will assume the deck can pay for through other cards 
		
		
	}
	
	// Uses the "normalCard" method to process both sides
	private void specialCard(JSONObject card) {
		JSONArray cardFaces = (JSONArray) card.get("card_faces");
		JSONObject front = (JSONObject) cardFaces.get(0);
		JSONObject back = (JSONObject) cardFaces.get(1);
		
		normalCard(front);
		normalCard(back);
	}
	
	public String getCardName() {
		return cardName;
	}
	
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	public void collector() {
		JSONArray cardInfo;
		
		attemptConnection();
		
		// Setting up the connection
		try {
			conn = (HttpURLConnection) url.openConnection();
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			conn.setRequestMethod("GET");
		}
		
		catch(ProtocolException e) {
			e.printStackTrace();
		}
		
		try {
			conn.connect();
			responseCode = conn.getResponseCode();
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
		
		// WE NEED TO EDIT THIS TO SAVE THE NAMES OF ANY MISSING CARDS AND LET THE USER KNOW
		if (responseCode == 404) {
			missingCards.add(cardName);
		}
		
		else if (responseCode != 200){
			throw new RuntimeException("" + responseCode);
		}
		
		// Start processing the request
		else {
			returnedString = new StringBuilder();
			
			try {
				scanner = new Scanner(url.openStream());
			}
			
			catch (IOException e) {
				e.printStackTrace();
			}
			
			while (scanner.hasNext()) {
				returnedString.append(scanner.nextLine());
			}
			
			scanner.close();
			
			// Convert the strings to JSON
			JSONParser parser = new JSONParser();
			JSONObject obj = null;
			
			try {
				obj = (JSONObject) parser.parse(String.valueOf(returnedString));
			}
			
			catch(ParseException e) {
				e.printStackTrace();
			}
			
			cardInfo = new JSONArray();
			cardInfo.add(obj);
			
			JSONObject jsonCard = (JSONObject) cardInfo.get(0);
			
			// Check to see if card is a "special card"
			if (jsonCard.containsKey("card_faces")) {
				specialCard = true;
			}
			
			if (specialCard == true) {
				
			}
			
			else {
				
			}
			
			//System.out.println(jsonCard);
			//String test =  jsonCard.get("oracle_text").toString();
			JSONArray faces = (JSONArray) jsonCard.get("card_faces");
			JSONObject f1 = (JSONObject) faces.get(0);
			System.out.println(f1.get("oracle_text"));
			
			
			//String [] tesr = faces.get(0).toString().replace("\",\"", "\" , \"").split(" , ");
			//System.out.println(tesr);
			
			
			//System.out.println(jsonCard.get("card_faces"));
			//System.out.println(jsonCard.get("mana_cost").toString());
			
			
			
			/*for (String a : tesr) {
				System.out.println(a);
			}*/
		}
	}
}
;