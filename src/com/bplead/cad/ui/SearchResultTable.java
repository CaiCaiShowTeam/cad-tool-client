package com.bplead.cad.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.bplead.cad.bean.SimpleDocument;

import priv.lee.cad.model.ResourceMap;
import priv.lee.cad.model.ResourceMapper;
import priv.lee.cad.model.impl.DefaultResourceMap;
import priv.lee.cad.util.ClientAssert;
import priv.lee.cad.util.StringUtils;
import priv.lee.cad.util.XmlUtils;

public class SearchResultTable extends JTable implements ResourceMapper, MouseListener {

	private static final int FIXED_WIDTH = 5;
	private static final long serialVersionUID = -5844495101340439741L;
	private final String COL_HEADER_SUFFIX = "].header";
	private final String COL_NAME_SUFFIX = "].value.name";
	private final String COL_TYPE_SUFFIX = "].value.type";
	private final String COL_WIDTH_SUFFIX = "].proportion.width";
	private final String COLUMN_TOTAL = "column.total";
	private List<SimpleDocument> documents;
	private final String PREFIX_COL_HEADER = "column[";
	private ResourceMap resourceMap;

	{
		resourceMap = new DefaultResourceMap(SearchResultTable.class);
	}

	public SearchResultTable(List<SimpleDocument> documents) {
		this.documents = documents;
		initTable();
	}

	public void clear() {
		DefaultTableModel tableModel = (DefaultTableModel) getModel();
		tableModel.setRowCount(0);
	}

	@SuppressWarnings("unchecked")
	private <T> T getCellContent(SimpleDocument product, String name, Class<T> clatt) {
		if (product == null || StringUtils.isEmpty(name) || clatt == null) {
			return null;
		}

		try {
			Field field = XmlUtils.findField(product.getClass(), name);
			field.setAccessible(true);
			return (T) field.get(product);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getCellContentName(int column) {
		return resourceMap.getString(PREFIX_COL_HEADER + column + COL_NAME_SUFFIX);
	}

	private Class<?> getCellContentType(int column) throws ClassNotFoundException {
		String type = resourceMap.getString(PREFIX_COL_HEADER + column + COL_TYPE_SUFFIX);
		ClientAssert.hasText(type, "Column " + column + " type is required");
		return Class.forName(type);
	}

	@Override
	public Class<?> getColumnClass(int column) {
		try {
			return getCellContentType(column);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return Object.class;
	}

	private List<String> getColumnHeaders() {
		int total = resourceMap.getInt(COLUMN_TOTAL);
		List<String> headers = new ArrayList<String>();
		for (int column = 0; column < total; column++) {
			String header = resourceMap.getString(PREFIX_COL_HEADER + column + COL_HEADER_SUFFIX);
			headers.add(header.equals(PREFIX_COL_HEADER + column + COL_HEADER_SUFFIX) ? "" : header);
		}
		return headers;
	}

	private int getColumnWidth(int column) {
		String proportion = resourceMap.getString(PREFIX_COL_HEADER + column + COL_WIDTH_SUFFIX);
		return new BigDecimal(getParent().getPreferredSize().width).multiply(new BigDecimal(proportion)).intValue();
	}

	public List<SimpleDocument> getProducts() {
		return documents;
	}

	@Override
	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public SimpleDocument getRowData(int row) {
		if (documents == null) {
			return null;
		}
		ClientAssert.isTrue(row < documents.size(), "Row out of bounds:" + row);
		return documents.get(row);
	}

	public List<SimpleDocument> getSelectedDocuments() {
		List<SimpleDocument> selectedDocuments = new ArrayList<SimpleDocument>();
		int rows = getModel().getRowCount();
		for (int i = 0; i < rows; i++) {
			Boolean selected = (Boolean) getValueAt(i, 0);
			if (selected) {
				selectedDocuments.add(documents.get(i));
			}
		}
		return selectedDocuments;
	}

	private void initTable() {
		DefaultTableModel model = (DefaultTableModel) getModel();
		List<String> headers = getColumnHeaders();
		for (int column = 0; column < headers.size(); column++) {
			if (model.findColumn(headers.get(column)) == -1) {
				model.addColumn(headers.get(column));
			}

			setRows(model, column);
		}
		addMouseListener(this);

		invalidate();
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return Boolean.class.isAssignableFrom(getValueAt(row, column).getClass());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	public void refresh(List<SimpleDocument> documents) {
		this.documents = documents;
		initTable();
	}

	public void setColumnWidth() {
		for (int column = 0; column < getColumnCount(); column++) {
			Class<?> cls = getColumnClass(column);
			if (column == 0 && Boolean.class.isAssignableFrom(cls)) {
				TableColumn firsetColumn = getColumnModel().getColumn(0);
				firsetColumn.setPreferredWidth(FIXED_WIDTH);
				firsetColumn.setMaxWidth(FIXED_WIDTH);
				firsetColumn.setMinWidth(FIXED_WIDTH);
				continue;
			}
			int width = getColumnWidth(column);
			getColumnModel().getColumn(column).setPreferredWidth(width);
		}
	}

	@Override
	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	private void setRows(DefaultTableModel model, int column) {
		if (documents == null || documents.isEmpty()) {
			clear();
			return;
		}

		for (int row = 0; row < documents.size(); row++) {
			if (model.getRowCount() <= row) {
				model.addRow(new Object[] {});
			}

			try {
				Object content = getCellContent(documents.get(row), getCellContentName(column),
						getCellContentType(column));
				model.setValueAt(content == null ? "" : content, row, column);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
