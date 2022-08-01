package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DeckReader {

	private int cardCount, symbolCount = 0;
	private ArrayList<String> deck = new ArrayList<String>();
	private File ourFile;
	private Scanner ourScanner;
	private ManaCollector collector = new ManaCollector();
	
	DeckReader(String fileName){
		ourFile = new File(fileName);
		
		if(openFile() == true) {
			startProcessing();
			ourScanner.close();
		}
		
		else {
			System.out.println("Looks like that file is invalid.");
		}
		
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

	private void startProcessing() {	
		// Make sure to change the spaces in the card name to "+" symbols
		
		while(ourScanner.hasNext()) {
			int cardAmount = ourScanner.nextInt();
			String cardName = ourScanner.nextLine();
			
			System.out.println(cardAmount);
			System.out.println(cardName);
			
			collector.setCardName(cardName);
			collector.setCardAmount(cardAmount);
			
			
			
			
			
			/*if (cardName.equals("main") || cardName.equals("")) {
				continue;
			}*/
			
			// MAKE SURE TO GET THE MISSING CARDS AND TAKE THOSE INTO ACCOUNT
		}
		
		System.out.println("Missing cards: " + collector.getMissingCards());
	}
}