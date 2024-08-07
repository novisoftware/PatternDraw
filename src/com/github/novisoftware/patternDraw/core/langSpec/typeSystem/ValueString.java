package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

public class ValueString extends Value {
	public ValueString(String s) {
		super(ValueType.STRING);
		internal = s;
		if (s == null) {
			// デバッグ用途
			internal = "NULL";
		}
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
