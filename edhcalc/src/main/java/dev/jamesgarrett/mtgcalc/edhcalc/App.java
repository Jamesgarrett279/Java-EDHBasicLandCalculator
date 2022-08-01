package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.File;
import java.util.Scanner;

public class App {
	
    public static void main(String[] args) {
    	
    	boolean isValid = false;
    	String fileName = "";
    	Scanner sc = new Scanner(System.in);
    	
    	// This is a double-faced card search:   "Wandering+Archaic+//+Explore+the+Vastlands"
    	
    	ManaCollector mana = new ManaCollector();
    	mana.collector();
    	
    	do {
    		System.out.print("Enter the filename: ");
    		fileName = sc.nextLine();
    		
    		if (new File(fileName).isFile() == true) {
    			isValid = true;
    		}
    		
    	} while (isValid == false);
    	sc.close();
    	
    	
    	DeckReader newDeck = new DeckReader(fileName);
    }
}