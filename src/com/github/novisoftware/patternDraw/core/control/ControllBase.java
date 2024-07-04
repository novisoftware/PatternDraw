package com.github.novisoftware.patternDraw.core.control;

public interface ControllBase {
	public boolean hasNext();

	public void nextState();

	public String getDebugString();
}
