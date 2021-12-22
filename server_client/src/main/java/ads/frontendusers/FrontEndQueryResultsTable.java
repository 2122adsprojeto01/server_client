package ads.frontendusers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import ads.users.QueryResultsTable;

/**
 * Class that creates and sends to the frontend the request with the results of a query
 * @author Susana Polido
 * @version 1
 *
 */
public class FrontEndQueryResultsTable extends FrontEndUser{

	public FrontEndQueryResultsTable(String uri) {
		super(uri);
	}
	
	/**
	 * This method is called by the FrontEndBridge when it retrieved a message with a string query
	 * <p>
	 * This method is for testing purposes only since ideally the query request is passed through
	 * a form that doesn't require the frontend user to know how to manually write a SQWRL query
	 * 
	 * @param body of the request retrieved from the frontend that contains the client id and the query to be processed
	 */
	public void getQueryResultAsTable(String body) {
		System.out.println("hello");
		try {
			String id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			
			String query = objectMapper.readTree(body)
					.get("data")
					.get("queryToMake")
					.asText();
			
			System.out.println(query);
			getQueryResultAsTable(id, query);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * This method should be called by either the Query or the FrontEndQuery class
	 * so a frontend user can use a friendly for those who don't know the SQWRL api form
	 * and the resulting data gets processed by the those classes before it gets to this method which
	 * class the necessary methods to process the SQWRL friendly String query
	 * <p>
	 * For testing purposes, the other method in this class calls this one
	 * @param client_id String with the id of the client that requested the query
	 * @param query String formated to be run through the SQWRL api
	 */
	public void getQueryResultAsTable(String client_id, String query) {
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", client_id));
			params.add(new BasicNameValuePair("nextPage", "queryResultsTable"));
			
			
			JSONArray results = QueryResultsTable.getQueryResults(query);

			if(results != null && !results.isEmpty()) {
				JSONObject object = (JSONObject) results.get(0);
				for(String s : object.keySet())
					params.add(new BasicNameValuePair("fields", s));
				
				if(object.keySet().size() > 1)
					params.add(new BasicNameValuePair("multiple_fields", "true"));
				else
					params.add(new BasicNameValuePair("multiple_fields", "false"));
			}
			else {
				params.add(new BasicNameValuePair("fields", ""));
				params.add(new BasicNameValuePair("multiple_fields", "false"));
			}
			
			params.add(new BasicNameValuePair("queryResultsTable", results.toString()));
			
			HttpPost httppost = new HttpPost(uri+"/server_client_post");
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response2 = httpclient.execute(httppost);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
