package controller;

import java.awt.Component;

import javax.swing.JScrollBar;
import javax.swing.JTextArea;

import ui.Lobby;
import ui.PopupConfirm;
import utils.Delegate;

public class ViewControllerSingleton {
	
    private static final ViewControllerSingleton instance = new ViewControllerSingleton();
	    
    //private constructor to avoid client applications to use constructor
    private ViewControllerSingleton(){}

    public static ViewControllerSingleton getInstance() {
        return instance;
    }
    
    public void clearTextArea(Lobby lobby) {
    	lobby.getTextArea().setText("");
    	JScrollBar scrollbar = lobby.getScrollPane().getVerticalScrollBar();
    	scrollbar.setValue(scrollbar.getMaximum());
    }
    
    public void appendInTextArea(Lobby lobby, String message) {
    	lobby.getTextArea().append(message);
    	lobby.getTextArea().append("\n");

    	JScrollBar scrollbar = lobby.getScrollPane().getVerticalScrollBar();
    	scrollbar.setValue(scrollbar.getMaximum());
    }
    
    public void refreshRoomList(Lobby lobby, String message) {
    	lobby.getRoomListTextArea().setText(message);
    }
    
    public PopupConfirm createPopupConfirm(String description, Delegate setOnOk, Component component) {
    	PopupConfirm popupConfirm = new PopupConfirm(description, setOnOk);
    	popupConfirm.setLocationRelativeTo(component);
    	popupConfirm.setVisible(true);
    	return popupConfirm;
    }
    
}
