package ads.knowledgebase;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomVisitor;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import com.clarkparsia.owlapi.explanation.util.OntologyUtils;

/**
 * Class that uses the owl api
 * @author Susana Polido
 * @version 0.1
 */
public class OWLInteraction {
	OWLOntologyManager manager;
	OWLOntology ontology;
	OWLDataFactory factory;
	OWLDocumentFormat format;
	
	
	/**
	 * Loads an input stream into an owl ontology
	 * @param ins input stream that contains the ontology to be loaded
	 * @throws OWLOntologyCreationException
	 * @version 0.1
	 */
	public OWLInteraction(InputStream ins) throws OWLOntologyCreationException {
		manager = OWLManager.createOWLOntologyManager();
		ontology =  manager.loadOntologyFromOntologyDocument(ins);
		factory = ontology.getOWLOntologyManager().getOWLDataFactory();
		format = manager.getOntologyFormat(ontology);
	}
	
	
	
	
	
	// MISSING THE DATATYPE!!!
	
	
	
	
	
	// FUNCTIONS THAT ADD COMENT ANNOTATIONS
	
	/**
	 * Adds a comment annotation to an entity
	 * @param annotation to be created
	 * @param entity name of the entity
	 * @param eType type of the entity
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addCommentAnnotation(String annotation, String entity, String eType) {
		switch(eType) {
			case "class":
				return addCommentAnnotationToClass(annotation, entity);
			case "individual":
				return addCommentAnnotationToNamedIndividual(annotation, entity);
			case "dataProperty":
				return addCommentAnnotationToDataProperty(annotation, entity);
			default:
				return null;
		}
	}
	
	/**
	 * Adds a comment to a class
	 * @param annotation to be created
	 * @entity that the annotation belongs to
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addCommentAnnotationToClass(String annotation, String entity) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(annotation));
		OWLEntity en = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, entity));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(en.getIRI(), anno);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a comment to a named individual
	 * @param annotation to be created
	 * @entity that the annotation belongs to
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addCommentAnnotationToNamedIndividual(String annotation, String entity) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(annotation));
		OWLEntity en = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, entity));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(en.getIRI(), anno);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a comment to a data property
	 * @param annotation to be created
	 * @entity that the annotation belongs to
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addCommentAnnotationToDataProperty(String annotation, String entity) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(annotation));
		OWLEntity en = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, entity));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(en.getIRI(), anno);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	// FUNCTIONS THAT CREATE THINGS (class, named individual, data property)
	
	/**
	 * Creates a new entity
	 * @param entityName
	 * @param entityType
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String createEntity(String entityName, String entityType) {
		switch(entityType) {
		case "class":
			return createClass(entityName);
		case "individual":
			return createNamedIndividual(entityName);
		case "dataProperty":
			return createDataProperty(entityName);
		default:
			return null;
		}
	}
	
	/**
	 * Adds a new class to the ontology
	 * @param className name of class to be created
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String createClass(String className) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLClass entity = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, className));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a new data property to the ontology
	 * @param dataProperty name of data property to be created
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String createDataProperty(String dataProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLDataProperty entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, dataProperty));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a new namedIndividual to the ontology
	 * @param namedIndividual name of the named individual to be added
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String createNamedIndividual(String namedIndividual) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLNamedIndividual entity = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, namedIndividual));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds a new object property to the ontology
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String createObjectProperty(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLDeclarationAxiom(entity);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	// FUNCTIONS THAT CREATE RELATIONSHIPS BETWEEN THINGS
	
	/**
	 * Adds that a class is a subclass of another class
	 * @param parent
	 * @param child
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addIsSubclassOf(String parent, String child) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLClass parentClass = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, parent));
		OWLClass childClass = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, child));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(childClass, parentClass);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds an object property between 2 named individuals
	 * @param objectProperty the object property
	 * @param individual1 the 1st individual
	 * @param individual2 the 2nd individual
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addObjectPropertyTo2NamedIndividuals(String objectProperty, String individual1, String individual2) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		
		OWLNamedIndividual ni1 = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, individual1));
		OWLNamedIndividual ni2 = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, individual2));
		OWLObjectProperty op = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		
		OWLAxiom axiom = factory.getOWLObjectPropertyAssertionAxiom(op, ni1, ni2);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Adds that a named individual is part of a class
	 * @param className the class name
	 * @param namedIndividual the named individual
	 * @return String that contains all the ontology
	 * @since 0.1
	 */
	public String addNamedIndividualBelongsToClass(String className, String namedIndividual) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		
		OWLNamedIndividual ni = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, namedIndividual));
		OWLClass oc = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, className));
		
		OWLClassAssertionAxiom  axiom = factory.getOWLClassAssertionAxiom(oc, ni);
		manager.addAxiom(ontology, axiom);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	
	// FUNCTIONS THAT DELETE THINGS
	public String removeNamedIndividual(String namedIndividual) {
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLNamedIndividual entity = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, namedIndividual));
		entity.accept(remover);
		manager.applyChanges(remover.getChanges());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String removeClass(String className) {
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLClass entity = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, className));
		entity.accept(remover);
		manager.applyChanges(remover.getChanges());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String removeDataProperty(String dataProperty) {
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLDataProperty entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, dataProperty));
		entity.accept(remover);
		manager.applyChanges(remover.getChanges());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String removeIsSubclassOf(String parent, String child) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLClass parentClass = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, parent));
		OWLClass childClass = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, child));
		OWLAxiom axiom = factory.getOWLSubClassOfAxiom(childClass, parentClass);
		RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
		manager.applyChanges(remover);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	// FUNCTIONS THAT CHANGE THINGS
	public String changeClassName(String oldName, String newName) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager,Collections.singleton(ontology));
		OWLClass entity = factory.getOWLEntity(EntityType.CLASS, IRI.create(prefix, oldName));
		
		Map<OWLEntity, IRI> map = new HashMap<>();
		
		for(OWLClass toRename : ontology.getClassesInSignature())
			if(toRename.equals(entity))
	        	map.put(toRename, IRI.create(prefix, newName));
	    
		manager.applyChanges(renamer.changeIRI(map));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String changeNamedIndividualName(String oldName, String newName) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager,Collections.singleton(ontology));
		OWLNamedIndividual entity = factory.getOWLEntity(EntityType.NAMED_INDIVIDUAL, IRI.create(prefix, oldName));
		
		Map<OWLEntity, IRI> map = new HashMap<>();
		
		for(OWLNamedIndividual toRename : ontology.getIndividualsInSignature())
			if(toRename.equals(entity))
	        	map.put(toRename, IRI.create(prefix, newName));
	    
		manager.applyChanges(renamer.changeIRI(map));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String changeDataPropertyName(String oldName, String newName) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager,Collections.singleton(ontology));
		OWLDataProperty entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, oldName));
		
		Map<OWLEntity, IRI> map = new HashMap<>();
		
		for(OWLDataProperty toRename : ontology.getDataPropertiesInSignature())
			if(toRename.equals(entity))
	        	map.put(toRename, IRI.create(prefix, newName));
	    
		manager.applyChanges(renamer.changeIRI(map));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {
			manager.saveOntology(ontology, outputStream);
			return new String(outputStream.toByteArray());
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	
	//Tests
	public static void main(String[] args) {
		try {
			OWLInteraction test = new OWLInteraction(new FileInputStream("test.owl"));
			String result = test.createClass("Animal");
			result = test.createClass("Superhero");
			result = test.createClass("Human");
			result = test.createClass("Person");
			result = test.createClass("Person");
			result = test.createNamedIndividual("Superman");
			result = test.createNamedIndividual("zane");
			result = test.createNamedIndividual("john");
			result = test.addIsSubclassOf("Animal", "Human");
			result = test.addIsSubclassOf("Human", "Person");
			result = test.createObjectProperty("hasFather");
			result = test.addObjectPropertyTo2NamedIndividuals("hasFather","zane","john");
			result = test.createDataProperty("name");
			result = test.addNamedIndividualBelongsToClass("Person","john");
			result = test.addCommentAnnotationToClass("IDK what to put here","Animal");
			result = test.addCommentAnnotationToNamedIndividual("a darling baby","zane");
			System.out.println(result);
			System.out.println("");
			System.out.println("");
			System.out.println("");
			result = test.removeNamedIndividual("zane");
			result = test.removeIsSubclassOf("Human", "Person");
			result = test.changeClassName("Animal","Hello");
			System.out.println(result);
		} catch (OWLOntologyCreationException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
