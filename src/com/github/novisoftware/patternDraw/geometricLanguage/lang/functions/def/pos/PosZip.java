package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;

public class PosZip implements FunctionDefInterface {
	public static final String NAME = "pos_zip";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDisplayName() {
		return "混ぜる";
	}

	@Override
	public String getDescription() {
		return "2つの座標の系列を混ぜます。";
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

		Iterator<Pos> itr1 = posList1.iterator();
		Iterator<Pos> itr2 = posList2.iterator();

		ArrayList<Pos> newPosList = new ArrayList<Pos>();
		while (itr1.hasNext() || itr2.hasNext()) {
			if (itr1.hasNext()) {
				newPosList.add(itr1.next());
			}
			if (itr2.hasNext()) {
				newPosList.add(itr2.next());
			}
		}

		return new ValuePosList(newPosList);
	}
}
