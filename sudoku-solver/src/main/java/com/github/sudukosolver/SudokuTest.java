package com.github.sudukosolver;

import java.util.Scanner;

import com.github.sudukosolver.print.Printer;
import com.github.sudukosolver.print.SudokuPrinter;

public class SudokuTest {

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
        Printer<AbstractSuduko<?>> printer = new SudokuPrinter<AbstractSuduko<?>>();
        printer.printTarget(sudoku);
        gui(sudoku,printer,scan);
        
        Killer killer = null;
        
        try {
            killer = Killer.create(args[1]);
        } catch (Exception e) {
            System.err.println("Error creating Sudoku");
            e.printStackTrace(System.err);
            System.exit(0);
        }
        printUsage();
        printer.printTarget(killer);
        gui(killer,printer,scan);
        scan.close();
        System.exit(0);
        
    }

    private static void gui(AbstractSuduko<?> sudoku, Printer<?> printer, Scanner scan) {
        try {
            while (true) {
                System.out.println("Enter argument");
                String argument = scan.next();
                switch (argument) {
                case "s":
                    sudoku.solve();
                    System.out.println(sudoku.isComplete());
                    break;
                case "p":
                    printer.print(System.out);
                    break;
                case "r":
                    sudoku.reset();
                    break;
                case "o":
                    printUsage();
                    break;
                case "x":
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing Sudoku : " + e.getMessage());
            System.exit(0);
        }finally{
        }
    }

    private static void printUsage() {
        System.out.print("Usage\n");
        System.out.println(
            "s : solve Sudoku. Prints true is solved or false otherwise"
        );
        System.out.println("p : print Sudoku");
        System.out.println("r : reset the Sudoku");
        System.out.println("o : print options");
        System.out.println("x : exit");
    }
}
