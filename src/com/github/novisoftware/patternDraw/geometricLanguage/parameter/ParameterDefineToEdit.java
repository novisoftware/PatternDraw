package com.github.novisoftware.patternDraw.geometricLanguage.parameter;

import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;

public class ParameterDefineToEdit {
	/**
	 * 変数名
	 */
	public String name;
	/**
	 * 説明
	 */
	public String description;
	/**
	 * デフォルト値
	 */
	public String defaultValue;

	/**
	 * 型
	 */
	public ValueType valueType;

	/**
	 * 列挙入力を有効にする場合の選択値
	 */
	boolean enableEnum;

	/**
	 * 列挙入力を有効にする場合の選択値
	 */
	public String enumValueList;

	/**
	 * スライダーを有効にする
	 */
	boolean enableSlider;

	/**
	 * スライダー最小値
	 */
	public String sliderMin;

	/**
	 * スライダー最大値
	 */
	public String sliderMax;

	/**
	 *
	 * @param name 変数名
	 * @param description 説明
	 * @param defaultValue デフォルト値
	 * @param valueType 型
	 * @param enableEnum 列挙入力有効フラグ
	 * @param enumValueList enumValueList
	 * @param enableSlider スライダー有効フラグ
	 * @param sliderMin スライダー最小値
	 * @param sliderMax スライダー最大値
	 */
	public ParameterDefineToEdit(
			/**
			 * 変数名
			 */
			String name,
			/**
			 * 説明
			 */
			String description,
			/**
			 * デフォルト値
			 */
			String defaultValue,

			/**
			 * 型
			 */
			ValueType valueType,

			/**
			 * 列挙入力有効フラグ
			 */
			boolean enableEnum,

			/**
			 * 列挙入力を有効にする場合の選択値
			 */
			String enumValueList,

			/**
			 * スライダー有効フラグ
			 */
			boolean enableSlider,

			/**
			 * スライダー最小値
			 */
			String sliderMin,

			/**
			 * スライダー最大値
			 */
			String sliderMax) {
		this.name = name;
		this.description = description;
		this.defaultValue = defaultValue;
		this.valueType = valueType;
		this.enableEnum = enableEnum;
		this.enumValueList = enumValueList;
		this.enableSlider = enableSlider;
		this.sliderMin = sliderMin;
		this.sliderMax = sliderMax;
	}

	public String str() {
		return String.format("PARAMETER: %s %s %s %s %s %s %s %s %s",
				FileReadUtil.escape(name),
				FileReadUtil.escape(description),
				FileReadUtil.escape(defaultValue),
				FileReadUtil.escape(Value.valueType2str.get(valueType)),
				FileReadUtil.escape(enableEnum),
				FileReadUtil.escape(enumValueList),
				FileReadUtil.escape(enableSlider),
				FileReadUtil.escape(sliderMin),
				FileReadUtil.escape(sliderMax));
	}

	public static ParameterDefineToEdit getParameterDefineToEdit(String s) {
		String a[] = FileReadUtil.tokenizeToArray(s);
		System.out.println("parameters (line) = " + s);
		System.out.println("parameters.len = " + a.length);
		System.out.println("parameters:");
		for (int i = 0 ; i < a.length; i++) {
			System.out.printf("[param def] %d \"%s\"\n", i, a[i]);
		}

		String name = a[1];
		String description = a[2];
		String defaultValue = a[3];
		ValueType valueType = Value.str2valueType.get(a[4]);
		Boolean enableEnum = FileReadUtil.booleanValue(a[5]);
		String enumValueList = a[6];
		Boolean enableSlider = FileReadUtil.booleanValue(a[7]);
		String sliderMin = a[8];
		String sliderMax = a[9];

		return new ParameterDefineToEdit(
				name,
				description,
				defaultValue,
				valueType,
				enableEnum,
				enumValueList,
				enableSlider,
				sliderMin,
				sliderMax);
	}

	public ParameterDefineToEdit clone() {
		return new ParameterDefineToEdit(
				name,
				description,
				defaultValue,
				valueType,
				enableEnum,
				enumValueList,
				enableSlider,
				sliderMin,
				sliderMax);
	}

	public void updateTo(ParameterDefineToEdit o) {
		this.name=o.name;
		this.description=o.description;
		this.defaultValue=o.defaultValue;
		this.valueType=o.valueType;
		this.enableEnum=o.enableEnum;
		this.enumValueList=o.enumValueList;
		this.enableSlider=o.enableSlider;
		this.sliderMin=o.sliderMin;
		this.sliderMax=o.sliderMax;
	}
}
