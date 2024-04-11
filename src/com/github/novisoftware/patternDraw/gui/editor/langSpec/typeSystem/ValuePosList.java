package com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem;

import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
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
