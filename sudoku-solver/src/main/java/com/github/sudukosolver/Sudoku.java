/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sudukosolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Matt
 *
 */
public class Sudoku {

    private static final String FILE_DELIMITER = ",";
    // Internal representation of the board
    private List<Square> board = new ArrayList<Square>(81);
    private boolean isComplete;
    // Milliseconds that this puzzle took to solve, or -1 if unsolved
    private long timeToSolve;
    // How many combonation of values were tried when solving the puzzle
    private int combosTried;

    private Sudoku() {
        isComplete = false;
        combosTried = 0;
    }

    public static Sudoku create(String filepath) throws FileNotFoundException {
        Sudoku sudoku = new Sudoku();
        sudoku.populate(filepath);

        return sudoku;
    }

    void populate(String filepath) throws FileNotFoundException {
        /*
         * Each 'square' has a local column, local grid and local column There
         * are 9 columns, 9 rows and 9 grids Each square is in exactly one of
         * each Index to column and row is maintained by X and Y coordinate of
         * square, origin (0,0) is to top left. Local grid is indexed by
         * (x/3)+((y/3)*3) which means index will move 1 value every 3
         * iterations across the y or x axis ;
         */
        List<List<Square>> column = new ArrayList<List<Square>>(9);
        List<List<Square>> grid = new ArrayList<List<Square>>(9);
        List<List<Square>> rows = new ArrayList<List<Square>>(9);

        Scanner scan = new Scanner(new File(filepath));

        for (int i = 0; i < 9; i++) {
            column.add(new ArrayList<Square>());
            grid.add(new ArrayList<Square>());
        }

        int gridIndex = 0;
        for (int y = 0; y < 9; y++) {
            List<Square> currentRow = new ArrayList<Square>();
            rows.add(currentRow);
            String[] nextRow = scan.nextLine().split(FILE_DELIMITER);
            for (int x = 0; x < 9; x++) {

                gridIndex = (x / 3) + ((y / 3) * 3);
                List<Square> currentColumn = column.get(x);
                List<Square> currentGrid = grid.get(gridIndex);

                int value = 0;
                try {
                    value = Integer.parseInt(nextRow[x]);
                } catch (NumberFormatException e) {
                }

                // Create a new square to represent this location on the board
                Square s = new Square(y, x, value);
                // Keep a pointer to squares local to this (row, local grid,
                // column)
                s.setRow(currentRow);
                s.setColumn(currentColumn);
                s.setLocalGrid(currentGrid);

                // Add square to local row, column and grid
                currentRow.add(s);
                currentColumn.add(s);
                currentGrid.add(s);

                board.add(s);
            }
        }

        scan.close();
    }

    /**
     * Attempt to solve this Sudoku. Use <code>isComplete()</code> to check if
     * puzzle was solved.
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
        Square s = board.get(index);
        // Do not alter 'final' values
        if (s.isFinal()) {
            return solve(index + 1);
        } else {
            // Attempt each number 1 - 9 for each square
            for (int i = 1; i <= 9; i++) {
                s.setValue(i);
                combosTried++;
                // If this is a valid value move on to the next square
                if (s.isValid()) {
                    boolean done = solve(index + 1);
                    if (done) {
                        return true;
                    }
                }
            }
            // When all attempts fail for this square, reset and return up stack
            s.setValue(0);
            return false;
        }
    }

    /**
     * Check if this Sudoku has been solved. A solved Sudoku has values 1..9 in
     * each column, row and local grid only once.
     * 
     * @return true is this sudoku has been solved false if it has not been
     *         solved.
     */
    public boolean isComplete() {
        for (Square s : board) {
            if (!s.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Print representation of this Suduko puzzle to standard output
     */
    public void print() {
        // TODO: Introduce Outputstream parameter, make local grids visible
        System.out.print(" ------------------------------------- \n");
        for (int i = 0; i < board.size(); i++) {
            if (i % 9 == 0 && i != 0) {
                System.out.print(" |\n");
                System.out.print(" ------------------------------------- \n");
            }
            System.out.print(" | " + board.get(i).getValue());
            if (i == board.size() - 1) {
                System.out.print(" | ");
            }
        }
        System.out.print("\n ------------------------------------- \n");
    }

    /**
     * Milliseconds that the puzzle took to solve 
     * @return long representation of milliseconds that the puzzle took to solve 
     * or -1 if unsolved.
     */
    public long getTimeToSolve() {
        return timeToSolve;
    }

    /**
     * Reset this Suduko
     */
    public void reset() {
        for (Square s : board) {
            if (!s.isFinal()) {
                s.setValue(0);
            }
            isComplete = false;
            timeToSolve = -1;
        }
    }

    /**
     * How many permutations of values were attempted when completing this
     * Suduko
     * 
     * @return int value of count of permutations or 0 if unsolved
     */
    public int getCombosTried() {
        return combosTried;
    }
}
