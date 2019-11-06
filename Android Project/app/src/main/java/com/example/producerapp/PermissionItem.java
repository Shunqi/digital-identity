package com.example.producerapp;

public class PermissionItem {

    String category;
    boolean readbox;
    boolean writebox;
    boolean sharebox;

    PermissionItem(String category, boolean readbox, boolean writebox, boolean sharebox){
        this.category = category;
        this.readbox = readbox;
        this.writebox = writebox;
        this.sharebox = sharebox;
    }
}
