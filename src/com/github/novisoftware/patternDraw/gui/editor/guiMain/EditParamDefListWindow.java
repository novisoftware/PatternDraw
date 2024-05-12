package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.EditParamDefWindow;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.Preference;

/**
 * パラメーター一覧の定義ウィンドウ。
 */
public class EditParamDefListWindow extends JFrame2 {
	public static final int WINDOW_POS_X = 50;
	public static final int WINDOW_POS_Y = 50;
	public static final int WINDOW_WIDTH = 640;
	public static final int WINDOW_HEIGHT = 600;

	final JPanel jp;

	/**
	 * パラメーター一覧
	 */
	public final ArrayList<ParameterDefine> params;
	/**
	 * パラメーター名の一覧(設定された変数のみを対象にする)
	 */
	// final public ArrayList<String> variableNameList;

	/**
	 * パラメーター一覧の定義ウィンドウに貼り付けてある、 パラメーターに対応するコンポーネントのリスト。
	 * <ul>
	 * <li>パラメーターの情報を表示するコンポーネントは、JPanel等でまとめずに個別に直接 pane に貼り付けている。
	 * </ul>
	 */
	HashMap<ParameterDefine, ArrayList<JComponent>> componentOnPane;

	/**
	 * 子ウィンドウ(個別パラメーターの設定画面)
	 */
	private EditParamDefWindow inputParamDefWindow = null;

	/**
	 * 追加ボタン
	 */
	final JButton addButton;

	public EditParamDefListWindow(
			final ArrayList<ParameterDefine> params,
			Runnable closeCallback) {
		super();
		this.setTitle("パラメーターの一覧");
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);

		this.params = params;

		// レイアウト
		jp = new JPanel();
		jp.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		jp.setBackground(Preference.BG_COLOR);
		jp.setForeground(Preference.TEXT_COLOR);

		this.add(jp);
		// Container pane = this.getContentPane();
		Container pane = jp;// .getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));
		jp.setLayout(new FlowLayout(FlowLayout.LEADING));

		pane.add(new JLabel2("パラメーターの一覧"));
		pane.add(horizontalRule(7));

		// Container pane = this.getContentPane();
		// this.setLayout(new FlowLayout(FlowLayout.LEADING));

		// コンテナ管理用
		// パラメーターに対応する JLabel 等をpaneに直接貼り付けている。
		// これを削除する際に使用する。
		this.componentOnPane = new HashMap<ParameterDefine, ArrayList<JComponent>>();
		for (ParameterDefine p : params) {
			addParamDefToPane(p);
		}

		this.addButton = generateAddButton();
		pane.add(addButton);


		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (closeCallback != null) {
					closeCallback.run();
				}
			}
		});

	}

	public void subWindowDisposeNotify() {
		inputParamDefWindow = null;
		addButton.setEnabled(true);
	}

	private void createInputWindow(ParameterDefine para) {
		HashSet<String> variableNameSet = new HashSet<String>();
		for (ParameterDefine p : params) {
			if (p != para) {
				variableNameSet.add(p.name);
			}
		}

    	if (inputParamDefWindow == null) {
			inputParamDefWindow = new EditParamDefWindow(this, para, variableNameSet);
    	}
		inputParamDefWindow.setVisible(true);
		addButton.setEnabled(false);
	}


	/**
	 * 追加ボタンを作る
	 *
	 * @param pane
	 */
	JButton generateAddButton() {
		final EditParamDefListWindow thisFrame = this;

		JButton buttonCancel = new JButton(Preference.ADD_BUTTON_STRING);
		buttonCancel.setFont(Preference.ADD_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParameterDefine parameter = new ParameterDefine("", "", "0", ValueType.INTEGER,
						false, "", false, "", "");
				params.add(parameter);

				thisFrame.jp.remove(addButton);
				thisFrame.addParamDefToPane(parameter);
				thisFrame.jp.add(addButton);

				thisFrame.repaintHard();
				createInputWindow(parameter);
			}
		});

		return buttonCancel;
	}

	/**
	 * repaint() だとコンポーネントの再レイアウトが行われないので (スキマがあくので)再描画する。
	 */
	public void repaintHard() {
		// TODO
		// リペイントの指示をしたい。。
		// (repaintでは再描画されない)
		// もう少し綺麗な書き方で書きたい。
		Dimension d = this.getSize();
		d.setSize(d.getWidth() + 1, d.getHeight());
		this.setSize(d);
		d.setSize(d.getWidth() - 1, d.getHeight());
		this.setSize(d);

	}

	void addParamDefToPane(final ParameterDefine para) {
		ArrayList<JComponent> pList = new ArrayList<JComponent>();
		this.componentOnPane.put(para, pList);

		JLabel2 name = new JLabel2("");
		pList.add(name);
		JLabel2 defo = new JLabel2("");
		pList.add(defo);
		JLabel2 desc = new JLabel2("");
		pList.add(desc);
		JLabel2 typeDesc = new JLabel2("");
		pList.add(typeDesc);
		// 追加情報は TODO
		JLabel2 addtional = new JLabel2("(追加情報)");
		pList.add(addtional);

		pList.add(horizontalRule(5));

		Container pane = this.jp;
		for (JComponent c : pList) {
			pane.add(c);
		}
		this.updateParamDef(para);

		// 押したときの動作を設定
		final EditParamDefListWindow thisFrame = this;
		MouseAdapter mlis = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            	thisFrame.createInputWindow(para);
            }
        };
		for (JComponent c : pList) {
			if (c instanceof JLabel) {
				((JLabel) c).addMouseListener(mlis);
			}
		}

	}

	public void updateParamDef(ParameterDefine para) {
		ArrayList<JComponent> pList = this.componentOnPane.get(para);
		((JLabel2) (pList.get(0))).setText(para.name);
		((JLabel2) (pList.get(1))).setText(para.defaultValue);
		((JLabel2) (pList.get(2))).setText(Value.valueTypeToDescString(para.valueType));
		((JLabel2) (pList.get(3))).setText(para.description);
	}

	public void removeParamDefFromPane(ParameterDefine para) {
		ArrayList<JComponent> pList = this.componentOnPane.get(para);
		this.componentOnPane.remove(para);
		Container pane = this.jp;
		for (JComponent c : pList) {
			pane.remove(c);
		}
	}

	static public void main(String args[]) {
		ArrayList<ParameterDefine> params = new ArrayList<ParameterDefine>();

		// このmainは、このウィンドウだけを立ち上げて動作確認するためのもの。
		EditParamDefListWindow frame = new EditParamDefListWindow(params, null);
		frame.setVisible(true);
		frame.setLocation(900, 40);
		return;
	}
}
