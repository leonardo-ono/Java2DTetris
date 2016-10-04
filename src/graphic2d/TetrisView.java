package graphic2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class TetrisView extends JFrame {
    
    private BufferedImage offscreen;
    private TetrisModel model = new TetrisModel();
    private Color[] colors = { Color.BLACK, Color.RED, Color.GREEN, Color.BLUE
            , Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.ORANGE };
    
    public TetrisView() throws HeadlessException {
        setSize(350, 350);
        setTitle("Graphic 2D Tetris test");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        offscreen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!model.isGameOver()) {
                        model.update();
                    }
                    repaint();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ex) { }
                }
            }
        }).start();
    }

    @Override
    public void paint(Graphics g) {
        draw(offscreen.getGraphics());
        g.drawImage(offscreen, 0, 0, null);
    }
    
    public void draw(Graphics g) {
        ((Graphics2D) g).setBackground(getBackground());
        g.clearRect(0, 0, getWidth(), getHeight());
        drawScore(g, 170, 65);
        drawGrid(g, 50, 10);
        drawNextPiece(g, 170, 100);
        if (model.isGameOver()) {
            drawGameOver(g);
        }
    }

    private void drawScore(Graphics g, int x, int y) {
        g.setColor(getForeground());
        g.drawString("SCORE: " + model.getScore(), x, y);
    }
    
    private void drawGrid(Graphics g, int dx, int dy) {
        int cellSize = 10;
        for (int row = 4; row < model.getGridRows(); row++) {
            for (int col = 0; col < model.getGridCols(); col++) {
                int x = col * cellSize + dx;
                int y = row * cellSize + dy;
                int c = model.getGridValue(col, row);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x, y, cellSize, cellSize);
                if (c > 0) {
                    g.setColor(colors[c]);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }
    
    private void drawNextPiece(Graphics g, int dx, int dy) {
        g.drawString("NEXT: ", dx, dy);
        int cellSize = 10;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int x = col * cellSize + dx;
                int y = row * cellSize + dy + 5;
                int c = model.getNextBlockValue(col, row);
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x, y, cellSize, cellSize);
                if (c > 0) {
                    g.setColor(colors[c]);
                    g.fillRect(x, y, cellSize, cellSize);
                }
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
            }
        }
    }

    public void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(75, 125, 200, 100);
        g.setColor(getForeground());
        g.drawRect(75, 125, 200, 100);
        g.drawString("GAME OVER", 140, 165);
        g.drawString("PRESS SPACE TO PLAY", 105, 195);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            if (model.isGameOver()) {
                if (e.getKeyCode() == 32) {
                    model.start();
                }
            }
            else {
                switch (e.getKeyCode()) {
                    case 37: model.move(-1); break;
                    case 39: model.move(1); break;
                    case 38: model.rotate(); break;
                    case 40: model.down(); break;
                    case 65: model.update(); break;
                }
            }
        }
        repaint();
    }
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TetrisView view = new TetrisView();
                view.setVisible(true);
            }
        });
    }
    
}
