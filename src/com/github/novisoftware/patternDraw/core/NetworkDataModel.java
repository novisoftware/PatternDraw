package com.github.novisoftware.patternDraw.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.github.novisoftware.patternDraw.core.control.ControllBase;
import com.github.novisoftware.patternDraw.core.exception.BreakSignal;
import com.github.novisoftware.patternDraw.core.exception.CaliculateException;
import com.github.novisoftware.patternDraw.core.exception.ContinueSignal;
import com.github.novisoftware.patternDraw.core.exception.EvaluateException;
import com.github.novisoftware.patternDraw.core.exception.LangSpecException;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value;
import com.github.novisoftware.patternDraw.core.langSpec.typeSystem.Value.ValueType;
import com.github.novisoftware.patternDraw.geometricLanguage.parameter.ParameterDefine;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P021____AbstractGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P022_____RpnGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P023_____FncGraphNodeElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P030____ControlElement;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement.KindId;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramPanel;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputGraphicsWindow;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.OutputTextWindow;
import com.github.novisoftware.patternDraw.utils.Debug;
import com.github.novisoftware.patternDraw.utils.FileWriteUtil;
import com.github.novisoftware.patternDraw.utils.OtherUtil;

public class NetworkDataModel {
	/**
	 * 変数
	 */
	final public HashMap<String, Value> variables;

	/**
	 * 型の妥当性検査のための変数型を記録するハッシュ表
	 */
	final public HashMap<String, ValueType> workCheckTypeVariables;

	/**
	 * ダイアログによるスクリプトのタイトル。
	 * 設定ファイルに入っている。
	 * ウィンドウタイトルの文字列の一部分として使用する。
	 */
	public String title = "";

	/**
	 * 保存先のファイル名
	 */
	private String filename;

	/**
	 * エレメントのリスト
	 */
	private ArrayList<P020___AbstractElement> elements = new ArrayList<P020___AbstractElement>();

	/**
	 * 編集用パネル(JPanel)
	 */
	public final EditDiagramPanel editPanel;

	/**
	 * パラメーター定義のリスト。
	 */
	public final ArrayList<ParameterDefine> paramDefList;

	/**
	 *  参照可能な - 変数名の一覧(設定された変数のみを対象にする)
	 */
	public ArrayList<String> refVariableNameList;

	/**
	 *  上書き可能な - 変数名の一覧(設定された変数のみを対象にする)
	 */
	public ArrayList<String> overWriteVariableNameList;

	/**
	 *  実行順情報: 制御用エレメントの配下のエレメント
	 */
	public HashMap<P030____ControlElement, ArrayList<P020___AbstractElement>> control_contains;

	/**
	 * 実行順情報: ルート要素。 GUI上では「地べたに置かれた要素」が該当する。
	 * (コントロールの中に入っていないもの)
	 */
	ArrayList<P020___AbstractElement> rootElement;
	// ルート要素以外も含む。
	// 型チェック用。
	ArrayList<P020___AbstractElement> allElement;

	// elementに表示用の「単連結グループID」(連番)をつける
	public HashMap<Integer,ArrayList<P021____AbstractGraphNodeElement>> graphGroup;
	public HashMap<P021____AbstractGraphNodeElement, P021____AbstractGraphNodeElement> node2groupHead;
	public HashMap<P030____ControlElement, ArrayList<P020___AbstractElement>> controlled_head;
	public HashMap<P030____ControlElement, ArrayList<P020___AbstractElement>> controlled_all;

	public NetworkDataModel(EditDiagramPanel editPanel, String filename) {
		this.editPanel = editPanel;
		this.filename = filename;
		this.paramDefList = new ArrayList<ParameterDefine>();
		// this.paramVariables = new HashMap<String, Value>();
		this.variables = new HashMap<String, Value>();
		this.workCheckTypeVariables = new HashMap<String, ValueType>();
	}

	public ArrayList<P020___AbstractElement> getElements() {
		return elements;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void debugVariables() {
		for (String s : variables.keySet()) {
			Debug.println("variables", s + " -> " + variables.get(s) );
		}
	}

	/**
	 * プログラムの実行時に、既存の値をクリアしてパラメーターを変数初期値として設定する。
	 * 
	 * @param initVariables
	 */
	public void resetVariables(HashMap<String, Value> initVariables) {
		this.variables.clear();
		this.variables.putAll(initVariables);
	}

	/**
	 * abc1, abc2, abc3 ... のような連番で、ユニークな名前を生成します。
	 *
	 * @param inputName 連番元(例: abc3)
	 * @return 生成した連番(例: abc4)
	 */
	public String generateUniqueName(String inputName) {
		HashSet<String> nameSet = new HashSet<>();
		for (P020___AbstractElement to :  elements) {
			nameSet.add(to.id);
		}
		return OtherUtil.generateUniqueName(inputName, nameSet);
	}

	/**
	 * 位置関係の比較
	 *
	 */
	class posComparator implements java.util.Comparator<P020___AbstractElement> {
		@Override
		public int compare(P020___AbstractElement o1, P020___AbstractElement o2) {
			// 上から下の順にソートする
			if (o1.y < o2.y) {
				return -1;
			}
			if (o1.y > o2.y) {
				return 1;
			}

			// 基本的には全く同一の高さを想定しないが、一応、同一の高さの場合の横方向の比較を行う。
			if (o1.x < o2.x) {
				return -1;
			}
			if (o1.x > o2.x) {
				return 1;
			}

			/*
			// 位置だけで見て 0 を返してはいけない。オブジェクトが同一視される
			// ★ TODO
			// 以下でいいのかどうか。 return 0 にする場面が必要かどうか検討。
			return 0;
			*/

			if (o1.hashCode() < o2.hashCode()) {
				return -1;
			}

			return 1;
		}
	}

	
	/**
	 * Control 配下で見れるようになる変数(および変数の型)の一覧を取得する
	 * (analyze を行った後に呼び出し可能)
	 * 
	 * @param ele
	 * @param control_contains
	 * @return
	 */
	public HashMap<String, ValueType> checkVariableType(
			P021____AbstractGraphNodeElement ele) {
		HashMap<String, ValueType> variableTypeList = new HashMap<String, ValueType>();
		
		P021____AbstractGraphNodeElement head = this.node2groupHead.get(ele);
		
		
		for (P030____ControlElement c : control_contains.keySet()) {
			if (control_contains.get(c).contains(head)) {
				variableTypeList.putAll(c.getVariableNamesAndTypes());
			}
		}
		
		return variableTypeList;
	}

	/**
	 * 変数名やパラメタ名が変更になったことを通知するインタフェース
	 * 
	 * @param before 変更前
	 * @param after 変更後
	 */
	public void notifyVarNameChange(String before, String after) {
		for (P020___AbstractElement e : elements) {
			e.notifyVarNameChange(before, after);
		}
	}

	public static class MyDim {
		public final int x;
		public final int y;

		MyDim(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static final int X_DIM_INIT = 1500;
	public static final int Y_DIM_INIT = 2000;
	
	/**
	 * 描画上に必要な長方形領域を計算する。
	 * 
	 * @return
	 */
	public MyDim getRenderDim() {
		int X_MARGIN = 50;
		int Y_MARGIN = 800;
		int X_MIN = X_DIM_INIT - X_MARGIN;
		int Y_MIN = Y_DIM_INIT - Y_MARGIN;

		int workX = X_MIN;
		int workY = Y_MIN;
		
		for (P020___AbstractElement e : elements) {
			int x = e.x + e.w;
			int y = e.y + e.h;
			if (workX < x) {
				workX = x;
			}
			if (workY < y) {
				workY = y;
			}
		}
		
		return new MyDim(workX + X_MARGIN, workY + Y_MARGIN);
	}

	/**
	 * 実行順をきめる等を行う。
	 *
	 */
	public void analyze() {
		// 上から下の順にソートした Element
		TreeSet<P020___AbstractElement> positionSortedElements = new TreeSet<>(new posComparator());
		for (P020___AbstractElement e : elements) {
			if (! e.isComment()) {
				positionSortedElements.add(e);
			}
		}

		// 変数名の一覧(設定された変数のみを対象にする)
		// 参照可能な
		refVariableNameList = new ArrayList<>();
		// 上書き可能な
		overWriteVariableNameList = new ArrayList<>();
	
		// パラメーター一覧から、変数名の一覧に追加する
		for (ParameterDefine d : paramDefList) {
			refVariableNameList.add(d.name);
			overWriteVariableNameList.add(d.name);
		}

		
		// 変数名の一覧を作成する
		// 変数の参照、上書きメニューの作成に使用する
		for (P020___AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof P022_____RpnGraphNodeElement) {
				if (((P022_____RpnGraphNodeElement)elementIcon).getKindId().equals(KindId.VARIABLE_SET)) {
				// if (((RpnGraphNodeElement)elementIcon).getKindString().equals("変数を設定")) {
					String rep = ((P022_____RpnGraphNodeElement)elementIcon).getRepresentExpression();
					if (! refVariableNameList.contains(rep)) {
						refVariableNameList.add(rep);
					}
					if (! overWriteVariableNameList.contains(rep)) {
						overWriteVariableNameList.add(rep);
					}
				}
			}
			// TODO
			// 厳密には、対応する Control の中だけで見せたい。
			if (elementIcon instanceof P030____ControlElement) {
				Set<String> reps = ((P030____ControlElement)elementIcon).getVariableNamesAndTypes().keySet();
				for (String rep : reps) {
					if (! refVariableNameList.contains(rep)) {
						refVariableNameList.add(rep);
					}
				}
			}
		}

		for (String varName: refVariableNameList) {
			Debug.println("Evaluate", "var name: " + varName);
		}

		//////////////////////////////////////////////////////////
		// グラフ構造を解析して、評価順をきめる

		// 幅優先探索での追加順
		ArrayList<P021____AbstractGraphNodeElement> addOrder = new ArrayList<>();

		// 他の要素を参照していない要素
		// 「計算済の値」の集合の初期値
		HashMap<P021____AbstractGraphNodeElement,Integer> addSet0 = new HashMap<>();
		int workCounter = 0;
		for (P020___AbstractElement elementIcon : positionSortedElements) {
			if (elementIcon instanceof P021____AbstractGraphNodeElement) {
				P021____AbstractGraphNodeElement referFrom = ((P021____AbstractGraphNodeElement)elementIcon);
				if (referFrom.paramMapObj.size() == 0) {
					// 単連結のグラフを区別したいので、異なる番号を振っておく
					addSet0.put(referFrom, workCounter);
					workCounter++;

					// 付随的に使用するデータ構造の組み立て(追加順)
					addOrder.add(referFrom);
				}
			}
		}

		// 単連結グラフに分解

		// 幅優先探索で、計算済の値の段階数を増やしていく
		ArrayList<Set<P021____AbstractGraphNodeElement>> addSet = new ArrayList<>();
		addSet.add(addSet0.keySet());

		HashMap<P021____AbstractGraphNodeElement,Integer> foundSet = new HashMap<>();
		foundSet.putAll(addSet0);

		// グループの合流。数字をマージする
		HashMap<Integer,Integer> mergeInfo_old = new HashMap<>();
		ArrayList<Integer> mergeFrom = new ArrayList<Integer>();
		ArrayList<Integer> mergeTo = new ArrayList<Integer>();

		while (true) {
			HashMap<P021____AbstractGraphNodeElement,Integer> nextAddSet = new HashMap<>();

			for (P020___AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof P021____AbstractGraphNodeElement) {
					P021____AbstractGraphNodeElement element = ((P021____AbstractGraphNodeElement)elementIcon);
					// 入力を持つノードを対象に処理をする
					if (foundSet.containsKey(element)) {
						continue;
					}
					boolean isOk = true;
					int num = -1;
					for (P020___AbstractElement e_ : element.paramMapObj.values()) {
						if (e_ instanceof P021____AbstractGraphNodeElement) {
							// 入力ノード
							P021____AbstractGraphNodeElement e = (P021____AbstractGraphNodeElement)e_;

							if (!foundSet.containsKey(e)) {
								// 入力ノードが、発見済みの集合に含まれていない場合は後回し
								isOk = false;
								break;
							}
							else {
								// 入力ノードが、発見済みの集合に含まれている場合のみ
								int m = foundSet.get(e);
								if (num == -1) {
									// TODO これは常にNOPになるはず
									num = m;
								}
								if (m < num) {
									mergeFrom.add(num);
									mergeTo.add(m);

									if (! mergeInfo_old.containsKey(num) || mergeInfo_old.get(num) > m) {
										mergeInfo_old.put(num, m);
										Debug.println("evaluate.mergeInfo", "" + num + " -> " + m);
									}
									num = m;
								}
								else if (num < m) {
									mergeFrom.add(m);
									mergeTo.add(num);

									mergeInfo_old.put(m, num);
									Debug.println("evaluate.mergeInfo", "" + m + " -> " + num);
								}
							}
						} else {
							isOk = false;
							break;
						}
					}
					if (isOk) {
						nextAddSet.put(element, num);

						// 付随的に使用するデータ構造の組み立て(追加順)
						addOrder.add(element);
					}
				}
			}

			if (nextAddSet.isEmpty()) {
				break;
			}
			foundSet.putAll(nextAddSet);
		}

		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ 作業状態");
		HashMap<P021____AbstractGraphNodeElement,Integer> groupIdBuild = new HashMap<>();
		// デバッグ確認用
		/*
		for (AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = foundSet.get(element);
			if ( mergeInfo_old.containsKey(groupIdWork)) {
				groupIdWork = mergeInfo_old.get(groupIdWork);
			}
			groupIdBuild.put(element,groupIdWork);
			Debug.println("evaluate",  "ID: " + element.id + "  groupId(raw):" + foundSet.get(element) + "  groupId:" + groupIdWork);
		}
		*/

		// 番号の付け替え
		for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = foundSet.get(element);
			groupIdBuild.put(element,groupIdWork);
		}
		int n = mergeFrom.size();
		for (int i = 0; i < n; i++) {
			int from = mergeFrom.get(i);
			int to = mergeTo.get(i);

			for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
				int t = groupIdBuild.get(element);
				if (t == from) {
					groupIdBuild.put(element, to);
				}
			}
		}
		// デバッグ確認用
		for (P021____AbstractGraphNodeElement element: foundSet.keySet()) {
			int groupIdWork = groupIdBuild.get(element);
			Debug.println("evaluate",  "ID: " + element.id + "  groupId(raw):" + foundSet.get(element) + "  groupId:" + groupIdWork);
		}


		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ(連番整列前)");
		// デバッグ確認用
		for (int i = 0;  i < addSet0.size(); i++) {
			if (!groupIdBuild.values().contains(i)) {
				continue;
			}
			Debug.println("evaluate", "------------------------------ id: " + i);

			for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
				if (groupIdBuild.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		/////////////////////////////////////////////
		// 単連結グループIDの番号を詰める
		TreeSet<Integer> groupIdSet = new TreeSet<>();
		for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupIdSet.add(groupIdBuild.get(element));
		}

		HashMap<Integer,Integer> groupIdTrim = new HashMap<>();
		int inc = 1;
		for (int oldId : groupIdSet) {
			groupIdTrim.put(oldId, inc);
			inc++;
		}

		// 切り詰めた番号を割り振る
		HashMap<P021____AbstractGraphNodeElement,Integer> groupId2 = new HashMap<>();
		for (P021____AbstractGraphNodeElement element: groupIdBuild.keySet()) {
			groupId2.put(element, groupIdTrim.get(groupIdBuild.get(element)));
		}

		// 詰めた番号のセット
		TreeSet<Integer> groupIdSet2 = new TreeSet<>();
		groupIdSet2.addAll(groupId2.values());

		// デバッグ確認用
		Debug.println("evaluate", "--------------------------------------------- 単連結グラフ(連番整列)");
		for (int i : groupIdSet2) {
			Debug.println("evaluate", "------------------------------ id: " + i);

			for (P021____AbstractGraphNodeElement element: addOrder) {
				if (groupId2.get(element).equals(i)) {
					Debug.println("evaluate",  "ID: " + element.id + "  groupId:" + i);
				}
			}
		}

		// elementに表示用の「単連結グループID」をつける
		graphGroup = new HashMap<>();
		for (P021____AbstractGraphNodeElement element : addOrder) {
			int id = groupId2.get(element);
			if (!graphGroup.containsKey(id)) {
				element.groupHead = id;
				ArrayList<P021____AbstractGraphNodeElement> list = new ArrayList<>();
				list.add(element);
				graphGroup.put(id, list);
			}
			else {
				element.groupHead = null;
				graphGroup.get(id).add(element);
			}
		}

		// 調べやすいように、メンバーオブジェクトからHeadオブジェクトへのマップを作成する
		node2groupHead = new HashMap<>();
		for (int groupId : graphGroup.keySet()) {
			P021____AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);
			node2groupHead.put(headElement, headElement);
			for (P021____AbstractGraphNodeElement members : graphGroup.get(groupId)) {
				node2groupHead.put(members, headElement);
			}
		}

		Debug.println("evaluate", "-");
		Debug.println("evaluate", "-");
		Debug.println("evaluate", "-");

		HashMap<P020___AbstractElement,P030____ControlElement> elementToControl = new HashMap<>();

		// controlオブジェクトと、制御対象になるオブジェクトの一式
		controlled_head = new HashMap<>();

		// group と control の関係をスキャンする
		//
		for (int groupId : graphGroup.keySet()) {
			P021____AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);

			for (P020___AbstractElement elementIcon : positionSortedElements) {
				if (elementIcon instanceof P030____ControlElement) {
					P030____ControlElement control = ((P030____ControlElement)elementIcon);

					// TODO
					// 複数のcontrolに含まれる場合がある
					if (control.includes(headElement)) {
						elementToControl.put(headElement, control);

						if (! controlled_head.containsKey(control)) {
							ArrayList<P020___AbstractElement> list = new ArrayList<>();
							list.add(headElement);
							controlled_head.put(control, list);
						}
						else {
							controlled_head.get(control).add(headElement);
						}
					}
				}
			}

			// TODO
			// Control の中に Control が入れ子になっている場合に
			// それを拾い上げてデータ構造を組み立てる
		}

		// 実行順(計算順)を作成

		// 単連結グラフ先頭ノード
		HashSet<P021____AbstractGraphNodeElement> headElementSet = new HashSet<P021____AbstractGraphNodeElement>();
		for (int groupId = 1; groupId <= graphGroup.size() ; groupId++) {
			P021____AbstractGraphNodeElement headElement = graphGroup.get(groupId).get(0);
			headElementSet.add(headElement);
		}

		// ControlとControlの包含関係を作る。
		// Control と Control の関係をスキャンする
		HashMap<P030____ControlElement,ArrayList<P030____ControlElement>> controlContains = new HashMap<P030____ControlElement,ArrayList<P030____ControlElement>>();

		HashSet<P020___AbstractElement> nonRoot = new HashSet<P020___AbstractElement>();

		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof P030____ControlElement)) {
				continue;
			}
			P030____ControlElement c0 = ((P030____ControlElement)ei0);
			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (ei0 == ei1) {
					continue;
				}
				/*
				if (nonRoot.contains(ei1)) {
					continue;
				}
				if (! headElementSet.contains(ei1)) {
					continue;
				}
				*/
				if (!(ei1 instanceof P030____ControlElement)) {
					continue;
				}
				P030____ControlElement c1 = (P030____ControlElement)ei1;

				if (c0.includes(c1)) {
					// 包含関係表現用オブジェクトに追加
					ArrayList<P030____ControlElement> a = controlContains.get(c0);
					if (a == null) {
						a = new ArrayList<P030____ControlElement>();
						controlContains.put(c0, a);
					}
					a.add(c1);
					nonRoot.add(c1);
				}
			}
		}

		if (Debug.enable) {
			// elementがどのcontrolにぶら下がるかを調べる:
			// デバッグ用情報のみ事前出力 0
			for (P020___AbstractElement ei1 : positionSortedElements) {
	//			ControlBlock c1 = (ControlBlock)ei1;

				if (ei1 != null) {
					Debug.println("evaluete", "pre0: " + ei1.id);
					Debug.println("evaluete", "pre0: " + ei1.str());
				}
			}



			// elementがどのcontrolにぶら下がるかを調べる:
			// デバッグ用情報のみ事前出力 0
			for (P020___AbstractElement ei1 : this.getElements()) {
	//			ControlBlock c1 = (ControlBlock)ei1;

				if (ei1 != null) {
					Debug.println("evaluete", "pre1: " + ei1.id);
					Debug.println("evaluete", "pre1: " + ei1.str());
				}
			}


			// デバッグ用情報のみ事前出力 1
			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (!(ei1 instanceof P030____ControlElement)) {
					continue;
				}
				P030____ControlElement c1 = (P030____ControlElement)ei1;

				Debug.println("evaluete", "pre: contol block --- " + c1.id);
			}
		}

		// elementがどのcontrolにぶら下がるかを調べる
		HashMap<P020___AbstractElement, P030____ControlElement> controlElementIn = new HashMap<P020___AbstractElement,P030____ControlElement>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!( headElementSet.contains(ei0) || ei0 instanceof P030____ControlElement)) {
				continue;
			}

			Debug.println("evaluate", "包含関係のチェック対象: " + ei0.getDebugIdString());

			for (P020___AbstractElement ei1 : positionSortedElements) {
				if (ei0 == ei1) {
					continue;
				}
				if (!(ei1 instanceof P030____ControlElement)) {
					continue;
				}
				P030____ControlElement c1 = (P030____ControlElement)ei1;

				if (c1.includes(ei0)) {
					P030____ControlElement c = controlElementIn.get(ei0);
					if (c == null) {
						controlElementIn.put(ei0, c1);
						Debug.println("evaluate", "包含関係 " + ei0.getDebugIdString() + " is in " + c1.getDebugIdString());
					}
					else {
						if (c.includes(c1)) {
							controlElementIn.put(ei0, c1);

							Debug.println("evaluate", "包含関係 " + ei0.getDebugIdString() + " is in " + c1.getDebugIdString());
						}
						else if (c1.includes(c)) {
							// 敢えて実行する必要がない
							/*
							controlElementIn.put(ei0, c);
							*/
						}
						else {
							//
							Debug.println("evaluate", "交差するcontrol。 invalid。");
						}
					}
				}
			}
		}

		// 実行順構造

		// 作業用のリスト。このリストの内容を、実行順構造に追加していく。
		ArrayList<P020___AbstractElement> workList = new ArrayList<P020___AbstractElement>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (headElementSet.contains(ei0)) {
				workList.add(ei0);
			}
			else if (ei0 instanceof P030____ControlElement) {
				workList.add(ei0);
			}
		}

		// 実行順情報: 地べたに置かれた要素
		this.rootElement = new ArrayList<P020___AbstractElement>();
		// 地べた以外
		this.allElement = new ArrayList<P020___AbstractElement>();
		for (P020___AbstractElement ei0 : workList) {
			if (controlElementIn.get(ei0) == null) {
				rootElement.add(ei0);
			}
			allElement.add(ei0);
		}

		// 実行順情報: 制御用エレメントの配下のエレメント
		this.control_contains = new HashMap<P030____ControlElement, ArrayList<P020___AbstractElement>>();
		for (P020___AbstractElement ei0 : positionSortedElements) {
			if (!(ei0 instanceof P030____ControlElement)) {
				continue;
			}
			P030____ControlElement c0 = (P030____ControlElement)ei0;

			for (P020___AbstractElement ei1 : positionSortedElements) {
				if ( controlElementIn.get(ei1) == c0) {
					ArrayList<P020___AbstractElement> a = control_contains.get(c0);
					if (a == null) {
						a = new ArrayList<P020___AbstractElement>();
						control_contains.put(c0, a);
					}
					a.add(ei1);
				}
			}
		}

		// 延長でconnector の型チェックを呼び出す。
		this.typeCheck();
	}

	Value evaluateOneGraph(P021____AbstractGraphNodeElement headElement) throws EvaluateException {
		ArrayList<P021____AbstractGraphNodeElement> eList = graphGroup.get(headElement.groupHead);
		Debug.println("evaluate", "GRPAH GROUP ---  " + headElement.groupHead + "   items: " + eList.size());
		for(P021____AbstractGraphNodeElement element : eList) {
			// Debug.println("evaluate", "begin: " + element.id);
			element.evaluate();
			// Debug.println("evaluate", "end: " + element.id);
		}

		P021____AbstractGraphNodeElement e = eList.get(eList.size() - 1);
		return e.workValue;
	}

	void evaluateControl(P030____ControlElement control) throws CaliculateException, InterruptedException {
		Debug.println("evaluate", "------------------------ CONTROL"  + "  " + control.id);
		if (control.getControlType().equals("REPEAT")) {
			ControllBase c = control.init();
			Debug.println("evaluate", "------------------------ REPEAT"  + "  " + c.getDebugString());
			while( c.hasNext() ) {
				// boolean getContinueSignal = false;
				
				try {
					ArrayList<P020___AbstractElement> eList = control_contains.get(control);
					if (eList != null) {
						for(P020___AbstractElement e0 : eList) {
							if (e0 instanceof P022_____RpnGraphNodeElement) {
								P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
								evaluateOneGraph(element);
							}
							else if (e0 instanceof P030____ControlElement) {
								evaluateControl((P030____ControlElement)e0);
							}
						}
					}
				} catch (EvaluateException e) {
					if (e instanceof CaliculateException) {
						throw (CaliculateException)e;
					}
					if (e instanceof BreakSignal) {
						return;
					}
					if (e instanceof ContinueSignal) {
						// getContinueSignal = true;
					}
				}

				if (this.hasStopRequest) {
					return;
				}
				Thread.sleep(0);

				c.nextState();
			}
		}
		else if(control.getControlType().equals("IF")) {
			Value lastValue = null;
			{
				ArrayList<P020___AbstractElement> eList = control_contains.get(control);
				if (eList != null) {
					for(P020___AbstractElement e0 : eList) {
						if (e0 instanceof P022_____RpnGraphNodeElement) {
							P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
							try {
								lastValue = evaluateOneGraph(element);
							} catch (EvaluateException e) {
								if (e instanceof CaliculateException) {
									throw (CaliculateException)e;
								} else {
									// 何もしない
									// そもそも if 節の中で break等を想定していない
								}
							}
						}
						else if (e0 instanceof P030____ControlElement) {
							Debug.println("RUN", "INVALID STRUCTURE. 妥当でない構造。");
						}
	
						if (this.hasStopRequest) {
							return;
						}
						Thread.sleep(0);
					}
				}
			}
			Debug.println("RUN", "last value is " + lastValue + (lastValue != null ? ("  " + lastValue.toDebugString()):""));


			P030____ControlElement thenBlock = null;
			P030____ControlElement elseBlock = null;
			for(P030____ControlElement c : control.controllerGroup) {
				if (c.getControlType().equals("THEN")) {
					thenBlock = c;
				}
				else if (c.getControlType().equals("ELSE")) {
					elseBlock = c;
				}
			}

			if (thenBlock != null) {
				if (lastValue != null && lastValue.valueType == Value.ValueType.BOOLEAN  && lastValue.sameTo(true)) {
					Debug.println("RUN", "THEN節");
					ArrayList<P020___AbstractElement> eList = control_contains.get(thenBlock);
					if (eList != null) {
						for(P020___AbstractElement e0 : eList) {
							if (e0 instanceof P022_____RpnGraphNodeElement) {
								P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
								try {
									evaluateOneGraph(element);
								} catch (EvaluateException e) {
									if (e instanceof CaliculateException) {
										throw (CaliculateException)e;
									} else {
										// 何もしない
										// そもそも then/else 節の中で break等を想定していない
									}
								}
							}
							else if (e0 instanceof P030____ControlElement) {
								evaluateControl((P030____ControlElement)e0);
							}
							if (this.hasStopRequest) {
								return;
							}
							Thread.sleep(0);
						}
					}
				}
			}
			if (elseBlock != null) {
				if (lastValue != null && lastValue.valueType == Value.ValueType.BOOLEAN  && lastValue.sameTo(false)) {
					Debug.println("RUN", "ELSE節");

					ArrayList<P020___AbstractElement> eList = control_contains.get(elseBlock);
					if (eList != null) {
						for(P020___AbstractElement e0 : eList) {
							if (e0 instanceof P022_____RpnGraphNodeElement) {
								P022_____RpnGraphNodeElement element = ((P022_____RpnGraphNodeElement) e0);
								try {
									evaluateOneGraph(element);
								} catch (EvaluateException e) {
									if (e instanceof CaliculateException) {
										throw (CaliculateException)e;
									} else {
										// 何もしない
										// そもそも then/else 節の中で break等を想定していない
									}
								}
							}
							else if (e0 instanceof P030____ControlElement) {
								evaluateControl((P030____ControlElement)e0);
							}
							
							if (this.hasStopRequest) {
								return;
							}
							Thread.sleep(0);
						}
					}
				}
			}
		}

	}

	static Object semaphore = new Object();
	
	OutputTextInterface outputTextInterface;

	private Thread currentRunning = null;
	private boolean hasStopRequest = false;
	
	public boolean hasStopRequest() {
		return hasStopRequest;
	}

	/**
	 * 排他制御付きの isRunning フラグのセット
	 * 
	 * @return フラグをセットできた場合 true
	 */
	public boolean setRunning(Thread t) {
		synchronized (semaphore) {
			if (currentRunning != null) {
				return false;
			}
			currentRunning = t;
			this.hasStopRequest = false;
		}
		return true;
	}

	public void unsetRunning() {
		synchronized (semaphore) {
			currentRunning = null;
		}
	}
	public void requestStop() {
		this.hasStopRequest = true;
		synchronized (semaphore) {
			if (currentRunning != null) {
				currentRunning.interrupt();
			}
		}
	}
	
	public void waitRunning() {
		Thread t = currentRunning;
		if (t != null) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// ここには interrupt しない
			}
		}
	}

	/**
	 * 厳密な排他制御が必要ない場面でのプログラム実行中かどうかの状態取得
	 * @return
	 */
	public boolean isRunning() {
		if (currentRunning != null) {
			return true;
		}

		return false;
	}
	
	public void runProgram(
			HashMap<String, Value> hashMap, Runnable callback, boolean isJoin) {
		boolean isRun = false;
		try {
			System.out.println("runProgram ... starting");
			if (!isJoin) {
				// すでに実行中の場合に、実行しないモード：
				if (setRunning(Thread.currentThread())) {
					isRun = true;
				} else {
					return;
				}
			} else {
				// すでに実行中の場合に、実行が終わるまで待つモード：
				while (true) {
					if (setRunning(Thread.currentThread())) {
						isRun = true;
						break;
					} else {
						waitRunning();
					}
				}
			}

			System.out.println("runProgram ... started.");

			// 更新用のパラメーターが指定されていたら、パラメーターの更新を行う。
			if (hashMap != null) {
				this.resetVariables(hashMap);
			}

			outputTextInterface = OutputTextWindow.getInstance();
			outputTextInterface.clear();
			OutputGraphicsWindow.reset();
			Debug.println("evaluate", "control_contains: " + control_contains.keySet().size());

			try {
				//////////////////////////////////////////////////////////
				// ここから実行
				Debug.println("evaluate", "--------------------------------------------- 「実行」★");
				for (P020___AbstractElement elementIcon : this.rootElement) {
					if (elementIcon instanceof P021____AbstractGraphNodeElement) {
						P021____AbstractGraphNodeElement ele = ((P021____AbstractGraphNodeElement)elementIcon);
						try {
							evaluateOneGraph(ele);
						} catch (EvaluateException e) {

							if (e instanceof CaliculateException) {
								throw (CaliculateException)e;
							} else {
								if (e instanceof BreakSignal) {
									P022_____RpnGraphNodeElement causeNode = ((BreakSignal)e).causeNode;
									
									causeNode.isError = true;
									causeNode.errorMessage = "想定していない場所での break です。";
									throw new CaliculateException(ele.errorMessage);
								}
								else if (e instanceof ContinueSignal) {
									P022_____RpnGraphNodeElement causeNode = ((ContinueSignal)e).causeNode;

									causeNode.isError = true;
									causeNode.errorMessage = "想定していない場所での continue です。";
									throw new CaliculateException(ele.errorMessage);
								}
								// 何もしない
								// そもそも、ここで break等を想定していない
								// TODO.
								// breakに対してエラーメッセージを表示すべきかも。
							}
						}
						/*
						ArrayList<GraphNodeElement> eList = graphGroup.get(((GraphNodeElement)elementIcon).groupHead);
						for(GraphNodeElement element : eList) {
							element.evaluate();
	
							Debug.println("evaluate", "done: " + element.id);
						}
						*/
					}
					else if(elementIcon instanceof P030____ControlElement) {
						evaluateControl((P030____ControlElement)elementIcon);
					}
					Thread.sleep(0);
				}
			} catch (CaliculateException e) {
				// 特に処理不要
			} catch (InterruptedException e) {
				// 特に処理不要
			}
	
			OutputGraphicsWindow.refresh();
		} finally {
			if (isRun) {
				try {
					// callback の呼び出しは、
					// 実行中に計算エラーの例外が発生したり、実行を中断したとしても行う。
					// ただし、 callback の呼び出し中、さらに例外が発生しても
					// unsetRunning() は実行する。
					// (さらに次が呼び出せなくなるため)
					callback.run();
				} catch (Exception e) {
					// 処理不要
				}
				this.unsetRunning();
			}
		}
	}


	void typeCheckForOneGraph(P021____AbstractGraphNodeElement headElement) {
		Debug.println("typeCheckForOneGraph called");

		ArrayList<P021____AbstractGraphNodeElement> eList = graphGroup.get(headElement.groupHead);
		for(P021____AbstractGraphNodeElement element : eList) {
			element.typeCheck();
		}
	}

	/**
	 * 各ノードへの入力の妥当性を検査する。
	 * (各ノードの入力型がおかしいかどうかを見ておきたい)
	 *
	 * おかしかったかどうか。
	 * 型は何になるか。
	 * を見る。
	 *
	 * 実際に計算しても良いが、
	 * 実際の計算だとゼロ除算等の実行時のエラーが発生しうるので、型の検査のみする。
	 * @throws CaliculateException 
	 *
	 */
	public void typeCheck() {
		Debug.println("静的な型チェック");

		for (P020___AbstractElement elementIcon : this.allElement) {
			if (elementIcon instanceof P021____AbstractGraphNodeElement) {
				typeCheckForOneGraph((P021____AbstractGraphNodeElement)elementIcon);
			}
			else if(elementIcon instanceof P030____ControlElement) {
				// evaluateControl((P010___ControlElement)elementIcon);
			}
		}
	}

	static final String FORMAT_STR = "PD_FORMAT_REV: ";
	static final String FORMAT_REV = "1.1";

	private static void checkFileRevision(String firstLine) throws LangSpecException {
		boolean formatSettingCheckError = false;
		if (firstLine == null) {
			// 空のファイルは素通しする
			return;
			// throw new LangSpecException("空のファイルです。");
		}
		else if (! firstLine.startsWith(FORMAT_STR)) {
			throw new LangSpecException("フォーマット文字列が含まれていません。");
		} else {
			String revStr = firstLine.substring(FORMAT_STR.length());
			double verInFile = 0;
			double nowVer = Double.parseDouble(FORMAT_REV);
			try {
				verInFile = Double.parseDouble(revStr);
				if (verInFile < nowVer) {
					throw new LangSpecException("ファイル側のフォーマット版数の方が新しいです。");
				}
			} catch (Exception e) {
				// リビジョン識別の parseDouble に失敗した場合
				formatSettingCheckError = true;
				if (Debug.enable) {
					e.printStackTrace();
				}
			}
		}
		if (formatSettingCheckError) {
			throw new LangSpecException("ファイルフォーマットのエラー。");
		}
	}

	public static void checkFile(String filename) throws LangSpecException, IOException {
		// ファイルから読み込む
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(
							new InputStreamReader(
							new FileInputStream(new File(filename)), FileWriteUtil.UTF8));
			String line = reader.readLine();
			NetworkDataModel.checkFileRevision(line);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public void load() throws LangSpecException, IOException {
		// ファイルから読み込む
		BufferedReader reader;
		try {
			reader = new BufferedReader(
							new InputStreamReader(
							new FileInputStream(new File(filename)), FileWriteUtil.UTF8));
			ArrayList<String> refInfo = new ArrayList<>();
			HashMap<String, P030____ControlElement> controllerMap = new HashMap<String, P030____ControlElement>();

			try {
				boolean isFirstLine = true;
				while( true ) {
					String line = reader.readLine();
					if (isFirstLine) {
						isFirstLine = false;
						try {
							this.checkFileRevision(line);
						} catch(Exception e) {
							// しばらくはリビジョンチェックを捨てる。
						}
						continue;
					}
					if( line==null ) {
						break;
					}
					if (line.length() == 0 || line.startsWith("#")) {
						continue;
					}
					if (line.startsWith("TITLE:")) {
						this.title = line.substring("TITLE:".length());
					} else if (line.startsWith("PARAMETER:")) {
						this.paramDefList.add(ParameterDefine.getParameterDefineToEdit(line));
					} else if (line.startsWith("RPN_ELEMENT:")) {
						this.getElements().add(new P022_____RpnGraphNodeElement(this.editPanel, line));
					} else if (line.startsWith("FNC_ELEMENT:")) {
						this.getElements().add(new P023_____FncGraphNodeElement(this.editPanel, line));
					} else if (line.startsWith("CONTROL:")) {
						P030____ControlElement c = new P030____ControlElement(this.editPanel, line);
						this.getElements().add(c);
	
						controllerMap.put(c.id, c);
					} else if (line.startsWith("REF:")) {
						refInfo.add(line);
					} else if (line.startsWith("CONTROL_GROUP:")) {
						HashSet<P030____ControlElement> controllerGroup = new HashSet<P030____ControlElement>();
						for (String id : line.substring("CONTROL_GROUP:".length()).split(" ")) {
							if (id.equals("")) {
								continue;
							}
							// String t_ = t.replaceAll(" ", "");
							P030____ControlElement c = controllerMap.get(id);
							Debug.println("LOAD", "Control_Group   id='" + id + "'  obj=" + c);
							controllerGroup.add(c);
						}
						for (P030____ControlElement c : controllerGroup) {
							Debug.println("LOAD", "c = " + (c));
	
							c.controllerGroup = controllerGroup;
						}
					} else {
						System.err.println("UNKNOWN LINE: " + line);
					}
				}
			} finally {
				reader.close();
			}

			// System.out.println("make s2t. getElements size = " + getElements().size());
			HashMap<String, P020___AbstractElement> s2t = new HashMap<>();
			for( P020___AbstractElement t : getElements()) {
				s2t.put(t.id, t);
				//System.out.println("  name = " + t.id);
			}
			// System.out.println();
			for(String line : refInfo) {
				String[] a = line.split(" ");
				String name = a[1];
				String parameterName = a[2];
				String targetName = a[3];
				// 参照しているオブジェクト
				P020___AbstractElement referFrom = s2t.get(name);
				// 参照されているオブジェクト
				P020___AbstractElement referTo = s2t.get(targetName);
				// System.out.println("LINE = " + line);
				// System.out.println("  name = " + name);
				// System.out.println("  paramMapInfo = " + t.paramMapInfo);
				referFrom.paramMapInfo.put(parameterName, targetName);
				if (referTo instanceof P021____AbstractGraphNodeElement) {
					referFrom.paramMapObj.put(parameterName, (P021____AbstractGraphNodeElement)referTo);
				}
				else {
					throw new LangSpecException(LangSpecException.WRONG_EXPORT_FILE);
				}
			}
		} catch (FileNotFoundException e) {
			// System.err.println("新規にファイルを作成します。" + filename);
		}
		/*
		catch (IOException e) {
			// System.err.println("途中でエラーが発生しました。処理を継続します。" + e.toString());
		} catch (LangSpecException e) {
			// System.err.println("途中でエラーが発生しました。該当ノードは作成せず、処理を継続します。" + e.toString());
		}
		*/
	}

	public void save() throws IOException {
		// ファイルに書き込む
		BufferedWriter writer;
		writer = new BufferedWriter(new FileWriter( new File(filename) ));
		writer.write(FORMAT_STR + FORMAT_REV + "\n");
		writer.write("");
		writer.write("TITLE:" + this.title + "\n");
		writer.write("\n");
		for (ParameterDefine p : paramDefList) {
			writer.write( p.str() + '\n' );
		}
		writer.write("\n");
		for (P020___AbstractElement n : getElements()) {
			writer.write( n.str() + '\n' );
		}
		for (P020___AbstractElement n : getElements()) {
			for (String s : n.optStr()) {
				writer.write( s + '\n' );
			}
		}
		for (P020___AbstractElement n : getElements()) {
			if (n instanceof P030____ControlElement) {
				String s = ((P030____ControlElement)n).contollerGroup_str();
				if (s != null) {
					writer.write( s + '\n' );
				}
			}
		}

		writer.close();
	}
}
