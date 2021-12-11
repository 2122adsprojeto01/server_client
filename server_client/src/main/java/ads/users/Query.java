package ads.users;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;
import ads.knowledgebase.OWLInteraction;

public class Query {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
	public static List<String> getQueryResult(String query){
		System.out.println("hello?");
		System.out.println(query);
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(owl.getOntology());
			SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);
			List<String> results = new ArrayList<>();
			while (result.next()) {
		    	  results.add(result.getNamedIndividual("ind").getShortName());
		      }
			return results;
		} catch (SQWRLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SWRLParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static String prepareQuery(String classToQuery, String dataProperty1, String operator1, String value1,
			String connection, String dataProperty2, String operator2, String value2) {
		String o1 = getOperator(operator1);
		String o2 = getOperator(operator2);
		
		if(connection.equals("and")) {
			String query = classToQuery + "(?ind) ^ " + dataProperty1 + "(?ind,?value1) ^ swrlb:" + o1 + "(?value1," + value1 + ")";
			query = query + dataProperty2 + "(?ind,?value2) ^ swrlb:" + o2 + "(?value2," + value2 + ")";
			query = query + " -> sqwrl:select(?ind) ^ sqwrl:orderBy(?ind)";
			return query;
		}
		else {
			//Couldn't find an or operator for swirl
			return "";
		}
	}
	
	public static String prepareQuery(String classToQuery, String dataProperty1, String operator1, String value1) {
		String operator = getOperator(operator1);
		String query = classToQuery + "(?ind) ^ " + dataProperty1 + "(?ind,?value1) ^ swrlb:" + operator + "(?value1," + value1 + ")" + " -> sqwrl:select(?ind) ^ sqwrl:orderBy(?ind)";
		return query;
	}
	
	private static String getOperator(String operator) {
		switch(operator) {
			case "==" :
				return "equal";
			case "!=":
				return "notEqual";
			case "<":
				return "lessThan";
			case "<=":
				return "lessThanOrEqual";
			case ">":
				return "greaterThan";
			case ">=":
				return "greaterThanOrEqual";
			default:
				return null;
		}
	}
	
	public static void main(String[] args)
	  {
		File owlFile = new File("ADS.owl");
	    try {
	      // Loading an OWL ontology using the OWLAPI
	      OWLOntologyManager ontologyManager = OWLManager.createOWLOntologyManager();
	      OWLOntology ontology =  ontologyManager.loadOntologyFromOntologyDocument(owlFile);

	      // Create SQWRL query engine using the SWRLAPI
	      SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

	      // Create and execute a SQWRL query using the SWRLAPI
	      // Os algoritmos que funcionam bem para problemas com 2,3,4,etc. objetivps são diferentes
	      
	      
	      
	      String numberOfObjectives = "2";
	      String query = "Algorithm(?alg) ^ "   
	    	+ "minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,"+numberOfObjectives+")"
	    	+ "maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,"+numberOfObjectives+")"
	    	+ " -> sqwrl:select(?alg) ^ sqwrl:orderBy(?alg)";  
	      
	      System.out.println(query);
	      query = prepareQuery("Character", "familyName", "==", "holm");
	      System.out.println(query);
	      SQWRLResult result = queryEngine.runSQWRLQuery("q1", query);
	      query = "Algorithm(?ind) ^ minObjectivesAlgorithmIsAbleToDealWith(?ind,?value1) ^ swrlb:lessThanOrEqual(?value1,2)maxObjectivesAlgorithmIsAbleToDealWith(?ind,?value2) ^ swrlb:lessThanOrEqual(?value2,2) -> sqwrl:select(?ind) ^ sqwrl:orderBy(?ind)";
	      //System.out.println(query);
	      //List<String> results = getQueryResult(query);
	      /*System.out.println(results);
	      // Process the SQWRL result
		  System.out.println("Query: \n" + query + "\n");
		  System.out.println("Result: ");
	      while (result.next()) {
	    	  //System.out.println(result.getNamedIndividual("ind").getShortName());
	    	  System.out.println(result);
	      }*/
	      
	    } catch (OWLOntologyCreationException e) {
	      System.err.println("Error creating OWL ontology: " + e.getMessage());
	      System.exit(-1);
	    } catch (SWRLParseException e) {
	      System.err.println("Error parsing SWRL rule or SQWRL query: " + e.getMessage());
	      System.exit(-1);
	    } catch (SQWRLException e) {
	      System.err.println("Error running SWRL rule or SQWRL query: " + e.getMessage());
	      System.exit(-1);
	    } catch (RuntimeException e) {
	      System.err.println("Error starting application: " + e.getMessage());
	      System.exit(-1);
	    }
	  }
}
