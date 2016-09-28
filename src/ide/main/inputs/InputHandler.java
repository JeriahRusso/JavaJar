package ide.main.inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import engine.main.Timer;
import ide.main.Environment;

public class InputHandler implements KeyListener, MouseListener, MouseMotionListener{
	
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
	
	public int mouseX = 0;
	public int mouseY = 0;
	
	public long timeReleased = Timer.getTicks();
	public long leftReleased = Timer.getTicks();
	
	public static boolean rightButton = false;
	public static boolean mousePressed = false;
	
	public InputHandler(Environment e){
		e.addKeyListener(this);
		e.addMouseListener(this);
		e.addMouseMotionListener(this);
	}
	
	public void mouseClicked(java.awt.event.MouseEvent e) {
		
	}
	
	public void mouseEntered(java.awt.event.MouseEvent e) {
		
	}

	public void mouseExited(java.awt.event.MouseEvent e) {
		
	}

	public void mousePressed(java.awt.event.MouseEvent e) {
		mousePressed = true;
		rightButton = false;
	}

	public void mouseReleased(java.awt.event.MouseEvent e) {
		mousePressed = false;
		timeReleased = Timer.getTicks();
		if(e.getButton() == 1){
			leftReleased = Timer.getTicks();
		}
		if(e.getButton() == 3){
			rightButton = true;
		}
	}

	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}

	public void keyTyped(KeyEvent e) {
		
	}

	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}
	
	public int getMouseX(){
		return mouseX;
	}
	
	public int getMouseY(){
		return mouseY;
	}
	
	public boolean getMouseReleased(){
		if(Timer.getTicks() - timeReleased == 1){
			return true;
		}
		return false;
	}
	
	public boolean getLeftReleased(){
		if(Timer.getTicks() - timeReleased == 1){
			return true;
		}
		return false;
	}
	
	public void toggleKey(int keyCode, boolean isPressed){
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
