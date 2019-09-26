package gaestra.standalone.tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.event.*;
import java.util.Random;

public class GameMain extends JFrame implements ActionListener {
  private static final char NONE = ' ', CIRCLE = 'O', CROSS = 'X';
  private static final int DEFAULT_GRIDSIZE = 3;
	
  private JButton oButton, xButton;
  private Color oColor = Color.BLUE, xColor = Color.RED;
  private Board board;
  private int lineThickness = 4;
  private int gridSize = DEFAULT_GRIDSIZE;

  private char[] position = new char[gridSize * gridSize];
  private int wins=0, losses=0, draws=0;
  
  public GameMain() {
	  init();
  }

  // Initialize
  public void init() {
  	// Props pre-setup
  	Arrays.fill(position, NONE);
  	
  	this.setSize(new Dimension(500,500));
    JPanel topPanel = new JPanel();
    topPanel.setLayout(new FlowLayout());
    topPanel.add(oButton = new JButton("O Color"));
    topPanel.add(xButton = new JButton("X Color"));
    oButton.addActionListener(this);
    xButton.addActionListener(this);
    add(topPanel, BorderLayout.NORTH);
    promptUserInput(false);
  }
  
  public void prepareBoard(int gridSize, boolean reUseBoard) {
  	// Prepare / Reset all board properties
    this.gridSize = gridSize;
    position = new char[gridSize * gridSize];
    Arrays.fill(position, NONE);
    
    if (!reUseBoard) {
    	add(board = new Board(), BorderLayout.CENTER);
    	setVisible(true);
    }
  }
  
  public void promptUserInput(boolean reUseBoard) {
  	String[] options = {"GO"};
  	String title = "Please put only a number";
  	JPanel panel = new JPanel();
  	JLabel lbl = new JLabel("Enter grid size (Min: 3): ");
  	JTextField txt = new JTextField(10);
  	panel.add(lbl);
  	panel.add(txt);
  	int selectedOption = 0;
  	
  	while (true){
  		selectedOption = JOptionPane.showOptionDialog(null, panel, title, 
    					JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options , options[0]);
  		
    	if(selectedOption == 0)
    	{
    		String input = txt.getText();
    		int userGridSize = 0;
    	  if (isNumeric(input) && (userGridSize = Integer.parseInt(input)) >= 3) {
    	    prepareBoard(userGridSize, reUseBoard);
    	    break;
    	  }
    	  else continue;
    	}
    	else {
    		prepareBoard(DEFAULT_GRIDSIZE, reUseBoard);
    		break;
    	}
  	}
  }
  
  public boolean isNumeric(String strNum) {
    try {
      double d = Double.parseDouble(strNum);
    } catch (NumberFormatException | NullPointerException nfe) {
      return false;
    }
    
    return true;
}

  // Change color of O or X
  public void actionPerformed(ActionEvent e) {
    if (e.getSource()==oButton) {
      Color newColor = JColorChooser.showDialog(this, "Choose a new color for O", oColor);
      if (newColor!=null)
        oColor=newColor;
    }
    else if (e.getSource()==xButton) {
      Color newColor = JColorChooser.showDialog(this, "Choose a new color for X", xColor);
      if (newColor!=null)
        xColor=newColor;
    }
    board.repaint();
  }

  private class Board extends JPanel implements MouseListener {
      
    private Random random = new Random();

    public Board() {
      addMouseListener(this);
    }

    // Redraw the board
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      int w = getWidth();
      int h = getHeight();
      Graphics2D g2d = (Graphics2D) g;

      // Draw the grid
      g2d.setPaint(Color.WHITE);
      g2d.fill(new Rectangle2D.Double(0, 0, w, h));
      g2d.setPaint(Color.BLACK);
      g2d.setStroke(new BasicStroke(lineThickness));
      for (int i = 1; i <= (gridSize - 1) * 2; i++) {
      	g2d.draw(new Line2D.Double(0, h*i/gridSize, w, h*i/gridSize));
      	g2d.draw(new Line2D.Double(w*i/gridSize, 0, w*i/gridSize, h));
      }

      // Draw the Os and Xs
      for (int i=0; i < gridSize * gridSize; ++i) {
        double xpos = (i % gridSize + 0.5) * w / gridSize;
        double ypos = (i / gridSize + 0.5) * h / gridSize;
        double xr = w / ((gridSize * gridSize) - (1.5 * (gridSize - 2)));
        double yr = h / ((gridSize * gridSize) - (1.5 * (gridSize - 2)));
        if (position[i] == CIRCLE) {
          g2d.setPaint(oColor);
          g2d.draw(new Ellipse2D.Double(xpos-xr, ypos-yr, xr * 2, yr * 2));
        }
        else if (position[i] == CROSS) {
          g2d.setPaint(xColor);
          g2d.draw(new Line2D.Double(xpos - xr, ypos - yr, xpos + xr, ypos + yr));
          g2d.draw(new Line2D.Double(xpos - xr, ypos + yr, xpos + xr, ypos - yr));
        }
      }
    }

    // Draw a CIRCLE where the mouse is clicked
    public void mouseClicked(MouseEvent e) {
      int xpos = e.getX() * gridSize / getWidth();
      int ypos = e.getY() * gridSize / getHeight();
      int pos = xpos + gridSize * ypos;
      if (pos >= 0 && pos < (gridSize * gridSize) && position[pos] == NONE) {
        position[pos] = CIRCLE;
        repaint();
        putX();  // computer plays
        repaint();
      }
    }

    // Ignore other mouse events
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    // Computer will be playing CROSS
    void putX() {
      
      if (won(CIRCLE))
        newGame(CIRCLE);
      else if (isDraw())
        newGame(NONE);

      else {
        nextMove();
        if (won(CROSS))
          newGame(CROSS);
        else if (isDraw())
          newGame(NONE);
      }
    }

    // Return true if a player has won
    boolean won(char player) {
    	int size = gridSize;
    	
      // Check for diagonal letter chain
      for (int i = 0; i < (size * size); i += size + 1) {
        if (position[i] != player)
            break;
        else if (i == (size * size) - 1)
            return true;
      }

      // Check for inline letter chain
      for (int i = 0; i < (size * size); i += size) {
        for (int j = i; j < (i + size); j++) {
          if (position[j] != player)
            break;
          else if (j == (i + size) - 1)
            return true;
        }
      }

      // Check for rundown letter chain
      for (int i = 0; i < size; i++) {
        for (int j = i; j < (size * size); j += size) {
          if (position[j] != player)
            break;
          else if (j == ((size * size) - 1) - ((size - 1) - i))
            return true;
          }
      }

      // Check for reverse-diagonal letter chain
      for (int i = size - 1; i < (size * size); i += size - 1) {
          if (position[i] != player)
              break;
          else if (i == ((size * size) - 1) - (size - 1))
              return true;
      }
      
      return false;
    }

    // Play CROSS in random spot
    void nextMove() {
    	int r = 0;
    	
    	// Put CPU on random position, next will be used to expect when player going to win... for later.
      do {
        r = random.nextInt(gridSize * gridSize);
      }
      while (position[r] != NONE);
      
      position[r] = CROSS;
    }

    // Draw if all grid is filled
    boolean isDraw() {
      for (int i = 0; i < (gridSize * gridSize); ++i)
        if (position[i] == NONE)
          return false;
      return true;
    }

    // Start a new game
    void newGame(char winner) {
      repaint();

      // Announce result of last game.  Ask user to play again.
      String result;
      if (winner == CIRCLE) {
        wins++;
        result = "You Win!";
      }
      else if (winner == CROSS) {
        losses++;
        result = "I Win!";
      }
      else {
        result = "Tie";
        draws++;
      }
      if (JOptionPane.showConfirmDialog(null, 
          "You have " + wins + " wins, " + losses + " losses, " + draws + " draws\n"
          + "Play again?", result, JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
        wins = losses = draws = 0;
      }
      
      promptUserInput(true);

      // Computer starts first every other game
      if ((wins + losses + draws) % 2 == 1)
        nextMove();
    }
  } 
} 