package com.bplead.cad.ui;

import java.awt.Dimension;
import java.util.Arrays;

import javax.swing.JScrollPane;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.SimplePdmLinkProduct;

import priv.lee.cad.model.Callback;
import priv.lee.cad.ui.AbstractDialog;
import priv.lee.cad.ui.Option;
import priv.lee.cad.ui.OptionPanel;
import priv.lee.cad.util.ClientAssert;

public class FolderChooseDialog extends AbstractDialog {

	private static final Logger logger = Logger.getLogger(FolderChooseDialog.class);
	private static final long serialVersionUID = 1802062156291539070L;
	private FolderTree folderTree;
	private SimplePdmLinkProduct product;
	private double TABLE_HEIGTH_PROPORTION = 0.8d;
	private double TABLE_WIDTH_PROPORTION = 0.98d;

	public FolderChooseDialog(Callback callback, SimplePdmLinkProduct product) {
		super(FolderChooseDialog.class, callback);
		ClientAssert.notNull(product, "SimplePdmLinkProduct is requried");

		this.product = product;
	}

	@Override
	public double getHorizontalProportion() {
		return 0.2d;
	}

	@Override
	public double getVerticalProportion() {
		return 0.5d;
	}

	@Override
	public void initialize() {
		logger.info("initialize " + FolderTree.class + " content...");
		folderTree = new FolderTree(product);
		JScrollPane sp = new JScrollPane(folderTree);
		sp.setPreferredSize(new Dimension((int) (getPreferredSize().width * TABLE_WIDTH_PROPORTION),
				(int) (getPreferredSize().height * TABLE_HEIGTH_PROPORTION)));
		add(sp);

		logger.info("initialize " + Option.class + " content...");
		Option confirm = new Option(Option.CONFIRM_BUTTON, null, this);
		add(new OptionPanel(Arrays.asList(confirm, Option.newCancelOption(this))));

		logger.info("initialize completed...");
	}

	@Override
	public Object setCallbackObject() {
		return folderTree.getSelectionPath().getPath();
	}
}
