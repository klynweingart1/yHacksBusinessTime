import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
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

public class PdfParser {

	private String apikey = "d991691d-4882-4733-9222-0cacd692d58a";
	private String url = "https://api.havenondemand.com/1/api/sync/extracttext/v1";
	public CloseableHttpResponse response = null;

	public void post1(String fileSrc){
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
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
			    entity.writeTo(System.out);
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
	}
	
	public static void main(String[] args) {
		PdfParser parser = new PdfParser();
		parser.post1("/Users/katelynweingart/Documents/workspace/YHack/weingart_resume.pdf");
		CloseableHttpResponse response = parser.response;
		System.out.println(response);
	}
}
