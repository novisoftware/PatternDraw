package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;


public class ValuePosList extends Value {
	public ValuePosList(ArrayList<Pos> p) {
		super(ValueType.POS_LIST);
		internal = p;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private ArrayList<Pos> internal;

	public ArrayList<Pos> getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return false;
	}
}
