package com.fertility.util;

import java.util.Objects;

public class Vector2 {
    int x;
    int y;
    public Vector2(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object other){
        if (other == null || other.getClass() != this.getClass())
            return false;
        if (this == other)
            return true;
        Vector2 vec = (Vector2) other;
        return x == vec.x && y == vec.y;
    }

    @Override
    public String toString(){
        return "[" + x + ", " + y + "]";
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }
}
