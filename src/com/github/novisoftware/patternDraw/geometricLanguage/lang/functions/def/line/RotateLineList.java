package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.line;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class RotateLineList  implements FunctionDefInterface {
	public static final String NAME = "rotate_line_list";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "並び順ローテート(線分)";
	}

	@Override
	public String getDescription() {
		return "線分の系列の並び順をローテートします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "positions"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"ローテートさせる数", "線分の系列"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		int n = Value.getInteger(param.get(0)).intValue();
		ArrayList<Line> a = Value.getLineList(param.get(1));
		/*
		 * 系列をローテートさせる
		 */
		Collections.rotate((ArrayList<Line>)a.clone(), n);

		return new ValueLineList(a);
	}

}
