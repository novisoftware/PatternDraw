package com.github.novisoftware.patternDraw.geometricLanguage;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;

class ObjectHolder implements Cloneable {
	private final TypeDesc typeDesc;
	private final String as_string;
	private final ArrayList<Line> as_line;
	private final ArrayList<Pos> as_pos;

	ObjectHolder(String s) {
		this.typeDesc = TypeDesc.STRING;
		this.as_line = null;
		this.as_pos = null;
		this.as_string = s;
	}

	ObjectHolder(Line dummy, ArrayList<Line> p) {
		this.typeDesc = TypeDesc.LINE_LIST;
		this.as_line = p;
		this.as_pos = null;
		this.as_string = null;
	}

	ObjectHolder(Pos dummy, ArrayList<Pos> p) {
		this.typeDesc = TypeDesc.POS_LIST;
		this.as_line = null;
		this.as_pos = p;
		this.as_string = null;
	}

	/**
	 * @return typeDesc
	 */
	public TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * @return as_line
	 */
	public ArrayList<Line> getAs_line() {
		return as_line;
	}

	/**
	 * @return as_pos
	 */
	public ArrayList<Pos> getAs_pos() {
		return as_pos;
	}

	/**
	 * @return as_string
	 */
	public String getAs_string() {
		return as_string;
	}

	double getDoubleValue() {
		return Double.parseDouble(this.as_string);
	}

	int getIntValue() {
		return Integer.parseInt(this.as_string);
	}

	public ObjectHolder clone() {
		if (this.typeDesc == TypeDesc.STRING) {
			return new ObjectHolder(this.as_string);
		} else if (this.typeDesc == TypeDesc.LINE_LIST) {
			ArrayList<Line> a = new ArrayList<Line>();
			a.addAll(this.as_line);
			return new ObjectHolder(a.get(0), a);
		} else if (this.typeDesc == TypeDesc.POS_LIST) {
			ArrayList<Pos> a = new ArrayList<Pos>();
			a.addAll(this.as_pos);
			return new ObjectHolder(a.get(0), a);
		} else {
			// こない
			return null;
		}
	}

}