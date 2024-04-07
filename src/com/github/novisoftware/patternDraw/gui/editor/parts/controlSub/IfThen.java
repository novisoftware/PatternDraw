package com.github.novisoftware.patternDraw.gui.editor.parts.controlSub;

public class IfThen implements ControllBase {
	int phase = 0; /* 0, 1 */

	public IfThen() {
	}

	// 単純なループ(添え字なし固定回数ループ)
	public boolean hasNext() {

		return false;
	}

	// 単純なループ(添え字なし固定回数ループ)
	public void nextState() {
	}


	public String getDebugString() {
		return String.format("If Then 制御");
	}
}
