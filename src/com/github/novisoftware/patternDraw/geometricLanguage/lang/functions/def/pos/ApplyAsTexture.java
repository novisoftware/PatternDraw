package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.PosUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class ApplyAsTexture implements FunctionDefInterface {
	public static final String NAME = "pos_apply_as_texture";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "系列の当てはめ";
	}

	@Override
	public String getDescription() {
		return "点の系列を別の系列に当てはめ、繰り返します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"initiator", "generator"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"系列1(ベースの系列)", "系列2(あてはめる系列)"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> basePosList = Value.getPosList(param.get(0));
		ArrayList<Pos> patternPosList = Value.getPosList(param.get(1));

		return new ValuePosList(PosUtil.applyAsTexture(basePosList, patternPosList));
	}
}
