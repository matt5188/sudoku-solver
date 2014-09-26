package com.github.sudukosolver;

import java.util.ArrayList;
import java.util.List;

class KillerCell extends Cell {

    private List<KillerCell> cage;
    private int cageTotal;

    public KillerCell(int x, int y, int value) {
        super(x, y, value);
        cage = new ArrayList<>();
    }

    public void setTotal(int total) {
        this.cageTotal = total;
    }

    public void setCage(List<KillerCell> cage) {
        this.cage = cage;
    }

    @Override
    public boolean isValid() {
        return isCageValid() && super.isValid();
    }

    private boolean isCageValid() {
        return cage.stream().anyMatch(c -> c.getValue() == 0)
                || cageTotal == cage.stream().mapToInt(c -> c.getValue()).sum();
    }

    @Override
    public boolean isFinal() {
        return false;
    }


}
