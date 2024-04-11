package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.util.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.functions.FunctionDef;
import com.github.novisoftware.patternDraw.gui.editor.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditFrame.MListener;
import com.github.novisoftware.patternDraw.gui.editor.util.Common;
import com.github.novisoftware.patternDraw.gui.editor.util.Debug;

public class ElementGenerator {
	final protected EditPanel editPanel;

	ElementGenerator(EditPanel editPanel) {
		this.editPanel = editPanel;
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
	public static ArrayList<ElementGenerator> partsListExtend(EditPanel editPanel, ArrayList<ElementGenerator> partsList) {
		// 定義ファイルに記載された要素の中で、特殊表記があった場合、要素を複数に展開する。
		ArrayList<ElementGenerator> expandedParts = new ArrayList<>();
		for (final ElementGenerator nParts : partsList) {
			if (nParts.dispName.indexOf(ElementGenerator.CONST_STR__META_EXIST_VARIABLE) != -1) {
				Debug.println("match " + ElementGenerator.CONST_STR__META_EXIST_VARIABLE);

				for (String varName : editPanel.networkDataModel.nameOfvaliables) {
					ElementGenerator add = nParts.getCopy();

					add.dispName = add.dispName.replaceAll(ElementGenerator.CONST_REG__META_EXIST_VARIABLE, varName);
					add.rpn = add.rpn.replaceAll(ElementGenerator.CONST_REG__META_EXIST_VARIABLE,  varName);
//					Debug.println("expand rpn: " + add.rpn);
					expandedParts.add(add);
				}
			}
			else if (nParts.dispName.indexOf(ElementGenerator.CONST_STR__META_NEW_VARIABLE) != -1) {
				Debug.println("match " + ElementGenerator.CONST_STR__META_NEW_VARIABLE);

				String newVariableName = Common.generateUniqName(editPanel.networkDataModel.nameOfvaliables, "");
				ElementGenerator add = nParts.getCopy();

				add.dispName = add.dispName.replaceAll(ElementGenerator.CONST_REG__META_NEW_VARIABLE , newVariableName);
				add.rpn = add.rpn.replaceAll(ElementGenerator.CONST_REG__META_NEW_VARIABLE,  newVariableName);
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
	 * 種類
	 */
	public String outputType;

	/**
	 * 式
	 */
	public String rpn;

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
	public void createNewElement(EditPanel editPanel, int x, int y) {
		ArrayList<AbstractElement> eleList = editPanel.networkDataModel.getElements();

		if (this.kindName.equals("制御")) {
			if (this.outputType.equals("IF")) {
				ControlElement controlBlock = new ControlElement(this.editPanel);
				String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");

				controlBlock.id = name;
				controlBlock.x = x;
				controlBlock.y = y;
				controlBlock.w = this.width;
				controlBlock.h = this.height;
				controlBlock.setKindString(this.kindName);
				controlBlock.outputType = this.outputType;
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
				controlBlock2.outputType = "THEN";
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
				controlBlock.outputType = this.outputType;
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
			element.outputType = this.outputType;
			// 演算式
			element.setRpnString(AbstractElement.unescape(this.rpn));
			element.buildParameterList(element.getRpnString());

			eleList.add(element);
		}
	}

	/**
	 * メニュー要素を生成する。
	 * 生成はオブジェクトを複製することにより行う。
	 *
	 * @return
	 */
	public ElementGenerator getCopy() {
		ElementGenerator parts = new ElementGenerator(editPanel);
		parts.description = this.description;
		parts.kindName = this.kindName;
		parts.outputType = this.outputType;
		parts.dispName = this.dispName;
		parts.rpn = this.rpn;
		parts.width = this.width;
		parts.height = this.height;

		return parts;
	}

	public ElementGenerator(EditPanel editPanel, FunctionDef f) {
		this.editPanel = editPanel;



	}

	/**
	 * テキストファイルの記載内容から ElementGenerator のリストを作成する。
	 *
	 * @param editPanel
	 * @param filename
	 * @return
	 */
	public static ArrayList<ElementGenerator> loadElementPartsList(EditPanel editPanel, String filename) {
		ArrayList<ElementGenerator> list = new ArrayList<>();

		// ファイルから読み込む
		BufferedReader reader;
		ArrayList<String> workList = new ArrayList<>();

		String workDescription = "";
		String workKindName = "";
		String workTypeName = "";
		String workDispName = "";
		String workRPN = "";
		int workWidth = 120;
		int workHeight= 60;

		try {
			reader = new BufferedReader(new FileReader( new File(filename) ));
			while( true ) {
				String line = reader.readLine();
				if( line==null ) {
					break;
				}
				line = Common.trim(line);

				if (line.startsWith("#")) {
					continue;
				}

				if (line.startsWith("DESCRIPTION:")) {
					workDescription = Common.trim( line.substring("DESCRIPTION:".length()));
				}
				else if (line.startsWith("KIND:")) {
					workKindName = Common.trim( line.substring("KIND:".length()));
				}
				else if (line.startsWith("TYPE:")) {
					workTypeName = Common.trim( line.substring("TYPE:".length()));
				}
				else if (line.startsWith("DISPNAME:")) {
					workDispName = Common.trim(line.substring("DISPNAME:".length()));
				}
				else if (line.startsWith("WIDTH:")) {
					workWidth = Integer.parseInt(Common.trim( line.substring("WIDTH:".length())));
				}
				else if (line.startsWith("HEIGHT:")) {
					workHeight = Integer.parseInt(Common.trim( line.substring("HEIGHT:".length())));
				}
				else if (line.startsWith("-->RPN")) {
					workRPN = Common.concatStrings(workList);
					workList = new ArrayList<>();
				}
				else if (line.startsWith("---->DEFINE")) {
					ElementGenerator parts = new ElementGenerator(editPanel);
					parts.description = workDescription;
					parts.kindName = workKindName;
					parts.outputType = workTypeName;
					parts.dispName = workDispName;
					parts.rpn = workRPN;
					parts.width = workWidth;
					parts.height = workHeight;

					list.add(parts);

					workWidth = 120;
					workHeight= 60;
				}
				else {
					String s = Common.trim(line);
					if (! s.equals("")) {
						workList.add(Common.trim(line));
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.err.println("途中でエラーが発生しました。" + e.toString());
		}

		return list;
	}
}

