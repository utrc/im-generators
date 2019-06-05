package com.utc.utrc.hermes.iml.gen.smt.encoding;


import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import com.utc.utrc.hermes.iml.gen.smt.encoding.custom.AtomicRelation;
import com.utc.utrc.hermes.iml.gen.smt.encoding.custom.InstanceConstructorWithBinding;
import com.utc.utrc.hermes.iml.gen.smt.encoding.custom.SymbolWithContext;
import com.utc.utrc.hermes.iml.iml.Alias;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.Inclusion;
import com.utc.utrc.hermes.iml.iml.Model;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.SymbolReferenceTerm;
import com.utc.utrc.hermes.iml.util.ImlUtil;
/**
 * Encodes IML types in a way that guarantee that each unique type has a unique ID
 * This should hide the way it generates the unique id for each IML object
 *
 * @author Ayman Elkfrawy (elkfraaf@utrc.utc.com)
 * @author Gerald Wang (wangg@utrc.utc.com)
 */
public class EncodedId {
	
	private QualifiedName container;
	private String name;
	
	EObject imlObject;

	public static QualifiedName DEFAULT_CONTAINER = QualifiedName.create("__unnamed__");
	public static String ASSERTION_DEFAULT_NAME="__assertion_";
	
	/**
	 * Create a unique EncoderId for each unique IML Object. The same IML type should return same EncoderID
	 * for example: {@code Int ~> Real}type declared in different symbols should return the same EncoderId (eId1.equals(eId2) is true)
	 * Current implementation uses the string of the actual type to generate unique id, for example the type {@code Int ~> Real}
	 * will generate string id with "Int~>Real"
	 * @param imlEObject
	 * @param qnp
	 */
	public EncodedId(EObject imlEObject, IQualifiedNameProvider qnp) {
		this.imlObject = imlEObject;
		if (imlEObject instanceof Model) {
			container = null;
			name = ((Model) imlEObject).getName();
		}
		if (imlEObject instanceof NamedType) {
			container = qnp.getFullyQualifiedName(imlEObject.eContainer());
			name = ((Symbol) imlEObject).getName();
		} else if (imlEObject instanceof ImlType) {
			// use the serialization as name 
			if (imlEObject instanceof SimpleTypeReference && ((SimpleTypeReference) imlEObject).getTypeBinding().size() == 0) {
				NamedType type = ((SimpleTypeReference) imlEObject).getType();
				container = qnp.getFullyQualifiedName(type.eContainer());
				name = type.getName();
			} else {
				container = DEFAULT_CONTAINER;
//				container = qnp.getFullyQualifiedName(((SimpleTypeReference) imlEObject).getType().eContainer());					
				// Use the name exactly as declared 					
				name = ImlUtil.getTypeNameManually((ImlType) imlEObject, qnp);
			}
		} else if (imlEObject instanceof AtomicRelation) {
			container = qnp.getFullyQualifiedName(((AtomicRelation) imlEObject).getRelation().eContainer());
			if (((AtomicRelation) imlEObject).getRelation() instanceof Alias) {
				name = "alias_" + ImlUtil.getTypeNameManually(((AtomicRelation) imlEObject).getRelatedType(), qnp);
			} else if (((AtomicRelation) imlEObject).getRelation() instanceof Inclusion) {
				name = "extends_" + ImlUtil.getTypeNameManually(((AtomicRelation) imlEObject).getRelatedType(), qnp);
			} else {
				// TODO handle traits
			}
		} else if (imlEObject instanceof SymbolDeclaration) {
			container = qnp.getFullyQualifiedName(imlEObject.eContainer());
			if (imlEObject instanceof Assertion) {
				if (((SymbolDeclaration) imlEObject).getName() != null && !((SymbolDeclaration) imlEObject).getName().isEmpty()) {
					name = ((SymbolDeclaration) imlEObject).getName();
				} else {
					EObject eContainer = imlEObject.eContainer();
					int index = 0;
					if (eContainer instanceof Model) {
						index = ((Model) eContainer).getSymbols().indexOf(imlEObject);
					} else { // Should be NamedType
						index = ((NamedType) eContainer).getSymbols().indexOf(imlEObject);
					}
					name = ASSERTION_DEFAULT_NAME + index;
				}
			} else {
				name = ((SymbolDeclaration) imlEObject).getName();
			}
		} else if (imlEObject instanceof SymbolReferenceTerm) {
			container = qnp.getFullyQualifiedName(((SymbolReferenceTerm) imlEObject).getSymbol().eContainer());
			name = ((SymbolReferenceTerm) imlEObject).getSymbol().getName();
			if (!((SymbolReferenceTerm) imlEObject).getTypeBinding().isEmpty()) { // Add the symbolref type binding to the name
				name = name + "<";
				name = name + ((SymbolReferenceTerm) imlEObject).getTypeBinding().stream()
					.map(type -> ImlUtil.getTypeName(type, qnp))
					.reduce((curr, acc) ->  acc + ", " +  curr).get();
				name = name + ">";
			}
		} else if (imlEObject instanceof InstanceConstructorWithBinding) {
			container = DEFAULT_CONTAINER;
			name = "__some_" + ImlUtil.getTypeName(((InstanceConstructorWithBinding) imlEObject).getInstanceSTR(), qnp) 
				+ "_" + System.identityHashCode(((InstanceConstructorWithBinding) imlEObject).getInstanceConstructor()); // Use hashcode to identify each some
		} else if (imlEObject instanceof SymbolWithContext) {
			if (((SymbolWithContext) imlEObject).getContext() != null)
				container = qnp.getFullyQualifiedName(((SymbolWithContext) imlEObject).getContext().getType());
			SymbolReferenceTerm symbolRef = ((SymbolWithContext) imlEObject).getSymbol();
			name = symbolRef.getSymbol().getName();
			if (!symbolRef.getTypeBinding().isEmpty()) { // Add the symbolref type binding to the name
				name = name + "<";
				name = name + symbolRef.getTypeBinding().stream()
					.map(type -> ImlUtil.getTypeName(type, qnp))
					.reduce((curr, acc) ->  acc + ", " +  curr).get();
				name = name + ">";
			}
		}
	}
	
	public EObject getImlObject() {
		return imlObject;
	}

	public void setImlObject(EObject imlObject) {
		this.imlObject = imlObject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((container == null) ? 0 : container.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EncodedId))
			return false;
		if ((container == null && ((EncodedId) obj).getContainer() != null) ||
			(container != null && ((EncodedId) obj).getContainer() == null)) {
			return false;
		}
		if (container != null && !container.equals(((EncodedId) obj).getContainer()))
			return false;
		if (!name.equals(((EncodedId) obj).getName()))
			return false;
		
		return true;
	}

	public QualifiedName getContainer() {
		return container;
	}

	public void setContainer(QualifiedName container) {
		this.container = container;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String stringId() {
		if (container == null || container.isEmpty()) {
			return name;
		} else {
			return (container.toString() + "." + name);
		}
	}
	
}
