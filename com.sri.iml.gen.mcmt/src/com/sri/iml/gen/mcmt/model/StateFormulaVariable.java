package com.sri.iml.gen.mcmt.model;

/* This empty interface is simply there to group together those classes that
can be used as variables in a StateFormula
	Classes implementing that interface:
	- Input
	- StateVariable
	- NamedStateFormula */

public interface StateFormulaVariable {
	public StateTransFormulaVariable convert(StateNext which);
}
