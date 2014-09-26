package com.github.sudukosolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class to solve Sudoku puzzles by loading a puzzle form a file.
 * Ability to print Sudoku and statistics.
 * 
 * @author Matt
 *
 */
public class Sudoku extends AbstractSuduko<Cell> {

    protected Sudoku() {
//        board = new ArrayList<>(81);
//        timeToSolve = -1;
//        isComplete = false;
//        combosTried = 0;
    }

    /**
     * file path to a Sudoku to solve. File must be in format of where each line
     * has 9 0-9 digits separated by a comma. 0 means an unknown value. There
     * should be 9 lines of 9 as per diagram below.
     * 
     * 
     *   0,0,0,9,2,0,0,0,4 
     *   0,7,0,0,0,0,8,5,0 
     *   0,0,0,6,0,5,0,0,0 
     *   4,0,0,8,0,0,3,0,5
     *   5,0,0,0,0,0,0,0,1 
     *   2,0,7,0,0,1,0,0,6 
     *   0,0,0,4,0,8,0,0,0 
     *   0,3,2,0,0,0,0,4,0
     *   6,0,0,0,1,3,0,0,0
     * 
     * @param filepath
     *            absolute path to file containing Sudoku
     * @return Sudoku object representing file
     * @throws FileNotFoundException
     *             when the is file not found
     */
    public static Sudoku create(String filepath) throws FileNotFoundException {
        Sudoku sudoku = new Sudoku();
        sudoku.populate(filepath);
        
        return sudoku;
    }

    protected void populate(String filepath) {
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
        Scanner scan;
        try {
            scan = new Scanner(new File(filepath));
        } catch (FileNotFoundException e1) {
            throw new RuntimeException(e1);
        }

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
     * Check if this Sudoku has been solved. A solved Sudoku has values 1..9 in
     * each column, row and local grid only once.
     * 
     * @return true is this sudoku has been solved false if it has not been
     *         solved.
     */
    public boolean isComplete() {
        return board.stream().allMatch(c -> c.isValid());
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
        board.stream().filter(c -> !c.isFinal()).forEach(c -> c.setValue(0));
        isComplete = false;
        timeToSolve = -1;
        combosTried = 0;
    }

    /**
     * How many permutations of values were attempted when completing this
     * Sudoku
     * 
     * @return int value of count of permutations or 0 if unsolved
     */
    public int getCombosTried() {
        return combosTried;
    }


}

