package com.github.sudukosolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.sudukosolver.print.Printer;
import com.github.sudukosolver.print.SudokuPrinter;

public abstract class AbstractSuduko<E extends AbstractCell> {
    protected static final String FILE_DELIMITER = ",";
    
    private static final int ROW_SIZE = 9;
    private static final int ROW_COUNT = 9;

    protected List<E> board;
    
    protected boolean isComplete;
    
    // Milliseconds that this puzzle took to solve, or -1 if unsolved
    protected long timeToSolve;
    
    // How many combonation of values were tried when solving the puzzle
    protected int combosTried;

    // Populate Sudoko
    protected abstract void populate(String filePath);
    
    // Is Sudoku complete?
    public abstract boolean isComplete();
    
    private Printer<AbstractSuduko<?>> printer = new SudokuPrinter<AbstractSuduko<?>>();
    
    
    public AbstractSuduko() {
        board = new ArrayList<>(81);
        timeToSolve = -1;
        isComplete = false;
        combosTried = 0;
        printer.printTarget(this);
    }
    /**
     * Attempt to solve this Sudoku. Use <code>isComplete()</code> to check if
     * puzzle was solved.
     * This method attempts to solve the puzzle by trying all possible combinations
     * starting from the top left cell and advancing left to right returning the first
     * correct solution. Therefore there may be other solutions to the puzzle.
     */
    public void solve() {
        if (board.isEmpty()) {
            throw new RuntimeException("Must specify puzzle to solve");
        }
        if (isComplete) {
            return;
        }
        combosTried = 0;
        long start = System.currentTimeMillis();
        isComplete = solve(0);
        timeToSolve = isComplete ? System.currentTimeMillis() - start : -1;
    }
    
    private boolean solve(int index) {
        // Reached the last square on the board, check if puzzle complete
        if (index == board.size()) {
            return isComplete();
        }
        E s = board.get(index);
        // Do not alter 'final' values
        if (s.isFinal()) {
            return solve(index + 1);
        } else {
            // Attempt each number 1 - 9 for each square
            for (int i = 1; i <= 9; i++) {
                s.setValue(i);
                combosTried++;
                if(combosTried % 6000000 == 0 ){
                    try {
                        printer.print(System.out);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                // If this is a valid value move on to the next square
                if (s.isValid()) {
                    boolean done = solve(index + 1);
                    if (done) {
                        return true;
                    }
                }
            }
            // When all attempts fail for this square, reset and return
            s.setValue(0);
            return false;
        }
    }

    public static int getRowCount() {
        return ROW_COUNT;
    }

    public static int getRowSize() {
        return ROW_SIZE;
    }
    
    public List<E> getBoard(){
        return Collections.unmodifiableList(board);
    }
    
    public void reset() {
        board.stream().filter(c -> !c.isFinal()).forEach(c -> c.setValue(0));
        isComplete = false;
        timeToSolve = -1;
        combosTried = 0;
    }

}
