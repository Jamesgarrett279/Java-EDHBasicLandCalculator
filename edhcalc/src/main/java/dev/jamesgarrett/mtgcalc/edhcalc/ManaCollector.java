package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ManaCollector {
	
	private double wSymbol, uSymbol, bSymbol, rSymbol, gSymbol = 0.0;
	private int totalSymbols = 0;
	private int cardAmount;
	private boolean specialCard;
	private ArrayList<String> missingCards = new ArrayList<String>();
	private HashMap<String, Integer> foundSymbols = new HashMap<String, Integer>();
	private Scanner scanner;
	private StringBuilder returnedString;
	private String cardName;
	private RestApi connection = new RestApi();
	
	ManaCollector(){
		
	}
	
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
	
	// List of the colors for symbols
	private ArrayList<String> colorSymbols = new ArrayList<String>() {
		{
			add("{W}");
			add("{U}");
			add("{R}");
			add("{B}");
			add("{G}");
		}
	};
	
	// Processes the found symbols
	private void processMana(HashMap<String, Integer> symbols) {
		for (Map.Entry<String, Integer> currentSet : symbols.entrySet()) {
			String currentSymbol = currentSet.getKey();
			int symbolAmount = currentSet.getValue();
			
			// Checks for Phyrexian mana
			if (currentSymbol.contains("P")) {
				switch (currentSymbol) {
					case "{W/P}":
						wSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{U/P}":
						uSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{B/P}":
						bSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{R/P}":
						rSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{G/P}":
						gSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					default:
						break;
				}
			}
			
			// Checks for 2 generic mana
			else if (currentSymbol.contains("2")) {
				switch(currentSymbol) {
					case "{2/W}":
						wSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{2/U}":
						uSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{2/B}":
						bSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{2/R}":
						rSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					case "{2/G}":
						gSymbol += symbolAmount;
						totalSymbols += symbolAmount;
						break;
					default:
						break;
				}
			}
			
			// Checks for hybrid
			else if (currentSymbol.contains("/")) {
				double splitAmount = (symbolAmount * 0.5);
				
				switch(currentSymbol) {
					case "{W/U}":
						wSymbol += splitAmount;
						uSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{W/B}":
						wSymbol += splitAmount;
						bSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{B/R}":
						bSymbol += splitAmount;
						rSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{B/G}":
						bSymbol += splitAmount;
						gSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{U/B}":
						uSymbol += splitAmount;
						bSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{U/R}":
						uSymbol += splitAmount;
						rSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{R/G}":
						rSymbol += splitAmount;
						gSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{R/W}":
						rSymbol += splitAmount;
						wSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{G/W}":
						gSymbol += splitAmount;
						wSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					case "{G/U}":
						gSymbol += splitAmount;
						uSymbol += splitAmount;
						totalSymbols += symbolAmount;
						break;
					default:
						break;
				}
			}
			
			else {
				// Ensure that the symbol is actually a symbol
				if (colorSymbols.contains(currentSymbol)) {
					switch(currentSymbol) {
						case "{W}":
							wSymbol += symbolAmount;
							totalSymbols += symbolAmount;
							break;
						case "{U}":
							uSymbol += symbolAmount;
							totalSymbols += symbolAmount;
							break;
						case "{B}":
							bSymbol += symbolAmount;
							totalSymbols += symbolAmount;
							break;
						case "{R}":
							rSymbol += symbolAmount;
							totalSymbols += symbolAmount;
							break;
						case "{G}":
							gSymbol += symbolAmount;
							totalSymbols += symbolAmount;
							break;
						default:
							break;
					}
				}
			}			
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
					
					foundSymbols.replace(splitSymbols[0], (temp + cardAmount));
				}
				
				else {
					foundSymbols.put(splitSymbols[0], cardAmount);
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
						
						foundSymbols.replace(currentSymbol, (temp + cardAmount));
					}
					
					else {
						foundSymbols.put(currentSymbol, cardAmount);
					}
				}
			}
		}
	}
	
	// Uses the "normalCard" method to process both sides
	private void specialCard(JSONObject card) {
		specialCard = false;
		JSONArray cardFaces = (JSONArray) card.get("card_faces");
		JSONObject front = (JSONObject) cardFaces.get(0);
		JSONObject back = (JSONObject) cardFaces.get(1);
		
		normalCard(front);
		normalCard(back);
	}
	
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
	public void setCardAmount(int cardAmount) {
		this.cardAmount = cardAmount;
	}
	
	public ArrayList<String> getMissingCards() {
		return missingCards;
	}
	
	public HashMap<String, Double> getSymbols(){
		processMana(foundSymbols);
		
		HashMap<String, Double> finalSymbols = new HashMap<String, Double>(){
			{
				put("wSymbol", wSymbol);
				put("uSymbol", uSymbol);
				put("bSymbol", bSymbol);
				put("rSymbol", rSymbol);
				put("gSymbol", gSymbol);
			}
		};
		
		return finalSymbols;
	}
	
	public int getTotalSymbols() {
		return totalSymbols;
	}
	
	public void collector() {
		JSONArray cardInfo;
		int responseCode;
		
		connection.setCardName(cardName);
		connection.startSearch();
		responseCode = connection.getResponseCode();
		
		// WE NEED TO ADD A DELAY TO THIS SO THAT THE API DOESN'T BAN US
		try {
			Thread.sleep(500);
		} 
		
		catch (InterruptedException e) {
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
				scanner = new Scanner(connection.getUrl().openStream());
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
				specialCard(jsonCard);
			}
			
			else {
				normalCard(jsonCard);
			}
		}
	}
}
