package com.github.novisoftware.patternDraw.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class RendererImpl extends AbstractRenderer {
	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		// Color fg = new Color(0.3f, 0.3f, 0.3f);

		/*
		 * Color yellow = Color.YELLOW; Color orange = new Color(1.0f, 0.6f, 0);
		 * Color red = new Color(0.6f, 0.1f, 0.6f); Color bule2 = new
		 * Color(0.5f, 0.5f, 1.0f);
		 */

		// int N_LOOP = 100;
		// int N_LOOP = 30;
		int N_LOOP = 100;

		if (s != null) {
			s.setStrokeWidth(0.1);
			// s.svg_stroke_color = "blue";
			s.setStrokeColor("gray");
		}

		for (int i = 0; i < N_LOOP; i++) {
			// double ratio = (double)i / N_LOOP;
			double ratio = (double) i / (N_LOOP - 0.5);

			double x0 = -1;
			double y0 = -1;
			double x1 = 1;
			double y1 = -1 + 2 * ratio;

			localDrawLine(g, svgBuff, s, x0, y0, x1, y1);
		}

		for (int i = 0; i < N_LOOP; i++) {
			double ratio = (double) i / (N_LOOP - 0.5);

			double x0 = 1;
			double y0 = -1;
			double x1 = 1 - 2 * ratio;
			double y1 = 1;

			localDrawLine(g, svgBuff, s, x0, y0, x1, y1);
		}

		for (int i = 0; i < N_LOOP; i++) {
			double ratio = (double) i / (N_LOOP - 0.5);

			double x0 = 1;
			double y0 = 1;
			double x1 = -1;
			double y1 = 1 - 2 * ratio;

			localDrawLine(g, svgBuff, s, x0, y0, x1, y1);
		}

		for (int i = 0; i < N_LOOP; i++) {
			double ratio = (double) i / (N_LOOP - 0.5);

			double x0 = -1;
			double y0 = 1;
			double x1 = -1 + 2 * ratio;
			double y1 = -1;

			localDrawLine(g, svgBuff, s, x0, y0, x1, y1);
		}

		if (g != null) {
			g.setColor(Color.BLACK);
		}

		if (s != null) {
			s.setStrokeWidth(0.4);
			s.setStrokeColor("black");
		}

		for (int i = 0; i < N_LOOP; i++) {
			// double ratio = (double)i / N_LOOP;
			double ratio = (double) i / (N_LOOP - 0.5);

			Line aLine;
			Line bLine;
			Line cLine;
			Line dLine;

			{
				double x0 = -1;
				double y0 = -1;
				double x1 = 1;
				double y1 = -1 + 2 * ratio;

				aLine = new Line(x0, y0, x1, y1);
			}
			{

				double x0 = 1;
				double y0 = -1;
				double x1 = 1 - 2 * ratio;
				double y1 = 1;

				bLine = new Line(x0, y0, x1, y1);
			}
			{
				double x0 = 1;
				double y0 = 1;
				double x1 = -1;
				double y1 = 1 - 2 * ratio;

				cLine = new Line(x0, y0, x1, y1);
			}
			{
				double x0 = -1;
				double y0 = 1;
				double x1 = -1 + 2 * ratio;
				double y1 = -1;

				dLine = new Line(x0, y0, x1, y1);
			}

			Pos aPos;
			Pos bPos;
			Pos cPos;
			Pos dPos;

			aPos = aLine.intersection(bLine);
			bPos = bLine.intersection(cLine);
			cPos = cLine.intersection(dLine);
			dPos = dLine.intersection(aLine);

			ArrayList<Pos> posList = new ArrayList<Pos>();
			posList.add(aPos);
			posList.add(bPos);
			posList.add(cPos);
			posList.add(dPos);
			posList.add(aPos); // 閉じる
			posList.add(bPos); // もう少し描画する( つなぎ目 )

			localPolyLine(g, svgBuff, s, posList, false);

			/*
			 * localDrawLine(g, svgBuff, s, aPos, bPos); localDrawLine(g,
			 * svgBuff, s, bPos, cPos); localDrawLine(g, svgBuff, s, cPos,
			 * dPos); localDrawLine(g, svgBuff, s, dPos, aPos);
			 */
		}

		/*
		 * for (int i = 0; i < N_LOOP; i++) { // double ratio = (double)i /
		 * N_LOOP; double r1 = (double)i / (N_LOOP - 0.5); (r1 * r2) }
		 */
	}
}