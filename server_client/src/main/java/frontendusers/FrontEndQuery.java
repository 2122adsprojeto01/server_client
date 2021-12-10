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

import ads.users.Editor;
import ads.users.Query;

public class FrontEndQuery extends FrontEndUser{

	public FrontEndQuery(String uri) {
		super(uri);
	}
	
	public void getQueryPageContent(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "queries"));
		params.add(new BasicNameValuePair("answers", ""));
		params.add(new BasicNameValuePair("multiple_answers", "false"));
		
		params = fillUpQueryPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	public void processFormQuery(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "queries"));
		
		
		params = fillUpQueryPage(params);
		
		String classToQuery = objectMapper.readTree(body)
				.get("data")
				.get("classToQuery")
				.asText();
		String dataProperty1 = objectMapper.readTree(body)
				.get("data")
				.get("dataProperty1")
				.asText();
		String operator1 = objectMapper.readTree(body)
				.get("data")
				.get("operator1")
				.asText();		
		String value1 = objectMapper.readTree(body)
				.get("data")
				.get("value1")
				.asText();
		
		String queryString = null;
		

		try {
			String complexQuery = objectMapper.readTree(body)
					.get("data")
					.get("complexQuery")
					.asText();
			if(complexQuery.equals("on")) {
				String connection = objectMapper.readTree(body)
						.get("data")
						.get("connection")
						.asText();
				String dataProperty2 = objectMapper.readTree(body)
						.get("data")
						.get("dataProperty2")
						.asText();
				String operator2 = objectMapper.readTree(body)
						.get("data")
						.get("operator1")
						.asText();		
				String value2 = objectMapper.readTree(body)
						.get("data")
						.get("value1")
						.asText();
				queryString = queryString = Query.prepareQuery(classToQuery, dataProperty1, operator1, value1,
						connection, dataProperty2, operator2, value2);
			}
		} catch(Exception e) {}
		
		if(queryString == null)
			queryString = Query.prepareQuery(classToQuery, dataProperty1, operator1, value1);
		
		List<String> answers = Query.getQueryResult(queryString);
		if(answers == null || answers.size() == 0) {
			params.add(new BasicNameValuePair("multiple_answers", "false"));
			params.add(new BasicNameValuePair("answers", ""));
		}
		else if(answers.size() == 1) {
			params.add(new BasicNameValuePair("multiple_answers", "false"));
			params.add(new BasicNameValuePair("answers", answers.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_answers", "true"));
			for(String s : answers)
				params.add(new BasicNameValuePair("classes", s));
		}
		
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		System.out.println("send reply to form query");
	}
	
	public void processStringQuery(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "queries"));
		
		
		params = fillUpQueryPage(params);
		System.out.println("filled the parameters");
		String queryString = objectMapper.readTree(body)
				.get("data")
				.get("queryToMake")
				.asText();
		
		System.out.println("reached before trying to get the results");
		List<String> answers = Query.getQueryResult(queryString);
		if(answers == null || answers.size() == 0) {
			params.add(new BasicNameValuePair("multiple_answers", "false"));
			params.add(new BasicNameValuePair("answers", ""));
		}
		else if(answers.size() == 1) {
			params.add(new BasicNameValuePair("multiple_answers", "false"));
			params.add(new BasicNameValuePair("answers", answers.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_answers", "true"));
			for(String s : answers)
				params.add(new BasicNameValuePair("classes", s));
		}
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
		System.out.println("send reply to string query");
	}
	
	public List<NameValuePair> fillUpQueryPage(List<NameValuePair> params) {
		List<String> classes = Editor.getClasses();
		if(classes == null || classes.size() == 0) {
			params.add(new BasicNameValuePair("multiple_classes", "false"));
			params.add(new BasicNameValuePair("classes", ""));
		}
		else if(classes.size() == 1) {
			params.add(new BasicNameValuePair("multiple_classes", "false"));
			params.add(new BasicNameValuePair("classes", classes.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_classes", "true"));
			for(String s : classes)
				params.add(new BasicNameValuePair("classes", s));
		}
		
		List<String> dataProperties = Editor.getDataProperties();
		
		if(dataProperties == null || dataProperties.size() == 0) {
			params.add(new BasicNameValuePair("multiple_data_properties", "false"));
			params.add(new BasicNameValuePair("dataProperties", ""));
		}
		else if(dataProperties.size() == 1) {
			params.add(new BasicNameValuePair("multiple_data_properties", "false"));
			params.add(new BasicNameValuePair("dataProperties", dataProperties.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_data_properties", "true"));
			for(String s : dataProperties) {
				params.add(new BasicNameValuePair("dataProperties", s));
				System.out.println(s);
			}
		}
		return params;
	}

}
