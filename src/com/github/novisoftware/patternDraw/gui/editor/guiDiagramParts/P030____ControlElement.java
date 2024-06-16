package com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import com.github.novisoftware.patternDraw.core.Rpn;
import com.github.novisoftware.patternDraw.core.RpnUtil;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.ValueNumeric;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.ControllBase;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.Looper;
import com.github.novisoftware.patternDraw.gui.misc.IconImage;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileReadUtil;
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

	public ArrayList<String> optStr() {
		ArrayList<String> ret = new ArrayList<>();

		return ret;
	}

	// Looper looper;


	/**
	 * 要素のRPN式。RPNは逆ポーランド記法。
	 */
	private Rpn rpn;

	public void setRpnString (String rpnString) {
		this.rpn = new Rpn(rpnString, this.editPanel.networkDataModel);
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

	/***
	 * 実行時の初期化
	 */
	public ControllBase init() {
		ControllBase ret = null;
		Stack<Value> stack = new Stack<>();

		Rpn rpn = this.getRpn();
		for (String s : rpn.getArray()) {
			String r = RpnUtil.getRepresent(s);

			if (r.equals(":loop")) {
				int to = ((ValueNumeric)(stack.pop())).getInternal().intValue();
				int from = ((ValueNumeric)(stack.pop())).getInternal().intValue();
				ret = new Looper(from, to);
				break;
			}
			else if (r.startsWith("<")) {
				r.replaceAll("[<>]", "");
				Value v = this.editPanel.networkDataModel.variables.get(r);
				stack.push(v);
			}
			else {
				stack.push(new ValueNumeric(r));
			}
		}

		if (ret == null) {
			Debug.println("CONTROL", "内部矛盾。 内部RPNで制御子を作成していない。");
		}

		return ret;
	}

	@Override
	public P030____ControlElement getCopy() {
		P030____ControlElement ret = new P030____ControlElement(this.editPanel, this.str());

		return ret;
	}


//	final Color colorBorder = new Color( 0.5f, 0.5f, 0.5f );
	private final Color colorBorder = new Color( 0.3f, 0.3f, 0.3f );
	private final Color colorLine = new Color( 0.9f, 0.9f, 1.0f );
	private int MARK_WIDTH = 20;

	private Font font = new Font("Meiryo UI", Font.BOLD, 13);


	/**
	 * 描画用メソッドは段階に分けて呼び出されます(引数: phase)。
	 */
	@Override
	public void paintWithPhase(Graphics2D g2, int phase) {
		P030____ControlElement t = this;

		// 結線
		if (phase == 0) {
			// 処理不要
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
					typeDisplay = "LOOP";
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

				g2.setColor(colorBorder);
				g2.drawRect(t.x, t.y, t.w, t.h);

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

		/*
		// 端子
		g2.setStroke(strokePlain);
		for (GraphConnector connector : connectors) {
			connector.paint(g2, phase);
		}
		*/
	}

	enum DragMode {
		MOVE,
		RESIZE
	};

	DragMode dragMode = DragMode.MOVE;

	/**
	 * ドラッグ開始時
	 */
	@Override
	public P001_IconGuiInterface getTouchedObject(EditDiagramPanel editDiagramPanel, int x, int y) {
		if ( this.x < x && x < this.x + this.MARK_WIDTH
				&& this.y < y && y < this.y + this.w) {
			this.dragMode = DragMode.MOVE;

			Debug.println("controller", "移動開始");
			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

			return this;
		}
		if ( this.x + this.w - 5 < x && x < this.x + this.w + 5
				&& this.y < y && y < this.y + this.w) {
			this.dragMode = DragMode.RESIZE;

			editDiagramPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			Debug.println("controller", "リサイズ開始");

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

							/*
							c.x += x;
							c.y += y;
							*/

						}
					}
				}
			}

			// HashMap<Controller, ArrayList<ElementIcon>> map = this.editPanel.networkDataModel.controlled_head;


		}
		else if (this.dragMode == DragMode.RESIZE) {
			int w_old = this.w;
//			int h_old = this.h;


			this.w += x;
			this.h += y;

			// サイズを制限する
			if (this.w < 100) {
				this.w = 100;
			}
			if (this.h < 20) {
				this.h = 20;
			}
			if (this.w > 1500) {
				this.w = 1500;
			}
			if (this.h > 5000) {
				this.h = 5000;
			}

			if (controllerGroup != null) {
				for (P030____ControlElement c : controllerGroup) {
					if (c != this) {
						if (c.y == this.y && c.x > this.x) {
							c.x += this.w - w_old;
						}
						c.h = this.h;
					}
				}
			}


		}
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