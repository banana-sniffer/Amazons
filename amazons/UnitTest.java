package amazons;

import org.junit.Test;

import static amazons.Piece.*;
import static org.junit.Assert.*;
import ucb.junit.textui;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Set;

import static amazons.Move.mv;

/** The suite of all JUnit tests for the amazons package.
 *  @author William Tai
 */

public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 6), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 10), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 6), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 8)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    @Test
    public void bigAssTest() {
        Board b = new Board();
        b.put(WHITE, Square.sq(5, 4));
        b.put(WHITE, Square.sq(5, 6));
        b.put(WHITE, Square.sq(7, 6));
        b.put(WHITE, Square.sq(7, 4));
        b.put(WHITE, Square.sq(7, 2));
        b.put(WHITE, Square.sq(5, 2));
        b.put(WHITE, Square.sq(3, 2));
        b.put(WHITE, Square.sq(3, 4));
        b.put(WHITE, Square.sq(3, 6));
        Iterator<Square> tester;
        tester = b.reachableFrom(Square.sq(5, 4), null);
        ArrayList<Square> storage = new ArrayList<Square>();
        while (tester.hasNext()) {
            storage.add(tester.next());
        }
        assertEquals(8, storage.size());
        assertTrue(storage.contains(Square.sq("f6")));
        assertTrue(storage.contains(Square.sq("g6")));
        assertTrue(storage.contains(Square.sq("g4")));
        assertTrue(storage.contains(Square.sq("g5")));
        assertTrue(storage.contains(Square.sq("e4")));
        assertTrue(storage.contains(Square.sq("e5")));
        assertTrue(storage.contains(Square.sq("e6")));
        assertTrue(storage.contains(Square.sq("f4")));
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
                    "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
                    "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";

    /** Tests reachableFromIterator to make sure it returns all reachable
     *  Squares. This method may need to be changed based on
     *   your implementation. */
    @Test
    public void testReachableFrom() {
        Board b = new Board();
        buildBoard(b, REACHABLEFROMTESTBOARD);
        int numSquares = 0;
        Set<Square> squares = new HashSet<>();
        Iterator<Square> reachableFrom = b.reachableFrom(Square.sq(5, 4), null);
        while (reachableFrom.hasNext()) {
            Square s = reachableFrom.next();
            assertTrue(REACHABLEFROMTESTSQUARES.contains(s));
            numSquares += 1;
            squares.add(s);
        }
        assertEquals(REACHABLEFROMTESTSQUARES.size(), numSquares);
        assertEquals(REACHABLEFROMTESTSQUARES.size(), squares.size());
    }

    /** Tests legalMovesIterator to make sure it returns all legal Moves.
     *  This method needs to be finished and may need to be changed
     *  based on your implementation. */
    @Test
    public void testLegalMoves() {
        Board b = new Board();
        buildBoard(b, TESTER);
        ArrayList<Move> storage = new ArrayList<Move>();
        Iterator<Move> legalMoves = b.legalMoves(Piece.WHITE);
        while (legalMoves.hasNext()) {
            Move m = legalMoves.next();
            storage.add(m);
        }
        assertEquals(113, storage.size());
        assertFalse(storage.contains(null));

        ArrayList<Move> nothing = new ArrayList<Move>();
        Iterator<Move> noMoves = b.legalMoves(Piece.BLACK);
        while (noMoves.hasNext()) {
            Move m = noMoves.next();
        }
        assertEquals(0, nothing.size());
    }

    @Test
    public void testWinner() {
        Board b = new Board();
        buildBoard(b, WINNER);
        System.out.println(b.toString());
        assertNull(b.winner());
        b.makeMove(mv("d9-e9(f10)"));
        b.makeMove(mv("g10-g9(g10)"));
        b.makeMove(mv("e9-d9(e9)"));
        assertNotNull(b.winner());
    }


    private void buildBoard(Board b, Piece[][] target) {
        for (int col = 0; col < Board.SIZE; col++) {
            for (int row = Board.SIZE - 1; row >= 0; row--) {
                Piece piece = target[Board.SIZE - row - 1][col];
                b.put(piece, Square.sq(col, row));
            }
        }
    }

    static final Piece E = Piece.EMPTY;

    static final Piece W = Piece.WHITE;

    static final Piece B = Piece.BLACK;

    static final Piece S = Piece.SPEAR;

    static final Piece[][] REACHABLEFROMTESTBOARD =
    {{ E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, W, W },
        { E, E, E, E, E, E, E, S, E, S },
        { E, E, E, S, S, S, S, E, E, S },
        { E, E, E, S, E, E, E, E, B, E },
        { E, E, E, S, E, W, E, E, B, E },
        { E, E, E, S, S, S, B, W, B, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E },
        { E, E, E, E, E, E, E, E, E, E }
    };

    static final Piece[][] TESTER =
    {{ S, S, S, S, E, E, E, E, E, E },
        { S, S, E, E, E, E, E, E, E, E },
        { S, E, E, E, E, E, E, E, E, E },
        { E, S, S, E, E, E, E, E, E, E },
        { S, S, S, S, S, S, S, E, E, S },
        { S, S, E, S, S, S, S, E, S, W },
        { S, S, S, E, S, E, E, S, E, S },
        { S, B, S, S, E, S, S, E, E, S },
        { S, S, S, E, E, E, E, E, S, S },
        { S, S, S, S, E, E, S, E, S, S }};

    static final Piece[][] WINNER =
    {{ S, S, S, S, E, E, B, S, S, E },
        { S, S, S, W, E, S, E, S, E, E },
        { S, B, S, E, E, S, S, S, E, E },
        { S, S, S, E, E, E, E, E, E, E },
        { S, S, S, S, S, S, S, E, E, S },
        { B, S, E, S, S, S, S, E, S, W },
        { S, S, S, E, S, E, E, S, E, S },
        { S, B, S, S, E, S, S, E, E, S },
        { S, S, S, E, E, E, E, E, S, S },
        { S, S, S, S, E, E, S, E, S, S }};
    static final Set<Square> REACHABLEFROMTESTSQUARES =
            new HashSet<>(Arrays.asList(
                    Square.sq(5, 5),
                    Square.sq(4, 5),
                    Square.sq(4, 4),
                    Square.sq(6, 4),
                    Square.sq(7, 4),
                    Square.sq(6, 5),
                    Square.sq(7, 6),
                    Square.sq(8, 7)));
}
