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
    // Internal representation of the board
    private List<Cell> board = new ArrayList<Cell>(81);
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
        List<List<Cell>> column = new ArrayList<List<Cell>>(9);
        List<List<Cell>> grid = new ArrayList<List<Cell>>(9);
        List<List<Cell>> rows = new ArrayList<List<Cell>>(9);

        Scanner scan = new Scanner(new File(filepath));

        for (int i = 0; i < 9; i++) {
            column.add(new ArrayList<Cell>());
            grid.add(new ArrayList<Cell>());
        }

        int gridIndex = 0;
        for (int y = 0; y < 9; y++) {
            List<Cell> currentRow = new ArrayList<Cell>();
            rows.add(currentRow);
            String[] nextRow = scan.nextLine().split(FILE_DELIMITER);
            for (int x = 0; x < 9; x++) {

                gridIndex = (x / 3) + ((y / 3) * 3);
                List<Cell> currentColumn = column.get(x);
                List<Cell> currentNonet = grid.get(gridIndex);

                int value = 0;
                try {
                    value = Integer.parseInt(nextRow[x]);
                } catch (NumberFormatException e) {
                }

                // Create a new square to represent this location on the board
                Cell s = new Cell(y, x, value);
                // Keep a pointer to squares local to this (row, local grid,
                // column)
                s.setRow(currentRow);
                s.setColumn(currentColumn);
                s.setLocalNonet(currentNonet);

                // Add square to local row, column and grid
                currentRow.add(s);
                currentColumn.add(s);
                currentNonet.add(s);

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
        Cell s = board.get(index);
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
            // When all attempts fail for this square, reset and return
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
        for (Cell s : board) {
            if (!s.isValid()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Milliseconds that the puzzle took to solve
     * 
     * @return long representation of milliseconds that the puzzle took to solve
     *         or -1 if unsolved.
     */
    public long getTimeToSolve() {
        return timeToSolve;
    }

    /**
     * Reset this Suduko
     */
    public void reset() {
        for (Cell s : board) {
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
    

    /**
     * Print representation of this Suduko puzzle to standard output 
     * a each nonet will be separated by double line, otherwise
     * cells will be separated by single line
     * 
     *  ╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗ 
     *  ║ 8 │ 1 │ 5 ║ 9 │ 2 │ 7 ║ 6 │ 3 │ 4 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 9 │ 7 │ 6 ║ 1 │ 3 │ 4 ║ 8 │ 5 │ 2 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 3 │ 2 │ 4 ║ 6 │ 8 │ 5 ║ 7 │ 1 │ 9 ║ 
     *  ╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣ 
     *  ║ 4 │ 6 │ 1 ║ 8 │ 9 │ 2 ║ 3 │ 7 │ 5 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 5 │ 8 │ 3 ║ 7 │ 4 │ 6 ║ 9 │ 2 │ 1 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 2 │ 9 │ 7 ║ 3 │ 5 │ 1 ║ 4 │ 8 │ 6 ║ 
     *  ╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣ 
     *  ║ 1 │ 5 │ 9 ║ 4 │ 7 │ 8 ║ 2 │ 6 │ 3 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 7 │ 3 │ 2 ║ 5 │ 6 │ 9 ║ 1 │ 4 │ 8 ║ 
     *  ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ 
     *  ║ 6 │ 4 │ 8 ║ 2 │ 1 │ 3 ║ 5 │ 9 │ 7 ║ 
     *  ╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝ 
     * 
     */
    public void print(Appendable out) {
        print(" ╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗ \n", out);
        printDoubleVerticleLine(out);
        print(board.get(0).getValue(), out);
        for (int i = 1; i < board.size(); i++) {
            // New Row
            if (i % ROW_SIZE == 0) {
                printDoubleVerticleLine(out);
                // Every 3rd row double line
                if (i % (ROW_SIZE * 3) == 0) {
                    printDoubleHorizontalLine(out);
                } else {
                    printSingleHorizontalLine(out);
                }
                printDoubleVerticleLine(out);
            } else if (i % 3 == 0) {
                printDoubleVerticleLine(out);
            } else {
                printSingleVerticleLine(out);
            }
            print(board.get(i).getValue(), out);
        }
        printDoubleVerticleLine(out);
        print("\n ╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝ \n", out);
    }

    private void printSingleHorizontalLine(Appendable out) {
        print("\n ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ \n", out);
    }

    private void printSingleVerticleLine(Appendable out) {
        print(" │ ", out);
    }

    private void printDoubleVerticleLine(Appendable out) {
        print(" ║ ", out);
    }

    private void printDoubleHorizontalLine(Appendable out) {
        print("\n ╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣ \n", out);
    }

    private void print(Object value, Appendable out) {
        System.out.print(value);
    }


    public static void main(String[] args) {
        try {
            Sudoku s = Sudoku
                    .create("C:\\Users\\Matt\\Documents\\Sudoku\\1.txt");
            s.solve();
            s.print(System.out);
        } catch (FileNotFoundException e) {

        }
    }

    private static final String FILE_DELIMITER = ",";
    private static final int ROW_SIZE = 9;
    private static final int ROW_COUNT = 9;
}
