package com.github.novisoftware.patternDraw.geometricLanguage;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;

public class ObjectHolder implements Cloneable {
	private final TypeDesc typeDesc;
	private final Double as_double;
	private final String as_string;
	private final ArrayList<Line> as_line;
	private final ArrayList<Pos> as_pos;
	private final ArrayList<Token> as_token;

	public ObjectHolder(Double d) {
		this.typeDesc = TypeDesc.DOUBLE;
		this.as_double = d;
		this.as_line = null;
		this.as_pos = null;
		this.as_string = null;
		this.as_token = null;
	}

	public ObjectHolder(String s) {
		this.typeDesc = TypeDesc.STRING;
		this.as_double = null;
		this.as_line = null;
		this.as_pos = null;
		this.as_string = s;
		this.as_token = null;
	}

	public ObjectHolder(Line dummy, ArrayList<Line> p) {
		this.typeDesc = TypeDesc.LINE_LIST;
		this.as_double = null;
		this.as_line = p;
		this.as_pos = null;
		this.as_string = null;
		this.as_token = null;
	}

	public ObjectHolder(Pos dummy, ArrayList<Pos> p) {
		this.typeDesc = TypeDesc.POS_LIST;
		this.as_double = null;
		this.as_line = null;
		this.as_pos = p;
		this.as_string = null;
		this.as_token = null;
	}

	public ObjectHolder(Token dummy, ArrayList<Token> p) {
		this.typeDesc = TypeDesc.POS_LIST;
		this.as_double = null;
		this.as_line = null;
		this.as_pos = null;
		this.as_string = null;
		this.as_token = p;
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
	 * @return as_token
	 */
	public ArrayList<Token> getAs_token() {
		return as_token;
	}

	/**
	 * @return as_string
	 */
	public String getAs_string() {
		return as_string;
	}

	/**
	 * @return as_double
	 * @throws InvaliScriptException
	 */
	public Double getAs_double() throws InvaliScriptException {
		if (this.typeDesc == TypeDesc.STRING) {
			return Double.parseDouble(this.as_string);
		}
		else if(this.typeDesc == TypeDesc.DOUBLE) {
			return this.as_double;
		}
		else {
			throw new InvaliScriptException("Invalid Type", null);
		}
	}

	double getDoubleValue() {
		return Double.parseDouble(this.as_string);
	}

	public int getIntValue() {
		return Integer.parseInt(this.as_string);
	}

	public ObjectHolder clone() {
		if (this.typeDesc == TypeDesc.DOUBLE) {
			return new ObjectHolder(this.as_double);
		} else if (this.typeDesc == TypeDesc.STRING) {
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