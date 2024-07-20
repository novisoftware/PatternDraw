package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class PosConcat implements FunctionDefInterface {
	public static final String NAME = "pos_concat";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "つなぐ";
	}

	@Override
	public String getDescription() {
		return "2つの座標の系列をつなぎます。";
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
		String[] ret = {"系列1", "系列2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList1 = Value.getPosList(param.get(0));
		ArrayList<Pos> posList2 = Value.getPosList(param.get(1));

		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		newPosList.addAll(posList1);
		newPosList.addAll(posList2);

		return new ValuePosList(newPosList);
	}
}
