package com.bplead.cad.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import priv.lee.cad.model.Callback;
import priv.lee.cad.ui.AbstractDialog;

public class PopProgress extends AbstractDialog implements ActionListener {

	private static final long serialVersionUID = 3270445558206143668L;
	private JProgressBar progress = new JProgressBar(0, 100);
	private JLabel prompt = new JLabel();

	public PopProgress(Callback callback) {
		super(PopProgress.class, callback);
		setUndecorated(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.dispose();
	}

	@Override
	public double getHorizontalProportion() {
		return 0.2d;
	}

	@Override
	public double getVerticalProportion() {
		return 0.05d;
	}

	@Override
	public void initialize() {
		add(prompt, BorderLayout.NORTH);

		Dimension dimension = getPreferredSize();
		progress.setPreferredSize(new Dimension(dimension.width, dimension.height / 2));
		add(progress, BorderLayout.CENTER);

		validate();
	}

	@Override
	public Object setCallbackObject() {
		return null;
	}

	public void setProgress(PromptProgress promptProgress) {
		progress.setValue(promptProgress.getProgress());
		// progress.setStringPainted(true);

		prompt.setText(promptProgress.getPrompt());

		if (promptProgress.getProgress() == progress.getMaximum()) {
			dispose();
		} else {
			validate();
		}
	}

	public static class PromptProgress {
		private int progress;
		private String prompt;

		public PromptProgress(String prompt, int progress) {
			this.prompt = prompt;
			this.progress = progress;
		}

		public int getProgress() {
			return progress;
		}

		public String getPrompt() {
			return prompt;
		}
	}
}
