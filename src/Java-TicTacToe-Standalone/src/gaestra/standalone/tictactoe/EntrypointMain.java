package gaestra.standalone.tictactoe;

import javax.swing.SwingUtilities;

public class EntrypointMain {
	
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(new Runnable(){
	        @Override
	        public void run() {
	            new GameMain();
	        }
	    });
	}

}
