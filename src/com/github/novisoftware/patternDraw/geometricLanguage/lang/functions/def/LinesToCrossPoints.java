package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;

// line_to_draw
public class LinesToCrossPoints implements FunctionDefInterface {
	public static final String NAME = "lines_to_cross_points";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線分の交点";
	}

	@Override
	public String getDescription() {
		return "2系列の線分が交わる点のあつまりを抽出します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.LINE_LIST, ValueType.LINE_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"lines1", "lines2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"線分のリスト1", "線分のリスト2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Line> lineList1 = Value.getLineList(param.get(0));
		ArrayList<Line> lineList2 = Value.getLineList(param.get(1));

		ArrayList<Pos> posList = new ArrayList<Pos>();

		for (Line line1 : lineList1) {
			for (Line line2 : lineList2) {
				Pos p = line1.crossPoint(line2);
				if (p != null) {
					posList.add(p);
				}
			}
		}

		return new ValuePosList(posList);
	}
}
