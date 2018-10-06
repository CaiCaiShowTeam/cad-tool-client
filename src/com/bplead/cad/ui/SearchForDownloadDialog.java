package com.bplead.cad.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.DataContent;
import com.bplead.cad.bean.SimpleDocument;
import com.bplead.cad.model.CustomPrompt;
import com.bplead.cad.util.ClientUtils;
import com.bplead.cad.util.FTPUtils;

import priv.lee.cad.model.Callback;
import priv.lee.cad.ui.AbstractDialog;
import priv.lee.cad.ui.AbstractPanel;
import priv.lee.cad.ui.Option;
import priv.lee.cad.ui.OptionPanel;
import priv.lee.cad.ui.PromptTextField;
import priv.lee.cad.util.ClientAssert;

public class SearchForDownloadDialog extends AbstractDialog implements ActionListener {

	private static LayoutManager layout = new FlowLayout(FlowLayout.LEFT);
	private static final Logger logger = Logger.getLogger(SearchForDownloadDialog.class);
	private static final long serialVersionUID = 1336292047030719519L;
	private DownloadSettingPanel downloadSettingPanel;
	private SearchConditionsPanel searchConditionPanel;
	private SearchResultPanel searchResultPanel;

	public SearchForDownloadDialog(Callback container) {
		super(SearchForDownloadDialog.class, container);
	}

	private void download(File serverFile, File localFile) {
		FTPUtils utils = FTPUtils.newInstance();
		utils.download(serverFile, localFile);
		utils.delete(serverFile);
	}

	@Override
	public double getHorizontalProportion() {
		return 0.5;
	}

	@Override
	public double getVerticalProportion() {
		return 0.5;
	}

	@Override
	public void initialize() {
		logger.info("initialize " + getClass() + " layout...");
		setLayout(layout);

		logger.info("initialize " + getClass() + " content...");
		searchConditionPanel = new SearchConditionsPanel();
		add(searchConditionPanel);

		logger.info("initialize " + getClass() + " content...");
		searchResultPanel = new SearchResultPanel();
		add(searchResultPanel);

		logger.info("initialize " + getClass() + " content...");
		downloadSettingPanel = new DownloadSettingPanel();
		add(downloadSettingPanel);

		logger.info("initialize " + getClass() + "  completed...");
	}

	@Override
	public Object setCallbackObject() {
		String localPath = downloadSettingPanel.setting.getText().getText();
		ClientAssert.hasText(localPath, CustomPrompt.LOCAL_REPOSITORY_NULL);

		List<SimpleDocument> documents = searchResultPanel.table.getSelectedDocuments();
		ClientAssert.notEmpty(documents, CustomPrompt.SELECTED_ITEM_NULL);

		DataContent content = ClientUtils.checkoutAndDownload(documents);
		ClientAssert.notNull(content, CustomPrompt.FAILD_OPTION);

		logger.info("download " + getClass() + "  completed...");
		File localFile = new File(localPath + File.separator + content.getServerFile().getName());
		download(content.getServerFile(), localFile);

		logger.info("unzip file...");
		return ClientUtils.unzip(localFile);
	}

	class DownloadSettingPanel extends AbstractPanel implements ActionListener {

		private static final long serialVersionUID = -6481481565984135229L;
		private final String DOWNLOAD_TO = "downloadTo";
		private final double HEIGHT_PROPORTION = 0.5d;
		private final double LABEL_PROPORTION = 0.1d;
		protected PromptTextField setting;
		private final double TEXT_PROPORTION = 0.5d;

		@Override
		public void actionPerformed(ActionEvent e) {
			new LocalFileChooser(JFileChooser.DIRECTORIES_ONLY, setting);
		}

		@Override
		public double getHorizontalProportion() {
			return 0.95d;
		}

		@Override
		public double getVerticalProportion() {
			return 0.1d;
		}

		@Override
		public void initialize() {
			setLayout(layout);

			PromptTextField.PromptTextFieldDimension dimension = PromptTextField.newDimension(getPreferredSize(),
					LABEL_PROPORTION, TEXT_PROPORTION, HEIGHT_PROPORTION);
			setting = PromptTextField.newInstance((getResourceMap().getString(DOWNLOAD_TO)), null, dimension);
			setting.setLabelAligment(SwingConstants.LEFT);
			add(setting);

			logger.info("initialize browser option...");
			Option browse = new Option(Option.BROWSE_BUTTON, null, this);

			Container parent = getParent();
			while (!(parent instanceof Window)) {
				parent = parent.getParent();
			}
			Option confirm = new Option(Option.CONFIRM_BUTTON, null, (ActionListener) parent);

			add(new OptionPanel(Arrays.asList(browse, confirm, Option.newCancelOption((Window) parent))));
		}
	}

	class SearchConditionsPanel extends AbstractPanel implements ActionListener {

		private static final String NAME = "name";
		private static final String NUMBER = "number";
		private static final String SEARCH = "search";
		private static final long serialVersionUID = 7488199863056895133L;
		private final double HEIGHT_PROPORTION = 0.3d;
		private final double LABEL_PROPORTION = 0.05d;
		public PromptTextField name;
		public PromptTextField number;
		private final double TEXT_PROPORTION = 0.2d;
		private final String TITLE = "title";

		@Override
		public void actionPerformed(ActionEvent e) {
			List<SimpleDocument> objects = ClientUtils.search(number.getText().getText(), name.getText().getText());
			searchResultPanel.initResultTable(objects);
		}

		@Override
		public double getHorizontalProportion() {
			return 0.95d;
		}

		@Override
		public double getVerticalProportion() {
			return 0.2d;
		}

		@Override
		public void initialize() {
			// set panel border to be title and etched type
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getResourceMap().getString(TITLE), TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

			PromptTextField.PromptTextFieldDimension dimension = PromptTextField.newDimension(getPreferredSize(),
					LABEL_PROPORTION, TEXT_PROPORTION, HEIGHT_PROPORTION);
			number = PromptTextField.newInstance((getResourceMap().getString(NUMBER)), null, dimension);
			number.setLabelAligment(SwingConstants.CENTER);
			add(number);

			name = PromptTextField.newInstance((getResourceMap().getString(NAME)), null, dimension);
			name.setLabelAligment(SwingConstants.CENTER);
			add(name);

			Option search = new Option(SEARCH, null, this);
			add(new OptionPanel(Arrays.asList(search)));
		}
	}

	class SearchResultPanel extends AbstractPanel {

		private static final long serialVersionUID = -7416585921364617464L;
		protected SearchResultTable table;
		private double TABLE_HEIGTH_PROPORTION = 0.85d;
		private double TABLE_WIDTH_PROPORTION = 0.98d;
		private final String TITLE = "title";

		@Override
		public double getHorizontalProportion() {
			return 0.95d;
		}

		@Override
		public double getVerticalProportion() {
			return 0.6d;
		}

		@Override
		public void initialize() {
			// set panel border to be title and etched type
			setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
					getResourceMap().getString(TITLE), TitledBorder.DEFAULT_JUSTIFICATION,
					TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

			table = new SearchResultTable(null);
			JScrollPane sp = new JScrollPane(table);
			sp.setPreferredSize(new Dimension((int) (getPreferredSize().width * TABLE_WIDTH_PROPORTION),
					(int) (getPreferredSize().height * TABLE_HEIGTH_PROPORTION)));
			table.setColumnWidth();
			add(sp);
		}

		public void initResultTable(List<SimpleDocument> documents) {
			table.refresh(documents);
		}
	}
}