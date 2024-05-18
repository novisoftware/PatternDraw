package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions;

import com.github.novisoftware.patternDraw.core.CaliculateException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.*;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;

public class FunctionUtil {
	public static FunctionDefInterface getFunctionDef(String name) throws LangSpecException {
		if (name.equals(LineToDraw.NAME)) {
			return new LineToDraw();
		}
		if (name.equals(LineFrom1Series.NAME)) {
			return new LineFrom1Series();
		}
		if (name.equals(LineFrom1SeriesClose.NAME)) {
			return new LineFrom1SeriesClose();
		}
		if (name.equals(LineFrom1SeriesCloseOverWrap.NAME)) {
			return new LineFrom1SeriesCloseOverWrap();
		}
		if (name.equals(PosToWalk.NAME)) {
			return new PosToWalk();
		}
		if (name.equals(RotateLineList.NAME)) {
			return new RotateLineList();
		}
		if (name.equals(RotatePointList.NAME)) {
			return new RotatePointList();
		}
		if (name.equals(SeriesOnCircle.NAME)) {
			return new SeriesOnCircle();
		}
		if (name.equals(LineToDraw.NAME)) {
			return new LineToDraw();
		}
		if (name.equals(PosToDrawPolyLine.NAME)) {
			return new PosToDrawPolyLine();
		}
		if (name.equals(PosToDrawPolyLineCloseOverWrap.NAME)) {
			return new PosToDrawPolyLineCloseOverWrap();
		}
		if (name.equals(PosToDrawPolyLineClose.NAME)) {
			return new PosToDrawPolyLineClose();
		}
		if (name.equals(PosToPosSkip.NAME)) {
			return new PosToPosSkip();
		}
		if (name.equals(SetLineWidthSVG.NAME)) {
			return new SetLineWidthSVG();
		}
		if (name.equals(SetLineWidthPNG.NAME)) {
			return new SetLineWidthPNG();
		}


		throw new LangSpecException("Specified function name '" + name + "' does not found.");
	}
}
