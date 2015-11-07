import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.*;
import java.io.*;

public class ResumeEvaluator {
	
	public String findGPA(String resumeContent) {
		Pattern gpaPattern = Pattern.compile("(GPA|grade point average)");
		Matcher gpaMatcher = gpaPattern.matcher(resumeContent);
		ArrayList<String> gpas = new ArrayList<String>();
		while (gpaMatcher.find()) {
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
		ResumeEvaluator ev = new ResumeEvaluator();
		String gpaString = ev.findGPA(resumeContent);
		System.out.println(gpaString);
	}

}