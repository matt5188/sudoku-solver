package com.github.sudukosolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Killer extends AbstractSuduko<KillerCell> {

    public Killer() {
        super();
    }

    public static Killer create(String filepath) {
        Killer sudoku = new Killer();
        sudoku.populate(filepath);

        return sudoku;
    }
    
    @Override
    protected void populate(String filepath) {
        
        List<List<KillerCell>> column = new ArrayList<>(9);
        List<List<KillerCell>> grid = new ArrayList<>(9);
        List<List<KillerCell>> rows = new ArrayList<>(9);

        Scanner scan;
        try {
            scan = new Scanner(new File(filepath));
        } catch (FileNotFoundException e1) {
            throw new RuntimeException(e1);
        }
        for (int i = 0; i < 9; i++) {
            column.add(new ArrayList<>());
            grid.add(new ArrayList<>());
        }

        int gridIndex = 0;
        Map<String, Integer> values = new HashMap<>();
        Map<String, List<KillerCell>> cage = new HashMap<>();
        for (int y = 0; y < 9; y++) {
            
            List<KillerCell> currentRow = new ArrayList<KillerCell>();
            rows.add(currentRow);
            String[] nextRow = scan.nextLine().split(FILE_DELIMITER);

            for (int x = 0; x < 9; x++) {

                gridIndex = (x / 3) + ((y / 3) * 3);
                List<KillerCell> currentColumn = column.get(x);
                List<KillerCell> currentNonet = grid.get(gridIndex);

                // Create a new square to represent this location on the board
                KillerCell s = new KillerCell(y, x, 0);
                // Keep a pointer to squares local to this (row, local grid,
                // column)
                s.setRow(currentRow);
                s.setColumn(currentColumn);
                s.setLocalNonet(currentNonet);

                //Get the 'cage' this cell is in and add to list with other cells in same cage
                String key = nextRow[x];
                List<KillerCell> currentCage = cage.getOrDefault(key, new ArrayList<>());
                currentCage.add(s);
                cage.put(key, currentCage);
                s.setCage(currentCage);

                // Add square to local row, column and grid
                currentRow.add(s);
                currentColumn.add(s);
                currentNonet.add(s);

                board.add(s);
            }

        }

        while (scan.hasNextLine()) {
            String[] splitLine = scan.nextLine().split("=");
            values.put(splitLine[0], Integer.valueOf(splitLine[1]));
        }

        cage.forEach((k, v) -> v.forEach(_v -> _v.setTotal(values.get(k))));

        scan.close();
    }

    @Override
    public void solve() {
        super.solve();
    }

    @Override
    public boolean isComplete() {
        return board.stream().allMatch(c -> c.isValid());
    }
    
}
