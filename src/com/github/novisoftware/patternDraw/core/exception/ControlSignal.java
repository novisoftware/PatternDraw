package com.github.novisoftware.patternDraw.core.exception;

import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;

public abstract class ControlSignal extends EvaluateException {
	public final P022_____RpnGraphNodeElement causeNode;
	
	public ControlSignal(String message, P022_____RpnGraphNodeElement causeNode) {
		super(message);
		
		this.causeNode = causeNode;
	}
}
