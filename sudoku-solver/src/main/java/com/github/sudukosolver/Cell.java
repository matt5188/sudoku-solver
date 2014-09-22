/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sudukosolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *  Internal class to represent a single cell on a suduko board
 * @author matt
 */
class Cell implements Comparable<Cell> {

    //x, y coordinate of square, 0,0 is top left
    private int x, y, value;
    //Local row
    private List<Cell> row = new ArrayList<Cell>();
    //Local column
    private List<Cell> column = new ArrayList<Cell>();
    //Local grid
    private List<Cell> localNonet = new LinkedList<Cell>();
    //If this value was specified when Square was initialized then isFinal is true
    private boolean isFinal;

    public Cell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        if (value != 0) {
            isFinal = true;
        }
    }

    public void addRowCell(Cell square) {
        this.row.add(square);
    }

    public void addColumnSquare(Cell square) {
        this.column.add(square);
    }

    private void addNonetCell(Cell square) {
        this.localNonet.add(square);
    }

    private int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Cell> getGrid() {
        return localNonet;
    }

    public List<Cell> getColumn() {
        return column;
    }

    public List<Cell> getRow() {
        return row;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public void setRow(List<Cell> row) {
        this.row = row;
    }

    public void setColumn(List<Cell> column) {
        this.column = column;
    }

    public void setLocalNonet(List<Cell> grid) {
        this.localNonet = grid;
    }

    @Override
    public String toString() {
        return "Square{" + "x=" + x + "y=" + y + "value=" + value + '}';
    }

    public int compareTo(Cell o) {
        int yComp = new Integer(y).compareTo(o.getY());
        if (yComp == 0) {
            return new Integer(x).compareTo(o.getX());
        }
        return yComp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell other = (Cell) obj;
            if (other.x == this.x && other.y == this.y) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }

    public boolean isValid() {
        if (!isSet()) {
            return false;
        }
        for (Cell s : column) {
            if (this.equals(s)) {
                continue;
            }
            if (s.value == value) {
                return false;
            }
        }
        for (Cell s : localNonet) {
            if (this.equals(s)) {
                continue;
            }
            if (s.value == value) {
                return false;
            }
        }
        for (Cell s : row) {
            if (this.equals(s)) {
                continue;
            }
            if (s.value == value) {
                return false;
            }
        }
        return true;
    }

    public boolean isSet() {
        return this.value != 0;
    }

    public void reset() {
        setValue(0);
    }

    void setValue(int currentValue) {
        this.value = currentValue;
    }

    boolean isFinal() {
        return isFinal;
    }

    public void setIsFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public int getValue() {
        return value;
    }
}

