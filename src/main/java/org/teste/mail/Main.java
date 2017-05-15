package org.teste.mail;

import javax.swing.UIManager;

public class Main {

	public static void main(String[] args) throws Exception{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new JFEmail().setVisible(true);
	}
	
}
