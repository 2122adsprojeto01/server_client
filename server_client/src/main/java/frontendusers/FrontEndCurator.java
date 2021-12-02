package frontendusers;

import java.io.IOException;
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

import ads.users.Curator;

/**
 * The treatment of curator requests should be done here
 * @author Susana Polido
 * @version 1
 */
public class FrontEndCurator extends FrontEndUser{

	public FrontEndCurator(String uri) {
		super(uri);
	}
	
	/**
	 * Used when someone is trying to enter the curator area
	 * <p>
	 * If the curator email is valid it sends them to the curator page, otherwise it warns them the email isn't valid
	 * <p>
	 * Uses the Curator class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void sendCuratorIsValidResponse(String body) throws ClientProtocolException, IOException {
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
			addCuratorParameters(params);
			params.add(new BasicNameValuePair("email", email));
			params.add(new BasicNameValuePair("message", ""));
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
	
	/**
	 * Completes a List<NameValuePair> object params with everything that is commom with all curator actions/pages
	 * so the code doesn't need to be repeated accross multiple methods
	 * <p>
	 * Uses the Curator class
	 * @param params that are to be completed
	 * @return the now more complete params object
	 * @since 0.1
	 */
	public List<NameValuePair> addCuratorParameters(List<NameValuePair> params){
		params.add(new BasicNameValuePair("nextPage", "curator"));
		
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
		
		return params;
	}
	
	
	/**
	 * Used to reply when a curator asks to see the content of a specific branch
	 * <p>
	 * Uses the Curator class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void sendOtherBranchContent(String body) throws ClientProtocolException, IOException {
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
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		addCuratorParameters(params);
		params.add(new BasicNameValuePair("email", email));
		
		params.add(new BasicNameValuePair("message", ""));
		params.add(new BasicNameValuePair("branchName", branchName));
		params.add(new BasicNameValuePair("branchContent", Curator.getFileContentFromBranch(branchName)));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	/**
	 * Used when a curator accepts a contribution
	 * <p>
	 * Uses the Curator class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void acceptContribution(String body) throws ClientProtocolException, IOException {
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
		
		String version = objectMapper.readTree(body)
				.get("data")
				.get("nextversion")
				.asText();
		
		if(version.equals(Curator.getLatestTag()))
			Curator.acceptChange(email, branchName, message);
		else
			Curator.acceptChange(email, branchName, message,version);
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		
		addCuratorParameters(params);
		params.add(new BasicNameValuePair("email", email));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Accepted"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	
	
	/**
	 * Used when a curator changes a contribution and merges it to the main
	 * <p>
	 * Uses the Curator class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void mixContribution(String body) throws ClientProtocolException, IOException {
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
		
		addCuratorParameters(params);
		params.add(new BasicNameValuePair("email", email));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Accepted With Changes"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
	/**
	 * Used when a curator rejects a contribution
	 * <p>
	 * Uses the Curator class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void rejectContribution(String body) throws ClientProtocolException, IOException {
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
		
		addCuratorParameters(params);
		params.add(new BasicNameValuePair("email", email));
		
		params.add(new BasicNameValuePair("branchName", ""));
		params.add(new BasicNameValuePair("branchContent", ""));
		params.add(new BasicNameValuePair("message", "Contribution Rejected"));
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		//HttpEntity entity = response2.getEntity();
	}
	
}
