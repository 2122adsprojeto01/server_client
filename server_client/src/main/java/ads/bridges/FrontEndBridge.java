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

public class FrontEndBridge {
	private static ObjectMapper objectMapper = new ObjectMapper();
	private String uri;
	
	public FrontEndBridge(String uri) {
		this.uri = uri;
	}
	
	
	public void connectJavaClient() throws IOException, InterruptedException {
		//.uri(URI.create("https://ads2122projeto01.herokuapp.com/server_client"))
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
				sendCuratorIsValidResponse(body);
				break;
			
			case "changeCuratorBranch":
				sendOtherBranchContent(body);
				break;
			case "curatorAction":
				String action = objectMapper.readTree(body)
							.get("data")
							.get("action")
							.asText();
				System.out.println(action);
				if(action.equals("acceptContribution"))
					acceptContribution(body);
				else if(action.equals("mixContribution"))
					mixContribution(body);
				else if(action.equals("rejectContribution"))
					rejectContribution(body);
				else
					unknownRequest(body);
				break;
				
			default:
				unknownRequest(body);
				break;
				
		}
			
	}
	
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
	
	private void sendCuratorIsValidResponse(String body) throws ClientProtocolException, IOException {
		String email = objectMapper.readTree(body)
				.get("data")
				.get("email")
				.asText();
	
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
	
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		if(Curator.isCurator(email)) {
			params.add(new BasicNameValuePair("nextPage", "curator"));
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("message", ""));
			params.add(new BasicNameValuePair("currversion", Curator.getLatestTag()));
			
			if(Curator.getBranchesNames().size() > 1) {
				for(String branch : Curator.getBranchesNames())
					params.add(new BasicNameValuePair("branch", branch));
				params.add(new BasicNameValuePair("multiple_branches", "true"));
			}
			else if(Curator.getBranchesNames().size() == 1) {
				for(String branch : Curator.getBranchesNames())
					params.add(new BasicNameValuePair("branch", branch));
				params.add(new BasicNameValuePair("multiple_branches", "false"));
			}
			else {
				params.add(new BasicNameValuePair("branch", ""));
				params.add(new BasicNameValuePair("multiple_branches", "false"));
			}
			
			params.add(new BasicNameValuePair("mainBranch", Curator.getFileContentFromMainBranch()));
			
			params.add(new BasicNameValuePair("branchName", ""));
			params.add(new BasicNameValuePair("branchContent", ""));
		}
		else {
			params.add(new BasicNameValuePair("nextPage", "curatorauth"));
			params.add(new BasicNameValuePair("message", "Not a valid curator email"));
		}
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	private void sendOtherBranchContent(String body) throws ClientProtocolException, IOException {
		String email = objectMapper.readTree(body)
				.get("data")
				.get("email")
				.asText();
	
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		String branchName = objectMapper.readTree(body)
				.get("data")
				.get("branchSelection")
				.asText();
		//System.out.println(branchName);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		
		params.add(new BasicNameValuePair("nextPage", "curator"));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("message", ""));
		params.add(new BasicNameValuePair("currversion", Curator.getLatestTag()));
		
		if(Curator.getBranchesNames().size() > 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "true"));
		}
		else if(Curator.getBranchesNames().size() == 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		else {
			params.add(new BasicNameValuePair("branch", ""));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		
		params.add(new BasicNameValuePair("mainBranch", Curator.getFileContentFromMainBranch()));
		
		params.add(new BasicNameValuePair("branchName", branchName));
		params.add(new BasicNameValuePair("branchContent", Curator.getFileContentFromBranch(branchName)));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	private void acceptContribution(String body) throws ClientProtocolException, IOException {
		String email = objectMapper.readTree(body)
				.get("data")
				.get("email")
				.asText();
		System.out.println("hello1");
		
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		System.out.println("hello2");
		
		String branchName = objectMapper.readTree(body)
				.get("data")
				.get("selectedBranch")
				.asText();
		System.out.println("hello3");
		
		String message = objectMapper.readTree(body)
				.get("data")
				.get("message")
				.asText();
		System.out.println("hello3");
		
		String version = objectMapper.readTree(body)
				.get("data")
				.get("nextversion")
				.asText();
		
		if(version.equals(Curator.getLatestTag()))
			Curator.acceptChange(email, branchName, message);
		else
			Curator.acceptChange(email, branchName, message,version);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("currversion", Curator.getLatestTag()));
		params.add(new BasicNameValuePair("id", id));
		
		params.add(new BasicNameValuePair("nextPage", "curator"));
		params.add(new BasicNameValuePair("email", email));
		
		if(Curator.getBranchesNames().size() > 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "true"));
		}
		else if(Curator.getBranchesNames().size() == 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		else {
			params.add(new BasicNameValuePair("branch", ""));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		
		params.add(new BasicNameValuePair("mainBranch", Curator.getFileContentFromMainBranch()));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Accepted"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	private void mixContribution(String body) throws ClientProtocolException, IOException {
		String email = objectMapper.readTree(body)
				.get("data")
				.get("email")
				.asText();
		System.out.println("hello1");
		
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		System.out.println("hello2");
		
		String branchName = objectMapper.readTree(body)
				.get("data")
				.get("selectedBranch")
				.asText();
		System.out.println("hello3");
		
		String message = objectMapper.readTree(body)
				.get("data")
				.get("message")
				.asText();
		
		String mixed = objectMapper.readTree(body)
				.get("data")
				.get("mixed")
				.asText();
		System.out.println("hello3");
		
		String version = objectMapper.readTree(body)
				.get("data")
				.get("nextversion")
				.asText();
		
		if(version.equals(Curator.getLatestTag()))
			Curator.mixChange(email, branchName, message, mixed);
		else
			Curator.mixChange(email, branchName, message, mixed, version);
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		params.add(new BasicNameValuePair("nextPage", "curator"));
		params.add(new BasicNameValuePair("email", email));
		
		if(Curator.getBranchesNames().size() > 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "true"));
		}
		else if(Curator.getBranchesNames().size() == 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		else {
			params.add(new BasicNameValuePair("branch", ""));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		
		params.add(new BasicNameValuePair("mainBranch", Curator.getFileContentFromMainBranch()));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Accepted With Changes"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	private void rejectContribution(String body) throws ClientProtocolException, IOException {
		String email = objectMapper.readTree(body)
				.get("data")
				.get("email")
				.asText();
		
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		
		String branchName = objectMapper.readTree(body)
				.get("data")
				.get("selectedBranch")
				.asText();
		
		String message = objectMapper.readTree(body)
				.get("data")
				.get("message")
				.asText();
		
		System.out.println(Curator.rejectChange(email, branchName, message));
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		params.add(new BasicNameValuePair("nextPage", "curator"));
		params.add(new BasicNameValuePair("email", email));
		
		if(Curator.getBranchesNames().size() > 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "true"));
		}
		else if(Curator.getBranchesNames().size() == 1) {
			for(String branch : Curator.getBranchesNames())
				params.add(new BasicNameValuePair("branch", branch));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		else {
			params.add(new BasicNameValuePair("branch", ""));
			params.add(new BasicNameValuePair("multiple_branches", "false"));
		}
		
		params.add(new BasicNameValuePair("mainBranch", Curator.getFileContentFromMainBranch()));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Rejected"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
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
