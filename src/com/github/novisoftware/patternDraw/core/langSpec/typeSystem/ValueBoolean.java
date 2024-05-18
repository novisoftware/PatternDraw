package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

public class ValueBoolean extends Value {
	public static final String LABEL_TRUE = "true";
	public static final String LABEL_FALSE = "false";

	public static final ValueBoolean TRUE = new ValueBoolean(true);
	public static final ValueBoolean FALSE = new ValueBoolean(false);

	public ValueBoolean(Boolean b) {
		super(ValueType.BOOLEAN);
		internal = b;
	}

	public ValueBoolean(String s) {
		super(ValueType.BOOLEAN);
		if (s.toLowerCase().equals(ValueBoolean.LABEL_TRUE)) {
			internal = new Boolean(true);
		}
		else {
			internal = new Boolean(false);
		}
	}

	@Override
	public String toString() {
		return internal ? LABEL_TRUE : LABEL_FALSE;
	}

	private Boolean internal;

	public Boolean getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return internal.equals(o);
	}
}
