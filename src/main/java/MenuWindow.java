import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

public class MenuWindow extends JDialog implements KeyListener {
    Menu menu;

    MenuWindow(Menu m){
        new JDialog();
        setTitle("MW");
        setVisible(true);
        addKeyListener(this);
        menu = m;
        m.afficheStart();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if(menu.isClosed()) dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        switch( keyCode ) {
            case KeyEvent.VK_RIGHT : menu.switchSelected(1); break;
            case KeyEvent.VK_LEFT : menu.switchSelected(-1); break;
            case KeyEvent.VK_UP : menu.switchLevel(-1); break;
            case KeyEvent.VK_DOWN : menu.switchLevel(1); break;
            case KeyEvent.VK_ENTER : menu.pressEnter(); break;
        }
        }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}
