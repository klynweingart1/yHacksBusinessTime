import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.regex.*;
import java.io.*;

import org.json.JSONException;

public class ResumeEvaluator {
	String universityFile;
	String content;
	HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
	HashMap<String, Integer> universityRanking = new HashMap<String, Integer>();

	public ResumeEvaluator(String content, String universityFile) {
		this.content = content;
		this.universityFile = universityFile;
	}
	public String numberWithinFiveChars(int index, int direction) {
		int count = 0;
		
		//find start of GPA
		while (!Character.isDigit(content.charAt(index))) {
			index += direction;
			count++;
			if(count > 5) return null;
		}
		//find end of GPA
		int startIndex = index; //could be start or end of GPA
		while ((Character.isDigit(content.charAt(index)) || content.charAt(index) == '.')) {
			index += direction;
			count++;
		}
		int secondIndex = index;
		if (direction == 1) {
			return content.substring(startIndex, secondIndex);
		} else {
			return content.substring(secondIndex + 1, startIndex + 1);
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
	
	public String findGPA() {
		Pattern gpaPattern = Pattern.compile("(GPA|grade point average)");
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
		FileReader input = new FileReader(universityFile);
		BufferedReader bufRead = new BufferedReader(input);
		String myLine = null;

		while ( (myLine = bufRead.readLine()) != null)
		{    
		    String[] array = myLine.split("#");
		    // check to make sure you have valid data
		    universityRanking.put(array[0], Integer.parseInt(array[1]));
		}
		bufRead.close();
	}
	
	public void evaluateResume() {
		
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

		ResumeEvaluator ev = new ResumeEvaluator(content, "CollegeRanksV1.txt");
		try{
			ev.universityFileToHashMap();
		} catch(Exception e) {
			System.out.println("Error parsing university file: \n" + e);
		}
		System.out.println("resume text: " + ev.content);
		String gpaString = ev.findGPA();
		System.out.println(gpaString);
	}

}