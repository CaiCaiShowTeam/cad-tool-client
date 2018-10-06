package com.bplead.cad.ui;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.bplead.cad.bean.SimpleFolder;
import com.bplead.cad.bean.SimplePdmLinkProduct;
import com.bplead.cad.util.ClientUtils;

import priv.lee.cad.util.ClientAssert;

public class FolderTree extends JTree {

	private static final Logger logger = Logger.getLogger(FolderTree.class);
	private static final long serialVersionUID = -8568945020359297230L;
	private SimpleFolder rootFolder;

	public FolderTree(SimplePdmLinkProduct product) {
		ClientAssert.notNull(product, "SimplePdmLinkProduct is required");

		this.rootFolder = ClientUtils.getSimpleFolders(product);
		logger.debug("rootFolder:" + rootFolder);
		initTree();
	}

	private void initTree() {
		setModel(new DefaultTreeModel(toFolderNode(rootFolder)));
		setRootVisible(true);
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	}

	private FolderNode toFolderNode(SimpleFolder folder) {
		FolderNode node = new FolderNode(folder);
		List<SimpleFolder> children = folder.getChildren();
		for (SimpleFolder child : children) {
			node.add(toFolderNode(child));
		}
		return node;
	}

	public class FolderNode extends DefaultMutableTreeNode {

		private static final long serialVersionUID = -8615146478575724605L;
		private SimpleFolder folder;

		public FolderNode(SimpleFolder folder) {
			super(folder.getName());
			this.folder = folder;
		}

		public SimpleFolder getFolder() {
			return folder;
		}
	}
}
