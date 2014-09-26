package com.github.sudukosolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Internal class to represent a single cell on a sudoku board
 * 
 * @author matt
 */
public class Cell extends AbstractCell implements Comparable<Cell> {

    // If this value was specified when Square was initialized then isFinal is
    // true
    private boolean isFinal;

    public Cell(int x, int y, int value) {
        super(x, y, value);
        isFinal = value != 0;
    }

    public boolean isSet() {
        return this.value != 0;
    }

    @Override
    public void reset() {
        setValue(0);
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public boolean isValid() {
        return isRowValid() && isColumnValid() && isNonetValid();
    }

    @Override
    public int compareTo(Cell c) {
        int yComp = new Integer(y).compareTo(c.getY());
        if (yComp == 0) {
            return new Integer(x).compareTo(c.getX());
        }
        return yComp;
    }

}