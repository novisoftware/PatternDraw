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

public class PosToPosSkip implements FunctionDefInterface {
	public static final String NAME = "pos_to_pos_skip";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "Nスキップ";
	}

	@Override
	public String getDescription() {
		return "座標の列をN飛ばしで辿ります。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.INTEGER, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "m", "positions"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"スキップ数", "新しい点の数", "点の並び"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		int skip = Value.getInteger(param.get(0)).intValue();
		int total = Value.getInteger(param.get(1)).intValue();
		ArrayList<Pos> posList = Value.getPosList(param.get(2));

		if (skip <= 0) {
			throw new CaliculateException(
					CaliculateException.MESSAGE_INVALID_VALUE
					+ "(Nは1以上にする必要があります。)");
		}

		int size = posList.size();
		ArrayList<Pos> newPosList = new ArrayList<Pos>();

		int index = 0;
		for (int i=0;i<total;i++) {
			Pos pos = posList.get(index % size);
			newPosList.add(pos);

			index += skip;
		}

		return new ValuePosList(newPosList);
	}
}
