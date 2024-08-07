package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.github.novisoftware.patternDraw.core.NetworkDataModel;
import com.github.novisoftware.patternDraw.core.NetworkDataModel.MyDim;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P001_IconGuiInterface;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P015__AbstractIcon2;
import com.github.novisoftware.patternDraw.gui.editor.guiDiagramParts.P020___AbstractElement;
import com.github.novisoftware.patternDraw.gui.editor.guiMain.EditDiagramWindow.MListener;
import com.github.novisoftware.patternDraw.utils.Debug;

public class EditDiagramPanel extends JPanel {
	/**
	 * 計算をするためのデータ構造。
	 */
	public NetworkDataModel networkDataModel;
	/**
	 * JPanel上にデバッグ情報を表示する
	 */
	public boolean isVisibleDebugInfo = false;

	/**
	 * サブウィンドウ(パラメーター定義の編集画面)
	 */
	public EditParamDefListWindow paramDefEditWindow = null;

	final public EditDiagramWindow editDiagramWindow;
	
	private Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 20);

	// マウスドラッグ中に関係線を変更する場合、表示する
	P001_IconGuiInterface workLineFrom = null;
	int workLineX, workLineY;

	final MListener listener;

	EditDiagramPanel(EditDiagramWindow editDiagramWindow, String filename) {
		this.editDiagramWindow = editDiagramWindow;
		this.networkDataModel = new NetworkDataModel(this, null);
		this.networkDataModel.analyze();
		this.listener = new MListener(this);
		this.addMouseListener(listener);
		this.addMouseMotionListener(listener);

		// 注:
		// new DropTarget(コンポーネント, リスナー);を実行すると、
		// DropTargetの内部でaddDropTargetListenerされる。
		// (だから、コンストラクタのみで良い)
		new DropTarget(this, new EditDiagramDropTargetListener(this));

		if (filename != null) {
			this.loadFile(new File(filename));
		}
	}

	/**
	 * @return ドラッグ対象オブジェクト
	 */
	public P001_IconGuiInterface getHandledObject() {
		return this.listener.handled;
	}

	/**
	 * 読み込み失敗時のエラーダイアログ
	 * 
	 * @param e
	 */
	public void showFileReadErrorDialog(Exception e) {
		if (Debug.enable) {
			e.printStackTrace();	// Debug.enable の場合のみ
		}

		String subMessage = e.getMessage();
		if (e instanceof IOException) {
			subMessage = "入出力エラー。 " + subMessage;
		}
		String message = String.format("ファイルの読み込みに失敗しました。\n%s",
				subMessage);
		JOptionPane
				.showMessageDialog(
						editDiagramWindow,
						message,
						"Error",
						JOptionPane.ERROR_MESSAGE);
	}
	
	public void loadFile(File file) {
		NetworkDataModel oldModel = this.networkDataModel;
		
		try {
			NetworkDataModel newModel = new NetworkDataModel(this, file.getAbsolutePath());
			this.networkDataModel = newModel;
			this.networkDataModel.load();
			this.networkDataModel.analyze();
			this.repaint();
		} catch(Exception e) {
			this.networkDataModel = oldModel;
			showFileReadErrorDialog(e);
		}
	}

	public boolean checkAndShowDialogIsValidFile(File file) {
		try {
			NetworkDataModel.checkFile(file.getAbsolutePath());
		} catch(Exception e) {
			// 変なファイルなのでダイアログを見せる必要もない？
			// (DnDでダイアログを出すとしたらこのタイミングだけ)
			showFileReadErrorDialog(e);
			return false;
		}
		return true;
	}

	public P015__AbstractIcon2 getElementIcon(String name) {
		for (P015__AbstractIcon2 t : networkDataModel.getElements()) {
			if (t.id.equals(name)) {
				return t;
			}
		}
		return null;
	}

	public Set<String> getToneNames() {
		TreeSet<String> s = new TreeSet<>();

		for (P015__AbstractIcon2 t : networkDataModel.getElements()) {
			s.add(t.id);
		}

		return s;
	}

	public void repaint() {
		if (this.networkDataModel != null) {
			MyDim dim = this.networkDataModel.getRenderDim();
			Dimension d = new Dimension(dim.x, dim.y);
			this.setPreferredSize(d);
			this.setSize(d);
		}

		super.repaint();
	}
	
	static Color workLineColor = new Color(0x70, 0x70, 0xFF);

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		g2.setFont(font);

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// ドラッグドロップ中の作業線
		if (workLineFrom != null) {
			// g2.setColor(Color.BLUE);
			g2.setColor(workLineColor);
			g2.drawLine(workLineFrom.getCenterX(), workLineFrom.getCenterY(), workLineX, workLineY);
		}

		/**
		 * 配置されている部品を描画します。
		 * 関係線一式を先に描画し( phase == 0 )、その後、アイコン形状を描画します( phase == 2 )。
		 */
		g2.setColor(Color.GRAY);
		for (int phase = 0; phase < 3; phase++) {
			for (P015__AbstractIcon2 t : networkDataModel.getElements()) {
				t.paintWithPhase(g2, phase);
			}
		}
	}

	P001_IconGuiInterface checkXY(int x, int y) {
		// 注: 描画順と検索順は同じにしているため、見つけたときに break をしない。
		// (breakがあると、重なったときに「下側」のアイコンを拾うことになる)

		P001_IconGuiInterface hit = null;
		for (P015__AbstractIcon2 t : networkDataModel.getElements()) {
			P001_IconGuiInterface check = t.getTouchedObject(this, x, y);
			if (check != null) {
				hit = check;
			}
		}

		if (hit == null) {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		return hit;
	}
}