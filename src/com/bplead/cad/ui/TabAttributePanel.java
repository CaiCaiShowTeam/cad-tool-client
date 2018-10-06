package com.bplead.cad.ui;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;

import javax.swing.JTabbedPane;

import com.bplead.cad.bean.io.MPMPart;

import priv.lee.cad.model.ResourceMap;
import priv.lee.cad.model.ResourceMapper;
import priv.lee.cad.model.StyleToolkit;
import priv.lee.cad.model.TieContainer;
import priv.lee.cad.model.impl.DefaultStyleToolkit;
import priv.lee.cad.model.impl.GlobalResourceMap;

public class TabAttributePanel extends JTabbedPane implements ResourceMapper, TieContainer {

	private static final long serialVersionUID = 3070127155164816200L;
	private List<MPMPart> mpmParts;
	protected String PREFIX;
	private ResourceMap resourceMap;
	protected StyleToolkit toolkit = new DefaultStyleToolkit();
	{
		PREFIX = getClass().getSimpleName();
	}

	public TabAttributePanel(List<MPMPart> mpmParts) {
		this.mpmParts = mpmParts;
	}

	@Override
	public void activate() {
		this.resourceMap = initWindowResourceMap(getParent());
		initComponents();
	}

	private void doSelfAdaption() {
		Dimension dimension = getParent().getPreferredSize();
		Double width = dimension.width * getHorizontalProportion();
		Double height = dimension.height * getVerticalProportion();
		setPreferredSize(new Dimension(width.intValue(), height.intValue()));
	}

	private double getHorizontalProportion() {
		return 0.96d;
	}

	@Override
	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	private double getVerticalProportion() {
		return 0.45d;
	}

	public void initComponents() {
		setTabLayoutPolicy(SCROLL_TAB_LAYOUT);
		
		doSelfAdaption();

		initialize();

		validate();
	}

	@Override
	public void initialize() {
		if (mpmParts == null || mpmParts.isEmpty()) {
			return;
		}

		for (MPMPart mpmPart : mpmParts) {
			DetailAttributePanel attributePanel = new DetailAttributePanel(mpmPart);
			attributePanel.setAutoResizeOff(true);
			attributePanel.setVerticalProportion(0.87d);
			attributePanel.setHorizontalProportion(0.98d);
			addTab(mpmPart.getJdeNum(), attributePanel);
		}
	}

	protected ResourceMap initWindowResourceMap(Container container) {
		if (container instanceof Window) {
			Window window = (Window) container;
			return new GlobalResourceMap(PREFIX, window.getClass());
		}
		return initWindowResourceMap(container.getParent());
	}

	@Override
	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}
}
