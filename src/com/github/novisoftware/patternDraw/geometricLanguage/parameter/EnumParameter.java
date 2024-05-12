package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

import java.util.ArrayList;

public class EnumParameter extends Parameter {
	public ArrayList<String> opts;

	/**
	 *
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param opts
	 */
	public EnumParameter(String name, String description, String defaultValue, ArrayList<String> opts) {
		super(name, description, defaultValue);
		this.opts = opts;
	}
}
