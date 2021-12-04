package ads.users;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import ads.bridges.EmailHandler;
import ads.bridges.GitHubRestAPI;
import ads.bridges.RepositoryAPI;
import ads.knowledgebase.OWLInteraction;

/**
 * This is to be called by the editor's "requests"
 * @author Susana Polido
 * @version 0.2
 */
public class Editor {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
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
		String new_branch =  repository.createBranch( repository.getMainBranchName(), email);
		if(new_branch == null)
			return "couldn't make the contribuition";
		 repository.updateFile(file, message, content, new_branch);
		return EmailHandler.sendEmail("", email, "Contribution Received", "The contribution you made to the knowledge base has been recieved and awaits evaluation");
	}
	
	/**
	 * Adds a new class to the ontology
	 * @param name of the new class to be created
	 * @param parent super class of the new class, can be null if it doesn't have a super class
	 * @param comment annotation to be added to the new class, can be null if one doesn't want to add a comment
	 * @param email the creation is to be attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void createClass(String name, String parent, String comment, String email, String commit) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = owl.createClass(name);
		if(parent != null)
			content = owl.addIsSubclassOf(parent, name);
		if(comment != null)
			content = owl.addCommentAnnotationToClass(comment, name);
		makeContribuition(file, content, commit, email);
	}
	
	/**
	 * Adds a new named individual to the ontology and sends the edit to a new branch on the repository
	 * @param name of the new named individual to be created
	 * @param parent class of the new named individual, can be null if it doesn't have a class
	 * @param comment annotation to be added to the new named individual, can be null if one doesn't want to add a comment
	 * @param email the creation is to be attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void createIndividual(String name, String parent, String comment, String email, String commit) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = owl.createNamedIndividual(name);
		if(parent != null)
			content = owl.addNamedIndividualBelongsToClass(parent, name);
		if(comment != null)
			content = owl.addCommentAnnotationToNamedIndividual(comment, name);
		makeContribuition(file, content, commit, email);
	}
	
	/**
	 * Adds a new data property to the ontology and sends the edit to a new branch on the repository
	 * @param name of the new data property to be created
	 * @param comment annotation to be added to the new data property, can be null if one doesn't want to add a comment
	 * @param email the creation is to be attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void createDataProperty(String name, String comment, String email, String commit) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = owl.createDataProperty(name);
		if(comment != null)
			content = owl.addCommentAnnotationToDataProperty(comment, name);
		makeContribuition(file, content, commit, email);
	}
	

	/**
	 * Deletes things from the ontology and sends the edit to a new branch on the repository
	 * <p>
	 * It can receive up to 1 class, 1 individual and 1 data property at the same time.
	 * @param classToDelete name of the class, can be null if no class is to be deleted
	 * @param individualToDelete name of the named individual to be deleted, can be null if none is to be deleted
	 * @param propertyToDelete name of the data property to be deleted, can be null if none is to be deleted
	 * @param email the deletion is attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void deleteStuff(String classToDelete, String individualToDelete, String propertyToDelete, String email, String commit) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = null;
		if(classToDelete != null)
			content = owl.removeClass(classToDelete);
		if(individualToDelete != null)
			content = owl.removeNamedIndividual(individualToDelete);
		if(propertyToDelete != null)
			content = owl.removeDataProperty(propertyToDelete);
		makeContribuition(file, content, commit, email);
	}
	
	
	/**
	 * Useful for the frontend
	 * @return list of the classes' names
	 * @since 0.2
	 */
	public static List<String> getClasses() {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getClasses();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Useful for the frontend
	 * @return list of the named individuals' names
	 * @since 0.2
	 */
	public static List<String> getNamedIndividuals() {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getNamedIndividuals();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Useful for the frontend
	 * @return list of the data properties' names
	 * @since 0.2
	 */
	public static List<String> getDataProperties() {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getDataProperties();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void main(String[] args) {
		System.out.println(Editor.makeContribuition(repository.getOwlFileName(), "coat of arms", "making a new branch from the editor", "sicpo1@iscte-iul.pt"));
	}
	
}
