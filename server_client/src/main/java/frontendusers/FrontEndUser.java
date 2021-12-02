package frontendusers;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A very simple abstract class to be extended by all classes that deal with the frontend requests
 * that arrive through the FrontEndBridge
 * <p>
 * This was done cause they all should need an object mapper and a string for the uri
 * <p>
 * By putting each type of client requests in a seperate class we can prevent the FrontEndBridge class from becoming too bloated
 * @author Susana Polido
 * @version 1
 */
public abstract class FrontEndUser {
	protected String uri;
	protected ObjectMapper objectMapper = new ObjectMapper();
	
	public FrontEndUser(String uri) {
		this.uri = uri;
	}
}
