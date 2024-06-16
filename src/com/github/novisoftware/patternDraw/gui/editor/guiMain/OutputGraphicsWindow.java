package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.geometricLanguage.lang.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.typeSystem.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.token.Token;
import com.github.novisoftware.patternDraw.geometricLanguage.token.TokenList;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.OutputGraphicsMenuBar;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.svg.SvgUtil;
import com.github.novisoftware.patternDraw.utils.FileWriteUtil;

public class OutputGraphicsWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 700;
	public static final int WINDOW_POS_Y = 50;
	public static int IMAGE_WIDTH = 800;
	public static int IMAGE_HEIGHT = 800;

	MyJPanel panel;

	static private OutputGraphicsWindow singleton;

	/**
	 * 使用されたパラメーターを取得するためのもの
	 */
	public EditParamWindow editParamWindow;
	public EditDiagramWindow editDiagramWindow;


	private OutputGraphicsWindow() {
		OutputGraphicsMenuBar menubar = new OutputGraphicsMenuBar(this);
		this.setJMenuBar(menubar);

		final InstructionRenderer renderer = new InstructionRenderer(new TokenList(new ArrayList<Token>()), new HashMap<String, ObjectHolder>());
		BufferedImage buffer = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);

		panel = new MyJPanel(renderer, buffer);
		this.getContentPane().add(panel, BorderLayout.CENTER);

		this.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);
		this.setTitle("グラフィックスの出力");
	}

	static public OutputGraphicsWindow getInstance() {
		if (singleton == null) {
			singleton = new OutputGraphicsWindow();
		}

		return singleton;
	}

	public void outputPNG(File file) throws IOException {
		BufferedImage buffer = panel.createBufferedImage();
		ImageIO.write(buffer, "png", file);
	}

	public void outputSVG(File file) throws IOException {
		String svg_stroke_color = "black";
		double svg_stroke_width = 0.3;
		SvgInstruction s = new SvgInstruction(svg_stroke_color, svg_stroke_width);
		SvgUtil.outSvg(s, file, panel.getRenderer());
	}

	public void outputParameterLog(File file, File orgFile) throws IOException {
		if (editParamWindow == null) {
			return;
		}

		String scriptFilename = this.editDiagramWindow.editPanel.networkDataModel.getFilename();
		String title = this.editDiagramWindow.editPanel.networkDataModel.title;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

		ArrayList<String> buff = new ArrayList<String>();
		buff.add("{");
		if (scriptFilename != null) {
			buff.add(EditParamWindow.jsonItem("script", scriptFilename, false));
		}
		buff.add(EditParamWindow.jsonItem("title", title, false));
		buff.add(EditParamWindow.jsonItem("image", orgFile.getName(), false));
		buff.add(EditParamWindow.jsonItem("timestamp", timestamp, false));
		buff.add("\"parameters\": {");
		buff.add(editParamWindow.getVariablesPrint());
		buff.add("}");
		buff.add("}");

		FileWriteUtil.fileOutput(file, buff);
	}

	class MyJPanel extends JPanel {
		private InstructionRenderer renderer;
		private BufferedImage buffer;

		public MyJPanel(InstructionRenderer renderer, BufferedImage buffer) {
			this.renderer = renderer;
			this.buffer = buffer;
			this.reset();
		}

		InstructionRenderer getRenderer() {
			return renderer;
		}

		public void paint(Graphics graphics) {
			Graphics2D g = (Graphics2D) graphics;
			g.drawImage(buffer, 0, 0, null);
		}

		public void reset() {
			Graphics2D g = (Graphics2D)buffer.getGraphics();

			// Color bg = new Color(1f, 1f, 1f);
			Color bg = new Color(0f, 0f, 0f);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			this.setBackground(bg);
			g.setColor(bg);
			g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			Color fg = new Color(0.7f, 0.7f, 0.7f);
			g.setColor(fg);
			renderer.init(g, null, null);
		}

		public void refresh() {
			Graphics2D g = (Graphics2D)buffer.getGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderer.render(g, null, null);
		}

		public BufferedImage createBufferedImage() {
			BufferedImage buffer = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D)buffer.getGraphics();
			Color bg = new Color(0f, 0f, 0f);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(bg);
			g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

			Color fg = new Color(1.0f, 1.0f, 1.0f);
			g.setColor(fg);
			renderer.render(g, null, null);

			return buffer;
		}
	}

	static public void reset() {
		getInstance().panel.reset();
	}

	static public void refresh() {
		getInstance().panel.refresh();
		getInstance().panel.repaint();
	}

	static public InstructionRenderer getRenderer() {
		return OutputGraphicsWindow.getInstance().panel.renderer;
	}

	/*
	static public void update() {
		getInstance().panel.update();
	}
	*/

}
