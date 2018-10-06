package com.bplead.cad.model;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.bplead.cad.ui.PreferencesDialog;

import priv.lee.cad.model.Callback;
import priv.lee.cad.model.impl.DefaultStyleToolkit;

public class CustomStyleToolkit extends DefaultStyleToolkit {

	private static final String PREFERENCES_MENU_ITEM = "menu.file.item2";
	private Callback callback;

	public CustomStyleToolkit(Callback callback) {
		this.callback = callback;
	}

	@Override
	public JMenuBar getStandardMenuBar(ActionListener checkinListener, ActionListener checkoutListener) {
		JMenuBar menuBar = super.getStandardMenuBar(checkinListener, checkoutListener);

		JMenu file = menuBar.getMenu(0);
		JMenuItem preferences = new JMenuItem(resourceMap.getString(PREFERENCES_MENU_ITEM));
		preferences.addActionListener(new PrefencesActionListener(callback));

		file.addSeparator();
		file.add(preferences);
		return menuBar;
	}

	public void startPreferencesDialog(Callback container) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				PreferencesDialog dialog = new PreferencesDialog(container);
				dialog.activate();
			}
		});
	}

	public class PrefencesActionListener implements ActionListener, Callback {

		private Callback callback;

		public PrefencesActionListener(Callback callback) {
			this.callback = callback;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			startPreferencesDialog(callback);
		}

		@Override
		public void call(Object object) {

		}
	}
}
