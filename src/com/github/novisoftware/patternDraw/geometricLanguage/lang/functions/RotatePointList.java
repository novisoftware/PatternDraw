package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDef;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValueLineList;

public class RotatePointList  implements FunctionDef {
	public static final String NAME = "rotate_point_list";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "線分の系列の並び順をローテートします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.LINE_LIST};
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
	public Value exec(List<Value> param, InstructionRenderer t) {
		/*
		 * 系列をローテートさせる
		 */
		int n = ((ValueInteger)(param.get(0))).getInternal().intValue();
		ArrayList<Line> a = ((ValueLineList)(param.get(1))).getInternal();
		Collections.rotate((ArrayList<Line>)a.clone(), n);

		return new ValueLineList(a);
	}
}
