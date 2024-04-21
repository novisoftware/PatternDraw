package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

/**
 * スクリプトを動作させる前に外から与えることができるパラメーター。
 * <ul>
 * <li> 変数名。
 * <li> 設定する人間が読むための説明。
 * <li> 設定しなかったときのためのデフォルト値。
 * </ul>
 *
 */
public class Parameter {
	/**
	 * 変数名
	 */
	private String name;
	/**
	 * 説明
	 */
	private String description;
	/**
	 * デフォルト値
	 */
	private String defaultValue;

	/**
	 * 選択時の列挙値
	 */
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
	 * @return name パラメーターの変数名
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
