package controller;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import utils.Delegate;

public class ListenerTextArea implements KeyListener{
	private Delegate delegate = null;
	
	public ListenerTextArea(Delegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if((KeyEvent.VK_ENTER) == e.getKeyCode())
		{
			delegate.doDelegate(null);
		}
	}
}
