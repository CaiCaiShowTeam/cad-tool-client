package com.bplead.cad;

import java.awt.EventQueue;

import com.bplead.cad.ui.LoginFrame;
import com.bplead.cad.util.ClientUtils;

import priv.lee.cad.util.ClientAssert;

public class Main {

	public static void main(String[] args) {
		ClientAssert.notEmpty(args, "Miss starting arguments");

		ClientUtils.args.setType(args[0]);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new LoginFrame().activate();
			}
		});
	}
}
