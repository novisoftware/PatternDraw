package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class LineFrom1Series implements FunctionDefInterface {
	public static final String NAME = "line_from_1_series";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線でなぞる";
	}

	@Override
	public String getDescription() {
		return "点を線で結びます。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));

		ArrayList<Line> ret = new ArrayList<Line>();
		int n = posList.size();
		for (int i = 1; i < n; i++) {
			ret.add(new Line(posList.get(i - 1), posList.get(i)));
		}

		return new ValueLineList(ret);
	}
}
