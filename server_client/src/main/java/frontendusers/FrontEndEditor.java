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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import ads.users.Editor;

/**
 * The treatment of editor requests should be done here
 * @author Susana Polido
 * @version 0.1
 */
public class FrontEndEditor extends FrontEndUser{

	public FrontEndEditor(String uri) {
		super(uri);
	}
	
	/**
	 * Responds to the frontend client when it just wants to enter the editor page
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void goToEditorPage(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
	
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		params.add(new BasicNameValuePair("page", "Editor Area"));
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	/**
	 * Completes a List<NameValuePair> object params with everything that is necessary for the edipor page
	 * so the code doesn't need to be repeated accross multiple methods
	 * <p>
	 * Uses the Editor class
	 * @param params that are to be completed
	 * @return the now more complete params object
	 * @since 0.1
	 */
	public List<NameValuePair> fillUpEditorPage(List<NameValuePair> params) {
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
		
		
		List<String> individuals = Editor.getNamedIndividuals();
		if(individuals == null || individuals.size() == 0) {
			params.add(new BasicNameValuePair("multiple_individuals", "false"));
			params.add(new BasicNameValuePair("individuals", ""));
		}
		else if(individuals.size() == 1) {
			params.add(new BasicNameValuePair("multiple_individuals", "false"));
			params.add(new BasicNameValuePair("individuals", individuals.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_individuals", "true"));
			for(String s : individuals)
				params.add(new BasicNameValuePair("individuals", s));
		}
		
		
		List<String> properties = Editor.getDataProperties();
		
		if(properties == null || properties.size() == 0) {
			params.add(new BasicNameValuePair("multiple_properties", "false"));
			params.add(new BasicNameValuePair("properties", ""));
		}
		else if(properties.size() == 1) {
			params.add(new BasicNameValuePair("multiple_properties", "false"));
			params.add(new BasicNameValuePair("properties", properties.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_properties", "true"));
			for(String s : properties) {
				params.add(new BasicNameValuePair("properties", s));
				System.out.println(s);
			}
		}
		return params;
	}
	
	/**
	 * Processes and replies to the frontend client when it wants to create a class
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void createClass(String  body) throws ClientProtocolException, IOException {
		String id = null;
		String name = null;
		String email = null;
		String commit = null;
		
		try {
			id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			name = objectMapper.readTree(body)
					.get("data")
					.get("name")
					.asText();
			email = objectMapper.readTree(body)
					.get("data")
					.get("email")
					.asText();
			commit = objectMapper.readTree(body)
					.get("data")
					.get("commitMessage")
					.asText();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String parent = null;
		try {
			String hasParent = objectMapper.readTree(body)
					.get("data")
					.get("isSubClass")
					.asText();
			if(hasParent.equals("on"))
			parent = objectMapper.readTree(body)
					.get("data")
					.get("parentClass")
					.asText();
		} catch(Exception e) {}
		String comment = null;
		try {
			String hasComment = objectMapper.readTree(body)
					.get("data")
					.get("addComment")
					.asText();
			if(hasComment.equals("on"))
			comment = objectMapper.readTree(body)
					.get("data")
					.get("comment")
					.asText();
		} catch(Exception e) {}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		try {
			Editor.createClass(name, parent, comment, email, commit);
			params.add(new BasicNameValuePair("message", "Operation successful"));
		} catch (OWLOntologyCreationException e) {
			params.add(new BasicNameValuePair("message", "Failed Operation"));
			e.printStackTrace();
		}
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	/**
	 * Processes and replies to the frontend client when it wants to create a named individual
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void createIndividual(String body) throws ClientProtocolException, IOException {
		String id = null;
		String name = null;
		String email = null;
		String commit = null;
		
		try {
			id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			name = objectMapper.readTree(body)
					.get("data")
					.get("name")
					.asText();
			email = objectMapper.readTree(body)
					.get("data")
					.get("email")
					.asText();
			commit = objectMapper.readTree(body)
					.get("data")
					.get("commitMessage")
					.asText();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String parent = null;
		try {
			String hasClass = objectMapper.readTree(body)
					.get("data")
					.get("isPartOfClass")
					.asText();
			if(hasClass.equals("on"))
			parent = objectMapper.readTree(body)
					.get("data")
					.get("parentClass")
					.asText();
		} catch(Exception e) {}
		String comment = null;
		try {
			String hasComment = objectMapper.readTree(body)
					.get("data")
					.get("addComment")
					.asText();
			if(hasComment.equals("on"))
			comment = objectMapper.readTree(body)
					.get("data")
					.get("comment")
					.asText();
		} catch(Exception e) {}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		try {
			Editor.createIndividual(name, parent, comment, email, commit);
			params.add(new BasicNameValuePair("message", "Operation successful"));
		} catch (OWLOntologyCreationException e) {
			params.add(new BasicNameValuePair("message", "Failed Operation"));
			e.printStackTrace();
		}
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	/**
	 * Processes and replies to the frontend client when it wants to create a data property
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void createDataProperty(String body) throws ClientProtocolException, IOException {
		String id = null;
		String name = null;
		String email = null;
		String commit = null;
		
		try {
			id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			name = objectMapper.readTree(body)
					.get("data")
					.get("name")
					.asText();
			email = objectMapper.readTree(body)
					.get("data")
					.get("email")
					.asText();
			commit = objectMapper.readTree(body)
					.get("data")
					.get("commitMessage")
					.asText();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String comment = null;
		try {
			String hasComment = objectMapper.readTree(body)
					.get("data")
					.get("addComment")
					.asText();
			if(hasComment.equals("on"))
			comment = objectMapper.readTree(body)
					.get("data")
					.get("comment")
					.asText();
		} catch(Exception e) {}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		try {
			Editor.createDataProperty(name, comment, email, commit);
			params.add(new BasicNameValuePair("message", "Operation successful"));
		} catch (OWLOntologyCreationException e) {
			params.add(new BasicNameValuePair("message", "Failed Operation"));
			e.printStackTrace();
		}
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	
	/**
	 * Processes and replies to the frontend client when it wants to delete things
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.1
	 */
	public void deleteStuff(String body) throws ClientProtocolException, IOException {
		String id = null;
		String name = null;
		String email = null;
		String commit = null;
		
		try {
			id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			email = objectMapper.readTree(body)
					.get("data")
					.get("email")
					.asText();
			commit = objectMapper.readTree(body)
					.get("data")
					.get("commitMessage")
					.asText();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String classToDelete = null;
		try {
			String deleteClass = objectMapper.readTree(body)
					.get("data")
					.get("deleteClass")
					.asText();
			if(deleteClass.equals("on"))
			classToDelete = objectMapper.readTree(body)
					.get("data")
					.get("classToDelete")
					.asText();
		} catch(Exception e) {}
		
		
		String individualToDelete = null;
		try {
			String deleteIndividual = objectMapper.readTree(body)
					.get("data")
					.get("deleteIndividual")
					.asText();
			if(deleteIndividual.equals("on"))
			individualToDelete = objectMapper.readTree(body)
					.get("data")
					.get("individualToDelete")
					.asText();
		} catch(Exception e) {}
		
		
		String propertyToDelete = null;
		try {
			String deleteDataProperty = objectMapper.readTree(body)
					.get("data")
					.get("deleteDataProperty")
					.asText();
			if(deleteDataProperty.equals("on"))
			propertyToDelete = objectMapper.readTree(body)
					.get("data")
					.get("propertyToDelete")
					.asText();
		} catch(Exception e) {}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		if(classToDelete != null || individualToDelete != null || propertyToDelete != null) {
			try {
				Editor.deleteStuff(classToDelete, individualToDelete, propertyToDelete, email, commit);
				params.add(new BasicNameValuePair("message", "Operation successful"));
			} catch (OWLOntologyCreationException e) {
				params.add(new BasicNameValuePair("message", "Failed Operation"));
				e.printStackTrace();
			}
		}
		else
			params.add(new BasicNameValuePair("message", "No Operation Sent"));
		
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}

}
