package com.github.novisoftware.patternDraw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.github.novisoftware.patternDraw.geometricLanguage.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.InvaliScriptException;
import com.github.novisoftware.patternDraw.geometricLanguage.ObjectHolder;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.token.TokenList;
import com.github.novisoftware.patternDraw.gui.MyJFrame;
import com.github.novisoftware.patternDraw.gui.MyJFrame2;
import com.github.novisoftware.patternDraw.gui.MyJPanel;
import com.github.novisoftware.patternDraw.gui.MyJPanel2;
import com.github.novisoftware.patternDraw.gui.SettingWindow;
import com.github.novisoftware.patternDraw.png.PngUtil;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.svg.SvgUtil;

public class Main {
	static public int IMAGE_WIDTH = 800;
	static public int IMAGE_HEIGHT = 800;

	static public void usage() {
		System.err.println("引数: 命令ファイル [svg出力ファイル] [png出力ファイル]");
		System.err.println("-N=10 の形式でパラメーターを設定することができます。");
	}

	static public void main(String args[]) throws IOException, InvaliScriptException {
		String svgFilename = null;
		String pngFilename = null;
		String scriptPath = null;
		boolean isDebugMode = false;

		HashMap<String, ObjectHolder> variables = new HashMap<String, ObjectHolder>();
		for (int i =0; i < args.length; i++) {

			if (args[i].startsWith("--")) {
				String opt = args[i].substring("--".length());
				if (opt.equals("debug")) {
					isDebugMode = true;
				}
				else {
					usage();
					System.exit(1);
				}
			} else if (args[i].startsWith("-")) {
				String a = args[i].substring(1);
				String[] v = a.split("=");
				if (v.length != 2) {
					usage();
					System.err.println("パラメーターの指定方法に誤りがあります。");
					System.exit(-1);
				}
				variables.put(v[0], new ObjectHolder(v[1]));
				System.out.println("arg: " + v[0] + " = " + v[1]);
			}
			else {
				if (scriptPath == null) {
					scriptPath = args[i];
				}
				else {
					if (args[i].endsWith(".svg")) {
						svgFilename = args[i];
					}
					else if (args[i].endsWith(".png")) {
						pngFilename = args[i];
					}
				}
			}
		}

		if (scriptPath == null) {
			usage();
			System.exit(1);
		}

		final TokenList tokenList = new TokenList(scriptPath);

		if (svgFilename != null) {
			final InstructionRenderer svgRenderer = new InstructionRenderer(tokenList, variables);
			String svg_stroke_color = "black";
			double svg_stroke_width = 0.3;
			SvgInstruction s = new SvgInstruction(svg_stroke_color, svg_stroke_width);
			try {
				svgRenderer.run();
			} catch (InvaliScriptException e1) {
				System.err.println(e1.toString());
				e1.printStackTrace();
				System.exit(1);
			}
			SvgUtil.outSvg(s, svgFilename, svgRenderer);
		}

		if (pngFilename != null) {
			int X_REPEAT = 1;
			int Y_REPEAT = 1;
			if (variables.containsKey("X_REPEAT")) {
				X_REPEAT = variables.get("X_REPEAT").getIntValue();
			}
			if (variables.containsKey("Y_REPEAT")) {
				Y_REPEAT = variables.get("Y_REPEAT").getIntValue();
			}
			final InstructionRenderer pngRenderer = new InstructionRenderer(tokenList, variables);
			// IMAGE_WIDTH = 400;
			// IMAGE_HEIGHT = 400;
			PngUtil.outPng(pngFilename, pngRenderer, IMAGE_WIDTH, IMAGE_HEIGHT, X_REPEAT, Y_REPEAT);
		}

		if (svgFilename != null || pngFilename != null) {
			System.exit(0);
		}

		if (isDebugMode) {
			final InstructionRenderer renderer = new InstructionRenderer(tokenList, variables);

			BufferedImage bimg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) bimg.getGraphics();
			Color fg = new Color(0.7f, 0.7f, 0.7f);
			Color bg = new Color(1f, 1f, 1f);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setBackground(bg);
			g.clearRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
			g.setColor(fg);

			renderer.init(g, null, null);

			final MyJPanel panel = new MyJPanel(renderer, bimg);
			final MyJFrame frame = new MyJFrame(panel);
			frame.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
			frame.setTitle("PatternDraw - " + scriptPath);
			frame.setVisible(true);
			frame.repaint();
		}
		else {
			// パラメーター設定用部分の前後で切り分ける
			ArrayList<TokenList> a = tokenList.separateWithKey("input_params");
			TokenList headList = null;
			TokenList bodyList = null;
			if (a.size() == 2) {
				headList = a.get(0);
				bodyList = a.get(1);
			} else {
				bodyList = a.get(0);
			}

			// パラメーター用
			final InstructionRenderer renderer = new InstructionRenderer(headList, variables);
			renderer.init(null, null, null);
			renderer.run();
			ArrayList<Parameter> params = renderer.getParams();
			HashMap<String, ObjectHolder> inputVariables = renderer.getVariables();


			// 描画用
			final InstructionRenderer renderer2 = new InstructionRenderer(bodyList, inputVariables);
			BufferedImage bimg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
			final MyJPanel2 panel = new MyJPanel2(renderer2, bimg);
			final SettingWindow setting = new SettingWindow(params, inputVariables, panel, false);

			final MyJFrame2 frame = new MyJFrame2(panel);

			frame.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
			frame.setTitle("PatternDraw - " + scriptPath);

			setting.setLocation(10, 20);
			frame.setLocation(10 + 10 + SettingWindow.WINDOW_WIDTH, 20);

			setting.setVisible(true);
			frame.setVisible(true);
			frame.repaint();
		}
	}
}
