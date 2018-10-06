package com.bplead.cad.ui;

import java.awt.EventQueue;

import com.bplead.cad.bean.client.Temporary;
import com.bplead.cad.model.CustomPrompt;
import com.bplead.cad.util.ClientUtils;

import priv.lee.cad.model.ServerClientTemporary;
import priv.lee.cad.ui.AbstractLoginFrame;
import priv.lee.cad.util.ClientAssert;
import priv.lee.cad.util.XmlUtils;

public class LoginFrame extends AbstractLoginFrame {

	private static final long serialVersionUID = 2220682440023001808L;

	public LoginFrame() {
		super(LoginFrame.class, Temporary.class);
	}

	@Override
	public void startNextFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				if (ClientUtils.StartArguments.CAD.equals(ClientUtils.args.getType())) {
					new CADMainFrame().activate();
				} else if (ClientUtils.StartArguments.CAPP.equals(ClientUtils.args.getType())) {
					new CAPPMainFrame().activate();
				} else if (ClientUtils.StartArguments.DOWNLOAD.equals(ClientUtils.args.getType())) {
					new SearchForDownloadDialog(null).activate();
				} else {
					throw new RuntimeException("Unsupport start arguments:" + ClientUtils.args);
				}
			}
		});
	}

	@Override
	public ServerClientTemporary toTemporary() {
		ClientAssert.hasText(server.host.getTextContent(), CustomPrompt.HOST_NULL);
		ClientAssert.hasText(server.user.getTextContent(), CustomPrompt.USER_NULL);
		ClientAssert.hasText(server.pwd.getTextContent(), CustomPrompt.PWD_NULL);

		Temporary temporary = XmlUtils.read(Temporary.class);
		if (temporary == null || !server.user.getTextContent().equals(temporary.getUserName())) {
			temporary = new Temporary(server.host.getTextContent(), server.user.getTextContent(),
					server.pwd.getTextContent(), server.remeberme.isSelected());
		} else {
			temporary.setUserName(server.user.getTextContent());
			temporary.setUserPasswd(server.pwd.getTextContent());
			temporary.setRememberMe(server.remeberme.isSelected());
		}
		ClientUtils.temprary = temporary;
		return temporary;
	}
}
