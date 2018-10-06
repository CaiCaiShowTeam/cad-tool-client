package com.bplead.cad.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.SimpleFolder;
import com.bplead.cad.bean.SimplePdmLinkProduct;
import com.bplead.cad.bean.client.Preference;
import com.bplead.cad.bean.io.Container;
import com.bplead.cad.util.ClientUtils;

import priv.lee.cad.model.Callback;
import priv.lee.cad.ui.AbstractPanel;
import priv.lee.cad.ui.Option;
import priv.lee.cad.ui.OptionPanel;
import priv.lee.cad.ui.PromptTextField;
import priv.lee.cad.util.ClientAssert;

public class ContainerPanel extends AbstractPanel {

	private static final long serialVersionUID = 1442969218942586007L;
	private final String BUTTON_ICON = "folder.search.icon";
	private final String EMPTY_FOLDER = "folder.empty.prompt";
	private final String EMPTY_PDMLINKPRODUCT = "pdm.empty.prompt";
	private final String FOLDER_PROMPT = "folder.prompt";
	private final String FOLDER_TITLE = "folder.title";
	private final Logger logger = Logger.getLogger(ContainerPanel.class);
	private final String PDM_PROMPT = "pdm.prompt";
	private final String PDM_TITLE = "pdm.title";
	public PDMLinkProductPanel pdmlinkProductPanel;
	private Preference preference = ClientUtils.temprary.getPreference();
	public SubFolderPanel subFolderPanel;

	private Container getDefaultContainer() {
		return preference == null ? null : preference.getContainer();
	}

	@Override
	public double getHorizontalProportion() {
		return 1d;
	}

	@Override
	public double getVerticalProportion() {
		return 0.1d;
	}

	@Override
	public void initialize() {
		Container container = getDefaultContainer();

		logger.info("initialize " + PDMLinkProductPanel.class + "...");
		pdmlinkProductPanel = new PDMLinkProductPanel(container == null ? null : container.getProduct());
		add(pdmlinkProductPanel);

		logger.info("initialize " + SubFolderPanel.class + "...");
		subFolderPanel = new SubFolderPanel(container == null ? null : container.getFolder());
		add(subFolderPanel);
	}

	class PDMLinkProductPanel extends SimpleButtonSetPanel<SimplePdmLinkProduct> {

		private static final long serialVersionUID = 5788762488066451045L;
		private SimplePdmLinkProduct product;

		public PDMLinkProductPanel(SimplePdmLinkProduct product) {
			super(product);
			this.product = product;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new PdmLinkProductChooseDialog(this).activate();
		}

		@Override
		public void call(Object object) {
			ClientAssert.notNull(object, "Callback object is required");
			ClientAssert.isInstanceOf(SimplePdmLinkProduct.class, object,
					"Callback object must be a SimplePdmLinkProduct type");

			product = (SimplePdmLinkProduct) object;

			refresh(product.getName());
		}

		public SimplePdmLinkProduct getProduct() {
			return product;
		}

		@Override
		protected String setButtonText() {
			return null;
		}

		@Override
		protected String setPrompt() {
			return getResourceMap().getString(PDM_PROMPT);
		}

		@Override
		protected String setText(SimplePdmLinkProduct product) {
			if (product == null) {
				return getResourceMap().getString(EMPTY_PDMLINKPRODUCT);
			}
			return product.getName();
		}

		@Override
		protected String setTitle() {
			return getResourceMap().getString(PDM_TITLE);
		}
	}

	abstract class SimpleButtonSetPanel<T> extends AbstractPanel implements ActionListener, Callback {

		private static final long serialVersionUID = -5690721799689305895L;
		private final double BUTTON_PROPORTION = 0.3d;
		private final double HEIGHT_PROPORTION = 0.3d;
		private final double LABEL_PROPORTION = 0.15d;
		private T object;
		public PromptTextField text;
		private final double TEXT_PROPORTION = 0.65d;

		public SimpleButtonSetPanel(T object) {
			this.object = object;
		}

		private Dimension getButtonPrerredSize() {
			BigDecimal width = new BigDecimal(getPreferredSize().height).multiply(new BigDecimal(BUTTON_PROPORTION));
			return new Dimension(width.intValue(), width.intValue());
		}

		@Override
		public double getHorizontalProportion() {
			return 0.47d;
		}

		@Override
		public double getVerticalProportion() {
			return 0.99d;
		}

		@Override
		public void initialize() {
			logger.info("modify " + getClass() + " to flow layout...");
			setLayout(new FlowLayout(FlowLayout.LEFT));

			logger.info("initialize " + getClass() + "  content...");
			// ~ initialize content
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), setTitle(),
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

			PromptTextField.PromptTextFieldDimension dimension = PromptTextField.newDimension(getPreferredSize(),
					LABEL_PROPORTION, TEXT_PROPORTION, HEIGHT_PROPORTION);
			text = PromptTextField.newInstance(setPrompt(), setText(object), dimension);
			add(text);

			add(new OptionPanel(Arrays.asList(new Option(null, BUTTON_ICON, this, getButtonPrerredSize()))));
		}

		protected void refresh(String text) {
			this.text.getText().setText(text);

			validate();
		}

		protected abstract String setButtonText();

		protected abstract String setPrompt();

		protected abstract String setText(T object);

		protected abstract String setTitle();
	}

	class SubFolderPanel extends SimpleButtonSetPanel<SimpleFolder> {

		private static final long serialVersionUID = 5788762488066451045L;
		private SimpleFolder folder;

		public SubFolderPanel(SimpleFolder folder) {
			super(folder);
			this.folder = folder;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new FolderChooseDialog(this, pdmlinkProductPanel.product).activate();
		}

		@Override
		public void call(Object object) {
			ClientAssert.notNull(object, "Callback object is required");

			Object[] nodes = (Object[]) object;
			folder = ((FolderTree.FolderNode) nodes[nodes.length - 1]).getFolder();

			refresh(folder.getName());
		}

		public SimpleFolder getFolder() {
			return folder;
		}

		@Override
		protected String setButtonText() {
			return null;
		}

		@Override
		protected String setPrompt() {
			return getResourceMap().getString(FOLDER_PROMPT);
		}

		@Override
		protected String setText(SimpleFolder folder) {
			if (folder == null) {
				return getResourceMap().getString(EMPTY_FOLDER);
			}
			return folder.getName();
		}

		@Override
		protected String setTitle() {
			return getResourceMap().getString(FOLDER_TITLE);
		}
	}
}
