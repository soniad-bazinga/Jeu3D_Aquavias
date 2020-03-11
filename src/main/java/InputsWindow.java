import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class InputsWindow extends JFrame implements KeyListener{
	Level level;


	public InputsWindow(Level level) {
		new JFrame();
		setTitle("IW");
		setVisible(true);
		addKeyListener(this);
		this.level = level;
		level.affiche();
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (level.compteur<=0 || level.Victory()) {
			arg0.setKeyCode(KeyEvent.VK_SPACE);
		}
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
	        case KeyEvent.VK_SPACE :
	        	if (level.Victory()) {
	        		try {
		        		dispose();
		        		Level.clearScreen();
	        			level.newLevel(++level.ID);
	        		}catch (Exception e) {
	        			Level.clearScreen();
	        			System.out.println("Vous avez fini d'irriguer les villes ! Quelle maitrise de l'eau !\nSeriez-vous Poseidon ??");
	        		}
        		}else {
        			try {
	        			dispose();
	        			Level.clearScreen();
	        			level.newLevel(level.ID);
        			}catch (Exception e) {
        				System.out.println("wtf ya un souci ...");
        			}
        		}
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
