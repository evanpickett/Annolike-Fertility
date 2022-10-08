package com.fertility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChunkData {
    public int version;
    public String data;
    public List<String> unraveledData;

    public void writeData(String data){
        this.data = data;
        String[] split = data.split("&");
        unraveledData = new ArrayList<>(Arrays.asList(split));
    }

    public ChunkData(int version, String data) {
        this.version = version;
        writeData(data);
    }

    @Override
    public String toString(){
        return "[" + this.version + "]: " + this.data;
    }
}
