package com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.def.posToDraw;


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
	
	public PosToDrawPolyLineClose() {
		super();
		this.isClosed = true;
	}
}
