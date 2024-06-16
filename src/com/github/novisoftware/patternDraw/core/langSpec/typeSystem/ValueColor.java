package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

public class ValueColor extends Value {
	public ValueColor(java.awt.Color c) {
		super(ValueType.COLOR);
		internal = c;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private java.awt.Color internal;

	public java.awt.Color getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		if (!(o instanceof java.awt.Color)) {
			return false;
		}
		return this.internal.equals((java.awt.Color)o);
	}
}
