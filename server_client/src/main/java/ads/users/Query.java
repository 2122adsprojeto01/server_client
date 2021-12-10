package ads.users;

import java.util.List;

import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;

public class Query {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
	public static List<String> getQueryResult(String query){return null;}
	public static String prepareQuery(String classToQuery, String dataProperty1, String operator1, String value1,
			String connection, String dataProperty2, String operator2, String value2) {return null;}
	public static String prepareQuery(String classToQuery, String dataProperty1, String operator1, String value1) {return null;}
}
