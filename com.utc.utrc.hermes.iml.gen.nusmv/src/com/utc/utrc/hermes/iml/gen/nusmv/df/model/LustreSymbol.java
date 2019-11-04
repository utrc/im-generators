package com.utc.utrc.hermes.iml.gen.nusmv.df.model;

import java.util.ArrayList;
import java.util.List;

import com.utc.utrc.hermes.iml.iml.FolFormula;

public class LustreSymbol {
	private LustreElementType elementType ;
	private String name ;
	private LustreTypeInstance type ;
	private String definition;
	private List<LustreVariable> parameters ;
	
	
	private static int id = 0;
	
	private LustreNode container ;
	
	public LustreSymbol(String name) {
		this.name = name;
		container = null ;
		parameters = new ArrayList<LustreVariable>() ;
		elementType = LustreElementType.VAR ;
	}
	
	public LustreSymbol(LustreSymbol other) {
		elementType = other.elementType;
		name = other.name;
		type = new LustreTypeInstance(other.type);
		definition = other.definition;
		parameters = new ArrayList<LustreVariable>() ;
		for(LustreVariable v : parameters) {
			parameters.add(new LustreVariable(v));
		}
		
	}
	
	public LustreElementType getElementType() {
		return elementType;
	}

	public void setElementType(LustreElementType elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LustreTypeInstance getType() {
		return type;
	}

	public void setType(LustreTypeInstance type) {
		this.type = type;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public LustreNode getContainer() {
		return container;
	}

	public void setContainer(LustreNode container) {
		this.container = container;
	}
	
	public List<LustreVariable> getParameters(){
		return parameters;
	}
	public void addParameter(LustreVariable v) {
		parameters.add(v);
	}
	
	public int indexOf(String pname) {
		for(int index = 0 ; index < parameters.size() ; index++) {
			if (parameters.get(index).getName().equals(pname)) {
				return index ;
			}
		}
		return -1;
	}
	
	
	
	
}
