package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.github.novisoftware.patternDraw.core.exception.LangSpecException;
import com.github.novisoftware.patternDraw.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P023_____FncGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P030____ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;

public class ElementFactory extends AbstractElementFactory {
	static final String CONST_REG__META_EXIST_VARIABLE = "\\{exist-variable\\}";
	static final String CONST_STR__META_EXIST_VARIABLE = reg2str(CONST_REG__META_EXIST_VARIABLE);

	static final String CONST_REG__META_EXIST_VARIABLE_REF = "\\{exist-variable-ref\\}";
	static final String CONST_STR__META_EXIST_VARIABLE_REF = reg2str(CONST_REG__META_EXIST_VARIABLE_REF);

	static final String CONST_REG__META_NEW_VARIABLE = "\\{new-variable\\}";
	static final String CONST_STR__META_NEW_VARIABLE = reg2str(CONST_REG__META_NEW_VARIABLE);


	final protected EditDiagramPanel editPanel;

	/**
	 * 説明文
	 */
	public String description;

	/**
	 * 分類
	 */
	public String kindName;

	/**
	 * 「制御」の場合だけ使用する
	 */
	public String controlType;

	/**
	 * 種類
	 */
	// public String outputType;
	public ValueType valueType;


	/**
	 * RPNかFNCかを区別する。
	 */
	public static enum PartsType {
		TYPE_UNDEF,
		TYPE_RPNDEF,
		TYPE_FNCDEF,
	}

	/**
	 * RPN または FNC
	 */
	final public PartsType defType;

	/**
	 * 式
	 */
	public String rpn;

	/**
	 * 関数
	 */
	public String fncName;

	/**
	 * 表示
	 */
	public String dispName;

	public int width;
	public int height;


	public ElementFactory(EditDiagramPanel editPanel, PartsType defType) {
		this.editPanel = editPanel;
		this.defType = defType;
	}

	public void addMenuList(
			JMenu menu,
			HashMap<String, JMenu> workHm2,
			final EditDiagramPanel editPanel,
			final MListener mListener,
			int x,
			int y
			) {
		String n = this.dispName;
		JMenuItem menuItem;
		if (n.indexOf('/') == -1) {
			menuItem = new JMenuItem(this.dispName);
			menu.add(menuItem);
		} else {
			String[] splited = n.split("/");

			String name = splited[splited.length - 1];
			String folder = n.substring(0, n.length() - 1 - name.length());

			if (!workHm2.containsKey(folder)) {
				JMenu addMenu = new JMenu(folder);
				menu.add(addMenu);
				workHm2.put(folder, addMenu);
			}
			JMenu parentMenu = workHm2.get(folder);

			menuItem = new JMenuItem(name);
			parentMenu.add(menuItem);
		}
		
		ElementFactory thisObj = this;
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				thisObj.createNewElement(editPanel, x, y);
				editPanel.networkDataModel.analyze();
				editPanel.repaint();

				// 閉じるので。
				mListener.isPopupExists = false;

				/*
				 * ElementIcon t =
				 * nParts.getNewElement(editPanel.networkDataModel.
				 * generateUniqueName(nParts.getKindName() + "0"), x, y);
				 * editPanel.networkDataModel.getElements().add(t);
				 * editPanel.networkDataModel.evaluate();
				 * editPanel.repaint();
				 */

			}
		});
	}

	/**
	// メニュー要素を展開する
	// (定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する)
	 *
	 * @param editPanel editPanelオブジェクト
	 * @param partsList 展開前のパーツリスト
	 * @return 展開後のパーツリスト
	 */
	public static ArrayList<AbstractElementFactory> partsListExtend(EditDiagramPanel editPanel, ArrayList<AbstractElementFactory> partsList) {
		// 定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する。
		ArrayList<AbstractElementFactory> expandedParts = new ArrayList<>();
		for (final AbstractElementFactory nPartsOrg : partsList) {
			if (nPartsOrg instanceof ElementFactory) {
				ElementFactory nParts = (ElementFactory)nPartsOrg;
				if (nParts.dispName.indexOf(ElementFactory.CONST_STR__META_EXIST_VARIABLE_REF) != -1) {
					// 既存の変数を参照
					Debug.println("match " + ElementFactory.CONST_STR__META_EXIST_VARIABLE_REF);

					ArrayList<String> variableNames = new ArrayList<String>();
					variableNames.addAll(editPanel.networkDataModel.refVariableNameList);

					/*
					for (ParameterDefine param : editPanel.networkDataModel.paramDefList) {
						variableNames.add(param.name);
					}
					*/

					for (String varName : variableNames) {
						ElementFactory add = nParts.getCopy();

						add.dispName = add.dispName.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE_REF, varName);
						add.rpn = add.rpn.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE_REF,  varName);
//						Debug.println("expand rpn: " + add.rpn);
						expandedParts.add(add);
					}
				}
				else if (nParts.dispName.indexOf(ElementFactory.CONST_STR__META_EXIST_VARIABLE) != -1) {
					// 既存の変数を上書き
					Debug.println("match " + ElementFactory.CONST_STR__META_EXIST_VARIABLE);

					// 「上書き可能な変数名」は、ループindex 等を含まない。
					// 「参照可能は変数名」には含まれる。

					HashSet<String> nameList = new HashSet<String>();
					ArrayList<String> variableNames = new ArrayList<String>();
					for (String name : editPanel.networkDataModel.overWriteVariableNameList) {
						if (!nameList.contains(name)) {
							nameList.add(name);
							variableNames.add(name);
							// System.out.println("***" + name + "***");
						}
					}

					for (String varName : variableNames) {
						ElementFactory add = nParts.getCopy();

						add.dispName = add.dispName.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE, varName);
						add.rpn = add.rpn.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE,  varName);
//						Debug.println("expand rpn: " + add.rpn);
						expandedParts.add(add);
					}

					/*
					// System.out.println("---");
					expandedParts.add(new SeparatorAdder());
					ArrayList<String> variableNames2 = new ArrayList<String>();
					for (ParameterDefine param : editPanel.networkDataModel.paramDefList) {
						if (!nameList.contains(param.name)) {
							nameList.add(param.name);
							variableNames2.add(param.name);
							// System.out.println("***" + param.name + "***");
						}
					}
					if (variableNames2.size() > 0) {
						for (String varName : variableNames2) {
							ElementFactory add = nParts.getCopy();

							add.dispName = add.dispName.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE, varName);
							add.rpn = add.rpn.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE,  varName);
//							Debug.println("expand rpn: " + add.rpn);
							expandedParts.add(add);
						}
					}
					*/
				}
				else if (nParts.dispName.indexOf(ElementFactory.CONST_STR__META_NEW_VARIABLE) != -1) {
					// 新しい変数に設定
					Debug.println("match " + ElementFactory.CONST_STR__META_NEW_VARIABLE);

					String newVariableName = GuiUtil.generateUniqName(editPanel.networkDataModel.refVariableNameList, "");
					ElementFactory add = nParts.getCopy();

					add.dispName = add.dispName.replaceAll(ElementFactory.CONST_REG__META_NEW_VARIABLE , newVariableName);
					add.rpn = add.rpn.replaceAll(ElementFactory.CONST_REG__META_NEW_VARIABLE,  newVariableName);
					Debug.println("expand rpn: " + add.rpn);
					expandedParts.add(add);
				}
				else {
					expandedParts.add(nParts);
				}
			}
			else {
				expandedParts.add(nPartsOrg);
			}
			
		}

		return expandedParts;
	}

	/**
	 * 編集パネルに新しいエレメントを追加する
	 *
	 * @param editPanel
	 * @param x
	 * @param y
	 */
	public void createNewElement(EditDiagramPanel editPanel, int x, int y) {
		ArrayList<P020___AbstractElement> eleList = editPanel.networkDataModel.getElements();

		if (this.defType.equals(PartsType.TYPE_RPNDEF)) {
			if (this.kindName.equals("制御")) {
				if (this.controlType.equals("IF")) {
					P030____ControlElement controlBlock = new P030____ControlElement(this.editPanel);
					String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");

					controlBlock.id = name;
					controlBlock.x = x;
					controlBlock.y = y;
					controlBlock.w = this.width;
					controlBlock.h = this.height;
					controlBlock.setKindString(this.kindName);
					controlBlock.controlType = this.controlType;
					controlBlock.setRpnString(P020___AbstractElement.unescape(this.rpn));
					// element.buildParameterList(element.getRpnString());

					eleList.add(controlBlock);


					P030____ControlElement controlBlock2 = new P030____ControlElement(this.editPanel);
					String name2 = editPanel.networkDataModel.generateUniqueName("THEN" + "0");

					controlBlock2.id = name2;
					controlBlock2.x = x + controlBlock.w;
					controlBlock2.y = y;
					controlBlock2.w = this.width;
					controlBlock2.h = this.height;
					controlBlock2.setKindString(this.kindName);
					controlBlock2.controlType = "THEN";
					controlBlock2.setRpnString(P020___AbstractElement.unescape(this.rpn));
					// element2.buildParameterList(element.getRpnString());

					eleList.add(controlBlock2);

					HashSet<P030____ControlElement> controllerGroup = new HashSet<P030____ControlElement>();
					controllerGroup.add(controlBlock);
					controllerGroup.add(controlBlock2);

					controlBlock.controllerGroup = controllerGroup;
					controlBlock2.controllerGroup = controllerGroup;
				}
				else {
					String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
					P030____ControlElement controlBlock = new P030____ControlElement(this.editPanel);

					controlBlock.id = name;
					controlBlock.x = x;
					controlBlock.y = y;
					controlBlock.w = this.width;
					controlBlock.h = this.height;
					controlBlock.setKindString(this.kindName);
					controlBlock.controlType = this.controlType;
					controlBlock.setRpnString(P020___AbstractElement.unescape(this.rpn));
					P022_____RpnGraphNodeElement.buildParameterList2(controlBlock, controlBlock.getRpnString());

					eleList.add(controlBlock);
				}
			}
			else {
				String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
				P022_____RpnGraphNodeElement element = new P022_____RpnGraphNodeElement(this.editPanel);

				element.id = name;
				element.x = x;
				element.y = y;
				element.w = this.width;
				element.h = this.height;
				element.setKindString(this.kindName);
				element.setValueType(this.valueType);
				element.setDescription(this.description);
				// 演算式
				element.setRpnString(P020___AbstractElement.unescape(this.rpn));
				P022_____RpnGraphNodeElement.buildParameterList2(element, element.getRpnString());

				eleList.add(element);
			}
		}
		else {
			String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
			FunctionDefInterface f;
			try {
				f = FunctionUtil.getFunctionDef(this.fncName);
				P023_____FncGraphNodeElement element = new P023_____FncGraphNodeElement(this.editPanel, this.fncName, f);

				element.id = name;
				element.x = x;
				element.y = y;
				element.w = this.width;
				element.h = this.height;
				element.setKindString(this.kindName);
				eleList.add(element);
			} catch (LangSpecException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	/**
	 * メニュー要素を生成する。
	 * 生成はオブジェクトを複製することにより行う。
	 *
	 * @return
	 */
	public ElementFactory getCopy() {
		ElementFactory parts = new ElementFactory(editPanel, this.defType);
		parts.description = this.description;
		parts.kindName = this.kindName;
		parts.valueType = this.valueType;
		parts.controlType = this.controlType;
		parts.dispName = this.dispName;
		parts.rpn = this.rpn;
		parts.width = this.width;
		parts.height = this.height;

		return parts;
	}

	static final String reg2str(String s) {
		return s.replaceAll("\\\\", "");
	}

	/*
	public ElementGenerator(EditPanel editPanel, FunctionDefInterface f) {
		this.editPanel = editPanel;
	}
	*/
}

