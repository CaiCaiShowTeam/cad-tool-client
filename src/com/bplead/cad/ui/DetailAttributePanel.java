package com.bplead.cad.ui;

import java.awt.Dimension;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.io.DetailModel;

import priv.lee.cad.ui.AbstractPanel;
import priv.lee.cad.util.ClientAssert;

public class DetailAttributePanel extends AbstractPanel {

	private static final Logger logger = Logger.getLogger(DetailAttributePanel.class);
	private static final long serialVersionUID = -206359105088128179L;
	private boolean autoResizeOff = false;
	private String[][] datas;
	private DetailModel detailModel;
	private double horizontalProportion = 0.95d;
	private String[] names;
	private double TABLE_HEIGTH_PROPORTION = 0.9d;
	private double TABLE_WIDTH_PROPORTION = 0.98d;
	private final String TITLE = "title";
	private double verticalProportion = 0.45d;

	public DetailAttributePanel(DetailModel detailModel) {
		this.detailModel = detailModel;
	}

	private Class<? extends Serializable> getActualType(List<? extends Serializable> detail) {
		if (detail == null || detail.isEmpty()) {
			return null;
		}
		return detail.get(0).getClass();
	}

	public DetailModel getDetailModel() {
		return detailModel;
	}

	@Override
	public double getHorizontalProportion() {
		return horizontalProportion;
	}

	@Override
	public double getVerticalProportion() {
		return verticalProportion;
	}

	@Override
	public void initialize() {
		logger.info("initialize content...");
		// ~ initialize content
		setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), getResourceMap().getString(TITLE),
						TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, toolkit.getFont()));

		logger.info("initialize detail table...");
		initTableData(detailModel.getDetail());

		DefaultTableModel model = new DefaultTableModel(datas, names) {
			private static final long serialVersionUID = 2965741710969780926L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table = new JTable(model);
		if (autoResizeOff) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		JScrollPane sp = new JScrollPane(table);
		Dimension dimension = getPreferredSize();
		sp.setPreferredSize(new Dimension((int) (dimension.width * TABLE_WIDTH_PROPORTION),
				(int) (dimension.height * TABLE_HEIGTH_PROPORTION)));
		add(sp);
	}

	private void initTableData(List<? extends Serializable> detail) {
		Class<? extends Serializable> cls = getActualType(detail);
		if (cls == null) {
			return;
		}

		Field[] fields = cls.getDeclaredFields();
		names = new String[fields.length];
		datas = new String[detail == null || detail.isEmpty() ? 0 : detail.size()][names.length];
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			String name = getResourceMap().getString(field.getName());
			ClientAssert.hasText(name, "Table column name[" + field.getName() + "] must not be null");
			names[i] = name;
			if (detail == null || detail.isEmpty()) {
				continue;
			}
			for (int j = 0; j < detail.size(); j++) {
				Serializable serializable = detail.get(j);
				Object value = null;
				try {
					value = field.get(serializable);
				} catch (Exception e) {
					e.printStackTrace();
				}
				logger.debug(name + "=" + value);
				datas[j][i] = String.valueOf(value == null ? "" : value);
			}
		}
	}

	public void setAutoResizeOff(boolean autoResizeOff) {
		this.autoResizeOff = autoResizeOff;
	}

	public void setDetailModel(DetailModel detailModel) {
		this.detailModel = detailModel;
	}

	public void setHorizontalProportion(double horizontalProportion) {
		this.horizontalProportion = horizontalProportion;
	}

	public void setVerticalProportion(double verticalProportion) {
		this.verticalProportion = verticalProportion;
	}
}
