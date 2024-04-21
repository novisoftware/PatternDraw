package com.github.novisoftware.patternDraw.gui.misc;

import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.utils.Preference;

public class JFrame2 extends JFrame {
	/**
	 * GUI 部品Look And Feel を設定する
	 *
	 * @param frame
	 */
	public static void setLookAndFeel(JFrame frame) {
		String tryLaf[] = {
				"com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
				// "com.sun.java.swing.plaf.motif.MotifLookAndFeel"
		};

		for (String lookAndFeel: tryLaf) {
			try {
				UIManager.setLookAndFeel(lookAndFeel);
				SwingUtilities.updateComponentTreeUI(frame);
				break;
			} catch(Exception e){
				// 処理不要
			}
		}
	}

	@Override
	public void setTitle(String s) {
		super.setTitle(GuiUtil.FRAME_TITLE_BASE + s);
	}

	protected JFrame2() {
		super();
		JFrame2.setLookAndFeel(this);

		// ウィンドウタイトルバーのアイコンを設定(Windows向け)
		try {
			this.setIconImage(ImageIO.read(this.getClass().getResource(Preference.ICON_IMAGE_PATH)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Container contentPane = getContentPane();
	    contentPane.setBackground(Preference.BG_COLOR);
	    contentPane.setForeground(Preference.TEXT_COLOR);
	}

	protected void addHorizontalRule(Container c, int width) {
		Box hr = Box.createHorizontalBox();
		// hr.setPreferredSize(new Dimension(WINDOW_WIDTH * 2, width));
		hr.setPreferredSize(new Dimension((int)this.getSize().getWidth() * 2, width));
		c.add(hr);
	}

	protected void addHorizontalRule__test(Container c, int width) {
		Box hr = Box.createHorizontalBox();
		// hr.setPreferredSize(new Dimension(WINDOW_WIDTH * 2, width));
		hr.setPreferredSize(new Dimension((int)2000, width));
		c.add(hr);
	}

	protected Box horizontalRule(int width) {
		Box hr = Box.createHorizontalBox();
		// hr.setPreferredSize(new Dimension(WINDOW_WIDTH * 2, width));
		hr.setPreferredSize(new Dimension((int)2000, width));
		return hr;
	}


	protected Box spacer(int width) {
		Box hr = Box.createHorizontalBox();
		// hr.setPreferredSize(new Dimension(WINDOW_WIDTH * 2, width));
		hr.setPreferredSize(new Dimension((int)width, 1));
		return hr;
	}
}
