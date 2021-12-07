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
 * @version 0.4
 */
public class Editor {
	private static RepositoryAPI repository = new GitHubRestAPI("remote_repo_config.ini");
	
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
	 * Adds a new object property to the ontology and sends the edit to a new branch on the repository
	 * @param name of the new object property to be created
	 * @param comment annotation to be added to the new data property, can be null if one doesn't want to add a comment
	 * @param email the creation is to be attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void createObjectProperty(String name, String comment, String email, String commit,
			boolean transitive, boolean symmetric, boolean reflexive, boolean irreflexive, boolean inverseFunctional, boolean asymmetric, boolean functional
			) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = owl.createObjectProperty(name);
		if(comment != null)
			content = owl.addCommentAnnotationToObjectProperty(comment, name);
		if(transitive)
			content = owl.addObjectPropertyIsTransitive(name);
		if(symmetric)
			content = owl.addObjectPropertyIsSymmetric(name);
		if(reflexive)
			content = owl.addObjectPropertyIsReflexive(name);
		if(irreflexive)
			content = owl.addObjectPropertyIsIrreflexive(name);
		if(inverseFunctional)
			content = owl.addObjectPropertyIsInverseFunctional(name);
		if(asymmetric)
			content = owl.addObjectPropertyIsAsymmetric(name);
		if(functional)
			content = owl.addObjectPropertyIsFunctional(name);
		makeContribuition(file, content, commit, email);
	}
	

	/**
	 * Deletes things from the ontology and sends the edit to a new branch on the repository
	 * <p>
	 * It can receive up to 1 class, 1 individual and 1 data property at the same time.
	 * @param classToDelete name of the class, can be null if no class is to be deleted
	 * @param individualToDelete name of the named individual to be deleted, can be null if none is to be deleted
	 * @param dataPropertyToDelete name of the data property to be deleted, can be null if none is to be deleted
	 * @param objectPropertyToDelete name of the object property to be deleted, can be null if none is to be deleted
	 * @param email the deletion is attributed to
	 * @param commit message for the repository
	 * @throws OWLOntologyCreationException
	 * @since 0.2
	 */
	public static void deleteStuff(String classToDelete, String individualToDelete, String dataPropertyToDelete, String objectPropertyToDelete, String email, String commit) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = null;
		if(classToDelete != null)
			content = owl.removeClass(classToDelete);
		if(individualToDelete != null)
			content = owl.removeNamedIndividual(individualToDelete);
		if(dataPropertyToDelete != null)
			content = owl.removeDataProperty(dataPropertyToDelete);
		if(objectPropertyToDelete != null)
			content = owl.removeObjectProperty(objectPropertyToDelete);
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
	
	/**
	 * Useful for the frontend
	 * @return list of the object properties' names
	 * @since 0.3
	 */
	public static List<String> getObjectProperties() {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectProperties();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Checks if a data property has comments
	 * @param name of the data property we want to check
	 * @return boolean true or false if it has comments
	 * @since 0.4
	 */
	public static boolean dataPropertyHasComments(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			if(owl.getDataPropertyComments(name).size() > 0)
				return true;
			else
				return false;
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Retrieves a data property's comment if it has one
	 * @param name of the data property we want to checl
	 * @return the 1st comment of the data property
	 * @since 0.4
	 */
	public static String getdataPropertyComment(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getDataPropertyComments(name).get(0);
		} catch (OWLOntologyCreationException e) {
			return "";
		}
	}
	
	
	
	/**
	 * Changes a data property in the ontology
	 * @param commit String message for the commit
	 * @param email String email to be associated with the requested change
	 * @param oldName String name that currently represents the data property in the ontology
	 * @param newName String new name we want the data property to be represented by. null if we want to keep the old name
	 * @param deleteComment boolean true if there's we want to delete a comment from the ontology
	 * @param newComment String comment to be added to the ontology. use with deleteComment = true to change a comment
	 * @return boolean true if the operation was successful, false otherwise
	 * @throws OWLOntologyCreationException
	 * @since 0.4
	 */
	public static boolean changeDataProperty(String commit, String email, String oldName, String newName, boolean deleteComment, String newComment) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = null;
		if(!deleteComment && newComment != null)
			content = owl.addCommentAnnotationToDataProperty(oldName, newComment);
		else if(newComment != null) {
			content = owl.removeCommentFromDataProperty(oldName, owl.getDataPropertyComments(oldName).get(0));
			content = owl.addCommentAnnotationToDataProperty(oldName, newComment);
		}
		else if(dataPropertyHasComments(oldName))
			content = owl.removeCommentFromDataProperty(oldName, owl.getDataPropertyComments(oldName).get(0));
		
		
		if(newName != null) {
			content = owl.changeDataPropertyName(oldName, newName);
			//System.out.println(content);
		}
		if(content != null) {
			makeContribuition(file, content, commit, email);
			return true;
		}
		return false;		
	}
	
	
	
	/**
	 * Checks is an object property has comments
	 * @param name of the object property we want to check
	 * @return boolean true if it has comments
	 * @since 0.4
	 */
	public static boolean objectPropertyHasComments(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			if(owl.getObjectPropertyComments(name).size() > 0)
				return true;
			else
				return false;
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	
	/**
	 * Retrived a comments associated with the object property
	 * @param name of the object property we want to check
	 * @return String with the 1st comment it finds associated with the object property
	 * @since 0.4
	 */
	public static String getObjectPropertyComment(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyComments(name).get(0);
		} catch (OWLOntologyCreationException e) {
			return "";
		}
	}
	
	
	/**
	 * Checks if the object property is transitive
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsTransitive(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsTransitive(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the object property is symmetric
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsSymmetric(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsSymmetric(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the object property is asymmetric
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsAsymmetric(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsAsymmetric(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the object property is reflexive
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsReflexive(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsReflexive(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	
	/**
	 * Checks if the object property is irreflexive
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsIrreflexive(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsIrreflexive(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the object property is functional
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsFunctional(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsFunctional(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	
	/**
	 * Checks if the object property is inverse functional
	 * @param name of the object property
	 * @return boolean true if it is
	 * @since 0.4
	 */
	public static boolean getObjectPropertyIsInverseFunctional(String name) {
		try {
			String file = repository.getOwlFileName();
			OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
			return owl.getObjectPropertyIsInverseFunctional(name);
		} catch (OWLOntologyCreationException e) {
			return false;
		}
	}
	

	/**
	 * Changes an object property in the ontology
	 * @param commit String message for the commit
	 * @param email String email to be associated with the requested change
	 * @param oldName String name that currently represents the object property in the ontology
	 * @param newName String new name we want the object property to be represented by. null if we want to keep the old name
	 * @param deleteComment boolean true if there's we want to delete a comment from the ontology
	 * @param newComment String comment to be added to the ontology. use with deleteComment = true to change a comment
	 * @param transitive if true makes the object property transitive, if false removes that characteristic
	 * @param asymmetric if true makes the object property asymmetric, if false removes that characteristic
	 * @param symmetric if true makes the object property symmetric, if false removes that characteristic
	 * @param reflexive if true makes the object property reflexive, if false removes that characteristic
	 * @param irreflexive if true makes the object property irreflexive, if false removes that characteristic
	 * @param functional if true makes the object property functional, if false removes that characteristic
	 * @param inverseFunctional if true makes the object property inverse functional, if false removes that characteristic
	 * @return boolean true if the operation was successful, false otherwise
	 * @throws OWLOntologyCreationException
	 * @since 0.4
	 */
	public static boolean changeObjectProperty(String commit, String email, String oldName, String newName, boolean deleteComment, String newComment,
			boolean transitive, boolean asymmetric, boolean symmetric, boolean reflexive, boolean irreflexive,
			boolean functional, boolean inverseFunctional) throws OWLOntologyCreationException {
		String file = repository.getOwlFileName();
		OWLInteraction owl = new OWLInteraction(repository.getInputStreamFileFromBranch(file, repository.getMainBranchName()));
		String content = null;

		if(!deleteComment && newComment != null) {
			content = owl.addCommentAnnotationToObjectProperty(oldName, newComment);
		}
		else if(newComment != null) {
			content = owl.removeCommentFromObjectProperty(oldName, owl.getDataPropertyComments(oldName).get(0));
			content = owl.addCommentAnnotationToObjectProperty(oldName, newComment);
		}
		else if(objectPropertyHasComments(oldName))
			content = owl.removeCommentFromObjectProperty(oldName, owl.getDataPropertyComments(oldName).get(0));

		if(newName != null)
			content = owl.changeObjectPropertyName(oldName, newName);
		
		if(transitive)
			content = owl.addObjectPropertyIsTransitive(oldName);
		else
			content = owl.removeObjectPropertyIsTransitive(oldName);
		
		if(asymmetric)
			content = owl.addObjectPropertyIsAsymmetric(oldName);
		else
			content = owl.removeObjectPropertyIsAsymmetric(oldName);
		
		if(symmetric)
			content = owl.addObjectPropertyIsSymmetric(oldName);
		else
			content = owl.removeObjectPropertyIsSymmetric(oldName);
		
		if(reflexive)
			content = owl.addObjectPropertyIsReflexive(oldName);
		else
			content = owl.removeObjectPropertyIsReflexive(oldName);
		
		if(irreflexive)
			content = owl.addObjectPropertyIsIrreflexive(oldName);
		else
			content = owl.removeObjectPropertyIsIrreflexive(oldName);
		
		if(functional)
			content = owl.addObjectPropertyIsFunctional(oldName);
		else
			content = owl.removeObjectPropertyIsFunctional(oldName);
		
		if(inverseFunctional)
			content = owl.addObjectPropertyIsInverseFunctional(oldName);
		else
			content = owl.removeObjectPropertyIsInverseFunctional(oldName);
		
		
		if(content != null) {
			makeContribuition(file, content, commit, email);
			return true;
		}
		return false;		
	}
	
	public static void main(String[] args) {
		System.out.println(Editor.makeContribuition(repository.getOwlFileName(), "coat of arms", "making a new branch from the editor", "sicpo1@iscte-iul.pt"));
	}
	
}
