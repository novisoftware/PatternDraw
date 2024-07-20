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

public class PosToSubPosList implements FunctionDefInterface {
	public static final String NAME = "pos_to_sub_pos_list";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "一部分を取り出す";
	}

	@Override
	public String getDescription() {
		return "点の系列の一部分を取り出します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST, ValueType.INTEGER, ValueType.INTEGER};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions", "start", "num"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び", "開始(1, 2, 3, ...)", "取り出す点の数"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));
		int start = Value.getInteger(param.get(1)).intValue();
		int num = Value.getInteger(param.get(2)).intValue();

		if (start <= 0) {
			throw new CaliculateException(
					CaliculateException.MESSAGE_INVALID_VALUE,
					"開始は1以上にする必要があります。");
		}
		if (num <= 0) {
			throw new CaliculateException(
					CaliculateException.MESSAGE_INVALID_VALUE,
					"取り出す点の数は1以上にする必要があります。");
		}

		List<Pos> newPosList_0 = posList.subList((start - 1), (start - 1) + num);
		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		newPosList.addAll(newPosList_0);

		return new ValuePosList(newPosList);
	}
}
