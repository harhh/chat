package controller;

import javax.swing.JTextArea;

public class ViewControllerSingleton {
	
    private static final ViewControllerSingleton instance = new ViewControllerSingleton();
	    
    //private constructor to avoid client applications to use constructor
    private ViewControllerSingleton(){}

    public static ViewControllerSingleton getInstance(){
        return instance;
    }
    
    public void AppendInTextArea(JTextArea textArea, String string)
    {
    	textArea.append(string);
    	textArea.append("\n");
    }
    
}
