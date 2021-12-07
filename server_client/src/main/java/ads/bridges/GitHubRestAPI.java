package ads.bridges;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ads.configurations.GitHubConfigurations;


/**
 * Uses the Github rest api to communicate to a remote repository in github
 * <p>
 * Feito com base em https://docs.github.com/en/rest/reference/repos
 * @author Susana Polido
 * @version 1.1
 */
public class GitHubRestAPI implements RepositoryAPI{
	private GitHubConfigurations githubConfig;
	private ObjectMapper objectMapper;
	
	public GitHubRestAPI(String config) {
		githubConfig = new GitHubConfigurations(config);
		objectMapper = new ObjectMapper();
	}
	
	/**
	 * Returns the name of the main branch stored in the configurations file
	 * @return name of the main branch
	 * @since 1
	 */
	public String getMainBranchName() {
		return githubConfig.getMainName();
	}
	
	/**
	 * Returns the name of the ontology file stored in the configurations file
	 * @return the name of the file
	 * @since 1
	 */
	public String getOwlFileName() {
		return githubConfig.getOntologyFileName();
	}
	
	/**
	 * Checks if the email matches that of a curator in the configurations file
	 * @param email
	 * @return if the email matches that of a curator
	 * @since 1
	 */
	public boolean isCurator(String email) {
		return githubConfig.isCurator(email);
	}
		
	/**
	 * Sends a get http request
	 * @param path to finish the get message
	 * @return HttpResponse<String> github's reply
	 * @since 1
	 */
	private HttpResponse<String> get(String path){
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(githubConfig.getGitHubBaseUrl() + path))
                .setHeader("Authorization", githubConfig.getGitHubToken())
                .GET()
                .build();

		try {
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
			return response;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Sends a delete  http request
	 * @param path to finish the delete message
	 * @return HttpResponse<String> github's reply
	 * @since 1
	 */	
	private HttpResponse<String> delete(String path) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(githubConfig.getGitHubBaseUrl() + path))
                .setHeader("Authorization", githubConfig.getGitHubToken())
                .DELETE()
                .build();

		try {
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
			return response;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Sends a post http request
	 * @param path to finish the post message
	 * @param body to finish the post message
	 * @return HttpResponse<String> github's reply
	 * @since 1
	 */	
	private HttpResponse<String> post(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(githubConfig.getGitHubBaseUrl() + path))
                .setHeader("Authorization", githubConfig.getGitHubToken())
                .POST(BodyPublishers.ofString(body))
                .build();
		try {
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
			return response;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Sends a put http request
	 * @param path to finish the put message
	 * @param body to finish the put message
	 * @return HttpResponse<String> github's reply
	 * @since 1
	 */
	public HttpResponse<String> put(String path, String body) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(githubConfig.getGitHubBaseUrl() + path))
                .setHeader("Authorization", githubConfig.getGitHubToken())
                .PUT(BodyPublishers.ofString(body))
                .build();

		try {
			HttpResponse<String> response = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
			return response;
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return null;
		}
    }
	
		
	/**
	 * Gets a branch's sha from github repository
	 * @param branch name
	 * @return branch's sha
	 * @since 1
	 */
	private String getBranchSHA(String branch) {
		HttpResponse<String> response = get("/branches/"+branch);
		try {
			String body = response.body();
			String sha = objectMapper.readTree(body)
			        .get("commit")
			        .get("sha")
			        .asText();
			return sha;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "Status code: " + response.statusCode();
		}
	}
	
	
	/**
	 * Gets the list of all the branches in the repository
	 * @return a list with all the branches' names in the github repository
	 * @since 1
	 */
	public List<String> getBranchesNames() {
    	HttpResponse<String> response = get("/branches");
		try {
			String body = response.body();
			JsonNode arrNode = new ObjectMapper().readTree(body);
			
			List<String> branches = new ArrayList<>();
	    	if (arrNode.isArray()) {
	    	    for (JsonNode objNode : arrNode) {
	    	        branches.add(objNode.toString());
	    	    }
	    	}
	    	
	    	List<String> names = new ArrayList<>();
	    	for(String branch : branches) {
				String name = objectMapper.readTree(branch)
					            .get("name")
					            .asText();
				names.add(name);
	    	}
			return names;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			List<String> error = new ArrayList<>();
			error.add("Status code: " + response.statusCode());
			return error;
		}
	}
	
	/**
	 * Gets a file from github as an input stream
	 * @param file name we want
	 * @param branch name the file is in
	 * @return inputstream of the file
	 * @since 1
	 */
	public InputStream getInputStreamFileFromBranch(String file, String branch) {
		HttpResponse<String> response = get("/contents/"+file+"?ref="+branch);
		try {
			String body = response.body();
			String url = objectMapper.readTree(body)
					.get("download_url")
					.asText();
			InputStream input = new URL(url).openStream();
			return input;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}	
    }
	
	/**
	 * Deletes a branch in the remote github repository
	 * @param branch we want to delete
	 * @return github's http response
	 * @since 1
	 */
	public HttpResponse<String> deleteBranch(String branch){
		return delete("/git/refs/heads/"+branch);
	}
	
	/**
	 * Merges a branch into another
	 * @param baseBranch the branch the other is going to be merged into
	 * @param toMergeBranch the branch that's going to be merged into another
	 * @return github's http response
	 * @since 1
	 */
	public HttpResponse<String> mergeBranches(String baseBranch, String toMergeBranch) {
    	Map<String, String> createBranchMap = Map.of(
                "base", baseBranch,
                "head", toMergeBranch);
		try {
			String requestBody = objectMapper.writeValueAsString(createBranchMap);
			return post("/merges", requestBody);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Creates a tagged released version
	 * @param branch 
	 * @param tag_name
	 * @param message
	 * @return github's http response
	 * @since 1
	 */
	public HttpResponse<String> releaseVersion(String branch, String tag_name, String message) {
    	Map<String, String> createRelease = Map.of(
                "tag_name", tag_name,
                "target_commitish", getBranchSHA(branch),
    			"body", message);
		try {
			String body = objectMapper.writeValueAsString(createRelease);
			return post("/releases",body);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Creates a branch named by the email and the current time from the branch
	 * @param branch
	 * @param email
	 * @return the name of the newly created branch
	 * @since 1
	 */
	public String createBranch(String branch, String email) {
		String time = LocalDateTime.now().toString().replace(":","_");
		String new_branch_name = time +"@" + email;
        Map<String, String> createBranchMap = Map.of(
                "ref", "refs/heads/"+new_branch_name,
                "sha", getBranchSHA(branch));
		try {
			String requestBody = objectMapper.writeValueAsString(createBranchMap);
			System.out.println(requestBody);
			HttpResponse<String> test = post("/git/refs", requestBody);
			System.out.println(test);
			return new_branch_name;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	
	
	
	
	/**
	 * Gets a file's sha from the repository
	 * @param file name
	 * @param branch name
	 * @return file's sha
	 * @since 1
	 */
	public String getFileSHAFromBranch(String file, String branch) {
    	HttpResponse<String> response = get("/contents/"+file+"?ref="+branch);
		try {
			String body = response.body();
			String sha = objectMapper.readTree(body)
					.get("sha")
					.asText();
			return sha;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "Status code: " + response.statusCode();
		}
    }
	
	
	
	
	
	/**
	 * Updates a file with the content
	 * @param file name
	 * @param message for the commit
	 * @param content to be uploaded to the file
	 * @param branch name
	 * @return github's http response
	 * @since 1
	 */
	public HttpResponse<String> updateFile(String file, String message, String content, String branch) {
		String sha = getFileSHAFromBranch(file, branch);
		if(sha.contains("Status code: "))
			return null;
		String encodedContent = java.util.Base64.getEncoder().encodeToString(content.getBytes());
		Map<String,String> createMap = Map.of(
				"content", encodedContent,
                "message", message,
                "branch", branch,
                "sha", sha);
		try {
			String requestBody = objectMapper.writeValueAsString(createMap);
			return put("/contents/"+file, requestBody);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	/**
	 * Gets the latest tag used in the repository
	 * @return the latest tag
	 * @since 1
	 */
	public String getLatestTag() {
		try {
			String tag = objectMapper.readTree(getTags().body())
			        .get(0)
			        .get("name")
			        .asText();
			return tag;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
    }
	
	/**
	 * Gets the list of tags used in the repository
	 * @return list of tags
	 * @since 1
	 */
	public HttpResponse<String> getTags(){
    	return get("/tags");
    }


	//Should be moved to a proper test section???
	public static void main(String[] args) {
		GitHubRestAPI githubrest = new GitHubRestAPI("remote_repo_config.ini");
		System.out.println(githubrest.getMainBranchName().equals("main"));
		System.out.println(githubrest.getOwlFileName().equals("ontology.owl"));
		System.out.println(githubrest.isCurator("boop"));
		System.out.println(githubrest.isCurator("adsprojet01@gmail.com"));
		
		String new_branch = githubrest.createBranch(githubrest.getMainBranchName(), "test");
		
		String content = "hello";
		System.out.println(githubrest.updateFile(githubrest.getOwlFileName(), "testing", content, new_branch));
		
		githubrest.mergeBranches(githubrest.getMainBranchName(), new_branch);
		githubrest.deleteBranch(new_branch);
		
		List<String> branches = githubrest.getBranchesNames();
		for(String branch : branches)
			System.out.println(branch);
		
		System.out.println(githubrest.releaseVersion(githubrest.getMainBranchName(), "0", "testing"));
		System.out.println(githubrest.getLatestTag());
		
		githubrest.getInputStreamFileFromBranch("ontology.owl", githubrest.getMainBranchName());
	}
}
