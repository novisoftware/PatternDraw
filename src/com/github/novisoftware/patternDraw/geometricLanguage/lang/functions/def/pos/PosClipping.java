package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.PosClipUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

// pos_clipping
public class PosClipping implements FunctionDefInterface {
	public static final String NAME = "pos_clipping";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "点のクリッピング";
	}

	@Override
	public String getDescription() {
		return "点の並びをクリッピングします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"pos1", "pos2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"クリッピング内側", "クリッピング外側"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		Value p0 = param.get(0);
		Value p1 = param.get(1);
		if (p0 == null || p1 == null) {
			throw new CaliculateException("入力が設定されていません。");
		}

		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));
		ArrayList<Pos> newPosList = PosClipUtil.clip(posList1, posList2);

		return new ValuePosList(newPosList);
		
	}
}
