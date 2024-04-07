package com.github.novisoftware.patternDraw.gui.editor.parts;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

/*
import com.github.novisoftware.dentakuTest.calculation.Rpn;
import com.github.novisoftware.dentakuTest.calculation.RpnUtil;
import com.github.novisoftware.dentakuTest.calculation.Value;
import com.github.novisoftware.dentakuTest.calculation.ValueNumeric;
import com.github.novisoftware.dentakuTest.gui.EditPanel;
import com.github.novisoftware.dentakuTest.gui.parts.controlSub.ControllBase;
import com.github.novisoftware.dentakuTest.gui.parts.controlSub.Looper;
import com.github.novisoftware.dentakuTest.util.Common;
import com.github.novisoftware.dentakuTest.util.Debug;
import com.github.novisoftware.dentakuTest.util.IconImage;
*/

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.util.Value;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon.KindId;
import com.github.novisoftware.patternDraw.gui.editor.EditPanel;
import com.github.novisoftware.patternDraw.gui.editor.parts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.ControllBase;
import com.github.novisoftware.patternDraw.gui.editor.parts.controlSub.Looper;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;
import com.github.novisoftware.patternDraw.gui.editor.util.IconImage;
import com.github.novisoftware.patternDraw.gui.editor.util.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.util.ValueNumeric;
import com.github.novisoftware.patternDraw.gui.editor.parts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.parts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.parts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.parts.IconGuiInterface;

public class ControlBlock extends ElementIcon {
	/**
	 * 制御用の箱の集まり・かたまり
	 */
	public HashSet<ControlBlock> controllerGroup;

	public String str() {
		return String.format("CONTROL: %d %d %d %d %s %s %s %s", x, y, w, h, escape(id), escape(getKindString()), escape(type), escape(getRpnString()));
	}

	public ArrayList<String> optStr() {
		ArrayList<String> ret = new ArrayList<>();

		return ret;
	}

	public ControlBlock(EditPanel EditPanel) {
		super(EditPanel);
	}

	public ControlBlock(EditPanel EditPanel, String s) {
		super(EditPanel);

		String a[] =s.split(" ");
		this.x = Integer.parseInt(a[1], 10);
		this.y = Integer.parseInt(a[2], 10);
		this.w = Integer.parseInt(a[3], 10);
		this.h = Integer.parseInt(a[4], 10);
		this.id = unescape(a[5]);
		this.setKindString(unescape(a[6]));
		this.type = unescape(a[7]);
		this.setRpnString(unescape(a[8]));
//		buildParameterList(this.getRpnString());
	}

	public String getControlType() {
		return this.type;
	}

	// Looper looper;

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
				Value v = GraphNodeElement.variables.get(r);
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
	public ControlBlock getCopy() {
		ControlBlock ret = new ControlBlock(this.editPanel, this.str());

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
		ControlBlock t = this;

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

			String typeDisplay = t.getKindString() + ": " + t.type;

			g2.setColor(Color.GRAY);
			g2.drawString(typeDisplay, t.x + 30, t.y - 9);

			/*
			g2.setColor(Color.GRAY);
			g2.drawString(t.getKindString(), t.x + 30, t.y - 9);
			*/

			if ( t.getKindString().equals("制御")  ) {
				if (t.type.equals("REPEAT") || t.type.equals("IF")) {
					g2.setColor(colorLine);
					g2.fillRect(t.x, t.y, MARK_WIDTH, t.h);
				}

				g2.setColor(colorBorder);
				g2.drawRect(t.x, t.y, t.w, t.h);

				if (t.type.equals("REPEAT")) {
					BufferedImage image = Common.getImage(IconImage.LOOP, this);
					g2.drawImage(image, t.x - 50, t.y, null);
				} else if (t.type.equals("IF")) {
					BufferedImage image = Common.getImage(IconImage.IF, this);
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
	public IconGuiInterface getTouchedObject(int x, int y) {
		/*
		for (GraphConnector connector : connectors) {
			if (connector.isTouched(x, y)) {
				return connector;
			}
		}
		*/

		if ( this.x < x && x < this.x + this.MARK_WIDTH
				&& this.y < y && y < this.y + this.w) {
			this.dragMode = DragMode.MOVE;

			Debug.println("controller", "移動開始");

			return this;
		}
		if ( this.x + this.w - 5 < x && x < this.x + this.w + 5
				&& this.y < y && y < this.y + this.w) {
			this.dragMode = DragMode.RESIZE;

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
	public boolean includes(ElementIcon e) {
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

			HashMap<ControlBlock, ArrayList<ElementIcon>> map = this.editPanel.networkDataModel.controlled_head;

			if (map.containsKey(this)) {
				for (ElementIcon ei : map.get(this)) {
					if (ei instanceof GraphNodeElement) {
						Integer groupId = ((GraphNodeElement)ei).groupHead;
						if (groupId != null) {
							for (ElementIcon ei2 : this.editPanel.networkDataModel.graphGroup.get(groupId)) {
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
			ArrayList<ElementIcon> cList = this.editPanel.networkDataModel.control_contains.get(this);
			if (cList != null) {
				for (ElementIcon ei : cList) {
					if (ei instanceof ControlBlock) {
						ControlBlock other = (ControlBlock)ei;

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
					for (ControlBlock c : controllerGroup) {
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
				for (ControlBlock c : controllerGroup) {
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

			for (ControlBlock c : controllerGroup) {
				sb.append(' ');
				sb.append(c.id);
			}

			return sb.toString();
		}
	}

}