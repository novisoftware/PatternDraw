package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDef;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValueFloat;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.ValuePosList;

public class SeriesOnCircle  implements FunctionDef {
	public static final String NAME = "line_to_draw";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "円周上に並んだ系列を生成します。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.INTEGER, ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"n", "r", "theta"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"分割数", "半径", "角度"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.POS_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws InvaliScriptException {
		int n = ((ValueInteger)(param.get(0))).getInternal().intValue();
		double r = ((ValueFloat)(param.get(1))).getInternal();
		double theta = ((ValueFloat)(param.get(2))).getInternal();

		ArrayList<Pos> posList = new ArrayList<Pos>();
		for (int i = 0; i < n; i++) {
			double x = r * Math.cos(2 * Math.PI * i / n + theta);
			double y = r * Math.sin(2 * Math.PI * i / n + theta);
			posList.add(new Pos(x, y));
		}

		return new ValuePosList(posList);
	}

}
