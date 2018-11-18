package ui;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import client.Client;
import client.ClientThread;
import controller.ListenerTextArea;
import controller.ViewControllerSingleton;
import global.FinalVariable;
import utils.Delegate;
import utils.Utils;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;

public class Lobby extends JFrame {
	
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

	private JPanel contentPanel;
	
	private JTextArea textArea = null;
	private JTextArea textAreaInput = null;
	private JTextArea roomListextArea = null;
	private JScrollPane scrollPane  = null;
	private JButton btnCreateRoom = null;
	private JButton btnInsertRoom = null;
	private JButton btnRefreshRoomList = null;
	
	public JPanel getLobbyPanel() {
		return this.contentPanel;
	}
	
	public JTextArea getTextArea() {
		return this.textArea;	
	}
	
	public JTextArea getTextAreaInput() {
		return this.textAreaInput;
	}
	
	public JScrollPane getScrollPane() {
		return this.scrollPane;
	}
	
	public JTextArea getRoomListTextArea() {
		return this.roomListextArea;
	}
	
	/**
	 * Create the frame.
	 */
	public Lobby() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		
		btnCreateRoom = new JButton("Create room");
		btnCreateRoom.setBounds(551, 396, 117, 76);
		
		contentPanel.setLayout(null);
		contentPanel.add(btnCreateRoom);
		
		btnInsertRoom = new JButton("Insert room");
		btnInsertRoom.setBounds(677, 396, 117, 76);
		contentPanel.add(btnInsertRoom);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(239, 6, 555, 326);
		contentPanel.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 6, 223, 386);
		contentPanel.add(scrollPane_1);
		
		roomListextArea = new JTextArea();
		roomListextArea.setEditable(false);
		scrollPane_1.setViewportView(roomListextArea);
		
		btnRefreshRoomList = new JButton("Refrersh Room");
		btnRefreshRoomList.setBounds(6, 396, 117, 76);

		contentPanel.add(btnRefreshRoomList);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(239, 336, 555, 56);
		contentPanel.add(scrollPane_2);

		textAreaInput = new JTextArea();
		scrollPane_2.setViewportView(textAreaInput);
	}
	
	// call after initialize clientThread
	private void SetEventLisnter() 
	{
		btnInsertRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final PopupInput popupInput = new PopupInput("방번호를 입력하세요(1~)", new Delegate() {
					@Override
					public void doDelegate(Object o) {
						String inputText = o.toString();
						if(!Utils.isNumeric(inputText)) {	
							 ViewControllerSingleton.getInstance().createPopupConfirm("숫자만 입력하세요 : 1 ~ " + Long.MAX_VALUE, null, contentPanel);
							return;
						}
						clientThread.insertRoom(Long.parseLong(inputText));
					}
				});
				popupInput.setLocationRelativeTo(contentPanel);
				popupInput.setVisible(true);
			}
		});
		
		btnCreateRoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientThread.createRoom();
			}
		});
		
		textAreaInput.addKeyListener(new ListenerTextArea(new Delegate() {
			@Override
			public void doDelegate(Object o) {
				String inputText = textAreaInput.getText().trim();
				if(inputText != null && inputText.length() > 0)
				{
					clientThread.chatMessage(inputText.replaceAll("\n", FinalVariable.LINEDELEMITER));
				}
				textAreaInput.setText("");
			}
		}));
		
		btnRefreshRoomList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clientThread.getRoomList();
			}
		});
		
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			int scrollBarValue = 0;
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				int eValue = e.getAdjustable().getValue();
				if(scrollBarValue != eValue && eValue == e.getAdjustable().getMinimum()) {
					clientThread.getRoomHistory();
				}
				scrollBarValue = eValue;
			}
		});
	}
}
