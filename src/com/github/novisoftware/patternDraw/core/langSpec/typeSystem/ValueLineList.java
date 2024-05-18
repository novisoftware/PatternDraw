package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;

public class ValueLineList extends Value {
	public ValueLineList(ArrayList<Line> lineList) {
		super(ValueType.POS_LIST);
		internal = lineList;
	}

	@Override
	public String toString() {
		return internal.toString();
	}

	private ArrayList<Line> internal;

	public ArrayList<Line> getInternal() {
		return internal;
	}

	public boolean sameTo(Object o) {
		return false;
	}
}
