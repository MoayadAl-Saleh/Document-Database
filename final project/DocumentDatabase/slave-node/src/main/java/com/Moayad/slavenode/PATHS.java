package com.Moayad.slavenode;

public enum PATHS
{
    //enum for storing paths
    SLAVE_NODE_PATH ("slave-node/Data/DatabaseFile/"), DATABASE_PATH ("slave-node/Data"),

    DATABASE_FILE ("slave-node/Data/DatabaseFile"), DATABASE_FILE_ZIP ("slave-node/Data/DatabaseFile.zip");
    private String path;

    PATHS (String path)
    {
        this.path = path;
    }

    public String getPath ()
    {
        return path;
    }

}
