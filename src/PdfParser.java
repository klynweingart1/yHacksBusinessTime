import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PdfParser {

	private String apikey = "d991691d-4882-4733-9222-0cacd692d58a";
	private String url = "https://api.havenondemand.com/1/api/sync/extracttext/v1";
	
	public CloseableHttpResponse response = null;
	
	public String extractText(String json) throws JSONException {
		 JSONObject result = new JSONObject(json); //Convert String to JSON Object

         JSONArray tokenList = result.getJSONArray("document");
         JSONObject obj = (JSONObject)tokenList.get(0);
         String content = obj.getString("content");
         return content;
	}
	
	public String post1(String fileSrc){
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
	    String str = null;

		try {
		    HttpPost httppost = new HttpPost(url);
		
		    File f = new File(fileSrc);
		    FileBody fileBody = new FileBody(f);
		    StringBody apikeyStringBody = new StringBody(apikey, ContentType.TEXT_PLAIN);
		
		    HttpEntity reqEntity = MultipartEntityBuilder.create()
			    .addPart("file", fileBody)
			    .addPart("apikey", apikeyStringBody)
			    .build();
		
		    httppost.setEntity(reqEntity);
		
		    CloseableHttpResponse response = null;
		    
		    try {
			response = httpclient.execute(httppost);
			
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			
			if (entity != null) {
		        str = EntityUtils.toString(entity); 
			}
			
		    }catch(ClientProtocolException cpe){
			cpe.printStackTrace();
		    }catch(IOException ioe){
			ioe.printStackTrace();
		    } finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		    }
		} finally {
		    try {
		    	httpclient.close();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		}
		return str;
	}
	
	public static void main(String[] args) throws JSONException {
		PdfParser parser = new PdfParser();
		String str = parser.post1("weingart_resume.pdf");
		System.out.println(str);
	}
}
