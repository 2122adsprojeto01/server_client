package ads.bridges;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * An interface to represent the repository of the knowledge base 
 * so it might be simpler to change the code if we decide to move to another repository
 * @author Susana Polido
 * @version 1
 */
public interface RepositoryAPI {
	public String getMainBranchName();
	public String getOwlFileName();
	public boolean isCurator(String email);
	public List<String> getBranchesNames();
	public HttpResponse<String> deleteBranch(String branch);
	public HttpResponse<String> releaseVersion(String branch, String tag_name, String message);
	public String createBranch(String branch, String email);
	public HttpResponse<String> updateFile(String file, String message, String content, String branch);
	public String getLatestTag();
	public HttpResponse<String> getTags();
	public HttpResponse<String> mergeBranches(String baseBranch, String toMergeBranch);
	public InputStream getInputStreamFileFromBranch(String file, String branch);
}
