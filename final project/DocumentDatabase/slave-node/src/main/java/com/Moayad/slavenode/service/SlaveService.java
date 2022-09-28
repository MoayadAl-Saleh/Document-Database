package com.Moayad.slavenode.service;


import com.Moayad.slavenode.opration.ReadOperation;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SlaveService
{
    ReadOperation opration = new ReadOperation ();

    //search document
    public ArrayList<JSONObject> searchDocument (String databaseName, String collectionName, String id)
    {
        return opration.searchDocument (databaseName, collectionName, id);
    }

    public ArrayList<JSONObject> searchDocument (String databaseName, String collectionName, String key, String value)
    {
        return opration.searchDocument (databaseName, collectionName, key, value);
    }

    //get all document from collection
    public JSONArray getAllDocument (String databaseName, String collectionName)
    {
        return opration.getAllDocument (databaseName, collectionName);
    }

    //get all collection from database
    public List<String> getAllCollection (String databaseName)
    {
        return opration.getAllCollection (databaseName);
    }

    //get all database
    public List<String> getAllDatabase ()
    {
        return opration.getAllDatabase ();
    }

    //get filed from document
    public String getFiled (String databaseName, String collectionName, String id, String filed)
    {
        return opration.getFiled (databaseName, collectionName, id, filed);
    }

    //update DB
    public void updateDB (String message)
    {
        opration.updateDB (message);
    }

    public void updateIndex (ArrayList<String> list)
    {
        opration.updateIndex (list);
    }

}
