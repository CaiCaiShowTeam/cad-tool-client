package com.bplead.cad.ui;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import priv.lee.cad.layout.DefaultGroupLayout;
import priv.lee.cad.ui.AbstractPanel;
import priv.lee.cad.ui.PromptTextField;

public class BasicAttributePanel extends AbstractPanel {

	private static final long serialVersionUID = 5723039852386303330L;
	private final double HEIGHT_PROPORTION = 0.1d;
	private final double HGAP_PROPORTION = 0.005d;
	private double labelProportion = 0.08d;
	private final Logger logger = Logger.getLogger(BasicAttributePanel.class);
	private Serializable serializable;
	private double textProportion = 0.2d;
	private final String TITLE = "title";
	private final double VGAP_PROPORTION = 0.02d;

	public BasicAttributePanel(Serializable serializable) {
		this.serializable = serializable;
	}

	private List<PromptTextField> conver2Texts() {
		// ~ reflect String type fields and convert to PromptTextField type
		List<PromptTextField> texts = new ArrayList<PromptTextField>();
		String value = "";
		PromptTextField.PromptTextFieldDimension dimension = PromptTextField.newDimension(getPreferredSize(),
				labelProportion, textProportion, HEIGHT_PROPORTION);
		Field[] fields = serializable.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			value = "";
			try {
				if (this.serializable != null) {
					Object object = field.get(serializable);
					if (!(object instanceof String)) {
						continue;
					}
					value = (String) object;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			PromptTextField text = PromptTextField.newInstance(getResourceMap().getString(field.getName()), value,
					dimension);
			text.setEditable(false);
			texts.add(text);
		}
		return texts;
	}

	public Serializable getCad() {
		return serializable;
	}

	@Override
	public double getHorizontalProportion() {
		return 0.95d;
	}

	public double getLabelProportion() {
		return labelProportion;
	}

	public double getTextProportion() {
		return textProportion;
	}

	@Override
	public double getVerticalProportion() {
		return 0.35d;
	}

	@Override
	public void initialize() {
		logger.info("initialize content...");
		// ~ initialize content
		setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getResourceMap().getString(TITLE),
						TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

		logger.info("convert to PromptTextField...");
		List<PromptTextField> texts = conver2Texts();

		// ~ performance hGap and vGap
		int hGap = ((Double) (getPreferredSize().width * HGAP_PROPORTION)).intValue();
		int vGap = ((Double) (getPreferredSize().height * VGAP_PROPORTION)).intValue();
		logger.debug("hGap:" + hGap + ",vGap:" + vGap);

		logger.info("use default group layout...");
		DefaultGroupLayout layout = new DefaultGroupLayout(this, hGap, vGap);
		layout.addComponent(texts).layout(3);
	}

	public void setCad(Serializable cad) {
		this.serializable = cad;
	}

	public void setLabelProportion(double labelProportion) {
		this.labelProportion = labelProportion;
	}

	public void setTextProportion(double textProportion) {
		this.textProportion = textProportion;
	}
}
