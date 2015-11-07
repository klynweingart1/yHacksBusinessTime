
import java.io.*;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class JSONParser {

	protected String getResponse(CloseableHttpResponse response) {
	    String result = null;
        
	    // Headers
	    org.apache.http.Header[] headers = response.getAllHeaders();
	    for (int i = 0; i < headers.length; i++) {
	        System.out.println(headers[i]);
	    }
	    return result;
	}
	
	private static String convertStreamToString(InputStream is) {

	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public static void main(String[] args){
		PdfParser ourPdfParser = new PdfParser();
		JSONParser ourJSONParser = new JSONParser();
		ourPdfParser.post1("/Users/WillB/Documents/workspace/yHacks/images/WilliamBarclayResume.pdf");
		CloseableHttpResponse response = ourPdfParser.response;
		
		ourJSONParser.getResponse(response);
	}
}