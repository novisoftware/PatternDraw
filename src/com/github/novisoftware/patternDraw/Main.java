package com.github.novisoftware.patternDraw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.github.novisoftware.patternDraw.geometricLanguage.InstructionRenderer;
import com.github.novisoftware.patternDraw.geometricLanguage.InvalidProgramException;
import com.github.novisoftware.patternDraw.geometricLanguage.TokenList;
import com.github.novisoftware.patternDraw.gui.MyJFrame;
import com.github.novisoftware.patternDraw.gui.MyJPanel;
import com.github.novisoftware.patternDraw.renderer.Renderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;
import com.github.novisoftware.patternDraw.svg.SvgUtil;

public class Main {
	static public int IMAGE_WIDTH = 800;
	static public int IMAGE_HEIGHT = 800;

	static public void main(String args[]) throws IOException {
		if (args.length < 2) {
			System.err.println("ファイルを指定してください");
			System.err.println("args[0] 命令ファイル");
			System.err.println("args[1] svg出力ファイル");
			System.exit(1);
		}
		String svgFilename = args[1];

		// final Renderer renderer = new RendererImpl();
		final TokenList tokenList = new TokenList(args[0]);
		final InstructionRenderer renderer = new InstructionRenderer(tokenList);

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
		frame.setTitle("test");
		frame.setVisible(true);
		frame.repaint();


		final InstructionRenderer svgRenderer = new InstructionRenderer(tokenList);
		String svg_stroke_color = "black";
		double svg_stroke_width = 0.3;
		SvgInstruction s = new SvgInstruction(svg_stroke_color, svg_stroke_width);
		try {
			svgRenderer.run();
		} catch (InvalidProgramException e1) {
			System.err.println(e1.toString());
			e1.printStackTrace();
			System.exit(1);
		}
		SvgUtil.outSvg(s, svgFilename, svgRenderer);


		// 	public void init(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {


		System.out.println("MAIN 0");
		try {
			while (true) {
				System.out.println("MAIN 1>");
		        Scanner sc1 = new Scanner(System.in);
		        String line1 = sc1.nextLine();
		        // ↑
		        // 値は使用しない


				boolean ret = renderer.step();


				frame.repaint();
				if (ret == false) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		// } catch (InvalidProgramException e) {
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		System.out.println("MAIN END");
	}
}
