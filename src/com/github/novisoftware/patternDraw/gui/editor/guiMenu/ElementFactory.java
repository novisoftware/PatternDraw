package com.github.novisoftware.patternDraw.gui.editor.guiMenu;

import java.util.ArrayList;
import java.util.HashSet;

import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.FncGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ConnectTerminal;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.GuiUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.core.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.functions.FunctionDefInterface;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.LangSpecException;
import com.github.novisoftware.patternDraw.geometricLanguage.lang.functions.FunctionUtil;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.gui.editor.guiMenu.ElementFactory;

public class ElementFactory {
	final protected EditDiagramPanel editPanel;

	public ElementFactory(EditDiagramPanel editPanel, PartsType defType) {
		this.editPanel = editPanel;
		this.defType = defType;
	}

	static final String reg2str(String s) {
		return s.replaceAll("\\\\", "");
	}

	static final String CONST_REG__META_EXIST_VARIABLE = "\\{exist-variable\\}";
	static final String CONST_STR__META_EXIST_VARIABLE = reg2str(CONST_REG__META_EXIST_VARIABLE);

	static final String CONST_REG__META_NEW_VARIABLE = "\\{new-variable\\}";
	static final String CONST_STR__META_NEW_VARIABLE = reg2str(CONST_REG__META_NEW_VARIABLE);

	/**
	// メニュー要素を展開する
	// (定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する)
	 *
	 * @param editPanel editPanelオブジェクト
	 * @param partsList 展開前のパーツリスト
	 * @return 展開後のパーツリスト
	 */
	public static ArrayList<ElementFactory> partsListExtend(EditDiagramPanel editPanel, ArrayList<ElementFactory> partsList) {
		// 定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する。
		ArrayList<ElementFactory> expandedParts = new ArrayList<>();
		for (final ElementFactory nParts : partsList) {
			if (nParts.dispName.indexOf(ElementFactory.CONST_STR__META_EXIST_VARIABLE) != -1) {
				Debug.println("match " + ElementFactory.CONST_STR__META_EXIST_VARIABLE);

				ArrayList<String> variableNames = new ArrayList<String>();
				variableNames.addAll(editPanel.networkDataModel.variableNameList);

				for (ParameterDefine param : editPanel.networkDataModel.paramDefList) {
					variableNames.add(param.name);
				}

				for (String varName : variableNames) {
					ElementFactory add = nParts.getCopy();

					add.dispName = add.dispName.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE, varName);
					add.rpn = add.rpn.replaceAll(ElementFactory.CONST_REG__META_EXIST_VARIABLE,  varName);
//					Debug.println("expand rpn: " + add.rpn);
					expandedParts.add(add);
				}
			}
			else if (nParts.dispName.indexOf(ElementFactory.CONST_STR__META_NEW_VARIABLE) != -1) {
				Debug.println("match " + ElementFactory.CONST_STR__META_NEW_VARIABLE);

				String newVariableName = GuiUtil.generateUniqName(editPanel.networkDataModel.variableNameList, "");
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

		return expandedParts;
	}

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

	/**
	 * 編集パネルに新しいエレメントを追加する
	 *
	 * @param editPanel
	 * @param x
	 * @param y
	 */
	public void createNewElement(EditDiagramPanel editPanel, int x, int y) {
		ArrayList<AbstractElement> eleList = editPanel.networkDataModel.getElements();

		if (this.defType.equals(PartsType.TYPE_RPNDEF)) {
			if (this.kindName.equals("制御")) {
				if (this.controlType.equals("IF")) {
					ControlElement controlBlock = new ControlElement(this.editPanel);
					String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");

					controlBlock.id = name;
					controlBlock.x = x;
					controlBlock.y = y;
					controlBlock.w = this.width;
					controlBlock.h = this.height;
					controlBlock.setKindString(this.kindName);
					controlBlock.controlType = this.controlType;
					controlBlock.setRpnString(AbstractElement.unescape(this.rpn));
					// element.buildParameterList(element.getRpnString());

					eleList.add(controlBlock);


					ControlElement controlBlock2 = new ControlElement(this.editPanel);
					String name2 = editPanel.networkDataModel.generateUniqueName("THEN" + "0");

					controlBlock2.id = name2;
					controlBlock2.x = x + controlBlock.w;
					controlBlock2.y = y;
					controlBlock2.w = this.width;
					controlBlock2.h = this.height;
					controlBlock2.setKindString(this.kindName);
					controlBlock2.controlType = "THEN";
					controlBlock2.setRpnString(AbstractElement.unescape(this.rpn));
					// element2.buildParameterList(element.getRpnString());

					eleList.add(controlBlock2);

					HashSet<ControlElement> controllerGroup = new HashSet<ControlElement>();
					controllerGroup.add(controlBlock);
					controllerGroup.add(controlBlock2);

					controlBlock.controllerGroup = controllerGroup;
					controlBlock2.controllerGroup = controllerGroup;
				}
				else {
					String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
					ControlElement controlBlock = new ControlElement(this.editPanel);

					controlBlock.id = name;
					controlBlock.x = x;
					controlBlock.y = y;
					controlBlock.w = this.width;
					controlBlock.h = this.height;
					controlBlock.setKindString(this.kindName);
					controlBlock.controlType = this.controlType;
					controlBlock.setRpnString(AbstractElement.unescape(this.rpn));
					// element.buildParameterList(element.getRpnString());

					eleList.add(controlBlock);
				}
			}
			else {
				String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
				RpnGraphNodeElement element = new RpnGraphNodeElement(this.editPanel);

				element.id = name;
				element.x = x;
				element.y = y;
				element.w = this.width;
				element.h = this.height;
				element.setKindString(this.kindName);
				element.setValueType(this.valueType);
				// 演算式
				element.setRpnString(AbstractElement.unescape(this.rpn));
				element.buildParameterList(element.getRpnString());

				eleList.add(element);
			}
		}
		else {
			String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
			FunctionDefInterface f;
			try {
				f = FunctionUtil.getFunctionDef(this.fncName);
				FncGraphNodeElement element = new FncGraphNodeElement(this.editPanel, this.fncName, f);

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

	/*
	public ElementGenerator(EditPanel editPanel, FunctionDefInterface f) {
		this.editPanel = editPanel;
	}
	*/
}

