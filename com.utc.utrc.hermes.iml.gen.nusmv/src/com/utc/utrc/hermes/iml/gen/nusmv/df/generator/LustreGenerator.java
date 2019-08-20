package com.utc.utrc.hermes.iml.gen.nusmv.df.generator;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;
import com.utc.utrc.hermes.iml.gen.nusmv.df.Node;
import com.utc.utrc.hermes.iml.gen.nusmv.df.SynchDf;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreElementType;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreModel;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreNode;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreSymbol;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreType;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreTypeInstance;
import com.utc.utrc.hermes.iml.gen.nusmv.df.model.LustreVariable;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.ComponentInstance;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.ComponentType;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Connection;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Direction;
import com.utc.utrc.hermes.iml.gen.nusmv.systems.Port;
import com.utc.utrc.hermes.iml.iml.Assertion;
import com.utc.utrc.hermes.iml.iml.FolFormula;
import com.utc.utrc.hermes.iml.iml.ImlType;
import com.utc.utrc.hermes.iml.iml.InstanceConstructor;
import com.utc.utrc.hermes.iml.iml.NamedType;
import com.utc.utrc.hermes.iml.iml.Refinement;
import com.utc.utrc.hermes.iml.iml.Relation;
import com.utc.utrc.hermes.iml.iml.SignedAtomicFormula;
import com.utc.utrc.hermes.iml.iml.SimpleTypeReference;
import com.utc.utrc.hermes.iml.iml.Symbol;
import com.utc.utrc.hermes.iml.iml.SymbolDeclaration;
import com.utc.utrc.hermes.iml.iml.TailedExpression;
import com.utc.utrc.hermes.iml.iml.TermExpression;
import com.utc.utrc.hermes.iml.iml.Trait;
import com.utc.utrc.hermes.iml.iml.TraitExhibition;
import com.utc.utrc.hermes.iml.iml.TupleConstructor;
import com.utc.utrc.hermes.iml.iml.TypeWithProperties;
import com.utc.utrc.hermes.iml.lib.ImlStdLib;
import com.utc.utrc.hermes.iml.typing.ImlTypeProvider;
import com.utc.utrc.hermes.iml.typing.TypingEnvironment;
import com.utc.utrc.hermes.iml.util.ImlUtil;
import com.utc.utrc.hermes.iml.util.Phi;

public class LustreGenerator {

	@Inject
	ImlTypeProvider typeProvider;

	@Inject
	LustreGeneratorServices generatorServices;

	@Inject
	private ImlStdLib stdLibs;

	@Inject
	@Extension
	private IQualifiedNameProvider qnp;

	private Configuration conf;

	private SynchDf sdf;

	public LustreGenerator() {
	}

	public LustreGenerator(Configuration conf, SynchDf sdf) {
		this.conf = conf;
		this.sdf = sdf;
	}

	public void setSdf(SynchDf sdf) {
		this.sdf = sdf;
	}

	public LustreNode getMainNode(LustreModel m, SimpleTypeReference spec, SimpleTypeReference impl) {
		/*
		NuSmvModule main = new NuSmvModule("main");
		NuSmvModule insttype = null ;
		NuSmvModule spectype = null ;
		if ( m.hasType(ImlUtil.getTypeName(impl, qnp)) ) {
			insttype = m.getType(ImlUtil.getTypeName(impl, qnp)) ;
		} else {
			insttype = generateStateMachine(m, impl) ;
		}
		if ( m.hasType(ImlUtil.getTypeName(spec, qnp)) ) {
			spectype = m.getType(ImlUtil.getTypeName(spec, qnp)) ;
		} else {
			spectype = generateStateMachine(m, spec) ;
		}
		
		NuSmvSymbol inst = new NuSmvSymbol("inst");
		NuSmvTypeInstance instti = new NuSmvTypeInstance(insttype);
		inst.setType(instti);
		inst.setElementType(NuSmvElementType.VAR);
		main.addSymbol(inst);
		
		
		for (NuSmvSymbol in : spectype.getParameters() ) {
			NuSmvSymbol target = new NuSmvSymbol( in.getName() );
			NuSmvTypeInstance ti = new NuSmvTypeInstance(in.getType().getType()) ;
			target.setType(ti);
			target.setElementType(NuSmvElementType.VAR);
			main.addSymbol(target);
			instti.setParam(insttype.paramIndex(in.getName()), new NuSmvVariable(in.getName()));
		}
		
		m.addModule(main);
		return main;
		*/
		return null ;
	}

	public LustreNode generateLustreNode(LustreModel m, ImlType t) {
		if (t instanceof SimpleTypeReference) {
			return generateLustreNode(m, sdf.getNode(t));
		}
		return (new LustreNode("__EMPTY__"));
	}

	public LustreNode generateLustreNode(LustreModel m, Node node) {

		String type_name = ImlUtil.getTypeName(node.getNodeType(), qnp);
		if (m.hasType(type_name))
			return m.getType(type_name);

		LustreNode target = new LustreNode(type_name);
		m.addNode(target);

		SimpleTypeReference tr = node.getNodeType();
		if (node.isComponent()) {
			ComponentType ct = node.getComponentType();
			for (Port p : ct.getPorts(Direction.IN)) {
				generateInput(target, p);
			}
			for (Port p : ct.getPorts(Direction.OUT)) {
				generateOutput(target, p, tr);
			}
			for (ComponentInstance sub : ct.getSubs().values()) {
				LustreNode gensm = generateLustreNode(target.getContainer(),
						sdf.getNode(sub.getComponentType().getType()));
				addSymbol(target, sub.getName(), gensm, LustreElementType.COMPONENT);
			}
			// add all connections
			for (Connection conn : ct.getConnections().values()) {
				generateConnection(target, conn, tr);
			}

			// add all other symbols
			TypingEnvironment typing = new TypingEnvironment(tr);
			for (SymbolDeclaration sd : ct.getOtherSymbols()) {
				if (! sdf.isLet(sd)) {
					if (sd.getType() instanceof SimpleTypeReference) {
						generateType(m, (SimpleTypeReference) typing.bind(sd.getType()));
						addSymbol(target, sd, tr);
					}
				}
			}
		} else {
			for (Symbol sd : node.getNodeType().getType().getSymbols()) {
				if (sd instanceof SymbolDeclaration) {
					if (! sdf.isLet( (SymbolDeclaration) sd)) {
						if ( ((SymbolDeclaration)sd).getType() instanceof SimpleTypeReference) {
							addSymbol(target, (SymbolDeclaration) sd, tr);
						}
					}
				}
			}
		}
		
		for(String s : node.getLets().keySet()) {
			LustreSymbol symbol = addSymbol(target, s, m.Bool, LustreElementType.LET);
			symbol.setDefinition(generatorServices.serialize(node.getLets().get(s), tr, "."));
		}
		return target;
	}

	public LustreNode generateType(LustreModel m, SimpleTypeReference tr) {
		// start from the definition
		String type_name = ImlUtil.getTypeName(tr, qnp);
		if (m.hasType(type_name))
			return m.getType(type_name);

		if (ImlUtil.isEnum(tr.getType())) {
			return generateEnumType(m, tr.getType());
		} else {
			for (ImlType b : tr.getTypeBinding()) {
				if (b instanceof SimpleTypeReference) {
					generateType(m, (SimpleTypeReference) b);
				}
			}
			LustreNode target = new LustreNode(type_name);
			m.addNode(target);
			target.setStruct(true);
			
			for (Symbol s : tr.getType().getSymbols()) {
				if (s instanceof SymbolDeclaration) {
					SymbolDeclaration sd = (SymbolDeclaration) s;
					addSymbol(target, sd, tr);
				}
			}
			
			// process relations
			gatherFromRelation(m, tr, target);

			return target;
		}
	}

	private void gatherFromRelation(LustreModel m, SimpleTypeReference str, LustreNode target) {
		String type_name = ImlUtil.getTypeName(str, qnp);
		for (Symbol s : str.getType().getSymbols()) {
			if (s instanceof SymbolDeclaration) {
				SymbolDeclaration sd = (SymbolDeclaration) s;
				addSymbol(target, sd, str);
			}
		}	
		TypingEnvironment tEnv = new TypingEnvironment(str);
		for (Relation rl : str.getType().getRelations()) {
			if (rl instanceof TraitExhibition) {
				TraitExhibition tr = (TraitExhibition) rl;
				for (TypeWithProperties twp : tr.getExhibitions()) {
					if (twp.getType() instanceof SimpleTypeReference) {
						ImlType boundType = tEnv.bind(twp.getType());
						gatherFromRelation(m, (SimpleTypeReference) boundType, target);
					}
				}
			}
			else if (rl instanceof Refinement) {
				Refinement tr = (Refinement) rl;
				for (TypeWithProperties twp : tr.getRefinements()) {
					if (twp.getType() instanceof SimpleTypeReference) {
						ImlType boundType = tEnv.bind(twp.getType());
						gatherFromRelation(m, (SimpleTypeReference) boundType, target);
					}
				}				
			}
		}		
	}

	public LustreSymbol addSymbol(LustreNode target, SymbolDeclaration sd, SimpleTypeReference ctx) {
		TypingEnvironment typing = new TypingEnvironment(ctx);
		ImlType bound = null;
		String name = null;
		if (sd instanceof Assertion) {
			if (sd.getName() != null) {
				name = sd.getName();
			} else {
				name = target.getContainer().newSymbolName();
			}
			return addSymbol(target, name, target.getContainer().getType("iml.lang.Bool"), LustreElementType.ASSERTION,
					generatorServices.serialize(sd.getDefinition(), ctx, "."));
		} else {
			name = sd.getName();
			bound = typing.bind(sd.getType());
			if (bound instanceof SimpleTypeReference) {
				LustreNode nbound = generateType(target.getContainer(), (SimpleTypeReference) bound);
				LustreSymbol toadd = addSymbol(target, name, nbound, LustreElementType.FIELD);
				if (sd.getDefinition() != null) {
					toadd.setDefinition(generatorServices.serialize(sd.getDefinition(), ctx, ".") );
				}
				return toadd ;
			}

		}
		return (new LustreSymbol("__ERROR__"));
	}

	private LustreNode generateEnumType(LustreModel m, NamedType type) {
		LustreNode target = new LustreNode(qnp.getFullyQualifiedName(type).toString());
		m.addNode(target);
		target.setEnum(true);
		target.getLiterals().addAll(ImlUtil.getLiterals(type));
		return target;
	}

	private LustreSymbol generateInput(LustreNode m, Port p) {

		if (p.getType() instanceof SimpleTypeReference) {
			LustreSymbol target = new LustreSymbol(p.getName());
			LustreNode nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getType());
			LustreTypeInstance ti = new LustreTypeInstance(nbound);
			target.setType(ti);
			target.setElementType(LustreElementType.PARAMETER);
			m.addSymbol(target);
			return target;
		}

		return new LustreSymbol("__UNSUPPORTED__");
	}
	
	private LustreSymbol generateOutput(LustreNode m, Port p, SimpleTypeReference ctx) {

		if (p.getType() instanceof SimpleTypeReference) {
			LustreSymbol target = new LustreSymbol(p.getName());
			LustreNode nbound = generateType(m.getContainer(), (SimpleTypeReference) p.getType());
			LustreTypeInstance ti = new LustreTypeInstance(nbound);
			target.setType(ti);
			if (p.getDefinition() != null) {
				// TODO need to generalize this
				if (p.getDefinition() instanceof SignedAtomicFormula) {
					if (p.getDefinition().getLeft() instanceof InstanceConstructor) {
						InstanceConstructor cons = (InstanceConstructor) p.getDefinition().getLeft();
						SymbolDeclaration var = cons.getRef();
						Map<Symbol, String> remap = new HashMap<>();
						remap.put(var, p.getName());
						String expr = generatorServices.serialize(cons.getDefinition(), ctx, remap, ".") ;
						target.setDefinition(expr.replace(p.getName() + "=", ""));
					}
				}
			}
			target.setElementType(LustreElementType.RETURN);
			
			m.addSymbol(target);
			return target;
		}

		return new LustreSymbol("__UNSUPPORTED__");
	}

	

	private void generateConnection(LustreNode m, Connection conn, SimpleTypeReference tr) {
		// If this is a connection to an output of the current machine
		// simply add a define
		if (conn.getTargetComponent() == ComponentInstance.self) {
			// Need to take the output symbol
			LustreSymbol out = m.getVariables().get(conn.getTargetPort().getName());
//			if (out != null) {
			if (out == null) {
				LustreSymbol toadd = new LustreSymbol("");
				FolFormula def = Phi.eq(
						EcoreUtil.copy(
								(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
										.getDefinition().getLeft()).getTail()).getElements().get(0).getLeft()),
						EcoreUtil.copy(
								(TermExpression) ((TupleConstructor) ((TailedExpression) conn.getSymbolDeclaration()
										.getDefinition().getLeft()).getTail()).getElements().get(1).getLeft()));
				toadd.setName(m.getContainer().newSymbolName());
				toadd.setElementType(LustreElementType.LET);
				toadd.setDefinition(generatorServices.serialize(def, tr, "_"));
				m.addSymbol(toadd);
			}
		} else {
			// otherwise
			LustreSymbol machine = m.getComponents().get(conn.getTargetComponent().getName());
			if (machine != null) {
				int index = machine.getType().getType().paramIndex(conn.getTargetPort().getName());
				if (index != -1) {

					String portname = "";
					if (conn.getSourceComponent() != ComponentInstance.self) {
						portname += conn.getSourceComponent().getName() + "_";
					}
					portname += conn.getSourcePort().getName();

					LustreVariable param = new LustreVariable(portname);
					machine.getType().setParam(index, param);
					;
				}
			}
		}

	}

	

	public boolean isDelay(SymbolDeclaration s) {
		return ImlUtil.hasType(s.getType(), stdLibs.getNamedType("iml.sms", "delay"));
	}

	public boolean isInput(SymbolDeclaration sd) {

			if ( ImlUtil.exhibits(sd.getType(), (Trait) stdLibs.getNamedType("iml.systems", "In")   ) ) {
				return true ;
			}
		
		return false ;
	}
	
	public boolean isDelay(ImlType st) {
		return (st == stdLibs.getNamedType("iml.sms", "delay"));
	}

	public boolean isSimpleTypeReference(ImlType imlType) {
		return (imlType instanceof SimpleTypeReference);
	}

	public Configuration getConf() {
		return conf;
	}

	public void setConf(Configuration conf) {
		this.conf = conf;
	}

	public LustreSymbol addSymbol(LustreNode container, String name, LustreNode type, LustreElementType et,
			String definition) {
		LustreSymbol target = addSymbol(container, name, type, et);
		target.setDefinition(definition);
		return target;
	}

	public LustreSymbol addSymbol(LustreNode container, String name, LustreNode type, LustreElementType et) {
		LustreSymbol target = new LustreSymbol(name);
		LustreTypeInstance ti = new LustreTypeInstance(type);
		target.setType(ti);
		target.setElementType(et);
		container.addSymbol(target);
		return target;
	}
	
	public String serialize(LustreModel m) {
		return generatorServices.serialize(m);
	}

	
	public String serialize(LustreNode n) {
		return generatorServices.serialize(n);
	}


}
