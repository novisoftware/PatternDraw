package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

// line_to_draw
public class ClosePosList implements FunctionDefInterface {
	public static final String NAME = "close_pos_list";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "座標の系列を閉じる";
	}

	@Override
	public String getDescription() {
		return "座標の系列を操作し、最初の点=最後の点にします。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"pos_list"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"座標の系列"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer _t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));

		ArrayList<Pos> posList2 = new ArrayList<Pos>();
		posList2.addAll(posList);
		if (posList.size() > 0) {
			posList2.add(posList.get(0));
		}

		return new ValuePosList(posList2);
	}
}
