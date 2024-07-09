package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class RotatePointList  implements FunctionDefInterface {
	public static final String NAME = "rotate_point_list";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "並び順ローテート(座標)";
	}

	@Override
	public String getDescription() {
		return "座標の系列の並び順をローテートします。";
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
		String[] ret = {"ローテートさせる数", "点の系列"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Value exec(List<Value> param, InstructionRenderer _t) throws CaliculateException {
		/*
		 * 系列をローテートさせる
		 */
		int n = Value.getInteger(param.get(0)).intValue();
		ArrayList<Pos> org = Value.getPosList(param.get(1));
		ArrayList<Pos> a = (ArrayList<Pos>)org.clone();
		Collections.rotate(a, n);

		return new ValuePosList(a);
	}
}
