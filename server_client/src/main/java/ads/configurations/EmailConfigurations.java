package ads.configurations;

/**
* Class to read email related ini configuration files
* @author Susana Polido
* @version 1
*/
public class EmailConfigurations extends Configurations{

	/**
	 * Creates the EmailConfigurations object from the name of the file the configurations are stored in
	 * @param file_name
	 * @since 1
	 */
	public EmailConfigurations(String file_name) {
		super(file_name);
	}
	
	/**
	 * Returns the email host
	 * @return String email host
	 * @since 1
	 */
	public String getHost() {
		return getValueFromKeySection("host", "email");
	}
	
	/**
	 * Returns the email address stored in the ini file to be used to send emails from
	 * @return String email address
	 * @since 1
	 */
	public String getAddress() {
		return getValueFromKeySection("address", "email");
	}
	
	/**
	 * Returns the email pass stored in the ini file to be used to send emails from
	 * @return String email pass
	 * @since 1
	 */
	public String getPass() {
		return getValueFromKeySection("pass", "email");
	}
	
	//Should be moved to a proper test section
	public static void main(String[] args) {
		EmailConfigurations test = new EmailConfigurations("tests_configs.ini");
		System.out.println(test.getHost().equals("smtp.gmail.com"));
		System.out.println(test.getAddress().equals("boop@gmail.com"));
		System.out.println(test.getPass().equals("nopenopenope"));
	}

}
