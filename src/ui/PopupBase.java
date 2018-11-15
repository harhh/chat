package ui;

import javax.swing.JDialog;

public class PopupBase extends JDialog {

	public PopupBase() {
		this.setModalityType(ModalityType.TOOLKIT_MODAL);
	}
	
// AWT-EventQueue-0" java.lang.StackOverflowError
//	public void show()
//	{
//		this.setVisible(true);
//	}
}
