package ads.configurations;

/**
* Class to read the ini file with the location of the frontend
* @author Susana Polido
* @version 1
*/
public class SiteConfigurations extends Configurations{

	/**
	 * Creates the EmailConfigurations object from the name of the file the location is stored in
	 * @param file_name
	 * @since 1
	 */
	public SiteConfigurations(String file_name) {
		super(file_name);
	}
	
	/**
	 * Returns the uri of where the frontend is running
	 * @return String uri of the frontend
	 * @since 1
	 */
	public String getUri() {
		return getValueFromKeySection("uri", "site");
	}
	
	//Should be moved to a proper test section
	public static void main(String[] args) {
		SiteConfigurations test = new SiteConfigurations("site_config.ini");
		System.out.println(test.getUri().equals("http://localhost:1337"));
	}
}
