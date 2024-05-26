package com.github.novisoftware.patternDraw.core.langSpec.functions;

import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;

public interface FunctionDefInterface {
	/**
	 * @return 関数名
	 */
	String getName();

	/**
	 * @return 関数名(表示用)
	 */
	String getDisplayName();

	/**
	 * @return 説明文
	 */
	String getDescription();

	/**
	 * 引数の型のリストを取得する。
	 * @return 引数の型のリスト
	 */
	ValueType[] getParameterTypes();

	/**
	 * 引数名のリストを取得する。
	 * @return 引数名のリスト
	 */
	String[] getParameterNames();

	/**
	 * 引数名のリストを取得する。
	 * @return 引数名のリスト
	 */
	String[] getParameterDescs();

	/**
	 * 返り値の型を取得する。
	 *
	 * @return 返り値の型
	 */
	ValueType getReturnType();

	/**
	 * 値を求める
	 * @param param
	 * @param t 副作用先
	 * @return
	 * @throws CaliculateException
	 */
	Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException;
}
