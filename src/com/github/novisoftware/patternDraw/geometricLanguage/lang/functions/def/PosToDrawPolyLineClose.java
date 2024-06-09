package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def;

import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometricLanguage.entity.Pos;


// line_to_draw
public class PosToDrawPolyLineClose extends PosToDrawPolyLine {
	public static final String NAME = "draw_polyline_close";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return "点の集まりを線で辿って描画します(閉じます)。";
	}

	@Override
	protected void overWrap(ArrayList<Pos> posList, ArrayList<Pos> src) {
		// オーバーラップする。
		// > 2 の条件は、 三角形, 四角形, ... でしか、オーバーラップする意味がないから。
		// (閉じる意味がないから)
		if (src.size() > 2) {
			posList.add(src.get(0));
		}
	}
}
