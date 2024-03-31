package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

import java.util.ArrayList;

public class EnumParameter extends Parameter {
	public ArrayList<String> enums;

	/**
	 *
	 * @param name
	 * @param description
	 * @param defaultValue
	 * @param enums
	 */
	public EnumParameter(String name, String description, String defaultValue, ArrayList<String> enums) {
		super(name, description, defaultValue);
		this.enums = enums;
	}
}
