package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Scanner;

public class DeckReader {

	private int cardCount = 0;
	private double wSymbol, uSymbol, bSymbol, rSymbol, gSymbol = 0.0;
	private double plains, islands, swamps, mountains, forests = 0.0;
	private File ourFile;
	private Scanner ourScanner;
	private ManaCollector collector = new ManaCollector();
	
	DeckReader(String fileName){
		welcomeText();
		
		
		ourFile = new File(fileName);
		
		if(openFile() == true) {
			startProcessing();
			ourScanner.close();
			
			// Calculate and print results
			landFormula();
			printResults();
		}
		
		else {
			System.out.println("Looks like that file is invalid.");
		}
	}
	
	private void welcomeText() {
		System.out.println("|***************************************|");
		System.out.println("|---------------------------------------|");
		System.out.println("|\tEDH BASIC LAND CALCULATOR\t|");
		System.out.println("|\t\t\t\t\t|");
		System.out.println("|\tCreated by James Garrett\t|");
		System.out.println("|\tFormula by Michael Donahue\t|");
		System.out.println("|---------------------------------------|");
		System.out.println("|***************************************|");
	}
	
	// Exception-handling for opening the file
	private boolean openFile() {
		try {
			ourScanner = new Scanner(ourFile);
			return true;
		} 
		
		catch(FileNotFoundException e) {
			return false;
		}
	}
	
	/* ----------------------------------------------
	 * THIS FORMULA WAS CREATED BY MICHAEL DONAHUE 
	 * ---------------------------------------------- */
	private double calculation(double colorSym, int totalSym, int openSpaces) {
		return (colorSym / totalSym) * openSpaces;
	}
	
	private void landFormula() {
		HashMap<String, Double> finalSymbols = collector.getSymbols();
		int totalSymbols = collector.getTotalSymbols();
		int openSpaces = (100 - cardCount); // 100 cards for EDH
		
		wSymbol = finalSymbols.get("wSymbol");
		uSymbol = finalSymbols.get("uSymbol");
		bSymbol = finalSymbols.get("bSymbol");
		rSymbol = finalSymbols.get("rSymbol");
		gSymbol = finalSymbols.get("gSymbol");
		
		plains = calculation(wSymbol, totalSymbols, openSpaces);
		islands = calculation(uSymbol, totalSymbols, openSpaces);
		swamps = calculation(bSymbol, totalSymbols, openSpaces);
		mountains = calculation(rSymbol, totalSymbols, openSpaces);
		forests = calculation(gSymbol, totalSymbols, openSpaces);
	}
	
	// Maybe make this a boolean and ask if they want to search for another deck?
	private void printResults() {
		DecimalFormat df = new DecimalFormat("###.####");
		
		System.out.println("\nMissing cards: " + collector.getMissingCards());
		System.out.println("Your total basic land count is: " + (100 - cardCount) + "\n");
		System.out.println("The number of plains you should have: " 
				+ df.format(plains) + " || " + wSymbol + " white symbols");
		System.out.println("The number of islands you should have: " 
				+ df.format(islands) + " || " + uSymbol + " blue symbols");
		System.out.println("The number of swamps you should have: " 
				+ df.format(swamps) + " || " + bSymbol + " black symbols");
		System.out.println("The number of mountains you should have: " 
				+ df.format(mountains) + " || " + rSymbol + " red symbols");
		System.out.println("The number of forests you should have: " 
				+ df.format(forests) + " || " + gSymbol + " green symbols");
	}

	private void startProcessing() {	
		int counter = 0;
		System.out.print("\nProcessing your deck...");
		
		while(ourScanner.hasNext()) {
			int cardAmount = ourScanner.nextInt();
			String cardName = ourScanner.nextLine();
			cardName = cardName.replace(' ', '+');
			counter++;
			
			cardCount += cardAmount;
			collector.setCardName(cardName);
			collector.setCardAmount(cardAmount);
			collector.collector();
			
			if (counter % 5 == 0) {
				//Processing animation
				System.out.print(".");
			}
		}
		
		System.out.println(" Completed. \n\nPrinting out results...");
	}
}