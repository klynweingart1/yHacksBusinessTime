import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.*;
import java.io.*;

import org.json.JSONException;

public class ResumeEvaluator {
		
	// Instance variables. Should these be private?
	private String type;
	private String content;
	private String[] noWhitespace;
	private double gpa;
	private double act;
	private int numTypos;
	
	HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
	
	static HashMap<String, Integer> universityRanking = new HashMap<String, Integer>();
	static HashSet<String> generalWords = new HashSet<String>();
	static HashSet<String> techWords = new HashSet<String>();
	static HashSet<String> businessWords = new HashSet<String>();
	static HashSet<String> bioWords = new HashSet<String>();
	
	static String dictionaryFileName = "dictionary.txt";
	static HashSet<String> dictionary = null;
	static String wordsFolder = "words/";
	static String generalWordsFileName = "general.txt";
	static String collegeRankingsFileName = "CollegeRanksV1.txt";
	
	public ResumeEvaluator(String content, String type) {
		
		this.content = content;
		
 		if (!type.equals("tech") && !type.equals("bio") && !type.equals("business")) {
 			System.out.println("Need argument; tech, business, or bio");
 			return;
 		} else { //set type
 	 		this.type = type;
 		}
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
	
	public void findACT() {
		Pattern actPattern = Pattern.compile("(ACT|ACT:|ACT Score:)");
		Matcher actMatcher = actPattern.matcher(content);
		// only look at first instance
		if (actMatcher.find()) {	        
	        String prevString = numberWithinFiveChars(actMatcher.start(), -1);
	        String nextString = numberWithinFiveChars(actMatcher.end(), 1);
	        act = Double.parseDouble(mostLikelyACT(prevString, nextString));
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
	
	public void findGPA() {
		Pattern gpaPattern = Pattern.compile("(GPA|grade point average|GPA:)");		
		Matcher gpaMatcher = gpaPattern.matcher(content);
		// only look at first instance
		if (gpaMatcher.find()) {	        
	        String prevString = numberWithinFiveChars(gpaMatcher.start(), -1);
	        String nextString = numberWithinFiveChars(gpaMatcher.end(), 1);
	        gpa = Double.parseDouble(mostLikelyGPA(prevString, nextString));
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
	
	public static void goodWordFileToHashSet() throws IOException {
		FileReader input = new FileReader(wordsFolder + generalWordsFileName);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null) {    
		    generalWords.add(myLine);
		}
		bufRead.close();
	}
	
	public static void specificWordFileToHashSet(String type) throws IOException {
		FileReader input = new FileReader(wordsFolder + type + ".txt");
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;
		HashSet<String> set = new HashSet<String>();
		if (type == "bio")
			set = bioWords;
		else if (type == "tech")
			set = techWords;
		else if (type == "business")
			set = businessWords;
		
		while ( (myLine = bufRead.readLine()) != null) {
			set.add(myLine);
				
		}
		bufRead.close();
	}
	
	public void evaluateResume() {
		System.out.println(universityRanking);
		System.out.println("resume text: " + content);
		findGPA();
		System.out.println("gpa: " + gpa);
		findACT();
		System.out.println("act: " + act);
		System.out.println("num typos: " + numTypos);
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
	
	public static void makeHashMaps() {
		
		try{
			specificWordFileToHashSet("bio");
			specificWordFileToHashSet("tech");
			specificWordFileToHashSet("business");
		} catch(Exception e) {
			System.out.println("Error parsing words files: \n" + e);
		}
	}
	
	public static void main(String[] args) throws JSONException {
		
		// as of now, args[0] is a path to a single PDF
		// we should change this so that it is a path to a folder
		// and then we can call the rest of this method on each file in that folder
		
		PdfParser parser = new PdfParser();
 		String jsonString = parser.post1(args[0]);
 		String content = parser.extractText(jsonString);

 		ResumeEvaluator ev = new ResumeEvaluator(content, args[1]);
 		ev.evaluateResume();
		
	}

}