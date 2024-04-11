package com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem;

import java.util.HashMap;

import com.github.novisoftware.patternDraw.gui.editor.util.Debug;


public class Value {

	/**
	 * 値の型
	 *
	 * <ul>
	 * <li> ANY	       何でもよい
	 * <li> NUMERIC    多倍長小数
	 * <li> INTEGER    多倍長整数
	 * <li> STRING     文字列
	 * <li> BOOLEAN    真偽値
	 * <li> POS_LIST   座標のリスト
	 * <li> LINE_LIST  線分のリスト
	 * <li> NONE       値が存在しない
	 * </ul>
	 *
	 */
	public enum ValueType {
		NUMERIC,
		FLOAT,
		INTEGER,
		BOOLEAN,
		STRING,
		POS_LIST,
		LINE_LIST,
		ANY,
		NONE
	}

	public static HashMap<String, ValueType> str2valueType;

	public static HashMap<ValueType, String> valueType2str;

	static {
		str2valueType = new HashMap<String, ValueType>();
		str2valueType.put("INTEGER", ValueType.INTEGER);
		str2valueType.put("FLOAT", ValueType.FLOAT);
		str2valueType.put("NUMERIC", ValueType.NUMERIC);
		str2valueType.put("BOOLEAN", ValueType.BOOLEAN);
		str2valueType.put("STRING", ValueType.STRING);
		str2valueType.put("POS_LIST", ValueType.POS_LIST);
		str2valueType.put("LINE_LIST", ValueType.LINE_LIST);
		str2valueType.put("ANY", ValueType.ANY);
		str2valueType.put("NONE", ValueType.NONE);

		valueType2str = new HashMap<ValueType, String>();
		valueType2str.put(ValueType.INTEGER  , "INTEGER");
		valueType2str.put(ValueType.NUMERIC  , "NUMERIC");
		valueType2str.put(ValueType.FLOAT    , "FLOAT");
		valueType2str.put(ValueType.BOOLEAN  , "BOOLEAN");
		valueType2str.put(ValueType.STRING   , "STRING");
		valueType2str.put(ValueType.POS_LIST   , "POS_LIST");
		valueType2str.put(ValueType.LINE_LIST  , "LINE_LIST");
		valueType2str.put(ValueType.ANY      , "ANY");
		valueType2str.put(ValueType.NONE     , "NONE");
	}


	public static boolean isAcceptable(ValueType send, ValueType accept) {
		Debug.println("VALUE", "SEND " + send + "  acccept " + accept);

		if (send.equals(ValueType.NONE)) {
			return false;
		}
		if (accept.equals(ValueType.ANY)) {
			return true;
		}
		if (send.equals(accept)) {
			return true;
		}
		if (send.equals(ValueType.INTEGER) && accept.equals(ValueType.NUMERIC)) {
			return true;
		}

		return false;

	}

	public ValueType valueType;

	public String toDebugString() {
		return this.valueType + "--" + this.toString();
	}

	static public Value createValue(ValueType valueKind, String s) {
		if (valueKind == ValueType.NUMERIC) {
			return new ValueNumeric(s);
		}
		if (valueKind == ValueType.FLOAT) {
			return new ValueFloat(s);
		}
		if (valueKind == ValueType.INTEGER) {
			return new ValueInteger(s);
		}
		if (valueKind == ValueType.STRING) {
			return new ValueString(s);
		}
		if (valueKind == ValueType.BOOLEAN) {
			if (s.equals("TRUE")) {
				return new ValueBoolean(true);
			}
			else if(s.equals("FALSE")) {
				return new ValueBoolean(false);
			}
			else {
				return null;
			}
		}
		// 注:
		// 上記以外は本メソッドでは作成をサポートしない
		return null;
	}

	protected Value(ValueType valueKind) {
		this.valueType = valueKind;
	}

	public boolean sameTo(Object o) {
		return false;
	}
}
