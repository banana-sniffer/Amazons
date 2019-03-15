package amazons;

import ucb.gui2.Pad;

import java.awt.Graphics2D;
import java.awt.Color;
import java.io.IOException;
import java.awt.BasicStroke;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static amazons.Piece.*;
import static amazons.Square.sq;
import static amazons.Move.mv;

/** A widget that displays an Amazons game.
 *  @author William Tai
 */
class BoardWidget extends Pad {

    /* Parameters controlling sizes, speeds, colors, and fonts. */

    /** Colors of empty squares and grid lines. */
    static final Color
        SPEAR_COLOR = new Color(64, 64, 64),
        LIGHT_SQUARE_COLOR = new Color(106, 197, 238),
        CLICK1 = new Color(193, 103, 205),
        CLICK2 = new Color(103, 197, 115),
        DARK_SQUARE_COLOR = new Color(255, 249, 234);

    /** Locations of images of white and black queens. */
    private static final String
        WHITE_QUEEN_IMAGE = "wq4.png",
        BLACK_QUEEN_IMAGE = "bq4.png";

    /** Size parameters. */
    private static final int
        SQUARE_SIDE = 30,
        BOARD_SIDE = SQUARE_SIDE * 10;

    /** A graphical representation of an Amazons board that sends commands
     *  derived from mouse clicks to COMMANDS.  */
    BoardWidget(ArrayBlockingQueue<String> commands) {
        _commands = commands;
        setMouseHandler("click", this::mouseClicked);
        setPreferredSize(BOARD_SIDE, BOARD_SIDE);

        try {
            _whiteQueen = ImageIO.read(Utils.getResource(WHITE_QUEEN_IMAGE));
            _blackQueen = ImageIO.read(Utils.getResource(BLACK_QUEEN_IMAGE));
        } catch (IOException excp) {
            System.err.println("Could not read queen images.");
            System.exit(1);
        }
        _acceptingMoves = true;
    }

    /** Draw the bare board G.  */
    private void drawGrid(Graphics2D g) {
        g.setColor(LIGHT_SQUARE_COLOR);
        g.fillRect(0, 0, BOARD_SIDE, BOARD_SIDE);
        g.setColor(DARK_SQUARE_COLOR);
        boolean draw = true;
        for (int i = 0; i < 10; i += 1) {
            for (int j = 0; j < 10; j += 1) {
                if ((i + j) % 2 == 1) {
                    g.fillRect(SQUARE_SIDE * i,
                            SQUARE_SIDE * j, SQUARE_SIDE, SQUARE_SIDE);
                }
                draw = !draw;
            }
        }

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        for (int k = 0; k <= 10; k++) {
            g.drawLine(SQUARE_SIDE * k, 0, SQUARE_SIDE * k, BOARD_SIDE);
            g.drawLine(0, SQUARE_SIDE * k, BOARD_SIDE, SQUARE_SIDE * k);
        }
    }

    @Override
    public synchronized void paintComponent(Graphics2D g) {
        drawGrid(g);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (_board.boardgetter()[i][j].equals("W")) {
                    drawQueen(g, Square.sq((9 * 11)
                            - (i * 10 + (9 - j))), WHITE);
                } else if (_board.boardgetter()[i][j].equals("B")) {
                    drawQueen(g, Square.sq((9 * 11)
                            - (i * 10 + (9 - j))), BLACK);
                } else if (_board.boardgetter()[i][j].equals("S")) {
                    g.setColor(SPEAR_COLOR);
                    g.fillRect(cy(9 - j), cx(i), SQUARE_SIDE, SQUARE_SIDE);
                }
            }
        }
        if (_holder.size() >= 1) {
            Square s = _holder.get(0);
            g.setColor(CLICK1);
            g.fillRect(cx(s.col()), cy(s.row() - 1), SQUARE_SIDE, SQUARE_SIDE);
        }
        if (_holder.size() >= 2) {
            if (_holder.get(1) == _holder.get(0)) {
                _holder.clear();
            } else {
                Square s = _holder.get(1);
                g.setColor(CLICK2);
                g.fillRect(cx(s.col()), cy(s.row() - 1),
                        SQUARE_SIDE, SQUARE_SIDE);
            }
        }
        if (_holder.size() == 3 && _holder.get(2) == _holder.get(1)) {
            _holder.clear();
        }
        repaint();
    }

    /** Draw a queen for side PIECE at square S on G.  */
    private void drawQueen(Graphics2D g, Square s, Piece piece) {
        g.drawImage(piece == WHITE ? _whiteQueen : _blackQueen,
                    cx(s.col()) + 2, cy(s.row() - 1) + 4, null);
    }

    /** Handle a click on S. */
    private void click(Square s) {
        _holder.add(s);
        String move = "";
        if (_holder.size() == 3) {
            for (int i = 0; i < _holder.size(); i++) {
                if (i == 0) {
                    move += _holder.get(i).toString() + "-";
                } else if (i == 1) {
                    move += _holder.get(i).toString() + "(";
                } else {
                    move += _holder.get(i).toString() + ")";
                }
            }
            if (mv(move) != null) {
                _commands.offer(move);
            }
            _holder.clear();
        }
        repaint();
    }

    /** Handle mouse click event E. */
    private synchronized void mouseClicked(String unused, MouseEvent e) {
        int xpos = e.getX(), ypos = e.getY();
        int x = xpos / SQUARE_SIDE,
            y = (BOARD_SIDE - ypos) / SQUARE_SIDE;
        if (_acceptingMoves
            && x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE) {
            click(sq(x, y));
        }
    }

    /** Revise the displayed board according to BOARD. */
    synchronized void update(Board board) {
        _board.copy(board);
        repaint();
    }

    /** Turn on move collection iff COLLECTING, and clear any current
     *  partial selection.   When move collection is off, ignore clicks on
     *  the board. */
    void setMoveCollection(boolean collecting) {
        _acceptingMoves = collecting;
        repaint();
    }

    /** Return x-pixel coordinate of the left corners of column X
     *  relative to the upper-left corner of the board. */
    private int cx(int x) {
        return x * SQUARE_SIDE;
    }

    /** Return y-pixel coordinate of the upper corners of row Y
     *  relative to the upper-left corner of the board. */
    private int cy(int y) {
        return (Board.SIZE - y - 1) * SQUARE_SIDE;
    }

    /** Return x-pixel coordinate of the left corner of S
     *  relative to the upper-left corner of the board. */
    private int cx(Square s) {
        return cx(9 - s.col());
    }

    /** Return y-pixel coordinate of the upper corner of S
     *  relative to the upper-left corner of the board. */
    private int cy(Square s) {
        return cy(s.row() - 1);
    }

    /** Queue on which to post move commands (from mouse clicks). */
    private ArrayBlockingQueue<String> _commands;

    /** Board being displayed. */
    private final Board _board = new Board();

    /** Image of white queen. */
    private BufferedImage _whiteQueen;
    /** Image of black queen. */
    private BufferedImage _blackQueen;

    /** True iff accepting moves from user. */
    private boolean _acceptingMoves;

    /** Commands. */
    private ArrayList<Square> _holder = new ArrayList<Square>();
}
