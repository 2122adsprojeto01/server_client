package ads.users;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.swrlapi.sqwrl.SQWRLResult;
import org.swrlapi.sqwrl.exceptions.SQWRLException;

import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;
import ads.knowledgebase.OWLInteraction;

/**
 * A class to process a query
 * <p>
 * Made for the individual part of the evaluation
 * @author Susana Polido
 * @version 1
 */
public class QueryResultsTable {

	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
	/**
	 * This method received a String query and returns an JSONArray. In case of error, that JSONArray should be empty.
	 * <p>
	 * This class first retrieves the knowledge base from the remote repository,
	 * then used the SQWRL api to calculate the result of the query.
	 * Finally it goes through all the results attempting to turn each result in the SQWRLResult into a JSONObject
	 * that then gets added to a JSONArray
	 * @param query SQWRL query valid string
	 * @return the SQWRL result as JSONArray
	 * @since 1
	 */
	public static JSONArray getQueryResults(String query){
		JSONArray treatedResults = new JSONArray();
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			/*OWLInteraction owl = null;
			try {
				owl = new OWLInteraction(new FileInputStream(new File("ADS.owl")));
			} catch (FileNotFoundException e6) {
				// TODO Auto-generated catch block
				e6.printStackTrace();
			}*/
			
			
			SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(owl.getOntology());
			SQWRLResult results = queryEngine.runSQWRLQuery("q1", query);
			//List<String> results = new ArrayList<>();
			while (results.next()) {
				JSONObject result = new JSONObject();
				for(int i = 0; i < results.getNumberOfColumns(); i++) {
					String column = results.getColumnName(i);
					String value = "";
					
					try {
						value = results.getDataProperty(i).getShortName();
					} catch (SQWRLException e) {
						try {
							value = results.getDataProperty(i).toString();
						} catch (SQWRLException e1) {
							try {
								value = results.getClass(i).getShortName();
							} catch (SQWRLException e2) {
								try {
									value = results.getNamedIndividual(i).getShortName();
								} catch (SQWRLException e3) {
									try {
										value = results.getAnnotationProperty(i).getShortName();
									} catch (SQWRLException e4) {
										try {
											value = results.getObjectProperty(i).getShortName();
										} catch (SQWRLException e5) {
											try {
												value = results.getLiteral(i).getValue();
											} catch (SQWRLException e6) {
												try {
													value = results.getLiteral(i).getString();
												} catch (SQWRLException e7) {
													value = "something went wrong";
												}
											}
										}
									}
								}
							}
						}
					}
					if(value.charAt(0) == ':')
						result.put(column, value.substring(1));
					else
						result.put(column, value);
				}
				treatedResults.put(result);
		    }
			return treatedResults;
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
		return treatedResults;
	}
	
	public static void main(String[] args) {
		String numberOfObjectives = "2";
		String query = "Algorithm(?alg) ^ "   
    	+ "minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ swrlb:lessThanOrEqual(?min,"+numberOfObjectives+")"
    	+ "maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max) ^ swrlb:greaterThanOrEqual(?max,"+numberOfObjectives+")"
    	+ " -> sqwrl:select(?alg) ^ sqwrl:orderBy(?alg)";
		System.out.println(QueryResultsTable.getQueryResults(query));
		
		String query2 = "Character(?c) ^ familyName(?c, \"Holm\") -> sqwrl:select(?c) ^ sqwrl:columnNames(\"Name\")";	
		System.out.println(QueryResultsTable.getQueryResults(query2));
		
		String query3 = "Character(?c) ^ familyName(?c, \"Holm\") ^ personalName(?c, ?n) -> sqwrl:select(?c, ?n) ^ sqwrl:columnNames(\"ID\", \"Name\")";	
		System.out.println(QueryResultsTable.getQueryResults(query3));
		
		
		JSONArray results = QueryResultsTable.getQueryResults(query);

		if(results != null && !results.isEmpty()) {
			JSONObject object = (JSONObject) results.get(0);
			if(object.keySet().size()>1)
				System.out.println("multiple columns");
			else
				System.out.println("just one key");
			for(String s : object.keySet())
				System.out.println(s);
		}
		
		String query4 = "Algorithm(?alg) ^ minObjectivesAlgorithmIsAbleToDealWith(?alg,?min) ^ maxObjectivesAlgorithmIsAbleToDealWith(?alg,?max)  -> sqwrl:select(?alg, ?min, ?max) ^ sqwrl:orderBy(?alg)";	
		System.out.println(QueryResultsTable.getQueryResults(query4));
		
		String query5 = "isTwin(?c1, ?c2)  -> sqwrl:select(?c1, ?c2) ^ sqwrl:columnNames(\"Sibling A\", \"Sibling B\")";	
		System.out.println(QueryResultsTable.getQueryResults(query5));
	}

}
