import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class InputsWindow extends JFrame implements KeyListener{
	Level level;

	public InputsWindow(Level level) {
		new JFrame();
		setTitle("invisible hehe ;)");
		setVisible(true);
		addKeyListener(this);
		this.level = level;
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int keyCode = arg0.getKeyCode();
	    switch( keyCode ) { 
	        case KeyEvent.VK_UP:
	        	level.movePointer("UP");
	            break;
	        case KeyEvent.VK_DOWN:
	        	level.movePointer("DOWN");
	            break;
	        case KeyEvent.VK_LEFT:
	        	level.movePointer("LEFT");
	            break;
	        case KeyEvent.VK_RIGHT :
	        	level.movePointer("RIGHT");
	            break;
	        case KeyEvent.VK_ENTER :
	        	level.rotatePointer();
	        	break;
	     }	
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
