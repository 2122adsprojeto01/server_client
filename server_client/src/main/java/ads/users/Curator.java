package ads.users;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ads.bridges.EmailHandler;
import ads.bridges.GitHubRestAPI;

/**
 * This is to be called by the curator's "requests"
 * @author Susana Polido
 * @version 0.2
 */
public class Curator {
	
	/**
	 * Merges the branch into the main branch, deletes the branch, releases a version of the merged main branch with the passed version tag
	 * and sends an email to both the curator and the editor
	 * @param curator the email that identifies the curator making the request
	 * @param branch the full branch name
	 * @param message the message to be sent to both the editor and the curator
	 * @param version new version tag
	 * @return a string that says if the request was successful or not
	 * @since 0.1
	 */
	public static String acceptChange(String curator, String branch, String message, String version) {
		String result = acceptChange(curator, branch, message);
		if(result.equals("not a curator") || result.equals("couldn't merge"))
			return result;
		
		GitHubRestAPI.releaseVersion(GitHubRestAPI.getMainBranchName(),version, "New version released");
		return result;
	}
	
	/**
	 * Updates the file with the changes, merges the branch into the main branch, deletes the branch,
	 * releases a version of the merged main branch with the passed version tag and sends an email to both the curator and the editor
	 * @param curator the email that identifies the curator making the request
	 * @param branch the full branch name
	 * @param message the message to be sent to both the editor and the curator
	 * @param changes the changes to me made to the file
	 * @param version new version tag
	 * @return a string that says if the request was successful or not
	 * @since 0.1
	 */
	public static String mixChange(String curator, String branch, String message, String changes, String version) {
		String result = mixChange(curator, branch, message, changes);
		if(result.equals("not a curator") || result.equals("couldn't do the changes"))
			return result;
		
		GitHubRestAPI.releaseVersion(GitHubRestAPI.getMainBranchName(),version, "New version released");
		return result;
	}
	
	/**
	 * Deletes the branch and sends an email to both the curator and the editor
	 * @param curator the email that identifies the curator making the request
	 * @param branch the full branch name
	 * @param message the message to be sent to both the editor and the curator
	 * @return a string that says if the request was successful or not
	 * @since 0.1
	 */
	public static String rejectChange(String curator, String branch, String message) {
		if(!GitHubRestAPI.isCurator(curator))
			return "not a curator";
		GitHubRestAPI.deleteBranch(branch);
		String editor = branch.substring(branch.indexOf("@")+1);
		return EmailHandler.sendEmail(curator, editor, "Contribution Rejected", message);
	}
	
	/**
	 * Merges the branch into the main branch, deletes the branch
	 * and sends an email to both the curator and the editor
	 * @param curator the email that identifies the curator making the request
	 * @param branch the full branch name
	 * @param message the message to be sent to both the editor and the curator
	 * @return a string that says if the request was successful or not
	 * @since 0.1
	 */
	public static String acceptChange(String curator, String branch, String message) {
		if(!GitHubRestAPI.isCurator(curator))
			return "not a curator";
		HttpResponse<String> reply = GitHubRestAPI.mergeBranches(GitHubRestAPI.getMainBranchName(),branch);
		if(reply == null)
			return "couldn't merge";
		GitHubRestAPI.deleteBranch(branch);
		String editor = branch.substring(branch.indexOf("@")+1);
		return EmailHandler.sendEmail(curator, editor, "Contribution Accepted", message);
	}
	
	/**
	 * Updates the file with the changes, merges the branch into the main branch, deletes the branch and sends an email to both the curator and the editor
	 * @param curator the email that identifies the curator making the request
	 * @param branch the full branch name
	 * @param message the message to be sent to both the editor and the curator
	 * @param changes the changes to me made to the file
	 * @return a string that says if the request was successful or not
	 * @since 0.1
	 */
	public static String mixChange(String curator, String branch, String message, String changes) {
		if(!GitHubRestAPI.isCurator(curator))
			return "not a curator";
		HttpResponse<String> reply = GitHubRestAPI.updateFile(GitHubRestAPI.getOwlFileName(), message,changes, branch);
		if(reply == null)
			return "couldn't do the changes";
		
		reply = GitHubRestAPI.mergeBranches(GitHubRestAPI.getMainBranchName(),branch);
		if(reply == null)
			return "couldn't merge";
		GitHubRestAPI.deleteBranch(branch);
		String editor = branch.substring(branch.indexOf("@")+1);
		return EmailHandler.sendEmail(curator, editor, "Contribution Accepted with changes", message);
	}
	
	/**
	 * Gets the list of all non-main branches in the repository
	 * @return list of all non-main branches
	 * @since 0.1
	 */
	public static List<String> getBranchesNames(){
		List<String> branches = GitHubRestAPI.getBranchesNames();
		branches.remove(GitHubRestAPI.getMainBranchName());
		return branches;
	}
	
	/**
	 * Retrieves the content of a file from a branch as a string
	 * @param file name we want
	 * @param branch name the file is on
	 * @return the content of the file as a String
	 * @since 0.1
	 */
	public static String getFileContentFromBranch(String file, String branch) {
		try {
			InputStream inputStream = GitHubRestAPI.getInputStreamFileFromBranch(file, branch);
			String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the latest tag used in the repository
	 * @return the latest tag as a string
	 * @since 0.1
	 */
	public static String getLatestTag() {
		return GitHubRestAPI.getLatestTag();
	}
	
	
	
	
	public static String getFileContentFromMainBranch() {
		return getFileContentFromBranch(GitHubRestAPI.getOwlFileName(), GitHubRestAPI.getMainBranchName());
	}
	
	
	public static boolean isCurator(String email) {
		return GitHubRestAPI.isCurator(email);
	}
	
	
	public static String getFileContentFromBranch(String branch) {
		return getFileContentFromBranch(GitHubRestAPI.getOwlFileName(), branch);
	}
	
	//Maybe should be moved to a proper test area
	public static void main(String[] args) {
		System.out.println(Curator.getFileContentFromBranch(GitHubRestAPI.getOwlFileName(), GitHubRestAPI.getMainBranchName()));
		//System.out.println(Curator.getLatestTag());
		//List<String> branches = Curator.getBranchesNames();
		//for(String branch : branches)
		//	System.out.println(branch);
		//System.out.println(Curator.rejectChange("sicpo1@iscte-iul.pt", "2021-11-20T19_03_18.419847800@sicpo1@iscte-iul.pt", "Changes not accepted because..."));
		//System.out.println(Curator.acceptChange("sicpo1@iscte-iul.pt", "2021-11-20T19_24_00.948490@sicpo1@iscte-iul.pt", "The changes you made were accepted", "0.1"));
		//String content = "did some changes";
		//System.out.println(Curator.mixChange("sicpo1@iscte-iul.pt", "2021-11-20T19_31_36.739701@sicpo1@iscte-iul.pt", "The changes you made were accepted", content,"0.2"));
	}
}
