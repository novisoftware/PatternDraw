package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.github.novisoftware.patternDraw.core.Rpn;
import com.github.novisoftware.patternDraw.core.RpnUtil;
import com.github.novisoftware.patternDraw.core.control.ControllBase;
import com.github.novisoftware.patternDraw.core.control.Looper;
import com.github.novisoftware.patternDraw.core.control.Looper2D;
import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueString;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.misc.IconImage;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;
import com.github.novisoftware.patternDraw.utils.GuiPreference;
import com.github.novisoftware.patternDraw.utils.GuiUtil;

public class P030____ControlElement extends P020___AbstractElement {
	/**
	 * 制御用の箱の集まり・かたまり
	 */
	public String controlType;

	public HashSet<P030____ControlElement> controllerGroup;

	public P030____ControlElement(EditDiagramPanel EditPanel) {
		super(EditPanel);
	}

	public P030____ControlElement(EditDiagramPanel EditPanel, String s) {
		super(EditPanel);

		String a[] = FileReadUtil.tokenizeToArray(s);
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = a[5];
		this.setKindString(a[6]);
		this.controlType = a[7];
		this.setRpnString(a[8]);
//		buildParameterList(this.getRpnString());
		
		/*
		connectors = new ArrayList<P010___ConnectTerminal>();
		connectors.add(new P010___ConnectTerminal(this, "test", Value.ValueType.NUMERIC, "",  0, 2));
		*/
	}

	// コネクタ表示用
	public int getXc() {
		return x - 20;
	}
	public int getYc() {
		return y + 60;
	}
	public int getWc() {
		return w;
	}
	public int getHc() {
		return 50;
	}

	HashMap<String, ValueType> variableNamesAndTypes = new HashMap<String, ValueType>();

	/**
	 * 添字ループの場合の変数名
	 * 「変数を参照する」メニュー要素を作成するために必要。
	 *
	 * @return
	 */
	public HashMap<String, ValueType> getVariableNamesAndTypes() {
		return this.variableNamesAndTypes;
	}

	public String getControlType() {
		return this.controlType;
	}

	public String str() {
		return String.format("CONTROL: %d %d %d %d %s %s %s %s",
				x,
				y,
				w,
				h,
				escape(id),
				escape(getKindString()),
				escape(controlType),
				escape(getRpnString()));
	}

	// Looper looper;

	/**
	 * 要素のRPN式。RPNは逆ポーランド記法。
	 */
	private Rpn rpn;

	public void setRpnString (String rpnString) {
		this.rpn = new Rpn(rpnString, this.editPanel.networkDataModel);

		P022_____RpnGraphNodeElement.buildParameterList2(this, this.getRpnString());

		this.variableNamesAndTypes = P030____ControlElement.analyzeGetVariableNames(this.rpn);
	}

	public Rpn getRpn() {
		return this.rpn;
	}

	public String getRpnString() {
		return this.rpn.getFormula();
	}

	public String getRepresentExpression() {
		return this.rpn.getDisplayString();
	}

	/**
	 * コントロール用のRPN解析:
	 * 
	 * たとえばループの場合、添え字をループ内で利用する。
	 * このループ添え字の「代入先変数名」のリストを取得する。
	 * 
	 * @param rpn
	 * @return
	 */
	static public HashMap<String, ValueType> analyzeGetVariableNames(Rpn rpn) {
		Stack<Value> stack = new Stack<>();

		for (String s : rpn.getExpandedArray()) {
			String r = RpnUtil.getRepresent(s);

			if (r.equals(":loop")) {
				return new HashMap<String, ValueType>();
			}
			else if (r.equals(":index_loop")) {
				HashMap<String, ValueType> set = new HashMap<String, ValueType>();
				String varName = ((ValueString)(stack.pop())).toString();
				set.put(varName, ValueType.FLOAT);
				return set;
			}
			else if (r.equals(":index_2d_loop")) {
				HashMap<String, ValueType> set = new HashMap<String, ValueType>();
				String varName1 = ((ValueString)(stack.pop())).toString();
				set.put(varName1, ValueType.FLOAT);
				String varName2 = ((ValueString)(stack.pop())).toString();
				set.put(varName2, ValueType.FLOAT);
				String varName3 = ((ValueString)(stack.pop())).toString();
				set.put(varName3, ValueType.TRANSFORM);
				return set;
			}
			else if (r.equals(":index_2d_loop_honeycomb")) {
				HashMap<String, ValueType> set = new HashMap<String, ValueType>();
				String varName1 = ((ValueString)(stack.pop())).toString();
				set.put(varName1, ValueType.FLOAT);
				String varName2 = ((ValueString)(stack.pop())).toString();
				set.put(varName2, ValueType.FLOAT);
				String varName3 = ((ValueString)(stack.pop())).toString();
				set.put(varName3, ValueType.TRANSFORM);
				
				Debug.println(
						String.format("var def ... %s %s %s\n", varName1, varName2, varName3)
						);
				return set;
			}
			else if (r.startsWith("<")) {
				// 何もしない
			}
			else {
				stack.push(new ValueString(r));
			}
		}

		return null;
	}

	/***
	 * 実行時の初期化
	 * 
	 * @throws CaliculateException 
	 */
	public ControllBase init() throws CaliculateException {
		ControllBase ret = null;
		Stack<Value> stack = new Stack<>();

		Rpn rpn = this.getRpn();
		for (String s : rpn.getArray()) {
			String r = RpnUtil.getRepresent(s);

			if (r.equals(":loop")) {
				String s_to = ((ValueString)(stack.pop())).toString();
				String s_from = ((ValueString)(stack.pop())).toString();
				BigDecimal to = new BigDecimal(s_to);
				BigDecimal from = new BigDecimal(s_from);
				BigDecimal step = BigDecimal.ONE;
				ret = new Looper(from, to, step);
				break;
			}
			else if (r.equals(":index_loop")) {
				String varName = ((ValueString)(stack.pop())).toString();
				String s_step = ((ValueString)(stack.pop())).toString();
				String s_to = ((ValueString)(stack.pop())).toString();
				String s_from = ((ValueString)(stack.pop())).toString();

				BigDecimal step = new BigDecimal(s_step);
				BigDecimal to = new BigDecimal(s_to);
				BigDecimal from = new BigDecimal(s_from);

				ret = new Looper(this.editPanel.networkDataModel.variables,
						varName,
						from, to, step);
				break;
			}
			else if (r.equals(":index_2d_loop")
					|| r.equals(":index_2d_loop_honeycomb")
					) {
				boolean isHoneyComb = r.equals(":index_2d_loop_honeycomb");
				// X, Y の添字はオマケで生成する
				String varName_Yn = ((ValueString)(stack.pop())).toString();
				String varName_Xn = ((ValueString)(stack.pop())).toString();
				/*
				 * 移動変換を作成すればよい
				 */
				String varName_pos = ((ValueString)(stack.pop())).toString();
				String s_y_N = ((ValueString)(stack.pop())).toString();
				String s_x_N = ((ValueString)(stack.pop())).toString();

				int x_N = Integer.parseInt(s_x_N);
				int y_N = Integer.parseInt(s_y_N);

				ret = new Looper2D(this.editPanel.networkDataModel.variables,
						varName_pos,
						varName_Xn,
						varName_Yn,
						x_N,
						y_N,
						isHoneyComb
						);
				break;
			}
			else if (r.startsWith("<")) {
				r.replaceAll("[<>]", "");
				Value v = this.editPanel.networkDataModel.variables.get(r);
				stack.push(v);
			}
			else {
				System.out.println("r = " + r);
				stack.push(new ValueString(r));
			}
		}

		if (ret == null) {
			// コントロールのRPNでこの関数の処理対象外の未知のことが記述されている場合
			Debug.println("CONTROL", "内部矛盾。 内部RPNで制御子を作成していない。");
			throw new CaliculateException(CaliculateException.MESSAGE_OTHER_ERROR);
		}

		return ret;
	}

	/***
	 * UI表示用文字列を取得
	 */
	public String getDisplayString() {
		ControllBase ret = null;
		Stack<String> stack = new Stack<>();
		Stack<String> commentStack = new Stack<>();

		Rpn rpn = this.getRpn();
		for (String s : rpn.getExpandedArray()) {
			String r = RpnUtil.getRepresent(s);
			String c = RpnUtil.getComment(s);

			if (r.equals(":loop")) {
				String s_to = stack.pop().toString();
				String s_from = stack.pop().toString();

				return "  × " + s_to;
			}
			else if (r.equals(":index_loop")) {
				String varName = stack.pop().toString();
				String s_step = stack.pop().toString();
				String s_to = stack.pop().toString();
				String s_from = stack.pop().toString();

				return "  " + varName + ": " + s_from + " → " + s_to + "  (刻み幅: " + s_step +" )";
			}
			else if (r.equals(":index_2d_loop")) {
				String varName_Yn =    stack.pop().toString();
				String varName_Xn =    stack.pop().toString();
				String varName_pos =   stack.pop().toString();
				String s_y_N =         stack.pop().toString();
				String s_x_N =         stack.pop().toString();

				return "  " + String.format("格子状に配列 %s = %s, %s (%s, %s)",
						varName_pos,
						varName_Xn,
						varName_Yn,
						s_x_N,
						s_y_N);
			}
			else if (r.equals(":index_2d_loop_honeycomb")) {
				String varName_Yn =    stack.pop().toString();
				String varName_Xn =    stack.pop().toString();
				String varName_pos =   stack.pop().toString();
				String s_y_N =         stack.pop().toString();
				String s_x_N =         stack.pop().toString();

				return "  " + String.format("ハニカム(はちのす)状に配列 %s = %s, %s (%s, %s)",
						varName_pos,
						varName_Xn,
						varName_Yn,
						s_x_N,
						s_y_N);
			}
			else if (r.startsWith("<")) {
				// Value v = this.editPanel.networkDataModel.variables.get();
				stack.push(r.replaceAll("[<>]", ""));
				commentStack.push(c);
			}
			else {
				stack.push(r);
				commentStack.push(c);
			}
		}

		return null;
	}

	@Override
	public P030____ControlElement getCopy() {
		P030____ControlElement ret = new P030____ControlElement(this.editPanel, this.str());

		return ret;
	}


//	final Color colorBorder = new Color( 0.5f, 0.5f, 0.5f );
	private final Color colorBorder = new Color( 0.5f, 0.5f, 0.5f );
	private final Color colorLine = new Color( 0.9f, 0.9f, 1.0f );
	private int MARK_WIDTH = 20;

	private Font font = new Font("Meiryo UI", Font.BOLD, 13);


	/**
	 * 描画。
	 * 描画用メソッドは段階に分けて呼び出されます(引数: phase)。
	 */
	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		P030____ControlElement t = this;

		// 結線
		if (phase == 0) {
			paintConnectedLine(g2);
		}

		// 箱
		if (phase == 1) {
			g2.setFont(font);

			if (this.editPanel.isVisibleDebugInfo) {
				g2.setColor(Color.RED);
				g2.drawString(t.getDebugIdString(), t.x + 30, t.y + 9);
			}

			String typeDisplay = "";
			if (t.controlType.equals("REPEAT")) {
					typeDisplay = "LOOP" + this.getDisplayString();
			}
			else {
				typeDisplay = t.getKindString() + ": " + t.controlType;
			}

			g2.setColor(Color.GRAY);
			g2.drawString(typeDisplay, t.x + 30, t.y - 9);

			/*
			g2.setColor(Color.GRAY);
			g2.drawString(t.getKindString(), t.x + 30, t.y - 9);
			*/

			if ( t.getKindString().equals("制御")  ) {
				if (t.controlType.equals("REPEAT") || t.controlType.equals("IF")) {
					g2.setColor(colorLine);
					g2.fillRect(t.x, t.y, MARK_WIDTH, t.h);
				}

				g2.setStroke(new BasicStroke(3));

				g2.setColor(colorBorder);
				g2.drawRoundRect(t.x, t.y, t.w, t.h, 15, 15);

				if (t.controlType.equals("REPEAT")) {
					BufferedImage image = GuiUtil.getImage(IconImage.LOOP, this);
					g2.drawImage(image, t.x - 50, t.y, null);
				} else if (t.controlType.equals("IF")) {
					BufferedImage image = GuiUtil.getImage(IconImage.IF, this);
					g2.drawImage(image, t.x - 50, t.y, null);
				}

				/*
				g2.setColor(Color.BLACK);
				g2.drawString(t.type, t.x + 35, t.y + t.h - 10 );
				*/
			}

		}

		// コネクタ用の端子
		g2.setStroke(GuiPreference.STROKE_PLAIN);
		for (P010___ConnectTerminal connector : connectors) {
			connector.paint(g2, phase, true);
		}
	}

	/**
	 * ドラッグ開始時
	 */
	@Override
	public P001_IconGuiInterface getTouchedObject(EditDiagramPanel editDiagramPanel, int x, int y) {
		for (P010___ConnectTerminal connector : connectors) {
			if (connector.isTouched(x, y)) {
				editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return connector;
			}
		}

		if ( this.x < x && x < this.x + this.MARK_WIDTH
				&& this.y < y && y < this.y + this.w) {
			// Debug.println("controller", "移動開始");
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			this.dragMode = DragMode.MOVE;
			return this;
		}
		if (this.isOnRightEdge(x) && this.isOnBottomEdge(y)) {
			// Debug.println("controller", "リサイズ開始");
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			this.dragMode = DragMode.RESIZE_XY;
			return this;
		} else if (this.isOnRightEdge(x) && this.isOnHeight(y)) {
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			this.dragMode = DragMode.RESIZE_X;
			return this;
		} else if (this.isOnWidth(x) && this.isOnBottomEdge(y)) {
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			this.dragMode = DragMode.RESIZE_Y;
			return this;
		}

		return null;
	}

	/**
	 * 含まれるかを判定する。
	 *
	 * 含まれるかは、見た目で囲まれるかを判定する
	 */
	public boolean includes(P020___AbstractElement e) {
		return (this.x < e.x
				&& e.x+e.w < this.x + this.w
				&& this.y < e.y
				&& e.y + e.h < this.y + this.h);
	}

	/**
	 * ドラッグ時
	 */
	@Override
	public void dragged(int x, int y) {
		dragged(x, y, true);
	}

	public void dragged(int x, int y, boolean requetOrigin) {
		if (this.dragMode == DragMode.MOVE) {
			this.x += x;
			this.y += y;

			HashMap<P030____ControlElement, ArrayList<P020___AbstractElement>> map = this.editPanel.networkDataModel.controlled_head;

			if (map.containsKey(this)) {
				for (P020___AbstractElement ei : map.get(this)) {
					if (ei instanceof P022_____RpnGraphNodeElement) {
						Integer groupId = ((P022_____RpnGraphNodeElement)ei).groupHead;
						if (groupId != null) {
							for (P020___AbstractElement ei2 : this.editPanel.networkDataModel.graphGroup.get(groupId)) {
								ei2.dragged(x, y);
							}
						}
					}
					/*
					if (ei instanceof Control) {
						Control other = (Control)ei;
						if (this.x < other.x && this.y < other.y && other.x + other.w < this.x + this.w && other.y + other.h < this.y + this.w) {
							// other.dragged(x, y);
							other.x += x;
							other.y += y;
						}
					}
					*/
				}
			}

			/*
			// 少し、ちがう。
			for (ElementIcon ei : this.editPanel.networkDataModel.getElements()) {
				if (ei instanceof Controller) {
					Controller other = (Controller)ei;
					if (this.x < other.x && this.y < other.y && other.x + other.w < this.x + this.w && other.y + other.h < this.y + this.h) {
						// other.dragged(x, y);
						other.x += x;
						other.y += y;
					}
				}
			}
			*/

			// 暫定。
			ArrayList<P020___AbstractElement> cList = this.editPanel.networkDataModel.control_contains.get(this);
			if (cList != null) {
				for (P020___AbstractElement ei : cList) {
					if (ei instanceof P030____ControlElement) {
						P030____ControlElement other = (P030____ControlElement)ei;

						// if (this.x < other.x && this.y < other.y && other.x + other.w < this.x + this.w && other.y + other.h < this.y + this.h) {
							// other.dragged(x, y);
							other.x += x;
							other.y += y;
						// }
					}
				}
			}

			if (requetOrigin) {
				if (controllerGroup != null) {
					for (P030____ControlElement c : controllerGroup) {
						if (c != this) {
//							Debug.println("controller", "recursive drag.");
							c.dragMode = this.dragMode;
							c.dragged(x, y, false);
						}
					}
				}
			}

		}
		else if (this.dragMode == DragMode.RESIZE_XY
				|| this.dragMode == DragMode.RESIZE_X
				|| this.dragMode == DragMode.RESIZE_Y
				) {
			int w_old = this.w;
//			int h_old = this.h;

			if (this.dragMode == DragMode.RESIZE_XY
					|| this.dragMode == DragMode.RESIZE_X) {
				this.w += x;
			}
			if (this.dragMode == DragMode.RESIZE_XY
					|| this.dragMode == DragMode.RESIZE_Y) {
				this.h += y;
			}

			// サイズを制限する
			if (this.w < GuiPreference.ControlElementLimit.SIZE_MIN_WIDTH) {
				this.w = GuiPreference.ControlElementLimit.SIZE_MIN_WIDTH;
			}
			if (this.h < GuiPreference.ControlElementLimit.SIZE_MIN_HEIGHT) {
				this.h = GuiPreference.ControlElementLimit.SIZE_MIN_HEIGHT;
			}
			if (this.w > GuiPreference.ControlElementLimit.SIZE_MAX_WIDTH) {
				this.w = GuiPreference.ControlElementLimit.SIZE_MAX_WIDTH;
			}
			if (this.h > GuiPreference.ControlElementLimit.SIZE_MAX_HEIGHT) {
				this.h = GuiPreference.ControlElementLimit.SIZE_MAX_HEIGHT;
			}

			if (controllerGroup != null) {
				for (P030____ControlElement c : controllerGroup) {
					if (c != this) {
						if (this.dragMode == DragMode.RESIZE_XY
								|| this.dragMode == DragMode.RESIZE_X) {
							if (c.y == this.y && c.x > this.x) {
								c.x += this.w - w_old;
							}
						}
						if (this.dragMode == DragMode.RESIZE_XY
							|| this.dragMode == DragMode.RESIZE_Y) {
							c.h = this.h;
						}
					}
				}
			}
		}
	}

	/**
	 * 変数名やパラメタ名が変更になったことを通知するインタフェース
	 * 
	 * @param before 変更前
	 * @param after 変更後
	 */
	@Override
	public void notifyVarNameChange(String before, String after) {
		rpn.notifyVarNameChange(before, after);
	}

	public String contollerGroup_str() {
		if (this.controllerGroup == null) {
			return null;
		}
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("CONTROL_GROUP:");

			for (P030____ControlElement c : controllerGroup) {
				sb.append(' ');
				sb.append(c.id);
			}

			return sb.toString();
		}
	}

	@Override
	public ValueType getValueType() {
		return ValueType.NONE;
	}

	@Override
	public boolean isComment() {
		return false;
	}
}