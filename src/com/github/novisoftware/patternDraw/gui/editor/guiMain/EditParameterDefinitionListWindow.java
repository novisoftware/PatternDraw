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

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefineToEdit;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiInputWindow.InputParamDefWindow;
import com.github.novisoftware.patternDraw.gui.misc.JFrame2;
import com.github.novisoftware.patternDraw.gui.misc.JLabel2;
import com.github.novisoftware.patternDraw.utils.Preference;

/**
 * パラメーター一覧の定義ウィンドウ。
 */
public class EditParameterDefinitionListWindow extends JFrame2 {
	final JPanel jp;

	/**
	 * パラメーター一覧
	 */
	public final ArrayList<ParameterDefineToEdit> params;
	/**
	 * パラメーター名の一覧(設定された変数のみを対象にする)
	 */
	final public ArrayList<String> variableNameList;

	/**
	 * パラメーター一覧の定義ウィンドウに貼り付けてある、 パラメーターに対応するコンポーネントのリスト。
	 * <ul>
	 * <li>パラメーターの情報を表示するコンポーネントは、JPanel等でまとめずに個別に直接 pane に貼り付けている。
	 * </ul>
	 */
	HashMap<ParameterDefineToEdit, ArrayList<JComponent>> componentOnPane;

	/**
	 * 子ウィンドウ(個別パラメーターの設定画面)
	 */
	private InputParamDefWindow inputParamDefWindow = null;

	/**
	 * 追加ボタン
	 */
	final JButton addButton;

	public EditParameterDefinitionListWindow(
			final ArrayList<ParameterDefineToEdit> params,
			Runnable closeCallback) {
		super();

		this.params = params;
		System.out.println("params (obj) = " + params);
		variableNameList = new ArrayList<String>();
		this.setTitle("パラメーターの一覧");
		this.setSize(800, 800);

		// レイアウト
		jp = new JPanel();
		jp.setSize(800, 800);
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
		this.componentOnPane = new HashMap<ParameterDefineToEdit, ArrayList<JComponent>>();
		for (ParameterDefineToEdit p : params) {
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

	private void createInputWindow(ParameterDefineToEdit para) {
    	if (inputParamDefWindow == null) {
			inputParamDefWindow = new InputParamDefWindow(this, para, variableNameList);
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
		final EditParameterDefinitionListWindow thisFrame = this;

		JButton buttonCancel = new JButton(Preference.ADD_BUTTON_STRING);
		buttonCancel.setFont(Preference.ADD_BUTTON_FONT);
		buttonCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ParameterDefineToEdit parameter = new ParameterDefineToEdit("", "", "0", ValueType.INTEGER,
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

	void addParamDefToPane(final ParameterDefineToEdit para) {
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
		final EditParameterDefinitionListWindow thisFrame = this;
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

	public void updateParamDef(ParameterDefineToEdit para) {
		ArrayList<JComponent> pList = this.componentOnPane.get(para);
		((JLabel2) (pList.get(0))).setText(para.name);
		((JLabel2) (pList.get(1))).setText(para.defaultValue);
		((JLabel2) (pList.get(2))).setText(Value.valueTypeToDescString(para.valueType));
		((JLabel2) (pList.get(3))).setText(para.description);
	}

	public void removeParamDefFromPane(ParameterDefineToEdit para) {
		ArrayList<JComponent> pList = this.componentOnPane.get(para);
		this.componentOnPane.remove(para);
		Container pane = this.jp;
		for (JComponent c : pList) {
			pane.remove(c);
		}
	}

	static public void main(String args[]) {
		ArrayList<ParameterDefineToEdit> params = new ArrayList<ParameterDefineToEdit>();

		// このmainは、このウィンドウだけを立ち上げて動作確認するためのもの。
		EditParameterDefinitionListWindow frame = new EditParameterDefinitionListWindow(params, null);
		frame.setVisible(true);
		frame.setLocation(900, 40);
		return;
	}
}
