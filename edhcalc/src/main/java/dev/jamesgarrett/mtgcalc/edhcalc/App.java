package dev.jamesgarrett.mtgcalc.edhcalc;

import java.io.File;
import java.util.Scanner;

public class App {
	
    public static void main(String[] args) {
    	
    	boolean isValid = false;
    	boolean runAgain = true;
    	String fileName = "";
    	String userResponse = "";
    	Scanner sc = new Scanner(System.in);

    	do {
        	do {
        		System.out.print("Enter the filename: ");
        		fileName = sc.nextLine();
        		
        		if (new File(fileName).isFile() == true) {
        			isValid = true;
        		}
        		
        	} while (isValid == false);
        	
        	DeckReader newDeck = new DeckReader(fileName);
        	System.out.println("--------------------------------------------------------");
        	
        	// Allows checking of multiple deck lists
        	do {
        		System.out.print("\n\nWould you like to process another deck list? "
        				+ "(\"yes\" or \"no\"): ");
        		userResponse = sc.nextLine().toLowerCase();
        		
        	} while (!(userResponse.equals("yes") || userResponse.equals("no")));
        	
        	if (userResponse.equals("no")) {
        		runAgain = false;
        	}
        	
        	else {
        		System.out.println("\n");
        	}
    		
    	} while (runAgain == true);
    	
    	sc.close();
    }
}