package ads.bridges;

// The post sends were done with the help of https://stackoverflow.com/questions/3324717/sending-http-post-request-in-java

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.ObjectMapper;

import ads.configurations.SiteConfigurations;
import ads.users.Curator;
import frontendusers.FrontEndCurator;
import frontendusers.FrontEndEditor;

/**
 * A class that makes the bridge between the client requests and the appropriate frontenduser class function
 * @author Susana Polido
 * @version 0.3
 */

public class FrontEndBridge {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private String uri;
	private FrontEndCurator curator;
	private FrontEndEditor editor;
	
	/**
	 * Creates the object from the location of the frontend site
	 * @param uri
	 * @since 0.1
	 */
	public FrontEndBridge(String uri) {
		this.uri = uri;
		this.curator = new FrontEndCurator(uri);
		this.editor = new FrontEndEditor(uri);
	}
	
	
	
	/**
	 * Connects to the site through a get request that receives a message from a frontend client.
	 * It figures out what that message is asking and calls the appropriate 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void connectJavaClient() throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			      .uri(URI.create(uri+"/server_client"))
			      .GET()
			      .build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		System.out.println("Server response code: " + response.statusCode());
		System.out.println(response.body());
		
		String body = response.body();
		String type = objectMapper.readTree(body)
				.get("data")
				.get("pedido")
				.asText();
		System.out.println(type);
		
		switch(type) {
			case "checkCuratorIsValid":
				curator.sendCuratorIsValidResponse(body);
				break;
			
			case "changeCuratorBranch":
				curator.sendOtherBranchContent(body);
				break;
				
			case "curatorAction":
				String action = objectMapper.readTree(body)
							.get("data")
							.get("action")
							.asText();
				if(action.equals("acceptContribution"))
					curator.acceptContribution(body);
				else if(action.equals("mixContribution"))
					curator.mixContribution(body);
				else if(action.equals("rejectContribution"))
					curator.rejectContribution(body);
				else
					unknownRequest(body);
				break;
			case "editor":
				editor.goToEditorPage(body);
				break;
			case "createClass":
				editor.createClass(body);
				break;
			case "createIndividual":
				editor.createIndividual(body);
				break;
			case "createDataProperty":
				editor.createDataProperty(body);
				break;
			case "createObjectProperty":
				editor.createObjectProperty(body);
				break;
			case "deleteStuff":
				editor.deleteStuff(body);
				break;
			case "editClass":
				break;
			case "editIndividual":
				break;
			case "editDataProperty":
				editor.editDataPropertyPage(body);
				break;
			case "changeDataProperty":
				editor.changeDataProperty(body);
			case "editObjectProperty":
				editor.editObjectPropertyPage(body);
				break;
			case "changeObjectProperty":
				editor.changeObjectProperty(body);
				break;
			case "viewer":
				break;
			default:
				unknownRequest(body);
				break;
				
		}
			
	}
	
	/**
	 * Sends the reply when the received message has a type of request it doesn't recognise
	 * <p>
	 * It should never be used
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	private void unknownRequest(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		params.add(new BasicNameValuePair("message", "unknown request"));
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	
	
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		while(true) {
			try {
				new FrontEndBridge(new SiteConfigurations("site_config.ini").getUri()).connectJavaClient();
				//new FrontEndBridge("https://ads2122projeto01.herokuapp.com").connectJavaClient();
			}
			catch(Exception e) {
				System.out.println("If you died, ignore and move on");
			}
		}
	}
}
