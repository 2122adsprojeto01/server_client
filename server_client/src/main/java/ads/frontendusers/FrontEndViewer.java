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
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ads.users.Viewer;

public class FrontEndViewer extends FrontEndUser{

	public FrontEndViewer(String uri) {
		super(uri);
	}
	

	public void getViewPageContent(String body) {
		try {
			String id = objectMapper.readTree(body)
					.get("client_id")
					.asText();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", id));
			params.add(new BasicNameValuePair("nextPage", "viewer"));
			
			
			//JSONObject tabelas = new JSONObject().put("nome", new JSONObject().put("hello", "world"));
			JSONArray tabelaClasses = Viewer.getClasses();
			JSONArray tabelaIndividuals = Viewer.getIndividuals();
			JSONArray tabelaObjectProperties = Viewer.getObjectProperties();
			
			
			params.add(new BasicNameValuePair("tabelaClasses", tabelaClasses.toString()));
			params.add(new BasicNameValuePair("tabelaIndividuals", tabelaIndividuals.toString()));
			params.add(new BasicNameValuePair("tabelaObjectProperties", tabelaObjectProperties.toString()));
			
			HttpPost httppost = new HttpPost(uri+"/server_client_post");
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response2 = httpclient.execute(httppost);
		} catch (IOException | OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
