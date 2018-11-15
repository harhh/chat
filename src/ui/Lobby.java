package ui;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;
import client.ClientThread;
import controller.ControllerTextArea;
import controller.ViewControllerSingleton;
import utils.Delegate;
import utils.Utils;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Lobby extends JFrame {

	private JPanel contentPane;

	private static Client client = null;
	private static ClientThread clientThread = null;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Lobby frame = new Lobby();
					
					client = new Client();
					clientThread = new  ClientThread(client, frame);
					clientThread.start();
					
					frame.SetEventLisnter();
					frame.setVisible(true);
				
					clientThread.login();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	private JTextArea textArea = null;
	private JTextArea textAreaInput = null;
	private JButton btnCreateRoom = null;
	JButton btnInsertRoom = null;
	
	public JTextArea GetTextArea()
	{
		return textArea;	
	}
	
	public JTextArea GetTextAreaInput()
	{
		return textAreaInput;
	}
	/**
	 * Create the frame.
	 */
	public Lobby() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		btnCreateRoom = new JButton("Create room");
		btnCreateRoom.setBounds(551, 396, 117, 76);
		
		contentPane.setLayout(null);
		contentPane.add(btnCreateRoom);
		
		btnInsertRoom = new JButton("Insert room");
		btnInsertRoom.setBounds(677, 396, 117, 76);
		contentPane.add(btnInsertRoom);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(239, 6, 555, 326);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 6, 223, 386);
		contentPane.add(scrollPane_1);
		
		JButton btnNewButton = new JButton("Refrersh Room");
		btnNewButton.setBounds(6, 396, 117, 76);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		contentPane.add(btnNewButton);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(239, 336, 555, 56);
		contentPane.add(scrollPane_2);
		
		// input
		textAreaInput = new JTextArea();
		scrollPane_2.setViewportView(textAreaInput);
		
	}
	
	// call after initialize clientThread
	private void SetEventLisnter() 
	{
		btnInsertRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new PopupInput("방번호를 입력하세요(1~)", new Delegate() {
					@Override
					public void doDelegate(Object o) {
						String inputText = o.toString();
					
						if(!Utils.isNumeric(inputText)) {	
							ViewControllerSingleton.getInstance().createPopupConfirm("숫자만 입력하세요 : 1 ~ " + Long.MAX_VALUE, null);
							return;
						}
						clientThread.insertRoom(Long.parseLong(inputText));
					}
				});
			}
		});
		
		btnCreateRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientThread.createRoom();
			}
		});
		
		textAreaInput.addKeyListener(new ControllerTextArea(new Delegate() {
			@Override
			public void doDelegate(Object o) {
				
				String inputText = textAreaInput.getText();
				if(!inputText.isEmpty())
				{
					clientThread.chatMessage(inputText.trim());
				}
				textAreaInput.setText("");
			}
		}));
	}
}
