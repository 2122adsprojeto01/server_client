package ads.users;

import ads.bridges.EmailHandler;
import ads.bridges.GitHubRestAPI;

/**
 * This is to be called by the editor's "requests"
 * @author Susana Polido
 * @version 0.1
 */
public class Editor {
	//TODO a way to actually make the changes??? Like, something that receives a bunch of Strings and then calls the right OWLInteraction functions???
	
	/**
	 * Creates a new branch from the main in the repository, updates its contents with the content and sends an email to the editor
	 * @param file name
	 * @param content to be placed in the file
	 * @param message message for the commit
	 * @param email of the editor
	 * @return a string talking about the sent email
	 * @since 0.1
	 */
	public static String makeContribuition(String file, String content, String message, String email) {
		String new_branch = GitHubRestAPI.createBranch(GitHubRestAPI.getMainBranchName(), email);
		if(new_branch == null)
			return "couldn't make the contribuition";
		GitHubRestAPI.updateFile(file, message, content, new_branch);
		return EmailHandler.sendEmail("", email, "Contribution Received", "The contribution you made to the knowledge base has been recieved and awats evaluation");
	}
	
	
	
	
	
	public static void main(String[] args) {
		System.out.println(Editor.makeContribuition(GitHubRestAPI.getOwlFileName(), "hello mars, hgghghghghg", "making a new branch from the editor", "sicpo1@iscte-iul.pt"));
	}
	
}
