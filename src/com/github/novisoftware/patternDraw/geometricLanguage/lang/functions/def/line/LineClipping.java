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
import com.github.novisoftware.patternDraw.geometricLanguage.entity.PosClipUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class LineClipping  implements FunctionDefInterface {
	public static final String NAME = "line_clipping";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "線分のクリッピング";
	}

	@Override
	public String getDescription() {
		return "線分の系列を点の並びで囲まれる図形でクリッピングします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.LINE_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"input", "region"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"線分の系列", "クリッピング領域"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Line> lineList = Value.getLineList(param.get(0));
		ArrayList<Pos> region = Value.getPosList(param.get(1));

		return new ValueLineList(PosClipUtil.lineClip(lineList, region, false));
	}
}
