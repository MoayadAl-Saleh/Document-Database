package com.Moayad.masternode;

public enum PATHS
{
    //enum for storing paths
    MASTER_NODE_PATH ("master-node/Data/DatabaseFile/"),
    DATABASE_PATH ("master-node/Data/DatabaseFile"),
    DATABASE_FILE_ZIP ("master-node/Data/DatabaseFile.zip");
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
