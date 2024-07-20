package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions;

import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.color.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.line.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.lineToDraw.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.pos.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.posToDraw.*;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.transform.*;

public class FunctionUtil {
	public static FunctionDefInterface getFunctionDef(String name) throws LangSpecException {
		// 注:
		// return しているので else if にする必要なし。
		// (幅を揃えて誤記があったら分かるようにしている)
		if (name.equals(Add2PointSeries.NAME)) {
			return new Add2PointSeries();
		}
		if (name.equals(ClosePosList.NAME)) {
			return new ClosePosList();
		}
		if (name.equals(LinesToCrossPoints.NAME)) {
			return new LinesToCrossPoints();
		}
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
		if (name.equals(LineFrom2Series.NAME)) {
			return new LineFrom2Series();
		}
		if (name.equals(LineClipping.NAME)) {
			return new LineClipping();
		}
		if (name.equals(LineClipping_masking.NAME)) {
			return new LineClipping_masking();
		}
		if (name.equals(PosClipping.NAME)) {
			return new PosClipping();
		}
		if (name.equals(PosToFill.NAME)) {
			return new PosToFill();
		}
		if (name.equals(PosToWalk.NAME)) {
			return new PosToWalk();
		}
		if (name.equals(ApplyAsTexture.NAME)) {
			return new ApplyAsTexture();
		}
		if (name.equals(PosToSubPosList.NAME)) {
			return new PosToSubPosList();
		}
		if (name.equals(PosToSubPosList.NAME)) {
			return new PosToSubPosList();
		}
		if (name.equals(PosToReverse.NAME)) {
			return new PosToReverse();
		}
		if (name.equals(PosConcat.NAME)) {
			return new PosConcat();
		}
		if (name.equals(PosZip.NAME)) {
			return new PosZip();
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
		if (name.equals(SeriesOnCircle2.NAME)) {
			return new SeriesOnCircle2();
		}
		if (name.equals(LineToDraw.NAME)) {
			return new LineToDraw();
		}
		if (name.equals(PosSortByAngle.NAME)) {
			return new PosSortByAngle();
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
		if (name.equals(SinglePosition.NAME)) {
			return new SinglePosition();
		}

		// 以降、変換関連
		if (name.equals(CombineTransforms.NAME)) {
			return new CombineTransforms();
		}
		if (name.equals(CreateMoveTransform.NAME)) {
			return new CreateMoveTransform();
		}
		if (name.equals(CreateRotTransform.NAME)) {
			return new CreateRotTransform();
		}
		if (name.equals(CreateScaleTransform.NAME)) {
			return new CreateScaleTransform();
		}
		if (name.equals(CreateSkewTransform.NAME)) {
			return new CreateSkewTransform();
		}
		if (name.equals(DoTransformPos.NAME)) {
			return new DoTransformPos();
		}
		if (name.equals(DoTransformLine.NAME)) {
			return new DoTransformLine();
		}

		// 以降、色関連
		if (name.equals(HsvToColor.NAME)) {
			return new HsvToColor();
		}
		if (name.equals(RgbToColor.NAME)) {
			return new RgbToColor();
		}
		if (name.equals(HtmlColor.NAME)) {
			return new HtmlColor();
		}
		if (name.equals(SetAlpha.NAME)) {
			return new SetAlpha();
		}
		if (name.equals(SetColorPNG.NAME)) {
			return new SetColorPNG();
		}
		if (name.equals(SetColorSVG.NAME)) {
			return new SetColorSVG();
		}

		throw new LangSpecException("Specified function name '" + name + "' does not found.");
	}
}
