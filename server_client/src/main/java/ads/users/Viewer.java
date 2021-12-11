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
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;
import ads.knowledgebase.OWLInteraction;

public class Viewer {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
	public static JSONObject getClasses() throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		System.out.println(repository);
		System.out.println(file);
		JSONArray jsonClasses = new JSONArray(); //Array que irá ter as classes do owl

		List<String> classes = owl.getClasses(); //Lista de classes no owl
		for(String s : classes) { //Para cada classe
			JSONObject newClass = new JSONObject().put("name", s); //Cria objeto da classe e adiciona o par com nome: <nome da classe>
			if(!owl.getParentClasses(s).isEmpty()){ //Se tiver parent classes
				for(String t : owl.getParentClasses(s)){ //Para cada parent class
					newClass.put("parentClass", t); //adiciona o par parentClass: <nome da parent class>
				}
			}
			jsonClasses.put(newClass); //Acrescenta a classe ao Array
		}

		return new JSONObject().put("classes", jsonClasses); //retorna classes: <Array com as classes>
	}
	public static JSONObject getIndividuals() throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		JSONArray jsonIndividuals = new JSONArray();

		List<String> individuals = owl.getNamedIndividuals();
		for(String i : individuals){ //Para cada individuo
			String ind = owl.getNamedIndividualClass(i); //Nome da classe a que pertence
			JSONObject jsonIndividuo = new JSONObject().put("name", i).put("classe", ind); //Adiciona os pares name: <nome do individuo>, classe: <nome da classe a que pertence>
			JSONArray indDataProps = new JSONArray(); //Array que irá conter as data properties do individuo
			JSONArray indObjProps = new JSONArray(); //Array que irá conter as object properties do individuo

			Map<String,String> aux = owl.getNamedIndividualDataProperties(i);
			for (Map.Entry<String, String> entry : aux.entrySet()) { //Para cada Data propertie do individuo
				indDataProps.put(new JSONObject().put(entry.getKey(), entry.getValue())); //Adiciona ao array o par <key>: <valor>
			}
			Map<String,String> aux2 = owl.getNamedIndividualObjectProperties(i);
			for (Map.Entry<String, String> entry : aux2.entrySet()) {
				indObjProps.put(new JSONObject().put(entry.getKey(), entry.getValue()));
			}

			jsonIndividuo.put("dataProperties", indDataProps); //acrescenta ao individuo as data properties
			jsonIndividuo.put("objectProperties", indObjProps); //acrescenta ao individuo as object properties
			jsonIndividuals.put(jsonIndividuo); //acrescenta o individuo a lista de individuos
		}

		return new JSONObject().put("individuals", jsonIndividuals); //retorna individuals: <Array com individuos>
		
	}
	public static JSONObject getObjectProperties() throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		JSONArray jsonObjProps = new JSONArray();

		List<String> objProps = owl.getObjectProperties();
		for(String s : objProps){
			JSONObject jsonObj = new JSONObject().put("name", s);

			jsonObj.put("transitive", owl.getObjectPropertyIsTransitive(s));
			jsonObj.put("symmetric", owl.getObjectPropertyIsSymmetric(s));
			jsonObj.put("reflexive", owl.getObjectPropertyIsReflexive(s));
			jsonObj.put("irreflexive", owl.getObjectPropertyIsIrreflexive(s));
			jsonObj.put("inverseFunctional", owl.getObjectPropertyIsInverseFunctional(s));
			jsonObj.put("functional", owl.getObjectPropertyIsFunctional(s));
			jsonObj.put("asymmetric", owl.getObjectPropertyIsAsymmetric(s));
			
			jsonObjProps.put(jsonObj);
		}
		
		return new JSONObject().put("objectProperties", jsonObjProps);
	}

	public static void main(String[] args) throws IOException, InterruptedException, OWLOntologyCreationException {
		System.out.println(Viewer.getClasses());
		System.out.println(Viewer.getIndividuals());
		System.out.println(Viewer.getObjectProperties());
	}
	
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
