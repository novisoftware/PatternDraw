package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.line;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class LineFrom2Series implements FunctionDefInterface {
	public static final String NAME = "line_from_2_series";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "2系列から線分";
	}

	@Override
	public String getDescription() {
		return "2系列の点から線の系列を作成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions1", "positions2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"始点の系列", "終点の系列"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));

		ArrayList<Line> ret = new ArrayList<Line>();
		int n = posList1.size();
		int m = posList2.size();
		int loopLen = n > m ? n : m;
		for (int i = 0; i < loopLen; i++) {
			ret.add(new Line(posList1.get(i % n), posList2.get(i % m)));
		}

		return new ValueLineList(ret);
	}
}
