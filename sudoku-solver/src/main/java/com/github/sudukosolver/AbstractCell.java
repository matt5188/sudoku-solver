package com.github.sudukosolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCell {
    // x, y coordinate of square, 0,0 is top left
    protected int x, y, value;
    // Local row
    protected List<? extends AbstractCell> row = new ArrayList<>();
    // Local column
    protected List<? extends AbstractCell> column = new ArrayList<>();
    // Local grid
    protected List<? extends AbstractCell> localNonet = new LinkedList<>();

    public AbstractCell(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<? extends AbstractCell> getGrid() {
        return localNonet;
    }

    public List<? extends AbstractCell> getColumn() {
        return column;
    }

    public List<? extends AbstractCell> getRow() {
        return row;
    }

    public  boolean isEmpty() {
        return value == 0;
    }
    
    // Some are final so we know that the value should not be changed
    public abstract boolean isFinal();
    
    public void setRow(List<? extends AbstractCell> row) {
        this.row = row;
    }

    public <E extends AbstractCell> void setColumn(List<E> column) {
        this.column = column;
    }

    public <E extends AbstractCell> void setLocalNonet(List<E> grid) {
        this.localNonet = grid;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Square{" + "x=" + x + "y=" + y + "value=" + value + '}';
    }

    public abstract boolean isValid();
    
    public abstract void reset();

    protected boolean valid(List<? extends AbstractCell> cells) {
        return cells.stream().filter(s -> !this.equals(s))
                .noneMatch(s -> s.getValue() == this.value);
    }

    protected boolean isNonetValid() {
       return valid(this.localNonet);
    }

    protected boolean isColumnValid() {
        return valid(this.column);
    }

    protected boolean isRowValid() {
        return valid(this.row);
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
}
