package com.github.novisoftware.patternDraw.core.langSpec.typeSystem;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.utils.Debug;


public class Value {
	/**
	 * 値の型。
	 * 単精度浮動小数点や、精度に限定のある整数型は使用しない。
	 * 大は小を兼ねるというか、この処理系は遅いので、単精度を使っても速くなる期待がないため。
	 *
	 * <ul>
	 * <li> ANY	       何でもよい
	 * <li> SCALAR     FLOAT, NUMERIC, INTEGER のいずれかを受け入れる
	 * <li> FLOAT      倍精度浮動小数点(注: 単精度をサポートしない)
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
		SCALAR,
		NUMERIC,
		FLOAT,
		INTEGER,
		BOOLEAN,
		STRING,
		POS_LIST,
		LINE_LIST,
		ANY,
		NONE,
		UNDEF
	}

	public ValueType valueType;

	protected Value(ValueType valueKind) {
		this.valueType = valueKind;
	}

	public static String valueTypeToDescString(ValueType valueType) {
		if (ValueType.SCALAR.equals(valueType)) {
			return "数値";
		}
		if (ValueType.NUMERIC.equals(valueType)) {
			return "任意精度実数";
		}
		if (ValueType.FLOAT.equals(valueType)) {
			return "浮動小数点数";
		}
		if (ValueType.BOOLEAN.equals(valueType)) {
			return "ブール値";
		}
		if (ValueType.INTEGER.equals(valueType)) {
			return "整数";
		}
		if (ValueType.STRING.equals(valueType)) {
			return "文字列";
		}
		if (ValueType.POS_LIST.equals(valueType)) {
			return "点";
		}
		if (ValueType.LINE_LIST.equals(valueType)) {
			return "線";
		}
		if (ValueType.ANY.equals(valueType)) {
			return "任意";
		}
		if (ValueType.NONE.equals(valueType)) {
			return "値なし";
		}
		if (ValueType.UNDEF.equals(valueType)) {
			// TODO
			// UNDEFは、入力が無くて決まらない等で使用する。
			// 表示までされることがあるかどうか。
			return "値なし";
		}

		// こない
		return "";
	}

	public static HashMap<String, ValueType> str2valueType;

	public static HashMap<ValueType, String> valueType2str;

	static {
		str2valueType = new HashMap<String, ValueType>();
		str2valueType.put("SCALAR", ValueType.SCALAR);
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
		valueType2str.put(ValueType.SCALAR  , "SCALAR");
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


	public static boolean isAcceptable(ValueType send, ValueType receive) {
		Debug.println("VALUE", "SEND " + send + "  acccept " + receive);

		if (receive.equals(ValueType.SCALAR)) {
			if (send.equals(ValueType.FLOAT)
					|| send.equals(ValueType.INTEGER)
					|| send.equals(ValueType.NUMERIC)
					|| send.equals(ValueType.SCALAR)) {
				return true;
			} else {
				return false;
			}
		}

		if (send.equals(ValueType.NONE)) {
			return false;
		}
		if (receive.equals(ValueType.ANY)) {
			return true;
		}
		if (send.equals(receive)) {
			return true;
		}
		if (send.equals(ValueType.INTEGER) && receive.equals(ValueType.NUMERIC)) {
			return true;
		}

		return false;

	}

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
			String s_ = s.toLowerCase();
			if (s.equals(ValueBoolean.LABEL_TRUE)) {
				return ValueBoolean.TRUE;
			}
			else if(s.equals(ValueBoolean.LABEL_FALSE)) {
				return ValueBoolean.FALSE;
			}
			else {
				return null;
			}
		}
		// 注:
		// 上記以外は本メソッドでは作成をサポートしない
		return null;
	}

	/**
	 * @throws CaliculateException
	 */
	public static BigInteger getInteger(Value v) throws CaliculateException {
		try {
			return ((ValueInteger)v).getInternal();
		} catch(ClassCastException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
		}
	}

	/**
	 * @throws CaliculateException
	 */
	public static Double getDouble(Value v) throws CaliculateException {
		try {
			return ((ValueFloat)v).getInternal();
		} catch(ClassCastException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
		}
	}

	/**
	 * @throws CaliculateException
	 */
	public static BigDecimal getDecimal(Value v) throws CaliculateException {
		try {
			return ((ValueNumeric)v).getInternal();
		} catch(ClassCastException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
		}
	}

	/**
	 * @throws CaliculateException
	 */
	public static ArrayList<Pos> getPosList(Value v) throws CaliculateException {
		try {
			return ((ValuePosList)v).getInternal();
		} catch(ClassCastException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
		}
	}

	/**
	 * @throws CaliculateException
	 */
	public static ArrayList<Line> getLineList(Value v) throws CaliculateException {
		try {
			return ((ValueLineList)v).getInternal();
		} catch(ClassCastException e) {
			throw new CaliculateException(CaliculateException.MESSAGE_INVALID_CLASS);
		}
	}

	public boolean sameTo(Object o) {
		return false;
	}
}
