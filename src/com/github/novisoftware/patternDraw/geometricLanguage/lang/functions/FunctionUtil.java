package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.*;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDefInterface;

public class FunctionUtil {
	public static FunctionDefInterface getFunctionDef(String str) throws LangSpecException {
		if (str.equals(LineToDraw.NAME)) {
			return new LineToDraw();
		}
		if (str.equals(PosToWalk.NAME)) {
			return new PosToWalk();
		}
		if (str.equals(RotateLineList.NAME)) {
			return new RotateLineList();
		}
		if (str.equals(RotatePointList.NAME)) {
			return new RotatePointList();
		}
		if (str.equals(SeriesOnCircle.NAME)) {
			return new SeriesOnCircle();
		}
		if (str.equals(LineToDraw.NAME)) {
			return new LineToDraw();
		}

		throw new LangSpecException("Specified function name '" + str + "' does not found.");
	}
}
