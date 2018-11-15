package controller;

import javax.swing.JTextArea;

import ui.PopupConfirm;
import utils.Delegate;

public class ViewControllerSingleton {
	
    private static final ViewControllerSingleton instance = new ViewControllerSingleton();
	    
    //private constructor to avoid client applications to use constructor
    private ViewControllerSingleton(){}

    public static ViewControllerSingleton getInstance() {
        return instance;
    }
    
    public void appendInTextArea(JTextArea textArea, String string) {
    	textArea.append(string);
    	textArea.append("\n");
    }
    
    public PopupConfirm createPopupConfirm(String description, Delegate setOnOk) {
    	return new PopupConfirm(description, setOnOk);
    }
    
}
