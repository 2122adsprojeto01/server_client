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
 * @version 0.3
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
		
		
		List<String> objectProperties = Editor.getObjectProperties();
		
		if(objectProperties == null || objectProperties.size() == 0) {
			params.add(new BasicNameValuePair("multiple_object_properties", "false"));
			params.add(new BasicNameValuePair("objectProperties", ""));
		}
		else if(objectProperties.size() == 1) {
			params.add(new BasicNameValuePair("multiple_object_properties", "false"));
			params.add(new BasicNameValuePair("objectProperties", objectProperties.get(0)));
		}
		else {
			params.add(new BasicNameValuePair("multiple_object_properties", "true"));
			for(String s : objectProperties) {
				params.add(new BasicNameValuePair("objectProperties", s));
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
	 * Processes and replies to the frontend client when it wants to create an object property
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.2
	 */
	public void createObjectProperty(String body) throws ClientProtocolException, IOException {
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
		
		boolean transitive = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isTransitive")
					.asText();
			if(isOn.equals("on"))
			transitive = true;
		} catch(Exception e) {}
		
		boolean symmetric = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isSymetric")
					.asText();
			if(isOn.equals("on"))
			symmetric = true;
		} catch(Exception e) {}
		
		boolean reflexive = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isReflexive")
					.asText();
			if(isOn.equals("on"))
			reflexive = true;
		} catch(Exception e) {}
		
		boolean irreflexive = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isIrreflexive")
					.asText();
			if(isOn.equals("on"))
			irreflexive = true;
		} catch(Exception e) {}
		
		boolean inverseFunctional = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isInverseFunctional")
					.asText();
			if(isOn.equals("on"))
			inverseFunctional = true;
		} catch(Exception e) {}
		
		boolean asymmetric = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isAsymetric")
					.asText();
			if(isOn.equals("on"))
			asymmetric = true;
		} catch(Exception e) {}
		
		boolean functional = false;
		try {
			String isOn = objectMapper.readTree(body)
					.get("data")
					.get("isFunctional")
					.asText();
			if(isOn.equals("on"))
			functional = true;
		} catch(Exception e) {}
		
		try {
			Editor.createObjectProperty(name, comment, email, commit, transitive, symmetric, reflexive, irreflexive, inverseFunctional, asymmetric, functional);
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
		
		
		String dataPropertyToDelete = null;
		try {
			String deleteDataProperty = objectMapper.readTree(body)
					.get("data")
					.get("deleteDataProperty")
					.asText();
			if(deleteDataProperty.equals("on"))
			dataPropertyToDelete = objectMapper.readTree(body)
					.get("data")
					.get("dataPropertyToDelete")
					.asText();
		} catch(Exception e) {}
		
		String objectPropertyToDelete = null;
		try {
			String deleteObjectProperty = objectMapper.readTree(body)
					.get("data")
					.get("deleteObjectProperty")
					.asText();
			if(deleteObjectProperty.equals("on"))
			objectPropertyToDelete = objectMapper.readTree(body)
					.get("data")
					.get("objectPropertyToDelete")
					.asText();
		} catch(Exception e) {}
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		if(classToDelete != null || individualToDelete != null || dataPropertyToDelete != null || objectPropertyToDelete != null) {
			try {
				Editor.deleteStuff(classToDelete, individualToDelete, dataPropertyToDelete, objectPropertyToDelete, email, commit);
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
	
	/**
	 * Processes and replies to the frontend client when it wants to edit a data property
	 * It only retrieves the information of the picked data property
	 * It doesn't change the data property
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.3
	 */
	public void editDataPropertyPage(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		String name = objectMapper.readTree(body)
				.get("data")
				.get("dataPropertyToEdit")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editDataProperty"));
		params.add(new BasicNameValuePair("dataProperty", name));
		
		if(Editor.dataPropertyHasComments(name)) {
			params.add(new BasicNameValuePair("hasComment", "true"));
			params.add(new BasicNameValuePair("comment", Editor.getdataPropertyComment(name)));
		}
		else {
			params.add(new BasicNameValuePair("comment", "false"));
			params.add(new BasicNameValuePair("comment", ""));
		}
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	
	/**
	 * Processes the changes to a data property a client asked for
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.3
	 */
	public void changeDataProperty(String body) throws ClientProtocolException, IOException {
		String id = null;
		String email = null;
		String commit = null;
		String oldName = null;
		
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
			oldName = objectMapper.readTree(body)
					.get("data")
					.get("oldName")
					.asText();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String newName = null;
		try {
			String changeName = objectMapper.readTree(body)
					.get("data")
					.get("changeName")
					.asText();
			if(changeName.equals("on")) {
				newName = objectMapper.readTree(body)
						.get("data")
						.get("newName")
						.asText();
			}	
		} catch(Exception e) {}
		
		boolean deleteComment = false;
		try {
			String deleteCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("deleteComment")
					.asText();
			if(deleteCommentIsOn.equals("on"))
				deleteComment = true;
		} catch(Exception e) {}
		
		
		boolean addComment = false;
		try {
			String addCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("deleteComment")
					.asText();
			if(addCommentIsOn.equals("on"))
				addComment = true;
		} catch(Exception e) {}
		
		boolean changeComment = false;
		try {
			String changeCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("changeComment")
					.asText();
			if(changeCommentIsOn.equals("on"))
				changeComment = true;
		} catch(Exception e) {}
		
		
		String newComment = null;
		if(changeComment || addComment) {
			try {
				newComment = objectMapper.readTree(body)
						.get("data")
						.get("newComment")
						.asText();
			} catch(Exception e) {}
		}
		
		if(changeComment)
			deleteComment = true;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		boolean result = false;
		try {
			result = Editor.changeDataProperty(commit, email, oldName, newName, deleteComment, newComment);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result)
			params.add(new BasicNameValuePair("message", "Operation Successful"));
		else
			params.add(new BasicNameValuePair("message", "Failed Operation"));
		
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	/**
	 * Retrieved the information about an object property so the client can then ask for changes
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.3
	 */
	public void editObjectPropertyPage(String body) throws ClientProtocolException, IOException {
		String id = objectMapper.readTree(body)
				.get("client_id")
				.asText();
		String name = objectMapper.readTree(body)
				.get("data")
				.get("objectPropertyToEdit")
				.asText();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editObjectProperty"));
		params.add(new BasicNameValuePair("objectProperty", name));
		
		if(Editor.objectPropertyHasComments(name)) {
			params.add(new BasicNameValuePair("hasComment", "true"));
			params.add(new BasicNameValuePair("comment", Editor.getObjectPropertyComment(name)));
		}
		else {
			params.add(new BasicNameValuePair("comment", "false"));
			params.add(new BasicNameValuePair("comment", ""));
		}
		
		params.add(new BasicNameValuePair("transitive", ""+Editor.getObjectPropertyIsTransitive(name)));
		params.add(new BasicNameValuePair("asymmetric", ""+Editor.getObjectPropertyIsAsymmetric(name)));
		params.add(new BasicNameValuePair("symmetric", ""+Editor.getObjectPropertyIsSymmetric(name)));
		params.add(new BasicNameValuePair("reflexive", ""+Editor.getObjectPropertyIsReflexive(name)));
		params.add(new BasicNameValuePair("irreflexive", ""+Editor.getObjectPropertyIsIrreflexive(name)));
		params.add(new BasicNameValuePair("functional", ""+Editor.getObjectPropertyIsFunctional(name)));
		params.add(new BasicNameValuePair("inverseFunctional", ""+Editor.getObjectPropertyIsInverseFunctional(name)));

		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}
	
	/**
	 * Processes the changes to an object property a client asked for
	 * @param body of the received message
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @since 0.3
	 */
	public void changeObjectProperty(String body) throws ClientProtocolException, IOException {
		String id = null;
		String email = null;
		String commit = null;
		String oldName = null;
		
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
			oldName = objectMapper.readTree(body)
					.get("data")
					.get("oldName")
					.asText();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		String newName = null;
		try {
			String changeName = objectMapper.readTree(body)
					.get("data")
					.get("changeName")
					.asText();
			if(changeName.equals("on")) {
				newName = objectMapper.readTree(body)
						.get("data")
						.get("newName")
						.asText();
			}	
		} catch(Exception e) {}
		
		
		boolean transitive = false;
		try {
			transitive = objectMapper.readTree(body).get("data").get("isTransitive").asText().equals("on");
		} catch(Exception e) {}
		
		boolean asymmetric = false;
		try {
			asymmetric = objectMapper.readTree(body).get("data").get("isAsymmetric").asText().equals("on");
		} catch(Exception e) {}
		
		boolean symmetric = false;
		try {
			symmetric = objectMapper.readTree(body).get("data").get("isSymmetric").asText().equals("on");
		} catch(Exception e) {}
		
		boolean reflexive = false;
		try {
			reflexive = objectMapper.readTree(body).get("data").get("isReflexive").asText().equals("on");
		} catch(Exception e) {}
		
		boolean irreflexive = false;
		try {
			irreflexive = objectMapper.readTree(body).get("data").get("isIrreflexive").asText().equals("on");
		} catch(Exception e) {}
		
		boolean functional = false;
		try {
			functional = objectMapper.readTree(body).get("data").get("isFunctional").asText().equals("on");
		} catch(Exception e) {}
		
		boolean inverseFunctional = false;
		try {
			inverseFunctional = objectMapper.readTree(body).get("data").get("isInverseFunctional").asText().equals("on");
		} catch(Exception e) {}
		
		boolean deleteComment = false;
		try {
			String deleteCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("deleteComment")
					.asText();
			if(deleteCommentIsOn.equals("on"))
				deleteComment = true;
		} catch(Exception e) {}
		
		
		boolean addComment = false;
		try {
			String addCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("deleteComment")
					.asText();
			if(addCommentIsOn.equals("on"))
				addComment = true;
		} catch(Exception e) {}
		
		boolean changeComment = false;
		try {
			String changeCommentIsOn = objectMapper.readTree(body)
					.get("data")
					.get("changeComment")
					.asText();
			if(changeCommentIsOn.equals("on"))
				changeComment = true;
		} catch(Exception e) {}
		
		
		String newComment = null;
		if(changeComment || addComment) {
			try {
				newComment = objectMapper.readTree(body)
						.get("data")
						.get("newComment")
						.asText();
			} catch(Exception e) {}
		}
		
		if(changeComment)
			deleteComment = true;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
		boolean result = false;
		try {
			result = Editor.changeObjectProperty(commit, email, oldName, newName, deleteComment, newComment, transitive, asymmetric, symmetric, 
					reflexive, irreflexive, functional, inverseFunctional);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(result)
			params.add(new BasicNameValuePair("message", "Operation Successful"));
		else
			params.add(new BasicNameValuePair("message", "Failed Operation"));
		
		params.add(new BasicNameValuePair("id", id));
		params.add(new BasicNameValuePair("nextPage", "editor"));
		
		params = fillUpEditorPage(params);
		
		HttpPost httppost = new HttpPost(uri+"/server_client_post");
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response2 = httpclient.execute(httppost);
	}

}
