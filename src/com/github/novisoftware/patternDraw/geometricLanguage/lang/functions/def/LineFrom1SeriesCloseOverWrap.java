package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.gui.editor.core.CaliculateException;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueInteger;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;

public class LineFrom1SeriesCloseOverWrap implements FunctionDefInterface {
	public static final String NAME = "line_from_1_series_close_ow";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "点を線で結び、閉じます(オーバーラップします)。";
	}

	@Override
	public ValueType[] getParameterTypes() {
		ValueType[] ret = {ValueType.POS_LIST};
		return ret;
	}

	@Override
	public String[] getParameterNames() {
		String[] ret = {"positions"};
		return ret;
	}

	@Override
	public String[] getParameterDescs() {
		String[] ret = {"点の並び"};
		return ret;
	}

	@Override
	public ValueType getReturnType() {
		return ValueType.LINE_LIST;
	}

	@Override
	public Value exec(List<Value> param, InstructionRenderer t) throws CaliculateException {
		ArrayList<Pos> posList = Value.getPosList(param.get(0));

		ArrayList<Line> ret = new ArrayList<Line>();
		int n = posList.size();
		for (int i = 1; i < n + 2; i++) {
			ret.add(new Line(posList.get((i - 1) % n), posList.get(i % n)));
		}

		return new ValueLineList(ret);
	}
}
