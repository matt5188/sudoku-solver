/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sudukosolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author matt
 */
class Square implements Comparable<Square> {

    private int x, y, value;
    //Local row
    private List<Square> row = new ArrayList<Square>();
    //Local column
    private List<Square> column = new ArrayList<Square>();
    //Local grid
    private List<Square> localGrid = new LinkedList<Square>();
    //If this value was specified when Square was initialized then isFinal is true
    private boolean isFinal;

    public Square(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
        if (value != 0) {
            isFinal = true;
        }
    }

    public void addRowSquare(Square square) {
        this.row.add(square);
    }

    public void addColumnSquare(Square square) {
        this.column.add(square);
    }

    private void addGridSquare(Square square) {
        this.localGrid.add(square);
    }

    private int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Square> getGrid() {
        return localGrid;
    }

    public List<Square> getColumn() {
        return column;
    }

    public List<Square> getRow() {
        return row;
    }

    public boolean isEmpty() {
        return value == 0;
    }

    public void setRow(List<Square> row) {
        this.row = row;
    }

    public void setColumn(List<Square> column) {
        this.column = column;
    }

    public void setLocalGrid(List<Square> grid) {
        this.localGrid = grid;
    }

    @Override
    public String toString() {
        return "Square{" + "x=" + x + "y=" + y + "value=" + value + '}';
    }

    public int compareTo(Square o) {
        int xComp = new Integer(x).compareTo(o.getX());
        if (xComp == 0) {
            return new Integer(y).compareTo(o.getY());
        }
        return xComp;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Square) {
            Square other = (Square) obj;
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
        for (Square s : column) {
            if (this.equals(s)) {
                continue;
            }
            if (s.value == value) {
                return false;
            }
        }
        for (Square s : localGrid) {
            if (this.equals(s)) {
                continue;
            }
            if (s.value == value) {
                return false;
            }
        }
        for (Square s : row) {
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
