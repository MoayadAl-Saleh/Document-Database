package com.Moayad.masternode.service;

import com.Moayad.masternode.operations.WriteOperations;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MasterService
{

    WriteOperations opration = new WriteOperations ();

    public String createDatabase (String databaseName)
    {
        return opration.createDatabase (databaseName);
    }

    //create database and collection
    public String createDatabase (String databaseName, String collectionName)
    {
        return opration.createDatabase (databaseName, collectionName);
    }

    //create collection
    public String createCollection (String databaseName, String collectionName)
    {
        return opration.createCollection (databaseName, collectionName);
    }

    public String insertDocument (String databaseName, String collectionName, JSONObject document)
    {
        return opration.insertDocument (databaseName, collectionName, document);
    }

    //delete database
    public String deleteDatabase (String databaseName)
    {
        return opration.deleteDatabase (databaseName);

    }

    //delete collection
    public String deleteCollection (String databaseName, String collectionName)
    {
        return opration.deleteCollection (databaseName, collectionName);
    }

    //delete document
    public String deleteDocument (String databaseName, String collectionName, String id)
    {
        return opration.deleteDocument (databaseName, collectionName, id);
    }

    //change name of database
    public String changeDatabaseName (String oldName, String newName)
    {
        return opration.changeDatabaseName (oldName, newName);
    }

    //change name of collection
    public String changeCollectionName (String databaseName, String oldName, String newName)
    {
        return opration.changeCollectionName (databaseName, oldName, newName);
    }

    //update document
    public String updateDocument (String databaseName, String collectionName, String id, JSONObject document)
    {
        return opration.updateDocument (databaseName, collectionName, id, document);
    }

    public String changeIndexCollection (String databaseName, String collectionName, String indexName)
    {
        return opration.changeIndexCollection (databaseName, collectionName, indexName);
    }


    //get hashmap collectionPulsTree


}
