package amazons;

import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;

import static amazons.Piece.*;
import static amazons.Move.mv;


/** The state of an Amazons Game.
 *  @author William Tai
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;

    /** the number of moves. */
    private int _numMoves = 0;

    /** the stack of undos. */
    private Stack<Move> myStack = new Stack<Move>();

    /** gets the stack of undos.
     *
     * @return s the stack.
     * */
    Stack getStack() {
        return myStack;
    }

    /** the board all of it. */
    private String[][] board = new String[][] {
            {"-", "-", "-", "B", "-", "-", "B", "-", "-", "-"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"B", "-", "-", "-", "-", "-", "-", "-", "-", "B"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"W", "-", "-", "-", "-", "-", "-", "-", "-", "W"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
            {"-", "-", "-", "W", "-", "-", "W", "-", "-", "-"}
    };

    /** gets the board.
     *
     * @return s the board.
     * */
    String[][] boardgetter() {
        return board;
    }

    /** Definitions of direction for queenMove.  DIR[k] = (dcol, drow)
     *  means that to going one step from (col, row) in direction k,
     *  brings us to (col + dcol, row + drow). */
    private static final int[][] DIR = {
            { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 },
            { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }
    };

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        if (model == this) {
            return;
        }
        this.board = new String[model.SIZE][model.SIZE];
        for (int i = 0; i < model.board.length; i++) {
            System.arraycopy(model.board[i], 0,
                    this.board[i], 0, model.board[i].length);
        }
        this._turn = model.turn();
        this._winner = model._winner;
    }

    /** Clears the board to the initial position. */
    void init() {
        board = new String[][] {
                {"-", "-", "-", "B", "-", "-", "B", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"B", "-", "-", "-", "-", "-", "-", "-", "-", "B"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"W", "-", "-", "-", "-", "-", "-", "-", "-", "W"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "-", "-", "-", "-", "-", "-", "-"},
                {"-", "-", "-", "W", "-", "-", "W", "-", "-", "-"}
        };
        _numMoves = 0;
        _turn = WHITE;
        _winner = EMPTY;
    }

    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return _numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        int count = 0;
        Iterator<Square> allSquares = Square.iterator();
        while (allSquares.hasNext()) {
            Square s = allSquares.next();
            if (board[SIZE - s.row()][s.col()].equals(turn().toString())) {
                for (int i = 0; i < 8; i++) {
                    if (!isLegal(s, s.queenMove(i, 1))) {
                        count += 1;
                    } else {
                        return null;
                    }
                }
            }
        }
        if (count == 32) {
            _winner = turn();
            return _winner;
        } else {
            return null;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        switch (board[SIZE - row][col]) {
        case "-": return EMPTY;
        case "B": return BLACK;
        case "W": return WHITE;
        case "S": return SPEAR;
        default: throw new NoSuchElementException();
        }
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        board[SIZE - row][col] = p.toString();
        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        int distance = 0;
        if (to != null && from != null && from.isQueenMove(to)) {
            int dir = from.direction(to);
            int colChange = DIR[dir][0];
            int rowChange = DIR[dir][1];
            if (dir == 0 || dir == 4) {
                distance = Math.abs(to.row() - from.row());
            } else {
                distance = Math.abs(to.col() - from.col());
            }
            for (int i = 1; i < distance + 1; i++) {
                int r = SIZE - from.row() - rowChange * i;
                int c = from.col() + colChange * i;
                if (r < 0 || c < 0) {
                    return false;
                }
                if (asEmpty == null) {
                    if (!board[r][c].equals("-")) {
                        return false;
                    }
                } else {
                    if (!board[r][c].equals("-")) {
                        if (r != SIZE - asEmpty.row() || c != asEmpty.col()) {
                            return false;
                        }
                    }
                }
            }
        }
        if (to == null || from == null) {
            return false;
        }
        return true;
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        Piece p = get(from);
        return p == turn();
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        return from != null && to != null && isUnblockedMove(from, to, null);
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        return from != null && to != null && spear != null
                && isLegal(from) && isUnblockedMove(from, to, null)
                && isUnblockedMove(to, spear, from);
    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (isLegal(from) && isLegal(from, to, spear)) {
            board[SIZE - to.row()][to.col()] = get(from).toString();
            board[SIZE - from.row()][from.col()] = "-";
            board[SIZE - spear.row()][spear.col()] = "S";
            myStack.add(mv(from, to, spear));
            _turn = _turn.opponent();
            _numMoves += 1;
        }
    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (!myStack.empty()) {
            Move remove = myStack.pop();
            board[SIZE - remove.spear().row()][remove.spear().col()] = "-";
            board[SIZE - remove.from().row()]
                    [remove.from().col()] = get(remove.to()).toString();
            board[SIZE - remove.to().row()][remove.to().col()] = "-";
            _numMoves -= 1;
            _turn = _turn.opponent();
        }
    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
     *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = -1;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            Square to = _from.queenMove(_dir, _steps);
            toNext();
            return to;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            if (_dir == -1) {
                _dir = 0;
            }
            Square sub = _from.queenMove(_dir, _steps + 1);
            if (sub == null || !isUnblockedMove(_from, sub, _asEmpty)) {
                _dir += 1;
                _steps = 0;
                if (hasNext()) {
                    toNext();
                }
            } else {
                _steps += 1;
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _startingSquares = Square.iterator();
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _startingSquares.hasNext();
        }

        @Override
        public Move next() {
            if (!_spearThrows.hasNext()) {
                if (!_pieceMoves.hasNext()) {
                    toNext();
                }
                _nextSquare = _pieceMoves.next();
                _spearThrows = reachableFrom(_nextSquare, _start);
            }
            Move m = mv(_start, _nextSquare, _spearThrows.next());
            if (!_pieceMoves.hasNext()) {
                toNext();
            }
            return m;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (_startingSquares.hasNext()) {
                _start = _startingSquares.next();
                if (get(_start.col(), _start.row()) == _fromPiece) {
                    temp = Square.sq(_start.col(), _start.row() - 1);
                    _pieceMoves = reachableFrom(_start, null);
                    if (_pieceMoves.hasNext()) {
                        _nextSquare = _pieceMoves.next();
                        _spearThrows = reachableFrom(_nextSquare, _start);
                    } else {
                        toNext();
                    }
                } else if (_startingSquares.hasNext()) {
                    toNext();
                } else {
                    _start = temp;
                }
            }
        }

        /** a temporary holder. */
        private Square temp;
        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Remaining starting squares to consider. */
        private Iterator<Square> _startingSquares;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {
        String boardString = "";
        for (int i = 0; i < SIZE; i++) {
            boardString += "   ";
            for (int j = 0; j < SIZE; j++) {
                boardString += board[i][j];
                if (j < SIZE - 1) {
                    boardString += " ";
                }
            }
            boardString += "\n";
        }
        return boardString;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
