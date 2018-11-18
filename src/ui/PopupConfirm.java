package ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import utils.Delegate;

import javax.swing.UIManager;
import javax.swing.JTextArea;

public class PopupConfirm extends PopupBase {

	private final JPanel contentPanel = new JPanel();
	/**
	 * Create the dialog.
	 */
	public PopupConfirm(String description, Delegate setOnOk) {
		super();
		
		setBounds(100, 100, 400, 247);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBackground(UIManager.getColor("Button.background"));
		textArea.setEditable(false);
		textArea.setBounds(77, 49, 242, 85);
		contentPanel.add(textArea);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(setOnOk == null) {
							dispose();
							return;
						}
						setOnOk.doDelegate(this);					
					}
				});
			}
		}
		
		textArea.setText(description);
	}
}
