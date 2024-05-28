package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;
import java.util.List;

import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueLineList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValuePosList;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Line;
import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.TypeDesc;
import com.github.novisoftware.patternDraw.geometricLanguage.primitives.Path;


// line_to_draw
public class PosToDrawPolyLineCloseOverWrap extends PosToDrawPolyLine {
	public static final String NAME = "draw_polyline_close_ow";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "点の集まりを線で辿って描画します。\\n(閉じて、オーバーラップします)";
	}

	@Override
	protected void overWrap(ArrayList<Pos> posList, ArrayList<Pos> src) {
		// オーバーラップする。
		// > 2 の条件は、 三角形, 四角形, ... でしか、オーバーラップする意味がないから。
		// (閉じる意味がないから)
		if (src.size() > 2) {
			posList.add(src.get(0));
			posList.add(src.get(1));
		}
	}
}
