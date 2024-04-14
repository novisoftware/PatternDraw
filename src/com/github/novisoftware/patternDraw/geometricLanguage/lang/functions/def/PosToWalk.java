package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;

public class PosToWalk implements FunctionDefInterface {
	public static final String NAME = "pos_to_walk";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "複数の座標の系列を辿ります。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.POS_LIST, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "positions1", "positions2"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"n", "positions1", "positions2"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) {
		// 分割数
		// TODO 変換に失敗する可能性
		int n = ((ValueInteger)(param.get(0))).getInternal().intValue();
		ArrayList<Pos> posList1 = ((ValuePosList)(param.get(1))).getInternal();
		ArrayList<Pos> posList2 = ((ValuePosList)(param.get(2))).getInternal();

		int size1 = posList1.size();
		int size2 = posList2.size();
		int sz = Integer.max(size1, size2);

		ArrayList<Pos> newPosList = new ArrayList<Pos>();

		for (int i=0;i<sz;i++) {
			Pos pos1 = posList1.get(i % size1);
			Pos pos2 = posList2.get(i % size2);

			for (int j=0 ; j <= n; j++) {
				newPosList.add(pos1.mix(pos2, (double)j/n));
			}
			for (int j=n ; j >= 0; j--) {
				newPosList.add(pos1.mix(pos2, (double)j/n));
			}
		}

		return new ValuePosList(newPosList);
	}
}
