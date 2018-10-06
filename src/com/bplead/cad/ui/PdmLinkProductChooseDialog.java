package com.bplead.cad.ui;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import priv.lee.cad.model.Callback;
import priv.lee.cad.ui.AbstractDialog;
import priv.lee.cad.ui.AbstractPanel;
import priv.lee.cad.ui.Option;
import priv.lee.cad.ui.OptionPanel;

public class PdmLinkProductChooseDialog extends AbstractDialog implements ActionListener {

	private static final Logger logger = Logger.getLogger(PdmLinkProductChooseDialog.class);
	private static final long serialVersionUID = 39269794179304687L;
	private PdmLinkProductTable table;
	private double TABLE_HEIGTH_PROPORTION = 0.9d;
	private double TABLE_WIDTH_PROPORTION = 0.98d;

	public PdmLinkProductChooseDialog(Callback container) {
		super(PdmLinkProductChooseDialog.class, container);
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
		logger.info("initialize " + PdmLinkProductsPanel.class + " content...");
		add(new PdmLinkProductsPanel());

		logger.info("initialize " + Option.class + " content...");
		Option confirm = new Option(Option.CONFIRM_BUTTON, null, this);
		add(new OptionPanel(Arrays.asList(confirm, Option.newCancelOption(this))));

		logger.info("initialize completed...");
	}

	@Override
	public Object setCallbackObject() {
		return table.getSelectedProduct();
	}

	class PdmLinkProductsPanel extends AbstractPanel {

		private static final long serialVersionUID = -9183060824080475562L;
		private final String TITLE = "title";

		@Override
		public double getHorizontalProportion() {
			return 0.95d;
		}

		@Override
		public double getVerticalProportion() {
			return 0.8d;
		}

		@Override
		public void initialize() {
			// set panel border to be title and etched type
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getResourceMap().getString(TITLE), TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

			table = new PdmLinkProductTable();
			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension((int) (getPreferredSize().width * TABLE_WIDTH_PROPORTION),
					(int) (getPreferredSize().height * TABLE_HEIGTH_PROPORTION)));
			table.setColumnWidth();
			add(sp);
		}
	}
}
