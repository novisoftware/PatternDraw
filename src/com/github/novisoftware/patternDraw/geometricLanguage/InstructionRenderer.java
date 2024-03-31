package com.github.novisoftware.patternDraw.geometricLanguage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

import javax.swing.JFrame;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.EnumParameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.Parameter;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.SliderParameter;
import com.github.novisoftware.patternDraw.geometricLanguage.token.Token;
import com.github.novisoftware.patternDraw.geometricLanguage.token.TokenList;
import com.github.novisoftware.patternDraw.geometry.Line;
import com.github.novisoftware.patternDraw.geometry.Pos;
import com.github.novisoftware.patternDraw.geometryLanguage.primitives.Path;
import com.github.novisoftware.patternDraw.gui.SettingWindow;
import com.github.novisoftware.patternDraw.renderer.AbstractRenderer;
import com.github.novisoftware.patternDraw.svg.SvgInstruction;

public class InstructionRenderer extends AbstractRenderer {
	TokenList tokenList;
	int counter;
	private Graphics2D g;
	private ArrayList<String> svgBuff;
	private SvgInstruction s;
	Stack<ObjectHolder> stack;
	ArrayList<Parameter> params;
	HashMap<String, ObjectHolder> variables;
	HashMap<String, ObjectHolder> initialVariables;
	ArrayList<Path> pathList;
	String currentStrokeColor = "black";
	String currentStrokeWidth = "1";
	double translateX;
	double translateY;

	public void setTranslate(double x, double y) {
		this.translateX = x;
		this.translateY = y;
	}

	public void addTranslate(double x, double y) {
		this.translateX += x;
		this.translateY += y;
	}

	/**
	 * デバッグ用。最後に実行したトークン。
	 */
	Token lastToken;
	/**
	 * 状態変数。入力待ちかどうか。
	 */
	boolean isWaitSetting = false;
	/**
	 * 入力待ちの場合の入力ウィンドウ。
	 */
	JFrame waitFrame;

	Runnable resetWait = new Runnable() {
		public void run() {
			isWaitSetting = false;
			waitFrame = null;
		}
	};

	public ArrayList<Parameter> getParams() {
		return this.params;
	}

	public HashMap<String, ObjectHolder> getVariables() {
		return this.variables;
	}


	public InstructionRenderer(TokenList tokenList, HashMap<String, ObjectHolder> initialVariables) {
		this.tokenList = tokenList;
		this.initialVariables = initialVariables;
	}

	@SuppressWarnings("unchecked")
	public void init(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		this.g = g;
		this.svgBuff = svgBuff;
		this.s = s;
		this.stack = new Stack<ObjectHolder>();
		this.variables = initialVariables != null ? (HashMap<String, ObjectHolder>) initialVariables.clone()
				: new HashMap<String, ObjectHolder>();
		this.params = new ArrayList<Parameter>();
		this.counter = 0;
		this.pathList = new ArrayList<Path>();
		this.evalToken = new Stack<ArrayList<Token>>();
		this.evalTokenCounter = new Stack<Integer>();
		this.translateX = 0;
		this.translateY = 0;
	}

	void Debug(String s) {
		System.out.println(s);
	}

	public void runSingleToken(Token token) throws InvaliScriptException {
		String tokenStr = token.getToken();
		lastToken = token;
		// Debug("stack depth = " + stack.size() + " token = " + tokenStr);
		if (tokenStr.equals("duplicate")) {
			ObjectHolder o = stack.pop();
			stack.push(o);
			stack.push(o.clone());
		} else if (tokenStr.equals("swap")) {
			ObjectHolder o1 = stack.pop();
			ObjectHolder o2 = stack.pop();
			stack.push(o1);
			stack.push(o2);
		} else if (tokenStr.equals("series_on_circle")) {
			/*
			 * 円周上に並んだ系列を作る
			 */
			double theta = stack.pop().getAs_double();
			double r = stack.pop().getAs_double();
			int n = stack.pop().getIntValue();

			ArrayList<Pos> posList = new ArrayList<Pos>();
			for (int i = 0; i < n; i++) {
				double x = r * Math.cos(2 * Math.PI * i / n + theta);
				double y = r * Math.sin(2 * Math.PI * i / n + theta);
				posList.add(new Pos(x, y));
			}
			stack.push(new ObjectHolder(posList.get(0), posList));
		} else if (tokenStr.equals("n_series_on_circle")) {
			/*
			 * 円周上に並んだ系列を作る（２）
			 */
			int nn = stack.pop().getIntValue();
			// 分割数
			int n = stack.pop().getIntValue();
			double[] theta = new double[nn];
			double[] f = new double[nn];
			double[] r = new double[nn];
			for (int k = 0; k < nn; k++) {
				theta[k] = stack.pop().getAs_double();
				f[k] = stack.pop().getAs_double();
				r[k] = stack.pop().getAs_double();
			}

			ArrayList<Pos> posList = new ArrayList<Pos>();
			for (int i = 0; i < n; i++) {
				double x = 0;
				double y = 0;
				for (int k = 0; k < nn; k++) {
					x += r[k] * Math.cos(f[k] * (2 * Math.PI * i + theta[k]) / n);
					y += r[k] * Math.sin(f[k] * (2 * Math.PI * i + theta[k]) / n);
				}
				posList.add(new Pos(x, y));
			}
			stack.push(new ObjectHolder(posList.get(0), posList));
		} else if (tokenStr.equals("param")
				|| tokenStr.startsWith("param-")
				|| tokenStr.equals("default")
				|| tokenStr.startsWith("default-")
				) {
			String defaultValue = null;
			if (tokenStr.startsWith("default")) {
				ObjectHolder o22 = stack.pop();
				TypeDesc t22 = o22.getTypeDesc();
				if (t22 == TypeDesc.STRING) {
					defaultValue = o22.getAs_string();
				} else if (t22 == TypeDesc.DOUBLE) {
					defaultValue = "" + o22.getAs_double();
				} else {
					throw new InvaliScriptException("Invalid operand type", token);
				}
			}

			ObjectHolder o1 = stack.pop();
			TypeDesc t = o1.getTypeDesc();
			String description = null;
			if (t == TypeDesc.STRING) {
				description = o1.getAs_string();
			} else {
				throw new InvaliScriptException("Invalid operand type", token);
			}
			ObjectHolder o2 = stack.pop();
			TypeDesc t2 = o2.getTypeDesc();
			String varName = null;
			if (t2 == TypeDesc.STRING) {
				varName = o2.getAs_string();
			} else {
				throw new InvaliScriptException("Invalid operand type", token);
			}

			boolean isSlider = false;
			Double sliderMin = null;
			Double sliderMax = null;

			boolean isEnum = false;
			ArrayList<String> enums = null;

			// param-default[0]-slider[0,6.28318530718]
			if (tokenStr.indexOf('-') != -1) {
				int dIndex = tokenStr.indexOf('-');
				String subStr = tokenStr.substring(dIndex + 1);
				if (subStr.startsWith("slider")) {
					String subStr2 = subStr.substring("slider".length());
					String[] optInOperator = subStr2.split("\\[|\\]|,");
					Debug("subStr2 = " + subStr2);
					for (int i = 0 ; i < optInOperator.length ; i ++) {
						Debug("optInOperator[i] = " + optInOperator[i]);
					}

					isSlider = true;
					sliderMin = Double.parseDouble(optInOperator[1]);
					sliderMax = Double.parseDouble(optInOperator[2]);
				}
				else if (subStr.startsWith("enum")) {
					String subStr2 = subStr.substring("enum".length());
					String[] paraInOperator_ = subStr2.split("[\\[\\],]");
					ArrayList<String> paraInOperator = new ArrayList<String>();
					for (String s : paraInOperator_) {
						if (s.length() > 0) {
							paraInOperator.add(s);
						}
					}

					isEnum = true;
					enums = paraInOperator;
				}
			}

			Debug("varName = " + varName);
			Debug("description = " + description);
			Debug("defaultValue = " + defaultValue);

			/// isSlider = false;

			Parameter parameter = null;
			if (isSlider) {
				parameter = new SliderParameter(varName, description, defaultValue, sliderMin, sliderMax);
			}
			else if (isEnum) {
				parameter = new EnumParameter(varName, description, defaultValue, enums);
			}
			else {
				parameter = new Parameter(varName, description, defaultValue);
			}

			this.params.add(parameter);

			if (defaultValue != null) {
				this.variables.put(varName, new ObjectHolder(defaultValue));
			}
		} else if (tokenStr.equals("input_params")) {
			boolean isFullyDefined = true;
			for (Parameter param : this.params) {
				if (! this.variables.containsKey(param.getName())) {
					isFullyDefined = false;
				}
			}
			if (! isFullyDefined) {
				// Debug("wait to set variable.");
				final SettingWindow setting = new SettingWindow(this.params, this.variables, resetWait, true);
				this.isWaitSetting = true;
				this.waitFrame = setting;
				setting.setVisible(true);
				setting.repaint();
			}
			// 一旦仮にNOP。
			// 入力欄を表示する箇所にしたい。

		} else if (tokenStr.equals("rand")) {
			double randomNumber = Math.random();
			stack.push(new ObjectHolder(randomNumber));
			// stack.push(new ObjectHolder("" + randomNumber));
		} else if (tokenStr.equals("round")) {
			double a = stack.pop().getAs_double();
			stack.push(new ObjectHolder(String.format("%d", Math.round(a))));
		} else if (tokenStr.equals("pi")) {
			stack.push(new ObjectHolder(Math.PI));
		} else if (tokenStr.equals("2pi")) {
			stack.push(new ObjectHolder(2*Math.PI));
		} else if (tokenStr.equals("*")) {
			double a = stack.pop().getAs_double();
			double b = stack.pop().getAs_double();
			stack.push(new ObjectHolder(a*b));
		} else if (tokenStr.equals("/")) {
			double a = stack.pop().getAs_double();
			double b = stack.pop().getAs_double();
			stack.push(new ObjectHolder(b/a));
		} else if (tokenStr.equals("+")) {
			double a = stack.pop().getAs_double();
			double b = stack.pop().getAs_double();
			stack.push(new ObjectHolder(a+b));
		} else if (tokenStr.equals("-")) {
			double a = stack.pop().getAs_double();
			double b = stack.pop().getAs_double();
			stack.push(new ObjectHolder(b-a));
		} else if (tokenStr.equals("pos")) {
			double y = stack.pop().getAs_double();
			double x = stack.pop().getAs_double();
			ArrayList<Pos> posList = new ArrayList<Pos>();
			posList.add(new Pos(x, y));
			stack.push(new ObjectHolder(posList.get(0), posList));
		} else if (tokenStr.equals("rt_pos")) {
			double t = stack.pop().getAs_double();
			double r = stack.pop().getAs_double();
			double x = r * Math.cos(t);
			double y = -r * Math.sin(t);
			ArrayList<Pos> posList = new ArrayList<Pos>();
			posList.add(new Pos(x, y));
			stack.push(new ObjectHolder(posList.get(0), posList));
		} else if (tokenStr.equals("pos_to_walk")) {
			// 分割数
			int n = stack.pop().getIntValue();
			ArrayList<Pos> posList1 = stack.pop().getAs_pos();
			ArrayList<Pos> posList2 = stack.pop().getAs_pos();

			int size1 = posList1.size();
			int size2 = posList2.size();
			int sz = Integer.max(size1, size2);

			ArrayList<Pos> newPosList = new ArrayList<Pos>();

			for (int i=0;i<sz;i++) {
				Pos pos1 = posList1.get(i % size1);
				Pos pos2 = posList2.get(i % size2);

				for (int j=0 ; j <= n; j++) {
					newPosList.add(pos1.mix(pos2, (double)j/n));
				}
				for (int j=n ; j >= 0; j--) {
					newPosList.add(pos1.mix(pos2, (double)j/n));
				}
			}
			stack.push(new ObjectHolder(newPosList.get(0), newPosList));
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
				throw new InvaliScriptException("Wrong Type " + t + " in stack.", token);
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
				throw new InvaliScriptException("Wrong Type " + t + " in stack.", token);
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
				throw new InvaliScriptException("Wrong Type " + t + " in stack.", token);
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
				// c.add(b.get(0));
				// c.add(a.get(0));
				c.add(b.get(i));
				c.add(a.get(i));
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
				throw new InvaliScriptException("error. Excepted same size list.", token);
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
				Line line2 = line.translateLine(this.translateX, this.translateY);

				localDrawLine(g, svgBuff, s, line2);
				String strokeColor = currentStrokeColor;
				String strokeWidth = currentStrokeWidth;
				boolean isFill = false;
				String fillColor = null;

				// String strokeColor, String strokeWidth, boolean isFill,
				// String fillColor

				Path path = new Path(line, strokeColor, strokeWidth, isFill, fillColor);

				this.pathList.add(path);
			}
		} else if (tokenStr.equals("translate")) {
			double y = stack.pop().getAs_double();
			double x = stack.pop().getAs_double();

			this.addTranslate(x, y);
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
		} else if (tokenStr.startsWith("'")) {
			// 変数名だと解釈させず、stack に push する。
			stack.push(new ObjectHolder(tokenStr.substring(1)));
		} else if (this.variables.containsKey(tokenStr)) {
			// 変数を参照する
			// 変数名
			ObjectHolder aObj = this.variables.get(tokenStr);
			if (aObj == null) {
				throw new InvaliScriptException("no variable: " + tokenStr, token);
			}
			stack.push(aObj);

			/*
			 * ObjectHolder aObj = stack.pop(); ObjectHolder bObj =
			 * this.variables.get(aObj.getAs_string()); if (bObj == null) {
			 * throw new InvalidProgramException("no variable: " +
			 * aObj.getAs_string(), token); } stack.push(bObj);
			 */
		} else {
			stack.push(new ObjectHolder(tokenStr));
		}
	}

	Stack<ArrayList<Token>> evalToken;
	Stack<Integer> evalTokenCounter;

	public boolean step() throws InvaliScriptException {
		if (isWaitSetting) {
			Debug("Waiting input.");

			if (waitFrame != null) {
				waitFrame.setVisible(true);
			}
			return true;
		}

		// Debug("this.counter = " + this.counter);

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
			// TODO
		} else {
			this.runSingleToken(token);
		}

		// 続きがあるかどうかを返り値にする
		int n = tokenList.getList().size();
		return this.counter < n;
	}

	public void run() throws InvaliScriptException {
		Debug("stack machine start.");
		while (true) {
			try {
				if (this.step() == false) {
					break;
				}
			} catch (InvaliScriptException e) {
				Token errorToken = e.getCausedToken();

				System.err.println("Exception: " + e.toString());
				System.err.println(errorToken.getToken());
				System.err.println("in line " + errorToken.getLineNumber());
				e.printStackTrace();
			}
			while (this.isWaitSetting) {
				// 入力中状態の場合、ポーリングで解除を待つ
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void render(Graphics2D g, ArrayList<String> svgBuff, SvgInstruction s) {
		System.out.println("pathList size is " + pathList.size());

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
			a.add(stack.elementAt(n - 1 - i));
		}

		// TODO
		// ここでサイズを決めるのは妥当か?
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
			// int y = 24;
			int y = 40;
			for (ObjectHolder o : a) {
				TypeDesc t = o.getTypeDesc();
				String addInfo = "";
				if (t == TypeDesc.LINE_LIST) {
					addInfo = "(" + o.getAs_line().size() + ")";
				} else if (t == TypeDesc.POS_LIST) {
					addInfo = "(" + o.getAs_pos().size() + ")";
				} else if (t == TypeDesc.STRING) {
					addInfo = "\"" + o.getAs_string() + "\"";
				} else if (t == TypeDesc.DOUBLE) {
					try {
						addInfo = "\"" + o.getAs_double() + "\"";
					} catch (InvaliScriptException e) {
						addInfo = "(内部エラー" + e.toString() + ")";
					}
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
			} catch (InvaliScriptException e) {
				Token errorToken = e.getCausedToken();

				System.err.println("Exception: " + e.toString());
				System.err.println(errorToken.getToken());
				System.err.println("in line " + errorToken.getLineNumber());
				e.printStackTrace();
			}
		}
	}
}
