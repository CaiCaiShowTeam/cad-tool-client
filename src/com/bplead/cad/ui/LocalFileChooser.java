package com.bplead.cad.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JLabel;

import priv.lee.cad.ui.PromptTextField;
import priv.lee.cad.util.ObjectUtils;

public class LocalFileChooser extends JFileChooser {

	private static final String CHOOSE = "Choose";
	private static final long serialVersionUID = -4355323193750393407L;

	public LocalFileChooser(int mode, PromptTextField text) {
		setFileSelectionMode(mode);
		showDialog(new JLabel(), CHOOSE);

		File selectedFile = getSelectedFile();
		if (!ObjectUtils.isEmpty(selectedFile)) {
			text.getText().setText(selectedFile.getAbsolutePath());
		}
	}
}
