package controller;

import java.awt.Component;

import javax.swing.JScrollBar;

import global.FinalVariable;
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
    }
    
    public void appendInTextArea(Lobby lobby, String message) {
    	lobby.getTextArea().append(message);
    	lobby.getTextArea().append(FinalVariable.UTFLINEDELEMITER);

    	JScrollBar scrollbar = lobby.getScrollPane().getVerticalScrollBar();
    	scrollbar.setValue(scrollbar.getMaximum());
    }
    
    public void appendFrontInTextArea(Lobby lobby, String message) {
    	StringBuilder stringBuilder = new StringBuilder(message);
    	stringBuilder.append("\n");
    	stringBuilder.append(lobby.getTextArea().getText());
    	lobby.getTextArea().setText(stringBuilder.toString());
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
