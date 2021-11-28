package ads.bridges;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ads.configurations.Configurations;
import ads.configurations.EmailConfigurations;

/**
 * Feito com base em https://netcorecloud.com/tutorials/send-email-in-java-using-gmail-smtp/
 * @author Susana Polido
 * @version 1
 */
public class EmailHandler {
	private static EmailConfigurations EMAILCONFIGS = new EmailConfigurations("email_config.ini");
	
	/**
	 * Sends an email with the passed subject and content to 1 cc and 1 receiver
	 * @param cc of the email 
	 * @param receiver of the email
	 * @param subject of the email
	 * @param content of the email
	 * @return String that can be used to see if the email was sent using the expected information
	 * @since 1
	 */
	public static String sendEmail(String cc, String receiver, String subject, String content) {
		// Get system properties
		Properties properties = System.getProperties();
		
		// Setup mail server
		properties.put("mail.smtp.host", EMAILCONFIGS.getHost());
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		// Get the Session object.// and pass username and password
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(EMAILCONFIGS.getAddress(), EMAILCONFIGS.getPass());
			}
		});

		// Used to debug SMTP issues
        session.setDebug(true);
        
        try {
        	// Create a default MimeMessage object.
        	MimeMessage message = new MimeMessage(session);
        	
        	// Set From: header field of the header.
        	message.setFrom(new InternetAddress(EMAILCONFIGS.getAddress()));
        	
        	// Set CC: header field of the header.
        	if(!cc.equals(""))
        		message.addRecipient(RecipientType.CC, new InternetAddress(cc));

        	// Set To: header field of the header.
        	message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

        	// Set Subject: header field
        	message.setSubject(subject);

        	// Now set the actual message
        	message.setText(content);

        	// Send message
        	Transport.send(message);
        	System.out.println("Sent message successfully....");
        	return "Successfull message - Sender: " + cc + "; Receiver: " + receiver + "; Subject: " + subject + "; Content: " + content;
        } catch (MessagingException mex) {
        	mex.printStackTrace();
        }
        return "Something went wrong";
	}
	
	//Should be moved to a proper test section
		public static void main(String[] args) {
			//Only works if the gmail has the allow less secure apps thing turned on
			String cc = "2122adsprojeto01@gmail.com";
			String receiver = "2122adsprojeto01@gmail.com";
			String subject = "ADS email sending test";
			String content = "If this email is delivered then everything is working right";
			System.out.println(EmailHandler.sendEmail(cc, receiver, subject, content).equals("Successfull message - Sender: " + cc + "; Receiver: " + receiver + "; Subject: " + subject + "; Content: " + content));
		}
}
