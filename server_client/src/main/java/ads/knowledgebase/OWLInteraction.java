package ads.knowledgebase;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;


/**
 * Class that uses the owl api
 * @author Susana Polido
 * @version 0.3
 */
/**
 * @author susan
 *
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
	
	
	/**
	 * Adds a comment to a object property
	 * @param annotation to be created
	 * @entity that the annotation belongs to
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addCommentAnnotationToObjectProperty(String annotation, String entity) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(annotation));
		OWLEntity en = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, entity));
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
	
	
	
	// FUNCTIONS THAT CREATE THINGS (class, named individual, data property, object property)
	
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
	
	
	
	/**
	 * Adds the object property is functional
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsFunctional(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLFunctionalObjectPropertyAxiom(entity);
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
	 * Adds the object property is asymmetric
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsAsymmetric(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLAsymmetricObjectPropertyAxiom(entity);
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
	 * Adds the object property is inverse functional
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsInverseFunctional(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(entity);
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
	 * Adds the object property is Irreflexive
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsIrreflexive(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(entity);
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
	 * Adds the object property is reflexive
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsReflexive(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLReflexiveObjectPropertyAxiom(entity);
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
	 * Adds the object property is symmetric
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsSymmetric(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLSymmetricObjectPropertyAxiom(entity);
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
	 * Adds the object property is transitive
	 * @param objectProperty name of the object property
	 * @return String that contains all the ontology
	 * @since 0.2
	 */
	public String addObjectPropertyIsTransitive(String objectProperty) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAxiom axiom = factory.getOWLTransitiveObjectPropertyAxiom(entity);
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
	
	/**
	 * Removes a named individual from the ontology
	 * @param namedIndividual to be removed
	 * @return String that contains all the ontology
	 * @version 0.1
	 */
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
	
	/**
	 * Removes a class from the ontology
	 * @param className to be removed
	 * @return String that contains all the ontology
	 * @version 0.1
	 */
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
	
	/**
	 * Removes a data property from the ontology
	 * @param dataProperty to be removed
	 * @return String that contains all the ontology
	 * @version 0.1
	 */
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
	
	
	/**
	 * Removes an object property from the ontology
	 * @param objectProperty to be removed
	 * @return String that contains all the ontology
	 * @version 0.1
	 */
	public String removeObjectProperty(String objectProperty) {
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
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
	
	/**
	 * Removes a comment annotation from a data property
	 * @param dataProperty the comment belongs to
	 * @param comment to be removed
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeCommentFromDataProperty(String dataProperty, String comment) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		
		OWLDataProperty entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, dataProperty));
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(comment));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), anno);
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
	
	/**
	 * Removes a comment annotation from an object property
	 * @param objectProperty the comment belongs to
	 * @param comment to be removed
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeCommentFromObjectProperty(String objectProperty, String comment) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
		OWLAnnotation anno = factory.getOWLAnnotation(factory.getRDFSComment(),factory.getOWLLiteral(comment));
		OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(entity.getIRI(), anno);
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
	
	
	/**
	 * Removes from the ontology that an object property is transitive
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsTransitive(String objectProperty) {
		if(getObjectPropertyIsTransitive(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLTransitiveObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	 * Removes from the ontology that an object property is asymmetric
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsAsymmetric(String objectProperty) {
		if(getObjectPropertyIsAsymmetric(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLAsymmetricObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	 * Removes from the ontology that an object property is symmetric
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsSymmetric(String objectProperty) {
		if(getObjectPropertyIsSymmetric(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLSymmetricObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
	
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
	 * Removes from the ontology that an object property is reflexive
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsReflexive(String objectProperty) {
		if(getObjectPropertyIsReflexive(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLReflexiveObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	 * Removes from the ontology that an object property is irreflexive
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsIrreflexive(String objectProperty) {
		if(getObjectPropertyIsIrreflexive(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	 * Removes from the ontology that an object property is functional
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsFunctional(String objectProperty) {
		if(getObjectPropertyIsFunctional(objectProperty)) {
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLFunctionalObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	 * Removes from the ontology that an object property is inverse functional
	 * @param objectProperty to be altered
	 * @return String that contains all the ontology
	 * @since 0.3
	 */
	public String removeObjectPropertyIsInverseFunctional(String objectProperty) {
		if(getObjectPropertyIsInverseFunctional(objectProperty)) {	
			String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
			OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, objectProperty));
			OWLAxiom axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(entity);
			RemoveAxiom remover = new RemoveAxiom(ontology, axiom);
			manager.applyChanges(remover);
		}
		
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
	
	public String changeObjectPropertyName(String oldName, String newName) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager,Collections.singleton(ontology));
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, oldName));
		
		Map<OWLEntity, IRI> map = new HashMap<>();
		
		for(OWLObjectProperty toRename : ontology.getObjectPropertiesInSignature())
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
	
	
	
	
	//Gets things from the ontology
	
	/**
	 * Gets the name of all the classes of the ontology as strings
	 * @return list of all classes' names as Strings
	 * @version 0.2
	 */
	public List<String> getClasses(){
		List<OWLClass> classes = new ArrayList<>();
		ontology.classesInSignature().forEach(classes::add);
		
		List<String> classesNames = new ArrayList<>();
		for(OWLClass c : classes)
			classesNames.add(c.getIRI().getFragment());
		return classesNames;
	}
	
	/**
	 * Gets the name of all the named individuals of the ontology as strings
	 * @return list of all named individuals' names as Strings
	 * @version 0.2
	 */
	public List<String> getNamedIndividuals(){
		List<OWLNamedIndividual> individuals = new ArrayList<>();
		ontology.individualsInSignature().forEach(individuals::add);
		
		List<String> individualsNames = new ArrayList<>();
		for(OWLNamedIndividual i : individuals)
			individualsNames.add(i.getIRI().getFragment());
		return individualsNames;
	}
	
	/**
	 * Gets the name of all the data properties of the ontology as strings
	 * @return list of all data properties' names as Strings
	 * @version 0.2
	 */
	public List<String> getDataProperties(){
		List<OWLDataProperty> properties = new ArrayList<>();
		ontology.dataPropertiesInSignature().forEach(properties::add);
		
		List<String> propertiesNames = new ArrayList<>();
		for(OWLDataProperty p : properties)
			propertiesNames.add(p.getIRI().getFragment());
		return propertiesNames;
	}
	
	
	/**
	 * Gets the name of all the object properties of the ontology as strings
	 * @return list of all object properties' names as Strings
	 * @version 0.2
	 */
	public List<String> getObjectProperties(){
		List<OWLObjectProperty> properties = new ArrayList<>();
		ontology.objectPropertiesInSignature().forEach(properties::add);
		
		List<String> propertiesNames = new ArrayList<>();
		for(OWLObjectProperty p : properties)
			propertiesNames.add(p.getIRI().getFragment());
		return propertiesNames;
	}
	
	
	public List<String> getDataPropertyComments(String name){
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLDataProperty entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(prefix, name));
		Stream<OWLAnnotation> anotations = EntitySearcher.getAnnotations(entity.getIRI(), ontology);
		List<String> comments = new ArrayList<>();
		for(OWLAnnotation s : anotations.toList())
			comments.add(s.getValue().toString().substring(s.getValue().toString().indexOf("\"")+1, s.getValue().toString().indexOf("\"^^")));
		return comments;
	}
	
	public List<String> getObjectPropertyComments(String name){
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		Stream<OWLAnnotation> anotations = EntitySearcher.getAnnotations(entity.getIRI(), ontology);
		List<String> comments = new ArrayList<>();
		for(OWLAnnotation s : anotations.toList())
			comments.add(s.getValue().toString().substring(s.getValue().toString().indexOf("\"")+1, s.getValue().toString().indexOf("\"^^")));
		return comments;
	}
	
	public boolean getObjectPropertyIsTransitive(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLTransitiveObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsAsymmetric(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLAsymmetricObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsSymmetric(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLSymmetricObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsReflexive(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLReflexiveObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsIrreflexive(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsFunctional(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLFunctionalObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	public boolean getObjectPropertyIsInverseFunctional(String name) {
		String prefix = format.	asPrefixOWLDocumentFormat().getDefaultPrefix();
		OWLObjectProperty entity = factory.getOWLEntity(EntityType.OBJECT_PROPERTY, IRI.create(prefix, name));
		OWLAxiom axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(entity);
		return EntitySearcher.containsAxiom(axiom, ontology, Imports.EXCLUDED);
	}
	
	//Tests
	public static void main(String[] args) {
		try {
			OWLInteraction test = new OWLInteraction(new FileInputStream("test.owl"));
			String result = "";
			/*result = test.createClass("Animal");
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
			result = test.addObjectPropertyIsTransitive("hasFather");
			result = test.addObjectPropertyIsSymmetric("hasFather");
			result = test.addObjectPropertyIsReflexive("hasFather");
			result = test.addObjectPropertyIsIrreflexive("hasFather");
			result = test.addObjectPropertyIsInverseFunctional("hasFather");
			result = test.addObjectPropertyIsAsymmetric("hasFather");
			result = test.addObjectPropertyIsFunctional("hasFather");
			System.out.println(result);
			result = test.addObjectPropertyTo2NamedIndividuals("hasFather","zane","john");*/
			result = test.createObjectProperty("name");
			//result = test.addObjectPropertyIsTransitive("name");
			/*System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));
			System.out.println(test.removeObjectPropertyIsTransitive("name"));*/
			System.out.println(test.addCommentAnnotationToObjectProperty("name", "boop"));
			//result = test.changeDataPropertyName("name", "nome");
			//result = test.addCommentAnnotationToDataProperty("this is a comment", "name");
			//System.out.println(result);
			
			//test.getDataPropertyDetails("name");
			/*result = test.addNamedIndividualBelongsToClass("Person","john");
			result = test.addCommentAnnotationToClass("IDK what to put here","Animal");
			result = test.addCommentAnnotationToNamedIndividual("a darling baby","zane");
			System.out.println(result);
			System.out.println("");
			System.out.println("");
			System.out.println("");
			result = test.removeNamedIndividual("zane");
			result = test.removeIsSubclassOf("Human", "Person");
			result = test.changeClassName("Animal","Hello");
			System.out.println(result);*/
			
			/*for(String c : test.getDataProperties())
				System.out.println(c);*/
			
		} catch (OWLOntologyCreationException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
