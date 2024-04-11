package com.github.novisoftware.patternDraw.geometricLanguage.lang;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.*;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDef;

public class FunctionUtil {
	public static FunctionDef getFunctionDef(String str) throws LangSpecException {
		if (str == LineToDraw.NAME) {
			return new LineToDraw();
		}
		if (str == PosToWalk.NAME) {
			return new PosToWalk();
		}
		if (str == RotateLineList.NAME) {
			return new RotateLineList();
		}
		if (str == SeriesOnCircle.NAME) {
			return new SeriesOnCircle();
		}
		if (str == LineToDraw.NAME) {
			return new LineToDraw();
		}

		throw new LangSpecException("Specified function name does not found.");
	}
}
