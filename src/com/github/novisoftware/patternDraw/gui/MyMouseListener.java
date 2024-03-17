package com.github.novisoftware.patternDraw.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
	private Runnable r;

	public MyMouseListener(Runnable r) {
		this.r = r;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.r.run();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// 不要。処理なし。
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// 不要。処理なし。
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// 不要。処理なし。
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// 不要。処理なし。
	}

}
