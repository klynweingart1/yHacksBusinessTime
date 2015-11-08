import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.*;
import java.io.*;

import org.json.JSONException;

public class ResumeEvaluator {
	
	// Should this be static? Also, could it just be an array? 
	// We only have three elements and they don't ever change.
	ArrayList<String> options = new ArrayList<String>();
	
	// Instance variables. Should these be private?
	String type;
	String content;
	String[] noWhitespace;
	double gpa;
	int numTypos;
	
	/* Question: make these static? 
	 * Thoughts: We only need to load the university file into the HashMap
	 * once, so I think this should be static. As we have it set up now,
	 * the goodWords HashSet is general good words plus the words specific
	 * to the type of resume this is (tech, business, or bio), so this shouldn't
	 * be static though unless we change how this is structured. 
	 */
	HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
	HashMap<String, Integer> universityRanking = new HashMap<String, Integer>();
	HashSet<String> goodWords = new HashSet<String>();
	
	static String dictionaryFileName = "dictionary.txt";
	static String wordsFolder = "words/";
	static String generalWordsFileName = "general.txt";
	static String collegeRankingsFileName = "CollegeRanksV1.txt";
	
	
	public ResumeEvaluator(String content) {
		options.add("tech");
		options.add("bio");
		options.add("business");
		
		try{
			universityFileToHashMap();
		} catch(Exception e) {
			System.out.println("Error parsing university file: \n" + e);
		}
		try{
			goodWordFileToHashSet();
		} catch(Exception e) {
			System.out.println("Error parsing good words file: \n" + e);
		}
		
		this.content = content;
	}
	
	public String mostLikelyACT(String act1, String act2) {
		if (act1 == null)
			return act2;
		if (act2 == null)
			return act1;
		if (act1.contains("."))
			return act2;
		else
			return act1;
	}
	
	public String findACT() {
		Pattern actPattern = Pattern.compile("(ACT|ACT:|ACT Score:)");
		Matcher actMatcher = actPattern.matcher(content);
		// only look at first instance
		if (actMatcher.find()) {	        
	        String prevString = numberWithinFiveChars(actMatcher.start(), -1);
	        String nextString = numberWithinFiveChars(actMatcher.end(), 1);
	        String act = mostLikelyACT(prevString, nextString);
	        return act;
		} else {
			return null;
		}
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String numberWithinFiveChars(int index, int direction) {
		int count = 0;
		
		//find start of number value
		while (!Character.isDigit(content.charAt(index))) {
			index += direction;
			count++;
			if(count > 5) return null;
		}
		//find end of number value
		int firstIndex = index; //could be start or end of number value
		while ((Character.isDigit(content.charAt(index)) || content.charAt(index) == '.')) {
			index += direction;
			count++;
		}
		int secondIndex = index;
		if (direction == 1) {
			return content.substring(firstIndex, secondIndex);
		} else {
			return content.substring(secondIndex + 1, firstIndex + 1);
		}
		
	}
	
	/**
	 * Given two numbers encoded as strings, which one is the most likely to be the GPA?
	 * @param gpa1
	 * @param gpa2
	 * @return Either gpa1 or gpa2. The logic isn't that spectacular, but it's something.
	 */
	public String mostLikelyGPA(String gpa1, String gpa2) {
		if (gpa1 == null)
			return gpa2;
		if (gpa2 == null)
			return gpa1;
		if (gpa1.contains("."))
			return gpa1;
		else
			return gpa2;
	}

	public void findWordFrequencies() {
		String strArray[] = content.split("[^a-zA-Z0-9']+");
		for(String str: strArray) {
			Integer freq = wordFrequencies.get(str);
			if(freq == null) wordFrequencies.put(str, 1);
			else wordFrequencies.put(str, freq + 1);
		}
	}
	
	public void splitContentOnWhitespace() {
		String regex = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)";
		noWhitespace = content.split(regex);
	}
	
	public String findGPA() {
		Pattern gpaPattern = Pattern.compile("(GPA|grade point average|GPA:)");		
		Matcher gpaMatcher = gpaPattern.matcher(content);
		// only look at first instance
		if (gpaMatcher.find()) {	        
	        String prevString = numberWithinFiveChars(gpaMatcher.start(), -1);
	        String nextString = numberWithinFiveChars(gpaMatcher.end(), 1);
	        String gpa = mostLikelyGPA(prevString, nextString);
	        return gpa;
		} else {
			return null;
		}
	}
	
	public void universityFileToHashMap() throws NumberFormatException, IOException {
		FileReader input = new FileReader(collegeRankingsFileName);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null) {    
		    String[] array = myLine.split("#");
		    // check to make sure you have valid data
		    universityRanking.put(array[0], Integer.parseInt(array[1]));
		}
		bufRead.close();
	}
	
	public void goodWordFileToHashSet() throws IOException {
		FileReader input = new FileReader(wordsFolder + generalWordsFileName);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null) {    
		    goodWords.add(myLine);
		}
		bufRead.close();
	}
	
	public void specificWordFileToHashSet() throws IOException {
		FileReader input = new FileReader(wordsFolder + type + ".txt");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null) {    
		    goodWords.add(myLine);
		}
		bufRead.close();
	}
	
	public void evaluateResume() {
		
	}
	
	public void setNumTypos() throws FileNotFoundException {
		SpellCheck sCheck = new SpellCheck(dictionaryFileName);
		splitContentOnWhitespace();
		numTypos = sCheck.numErrors(noWhitespace);
	}
	
	public static String readFile(String path, Charset encoding) 
			  throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	
	public static void main(String[] args) throws JSONException {
		PdfParser parser = new PdfParser();
 		String jsonString = parser.post1("weingart_resume.pdf");
 		String content = parser.extractText(jsonString);
		ResumeEvaluator ev = new ResumeEvaluator(content);

 		if (args.length < 1 || !ev.options.contains(args[1])) {
 			System.out.println("Need argument; tech, business, or bio");
 			return;
 		}
 		else { //set type
 	 		ev.setType(args[1]);
 		}
 		
		try{
			ev.specificWordFileToHashSet();
		} catch(Exception e) {
			System.out.println("Error parsing " + ev.type + " words file: \n" + e);
		}
		System.out.println(ev.universityRanking);
		System.out.println("resume text: " + ev.content);
		String gpaString = ev.findGPA();
		System.out.println(gpaString);
		String actString = ev.findACT();
		System.out.println(actString);
	}

}