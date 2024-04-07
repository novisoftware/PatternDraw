package com.github.novisoftware.patternDraw.gui.editor.util;

public class ValueBoolean extends Value {
	public ValueBoolean(Boolean b) {
		super(ValueType.BOOLEAN);
		internal = b;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private Boolean internal;

	public Boolean getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}
}
