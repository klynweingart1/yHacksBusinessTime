import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/******************************************************************************
 *  Compilation:  javac SpellChecker.java 
 *  Execution:    java SpellChecker words.txt
 *  Dependencies: SET.java In.java StdIn.java
 *  Data files:   http://www.cs.princeton.edu/introcs/data/words.utf-8.txt
 *
 *  Read in a dictionary of words from the file words.txt, and print
 *  out any misspelled words that appear on standard input.
 *
 *
 ******************************************************************************/

public class SpellCheck {        
	HashSet<String> dictionary = new HashSet<String>();

	public SpellCheck(String filename) {
		try {
			fileToHashMap(filename);
		} catch (IOException e) {
			System.out.println("Error reading dictionary file");
			e.printStackTrace();
		}
	}
	public void fileToHashMap(String fileName) throws IOException {
		FileReader input = new FileReader(fileName);
		BufferedReader bufRead = new BufferedReader(input);

		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null) {    
		    dictionary.add(myLine);
		}
		bufRead.close();
	}
	public int numErrors(String[] args) throws FileNotFoundException {
		int numErrors = 0;
		for(String str : args) {
			if(!dictionary.contains(str)) numErrors++;
		}
        return numErrors;
    }
}
