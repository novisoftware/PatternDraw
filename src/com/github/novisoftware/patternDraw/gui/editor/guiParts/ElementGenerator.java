package com.github.novisoftware.patternDraw.gui.editor.guiParts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.github.novisoftware.patternDraw.gui.editor.util.RpnUtil;
import com.github.novisoftware.patternDraw.gui.editor.util.Rpn;
import com.github.novisoftware.patternDraw.gui.editor.typeSystem.Value;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ControlBlock;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ElementGenerator;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ElementIcon;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphConnector;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.GraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiParts.ElementIcon.KindId;
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
	 * 重複しない名前を生成する(やっつけ仕事)。
	 *
	 * @param set
	 * @param base
	 * @return
	 */
	static String generateUniqName(List<String> set, String base) {
		for (char c = 'a' ; c < 'z' ; c ++) {
			if (! set.contains(base + c)) {
				return base + c;
			}
		}

		return null;
	}

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

				String newVariableName = generateUniqName(editPanel.networkDataModel.nameOfvaliables, "");
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
	public String typeName;

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


	/*
	public ElementIcon getNewElement(String name, int x, int y) {
		if (this.kindName.equals("制御")) {
			Controller element = new Controller(this.editPanel);

			element.id = name;
			element.x = x;
			element.y = y;
			element.w = this.width;
			element.h = this.height;
			element.setKindString(this.kindName);
			element.type = this.typeName;
			element.setRpnString(ElementIcon.unescape(this.rpn));
			element.buildParameterList(element.getRpnString());

			return element;
		}
		else {
			GraphNodeElement element = new GraphNodeElement(this.editPanel);

			element.id = name;
			element.x = x;
			element.y = y;
			element.w = this.width;
			element.h = this.height;
			element.setKindString(this.kindName);
			element.type = this.typeName;
			element.setRpnString(ElementIcon.unescape(this.rpn));
			element.buildParameterList(element.getRpnString());

			return element;
		}
	}
	*/

	public void createNewElement(EditPanel editPanel, int x, int y) {
		ArrayList<ElementIcon> eleList = editPanel.networkDataModel.getElements();

		if (this.kindName.equals("制御")) {
			if (this.typeName.equals("IF")) {
				ControlBlock element = new ControlBlock(this.editPanel);
				String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");

				element.id = name;
				element.x = x;
				element.y = y;
				element.w = this.width;
				element.h = this.height;
				element.setKindString(this.kindName);
				element.type = this.typeName;
				element.setRpnString(ElementIcon.unescape(this.rpn));
				// element.buildParameterList(element.getRpnString());

				eleList.add(element);


				ControlBlock element2 = new ControlBlock(this.editPanel);
				String name2 = editPanel.networkDataModel.generateUniqueName("THEN" + "0");

				element2.id = name2;
				element2.x = x + element.w;
				element2.y = y;
				element2.w = this.width;
				element2.h = this.height;
				element2.setKindString(this.kindName);
				element2.type = "THEN";
				element2.setRpnString(ElementIcon.unescape(this.rpn));
				// element2.buildParameterList(element.getRpnString());

				eleList.add(element2);

				HashSet<ControlBlock> controllerGroup = new HashSet<ControlBlock>();
				controllerGroup.add(element);
				controllerGroup.add(element2);

				element.controllerGroup = controllerGroup;
				element2.controllerGroup = controllerGroup;
			}
			else {
				String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
				ControlBlock element = new ControlBlock(this.editPanel);

				element.id = name;
				element.x = x;
				element.y = y;
				element.w = this.width;
				element.h = this.height;
				element.setKindString(this.kindName);
				element.type = this.typeName;
				element.setRpnString(ElementIcon.unescape(this.rpn));
				// element.buildParameterList(element.getRpnString());

				eleList.add(element);
			}
		}
		else {
			String name = editPanel.networkDataModel.generateUniqueName(this.kindName + "0");
			GraphNodeElement element = new GraphNodeElement(this.editPanel);

			element.id = name;
			element.x = x;
			element.y = y;
			element.w = this.width;
			element.h = this.height;
			element.setKindString(this.kindName);
			element.type = this.typeName;
			element.setRpnString(ElementIcon.unescape(this.rpn));
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
		parts.typeName = this.typeName;
		parts.dispName = this.dispName;
		parts.rpn = this.rpn;
		parts.width = this.width;
		parts.height = this.height;

		return parts;
	}

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
					parts.typeName = workTypeName;
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

