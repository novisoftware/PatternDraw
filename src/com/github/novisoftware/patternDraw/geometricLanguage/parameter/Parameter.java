package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

public class Parameter {
	private String name;
	private String description;
	private String defaultValue;

	public String[] enumValues;

	public Parameter(
			 String name,
			 String description,
			 String defaultValue
			) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @return defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param defaultValue セットする defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
