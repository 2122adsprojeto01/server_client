package ads.users;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;
import ads.knowledgebase.OWLInteraction;

public class Viewer {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
	public static JSONObject getClasses() {}
	public static JSONObject getIndividuals() {}
	public static JSONObject getObjectProperties() {}
	
	/*public static JSON getOntologyClassesAndIndividuals() {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		List<String> classes = owl.getClasses();
		for(String s : classes)
			owl.getParentClasses(s);
		
		List<String> individuals = owl.getNamedIndividuals();
		for(String i : individuals) {
			owl.getNamedIndividualClass(i);
			
			//Nome da data property e valor
			Map<String, String>  aux = owl.getNamedIndividualDataProperties(i);
			
			//Nome da object property e o nome do outro objecto associado
			Map<String, String>  aux2 = owl.getNamedIndividualObjectProperties(i);
		}
		
		List<String> objectProperties = owl.getObjectProperties();
		for(String i : objectProperties) {
			boolean transitive = owl.getObjectPropertyIsTransitive(i);
		}
		
		tableOP {
			nome do objeto{
			nome do que pode serr : true/false
			x 7}
		}
		
		tableIndivduo {
			nome do individuo{
			classe = ;
			datapropriedade {
				nome:;
				valor;
			} x muitos
			objectproperties: {
				nome:;
				individuo associado:;
			} x muitos}
		}
		
		tableClasse {
			nome da classe{
			nome da classe pai se existir }
		}
		
		new JSONObject().put("nome", new JSONObject().put()
		
		
	}*/
	
	
}
