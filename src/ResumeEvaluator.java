import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;

import com.sun.jna.platform.win32.WinDef.CHAR;

import java.io.*;

public class ResumeEvaluator {
	String content;
	HashMap<String, Integer> wordFrequencies = new HashMap<String, Integer>();
	
	public ResumeEvaluator(String content) {
		this.content = content;
	}
	/**
	 * @param content: text we're passing in
	 * @param index: index of the word "GPA"
	 * @param direction: direction to search in (-1 or 1)
	 * @return start and end indices
	 *
	public int[] findClosestNumber(String content, int index, int direction) {
		while (!Character.isDigit(content.charAt(index))) {
			index += direction;
		}
		int start = index;
		while ((Character.isDigit(content.charAt(index)) || content.charAt(index) == '.')) {
			index += direction;
		}
		int end = index;
		int[] pair = {start, end}; 
		return pair;
	}
	*/
	
	public String findClosestNumber(String content, int index, int direction) {
		while (!Character.isDigit(content.charAt(index))) {
			index += direction;
		}
		int start = index;
		while ((Character.isDigit(content.charAt(index)) || content.charAt(index) == '.')) {
			index += direction;
		}
		int end = index;
		int[] pair = {start, end}; 
		return pair;
	}
	
	public void findWordFrequencies() {
		String strArray[] = content.split("[^a-zA-Z0-9']+");
		for(String str: strArray) {
			Integer freq = wordFrequencies.get(str);
			if(freq == null) wordFrequencies.put(str, 0);
			else wordFrequencies.put(str, freq + 1);
		}
	}
	
	public String findGPA(String resumeContent) {
		Pattern gpaPattern = Pattern.compile("(GPA|grade point average)");
		Matcher gpaMatcher = gpaPattern.matcher(resumeContent);
		ArrayList<String> gpas = new ArrayList<String>();
		while (gpaMatcher.find()) {
	        System.out.print("Start index: " + gpaMatcher.start());
	        System.out.print(" End index: " + gpaMatcher.end());
	        System.out.println(" Found: " + gpaMatcher.group());
	        int[2] prev = findClosestNumber(resumeContent, gpaMatcher.start(), -1);
	        int[2] next = findClosestNumber(resumeContent, gpaMatcher.end(), 1);
	        int startIndexPrevNumber = prev[0];
	        int endIndexPrevNumber = prev[1];
	        int startIndexNextNumber = next[0];
	        int endIndexNextNumber = next[1];
	        String prevString = 
			gpas.add(gpaMatcher.group(0));
		}
		if (gpas.isEmpty())
			return null;
		else
			return gpas.get(0);
	}
	
	public void evaluateResume(String resumeContent) {
		
	}
	
	public static String readFile(String path, Charset encoding) 
			  throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static void main(String[] args) {
		Charset utf8charset = Charset.forName("UTF-8");
		String resumeContent;
		try {
			resumeContent = readFile("elijahResumeContentGPA.txt", utf8charset);
		} catch (IOException exception) {
			System.out.println("Unable to read file");
			return;
		}
		System.out.println(resumeContent);
		ResumeEvaluator ev = new ResumeEvaluator(resumeContent);
		String gpaString = ev.findGPA(resumeContent);
		System.out.println(gpaString);
	}

}