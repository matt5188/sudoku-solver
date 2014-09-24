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
public class Sudoku {
    // Internal representation of the board
    private List<Cell> board;
    private boolean isComplete;
    // Milliseconds that this puzzle took to solve, or -1 if unsolved
    private long timeToSolve;
    // How many combonation of values were tried when solving the puzzle
    private int combosTried;

    private Sudoku() {
        board = new ArrayList<Cell>(81);
        timeToSolve = -1;
        isComplete = false;
        combosTried = 0;
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


    /* Printing */
    
    /**
     * Print statistics about the sudoku , the time taken to complete and
     * combonations tried to complete
     * 
     * @param out
     *            Output stream
     * @throws IOException 
     */
    public void printStatistics(Appendable out) throws IOException {
        print("Time taken to solve : " + getTimeToSolve() + "ms", out);
        print("\nCombonations tried : " + getCombosTried() + "\n", out);
    }

    /**
     * Print representation of this Suduko puzzle to standard output a each
     * nonet will be separated by double line, otherwise cells will be separated
     * by single line
     * @throws IOException 
     * 
     */
    public void print(Appendable out) throws IOException {
        printTop(out);
        printDoubleVerticleLine(out);
        print(String.valueOf(board.get(0).getValue()), out);
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
            print(String.valueOf(board.get(i).getValue()), out);
        }
        printDoubleVerticleLine(out);
        printBottom(out);
    }

    private void printTop(Appendable out) throws IOException {
        print(" ╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗ \n", out);
    }

    private void printBottom(Appendable out) throws IOException {
        print("\n ╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝ \n", out);
    }

    private void printSingleVerticleLine(Appendable out) throws IOException {
        print(" │ ", out);
    }

    private void printDoubleVerticleLine(Appendable out) throws IOException {
        print(" ║ ", out);
    }

    private void printDoubleHorizontalLine(Appendable out) throws IOException {
        print("\n ╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣ \n", out);
    }
    
    private void printSingleHorizontalLine(Appendable out) throws IOException {
        print("\n ╟───┼───┼───╫───┼───┼───╫───┼───┼───╢ \n", out);
    }

    private void print(String value, Appendable out) throws IOException {
        out.append(value);
    }

    /**
     * 
     * @param args Single argument of a file path to a Sudoku to solve. 
     * File must be in format of where each line has 9 0-9 digits separated 
     * by a comma. 0 means an unknown value. There should be 9 lines of 9 
     * as per diagram below 
     * 
     *  0,0,0,9,2,0,0,0,4
     *  0,7,0,0,0,0,8,5,0
     *  0,0,0,6,0,5,0,0,0
     *  4,0,0,8,0,0,3,0,5
     *  5,0,0,0,0,0,0,0,1
     *  2,0,7,0,0,1,0,0,6
     *  0,0,0,4,0,8,0,0,0
     *  0,3,2,0,0,0,0,4,0
     *  6,0,0,0,1,3,0,0,0
     *   
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            throw new IllegalArgumentException("Must specify file path");
        }
        Sudoku sudoku = null;
        try {
            sudoku = Sudoku.create(args[0]);
        } catch (Exception e) {
            System.err.println("Error creating Sudoku " + e.getMessage());
            System.exit(0);
        }

        Scanner scan = new Scanner(System.in);

        printUsage();
        
        try {
            while (true) {
                System.out.println("Enter argument");
                String argument = scan.next();
                switch (argument) {
                case "s":
                    sudoku.solve();
                    System.out.println(sudoku.isComplete());
                    break;
                case "t":
                    sudoku.printStatistics(System.out);
                    break;
                case "p":
                    sudoku.print(System.out);
                    break;
                case "r":
                    sudoku.reset();
                    break;
                case "o":
                    printUsage();
                    break;
                case "x":
                    scan.close();
                    System.exit(0);
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing Sudoku" + e.getMessage());
            System.exit(0);
        }finally{
            scan.close();
        }
    }

    private static void printUsage() {
        System.out.print("Usage\n");
        System.out.println(
            "s : solve Sudoku. Prints true is solved or false otherwise"
        );
        System.out.println("t : print statistics about Sudoko");
        System.out.println("p : print Sudoku");
        System.out.println("r : reset the Sudoku");
        System.out.println("o : print options");
        System.out.println("x : exit");
    }

    private static final String FILE_DELIMITER = ",";
    private static final int ROW_SIZE = 9;
    private static final int ROW_COUNT = 9;
}

