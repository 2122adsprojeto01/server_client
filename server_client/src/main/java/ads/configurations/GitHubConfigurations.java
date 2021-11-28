package ads.configurations;

/**
* Class to read github related ini configuration files
* @author Susana Polido
* @version 1
*/
public class GitHubConfigurations extends Configurations{

	/**
	 * Creates the GitHubConfigurations object from the name of the file the configurations are stored in
	 * @param file_name
	 * @since 1
	 */
	public GitHubConfigurations(String file_name) {
		super(file_name);
	}
	
	/**
	 * Returns an authentication key stored in the file that must exist on github
	 * @return String authentication key
	 * @since 1
	 */
	public String getAuthKey() {
		return getValueFromKeySection("key", "github");
	}
	
	/**
	 * Returns the well formated token to be used in the github rest api
	 * @return String token
	 * @since 1
	 */
	public String getGitHubToken() {
		return "Bearer " + getAuthKey();
	}
	
	/**
	 * Retrieves the owner of the repository from the configurations file
	 * @return String owner of the repository
	 * @since 1
	 */
	public String getOwner() {
		return getValueFromKeySection("owner", "github");
	}
	
	/**
	 * Retrieves the name of the repository from the configurations file
	 * @return String name of the repository
	 * @since 1
	 */
	public String getRepo() {
		return getValueFromKeySection("repository", "github");
	}
	
	/**
	 * Returns the github base url made from the owner and name of the repository in the file
	 * @return String github base url
	 * @since 1
	 */
	public String getGitHubBaseUrl() {
		return "https://api.github.com/repos/" + getOwner()+"/"+getRepo();
	}
	
	/**
	 * Retrieves the name of the main branch of the repository stored in the configurations file
	 * @return String main branch name
	 * @since 1
	 */
	public String getMainName() {
		return getValueFromKeySection("main_branch", "github");
	}
	
	/**
	 * Retrieves the name of the ontology file from the configurations file
	 * @return String ontology file name
	 * @since 1
	 */
	public String getOntologyFileName() {
		return getValueFromKeySection("ontology", "github");
	}
	
	
	/**
	 * Checks if the passed string matches a curator in the configurations file
	 * @param curator to be checked the existance of in the configurations file
	 * @return boolean if the curator is in the configurations
	 * @since 1
	 */
	public boolean isCurator(String curator) {
		return getAllValuesFromKeySection("curator", "github").contains(curator);
	}
	
	
	//Should be moved to a proper test section
	public static void main(String[] args) {
		GitHubConfigurations test = new GitHubConfigurations("tests_configs.ini");
		System.out.println(test.getAuthKey().equals("helloworld"));
		System.out.println(test.getGitHubToken().equals("Bearer helloworld"));
		System.out.println(test.getOwner().equals("tests"));
		System.out.println(test.getRepo().equals("teste1"));
		System.out.println(test.getGitHubBaseUrl().equals("https://api.github.com/repos/tests/teste1"));
		System.out.println(test.getMainName().equals("main"));
		System.out.println(test.getOntologyFileName().equals("ontology.owl"));
		System.out.println(test.isCurator("hello"));
		System.out.println(test.isCurator("boop@gmail.com"));
	}
}
