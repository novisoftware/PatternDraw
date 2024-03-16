package com.github.novisoftware.patternDraw.geometricLanguage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

import javax.swing.JFrame;

import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.geometryLanguage.primitives.Path;
import com.github.novisoftware.patternDraw.gui.SettingWindow;
import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class InstructionRenderer extends AbstractRenderer {
	TokenList tokenList;
	int counter;
	Graphics2D g;
	ArrayList<String> svgBuff;
	SvgInstruction s;
	Stack<ObjectHolder> stack;
	HashMap<String, ObjectHolder> variables;
	ArrayList<Path> pathList;
	/**
	 * デバッグ用。最後に実行したトークン。
	 */
	Token lastToken;
	/**
	 * 状態変数。入力待ちかどうか。
	 */
	boolean isWaitSetting = false;
	JFrame waitFrame;

	Runnable resetWait = new Runnable() {
		public void run() {
			isWaitSetting = false;
			waitFrame = null;
		}
	};

	public InstructionRenderer(TokenList tokenList) {
		this.tokenList = tokenList;
	}

	public void init(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.g = g;
		this.svgBuff = svgBuff;
		this.s = s;
		this.stack = new Stack<ObjectHolder>();
		this.variables = new HashMap<String, ObjectHolder>();
		this.counter = 0;
		this.pathList = new ArrayList<Path>();
		this.evalToken = new Stack<ArrayList<Token>>();
		this.evalTokenCounter = new Stack<Integer>();
	}

	void Debug(String s) {
		System.out.println(s);
	}

	public void runSingleToken(Token token) throws InvalidProgramException {
		if (isWaitSetting) {
			if (waitFrame != null) {
				waitFrame.setVisible(true);
			}
			return;
		}

		String tokenStr = token.getToken();
		lastToken = token;
		Debug("stack depth = " + stack.size() + " token = " + tokenStr);
		if (tokenStr.equals("series_on_circle")) {
			/*
			 * 円周上に並んだ系列を作る
			 */
			double theta = stack.pop().getDoubleValue();
			double r = stack.pop().getDoubleValue();
			int n = stack.pop().getIntValue();

			ArrayList<Pos> posList = new ArrayList<Pos>();
			for (int i = 0; i < n; i++) {
				double x = r * Math.cos(2 * Math.PI * i / n + theta);
				double y = r * Math.sin(2 * Math.PI * i / n + theta);
				posList.add(new Pos(x, y));
			}
			stack.push(new ObjectHolder(posList.get(0), posList));
		} else if (tokenStr.equals("input_params")) {
			// 一旦仮にNOP。
		} else if (tokenStr.equals("param")) {
			ObjectHolder o1 = stack.pop();
			TypeDesc t = o1.getTypeDesc();
			String varName = null;
			if (t == TypeDesc.STRING) {
				varName = o1.getAs_string();
			}
			else {
				throw new InvalidProgramException("Invalid operand type", token);
			}
			ObjectHolder o2 = stack.pop();
			TypeDesc t2 = o2.getTypeDesc();
			String message = null;
			if (t2 == TypeDesc.STRING) {
				message = o2.getAs_string();
			}
			else {
				throw new InvalidProgramException("Invalid operand type", token);
			}
			final SettingWindow setting = new SettingWindow(
					varName,
					message,
					this.variables,
					resetWait);
			this.isWaitSetting = true;
			this.waitFrame = setting;
			setting.setVisible(true);
			setting.repaint();
		} else if (tokenStr.equals("duplicate")) {
			ObjectHolder o = stack.pop();
			stack.push(o);
			stack.push(o.clone());
		} else if (tokenStr.equals("close_list")) {
			/*
			 * 系列を閉じる
			 */
			ObjectHolder o = stack.pop();
			TypeDesc t = o.getTypeDesc();
			if (t == TypeDesc.LINE_LIST) {
				ArrayList<Line> a = o.getAs_line();
				a.add(a.get(0));
				stack.push(new ObjectHolder(a.get(0), a));
			} else if (t == TypeDesc.POS_LIST) {
				ArrayList<Pos> a = o.getAs_pos();
				a.add(a.get(0));
				stack.push(new ObjectHolder(a.get(0), a));
			} else {
				throw new InvalidProgramException("Wrong Type " + t + " in stack.", token);
			}
		} else if (tokenStr.equals("rotate_list")) {
			/*
			 * 系列をローテートさせる
			 */
			int n = stack.pop().getIntValue();

			ObjectHolder o = stack.pop();
			TypeDesc t = o.getTypeDesc();
			if (t == TypeDesc.LINE_LIST) {
				ArrayList<Line> a = o.getAs_line();
				Collections.rotate(a, n);
				stack.push(new ObjectHolder(a.get(0), a));
			} else if (t == TypeDesc.POS_LIST) {
				ArrayList<Pos> a = o.getAs_pos();
				Collections.rotate(a, n);
				stack.push(new ObjectHolder(a.get(0), a));
			} else {
				throw new InvalidProgramException("Wrong Type " + t + " in stack.", token);
			}
		} else if (tokenStr.equals("reverse_list")) {
			/*
			 * 系列を逆順にする
			 */
			ObjectHolder o = stack.pop();
			TypeDesc t = o.getTypeDesc();
			if (t == TypeDesc.LINE_LIST) {
				ArrayList<Line> a = o.getAs_line();
				Collections.reverse(a);
				stack.push(new ObjectHolder(a.get(0), a));
			} else if (t == TypeDesc.POS_LIST) {
				ArrayList<Pos> a = o.getAs_pos();
				Collections.reverse(a);
				stack.push(new ObjectHolder(a.get(0), a));
			} else {
				throw new InvalidProgramException("Wrong Type " + t + " in stack.", token);
			}
		} else if (tokenStr.equals("zipper_from_2_series")) {
			ObjectHolder aObj = stack.pop();
			ObjectHolder bObj = stack.pop();
			Debug("aObj = " + aObj.getTypeDesc());
			Debug("bObj = " + bObj.getTypeDesc());
			ArrayList<Pos> a = aObj.getAs_pos();
			ArrayList<Pos> b = bObj.getAs_pos();
			ArrayList<Pos> c = new ArrayList<Pos>();
			int n = a.size();
			for (int i = 0; i < n; i++) {
				c.add(b.get(0));
				c.add(a.get(0));
			}
			stack.push(new ObjectHolder(c.get(0), c));
		} else if (tokenStr.equals("line_from_2_series")) {
			ObjectHolder aObj = stack.pop();
			ObjectHolder bObj = stack.pop();
			Debug("aObj = " + aObj.getTypeDesc());
			Debug("bObj = " + bObj.getTypeDesc());
			ArrayList<Pos> a = aObj.getAs_pos();
			ArrayList<Pos> b = bObj.getAs_pos();

			if (a.size() != b.size()) {
				System.out.println("error. Excepted same size list.");
				System.exit(1);
			}
			ArrayList<Line> ret = new ArrayList<Line>();
			int n = a.size();
			for (int i = 0; i < n; i++) {
				ret.add(new Line(a.get(i), b.get(i)));
			}
			stack.push(new ObjectHolder(ret.get(0), ret));
		} else if (tokenStr.equals("line_from_1_series")) {
			ObjectHolder aObj = stack.pop();
			ArrayList<Pos> a = aObj.getAs_pos();

			ArrayList<Line> ret = new ArrayList<Line>();
			int n = a.size();
			for (int i = 1; i < n; i++) {
				ret.add(new Line(a.get(i - 1), a.get(i)));
			}
			stack.push(new ObjectHolder(ret.get(0), ret));
		} else if (tokenStr.equals("line_split_to_pos")) {
			int n = stack.pop().getIntValue();
			ArrayList<Pos> newList = new ArrayList<Pos>();
			ArrayList<Line> a = stack.pop().getAs_line();
			Debug("draw ... line is " + a.size());
			for (Line line : a) {
				newList.addAll(line.splitToPoints(n));
			}
			stack.push(new ObjectHolder(newList.get(0), newList));
		} else if (tokenStr.equals("line_to_draw")) {
			ArrayList<Line> a = stack.pop().getAs_line();
			Debug("draw ... line is " + a.size());
			for (Line line : a) {
				localDrawLine(g, svgBuff, s, line);
				String strokeColor = "black";
				String strokeWidth = "1";
				boolean isFill = false;
				String fillColor = null;

				// String strokeColor, String strokeWidth, boolean isFill,
				// String fillColor

				Path path = new Path(line, strokeColor, strokeWidth, isFill, fillColor);

				this.pathList.add(path);
			}

		} else if (tokenStr.equals("pos_to_fill")) {
			ArrayList<Pos> posList = stack.pop().clone().getAs_pos();
			Debug("draw ... pos is " + posList.size());
			// this.localPolyLine(g, svgBuff, s, posList, true);
		} else if (tokenStr.equals("pos_to_stroke")) {
			ArrayList<Pos> posList = stack.pop().clone().getAs_pos();
			Debug("draw ... pos is " + posList.size());
			// this.localPolyLine(g, svgBuff, s, posList, false);
		} else if (tokenStr.equals("set")) {
			// 変数名
			ObjectHolder aObj = stack.pop();
			// 内容
			ObjectHolder bObj = stack.pop();
			this.variables.put(aObj.getAs_string(), bObj);
		} else if (this.variables.containsKey(tokenStr)) {
			// } else if (tokenStr.equals("get")) {
			// 変数名
			ObjectHolder aObj = this.variables.get(tokenStr);
			if (aObj == null) {
				throw new InvalidProgramException("no variable: " + tokenStr, token);
			}
			stack.push(aObj);

			/*
			ObjectHolder aObj = stack.pop();
			ObjectHolder bObj = this.variables.get(aObj.getAs_string());
			if (bObj == null) {
				throw new InvalidProgramException("no variable: " + aObj.getAs_string(), token);
			}
			stack.push(bObj);
			*/
		} else {
			stack.push(new ObjectHolder(tokenStr));
		}
	}

	Stack<ArrayList<Token>> evalToken;
	Stack<Integer> evalTokenCounter;

	public boolean step() throws InvalidProgramException {
		Debug("this.counter = " + this.counter);

		Token token;
		if (!this.evalToken.isEmpty()) {
			ArrayList<Token> wk = evalToken.pop();
			ArrayList<Token> wk2 = (ArrayList<Token>) wk.subList(1, wk.size());
			if (wk2.size() > 0) {
				evalToken.push(wk2);
			}
			token = wk.get(0);
		} else {
			token = tokenList.getList().get(this.counter);
			this.counter++;
		}
		if (token.getToken().equals("begin_quote")) {
			ArrayList<Token> quotedTokenList = new ArrayList<Token>();
			int depth = 0;
			while (true) {
				this.counter++;
				Token work = tokenList.getList().get(this.counter);
				if (work.getToken().equals("begin_quote")) {
					depth++;
				} else if (work.getToken().equals("end_quote")) {
					if (depth == 0) {
						break;
					} else {
						depth--;
					}
				}
				quotedTokenList.add(work);
			}
			stack.push(new ObjectHolder(quotedTokenList.get(0), quotedTokenList));
		} else if (token.getToken().equals("eval")) {

		} else {
			this.runSingleToken(token);
		}

		// 続きがあるかどうかを返り値にする
		int n = tokenList.getList().size();
		return this.counter < n;
	}

	public void run() throws InvalidProgramException {
		this.init(g, svgBuff, s);
		Debug("stack machine start.");
		while (true) {
			try {
				if (this.step() == false) {
					break;
				}
			} catch (InvalidProgramException e) {
				Token errorToken = e.getCausedToken();

				System.err.println("Exception: " + e.toString());
				System.err.println(errorToken.getToken());
				System.err.println("in line " + errorToken.getLineNumber());
				e.printStackTrace();
			}
		}
	}

	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		for (Path p : pathList) {
			p.render(g, svgBuff, s);
		}
	}

	public void preveiwPoint(Graphics2D g, double dx0, double dy0) {
		int wx1 = x2int(dx0);
		int wy1 = y2int(dy0);

		int r = 5;

		Ellipse2D ellipse = new Ellipse2D.Double(wx1 - r, wy1 - r, r * 2, r * 2);
		g.fill(ellipse);
	}

	public void preview(Graphics2D g) {
		ArrayList<ObjectHolder> a = new ArrayList<ObjectHolder>();
		int n = stack.size();
		for (int i = 0; i < n; i++) {
			a.add(stack.elementAt(i));
		}

		// TODO
		int width = 700;
		int height = 700;

		// Color fg = new Color(0.7f, 0.7f, 0.7f);
		Color fg = new Color(1f, 0.7f, 0.7f);
		Color bg = new Color(1f, 1f, 1f);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setBackground(bg);
		g.clearRect(0, 0, width, height);
		g.setColor(fg);

		if (lastToken != null) {
			g.drawString("last token  = " + lastToken.getToken(), 20, 10);
		}

		{
			g.drawString("variable", 400, 25);
			int y = 40;
			for (String key : this.variables.keySet()) {
				ObjectHolder o = this.variables.get(key);

				TypeDesc t = o.getTypeDesc();
				String addInfo = "";
				if (t == TypeDesc.LINE_LIST) {
					addInfo = "(" + o.getAs_line().size() + ")";
				} else if (t == TypeDesc.POS_LIST) {
					addInfo = "(" + o.getAs_pos().size() + ")";
				} else if (t == TypeDesc.STRING) {
					addInfo = o.getAs_string();
				}

				g.drawString("" + key + " " + addInfo, 400, y);

				y += 20;
			}
		}


		{
			// スタックの内容表示
			int y = 24;
			for (ObjectHolder o : a) {
				TypeDesc t = o.getTypeDesc();
				String addInfo = "";
				if (t == TypeDesc.LINE_LIST) {
					addInfo = "(" + o.getAs_line().size() + ")";
				} else if (t == TypeDesc.POS_LIST) {
					addInfo = "(" + o.getAs_pos().size() + ")";
				} else if (t == TypeDesc.STRING) {
					addInfo = o.getAs_string();
				}

				g.drawString("" + t + " " + addInfo, 20, y);

				y += 20;
			}
		}

		// スタックに入っている図形情報をプレビューレンダリングする
		for (ObjectHolder o : a) {
			TypeDesc t = o.getTypeDesc();

			if (t == TypeDesc.LINE_LIST) {
				for (Line line : o.getAs_line()) {
					this.localDrawLine(g, null, null, line);
				}
			} else if (t == TypeDesc.POS_LIST) {
				for (Pos line : o.getAs_pos()) {
					this.preveiwPoint(g, line.getX(), line.getY());
				}
			} else {
				//
			}
		}

		Color fg2 = new Color(0.7f, 0.7f, 0.7f);
		g.setColor(fg2);

		for (Path p : pathList) {
			p.render(g, null, null);
		}



	}

	public void render__old(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.init(g, svgBuff, s);
		Debug("stack machine start.");
		while (true) {
			try {
				if (this.step() == false) {
					break;
				}
			} catch (InvalidProgramException e) {
				Token errorToken = e.getCausedToken();

				System.err.println("Exception: " + e.toString());
				System.err.println(errorToken.getToken());
				System.err.println("in line " + errorToken.getLineNumber());
				e.printStackTrace();
			}
		}
	}
}
