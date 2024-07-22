package com.github.novisoftware.patternDraw.gui.editor.guiMain;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

public class EditDiagramDropTargetListener implements DropTargetListener {
	final EditDiagramPanel editDiagramPanel;
	
	EditDiagramDropTargetListener(EditDiagramPanel editDiagramPanel) {
		this.editDiagramPanel = editDiagramPanel;
	}
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent e) {
		e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable tr = e.getTransferable();

		boolean gotData = false;
		try {
			if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				List<File> files = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
				if (files.size() > 1) {
					return;
				}
				for (File file : files) {
					if (editDiagramPanel.checkAndShowDialogIsValidFile(file)) {
						// ドラッグドロップ操作があった時点で、
						// 関連ウィンドウは閉じる
						// (読み込めないものがドラッグされた場合以外)
						OutputTextWindow outputTextWindow = OutputTextWindow.getInstance();
						OutputGraphicsWindow outputGraphicsWindow = OutputGraphicsWindow.getInstance();
						outputTextWindow.setVisible(false);
						outputGraphicsWindow.setVisible(false);
						editDiagramPanel.editDiagramWindow.editParamWindow.setVisible(false);
						editDiagramPanel.editDiagramWindow.editParamWindow.clearVariables();

						// editParamWindow.setVisible(true);
						
						if (!editDiagramPanel.editDiagramWindow.dataLostConfirm()) {
							return;
						}

						editDiagramPanel.loadFile(file);
						editDiagramPanel.editDiagramWindow.updateTitle();
						editDiagramPanel.editDiagramWindow.editMenuBar.setEnableOverWriteMenuItem(true);
					}
					/*
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (!editDiagramPanel.editDiagramWindow.dataLostConfirm()) {
								return;
							}
							editDiagramPanel.loadFile(file);
						}
					});
					*/
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			e.dropComplete(gotData);
		}
	}

}
