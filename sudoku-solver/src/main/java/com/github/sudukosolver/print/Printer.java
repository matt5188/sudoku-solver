package com.github.sudukosolver.print;

import java.io.IOException;

public interface Printer<T>  {

    public void printTarget(T t);
    
    public void print(Appendable out) throws IOException;
    
    public void printStatistics(Appendable out) throws IOException;
    
}
