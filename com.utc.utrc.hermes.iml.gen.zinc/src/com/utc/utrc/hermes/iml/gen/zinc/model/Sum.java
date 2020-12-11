/**
 * Copyright Siemens AG, 2016-2017
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.utc.utrc.hermes.iml.gen.zinc.model;

import at.siemens.ct.jmz.expressions.array.ArrayExpression;

/**
 * Represents the sum over an array of integers.
 *
 * @author Copyright Siemens AG, 2016-2017
 */
public class Sum implements FloatExpression {

	private ArrayExpression<Float> summands;

	public Sum(ArrayExpression<Float> summands) {
		this.summands = summands;
	}

	@Override
	public String toString() {
		return use();
	}

	@Override
	public String use() {
		return String.format("sum(%s)", summands.use());
	}

  @Override
  public Sum substitute(String name, Object value) {
    return new Sum(summands.substitute(name, value));
  }

}
