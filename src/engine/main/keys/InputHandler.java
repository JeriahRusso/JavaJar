package engine.main.keys;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import engine.main.Main;

public class InputHandler implements KeyListener{
	
	public class Key{
		private boolean isPressed = false;
		
		public boolean getPressed(){
			return isPressed;
		}
		
		public void toggle(boolean pressed){
			isPressed = pressed;
		}
	}
	
	public Key down = new Key();
	public Key up = new Key();
	public Key right = new Key();
	public Key left = new Key();
	
	public InputHandler(Main main){
		main.addKeyListener(this);
	}
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {
		
	}
	
	private void toggleKey(int keyCode, boolean isPressed){
		if(keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN){
			down.toggle(isPressed);
		}
		if(keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP){
			up.toggle(isPressed);
		}
		if(keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT){
			right.toggle(isPressed);
		}
		if(keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT){
			left.toggle(isPressed);
		}
	}

}
