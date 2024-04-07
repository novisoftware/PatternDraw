package com.github.novisoftware.patternDraw.gui.editor.util;

public class ValueString extends Value {
	public ValueString(String s) {
		super(ValueType.STRING);
		internal = s;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private String internal;

	public String getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}
}
