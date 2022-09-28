package com.Moayad.masternode.controller;


import com.Moayad.masternode.PATHS;
import com.Moayad.masternode.service.MasterService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.util.ArrayList;

@RestController
@RequestMapping("/master")
public class MasterController
{
    MasterService databaseService;
    final RestTemplate restTemplate;

    //constructor
    @Autowired
    public MasterController (MasterService databaseService, RestTemplate restTemplate)
    {
        this.databaseService = databaseService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/createDatabase/{databaseName}")
    public String creatDatabase (@PathVariable String databaseName)
    {
        String result = databaseService.createDatabase (databaseName);
        updateDatabase ();
        return result;
    }

    //create database and collection
    @PostMapping("/createDatabase/{databaseName}/{collectionName}")
    public String createDatabaseAndCollection (@PathVariable String databaseName, @PathVariable String collectionName)
    {
        String result = databaseService.createDatabase (databaseName, collectionName);
        updateDatabase ();
        return result;
    }

    //create collection
    @PostMapping("/createCollection/{databaseName}/{collectionName}")
    public String createCollection (@PathVariable String databaseName, @PathVariable String collectionName)
    {
        String result = databaseService.createCollection (databaseName, collectionName);
        updateDatabase ();
        return result;
    }

    //insert document
    @PostMapping("/insertDocument/{databaseName}/{collectionName}")
    public String insertDocument (@PathVariable String databaseName, @PathVariable String collectionName, @RequestBody String document)
    {
        String result = databaseService.insertDocument (databaseName, collectionName, new JSONObject (document));
        updateDatabase ();
        return result;
    }

    //delete database
    @DeleteMapping("/deleteDatabase/{databaseName}")
    public String deleteDatabase (@PathVariable String databaseName)
    {
        String result = databaseService.deleteDatabase (databaseName);
        updateDatabase ();
        return result;
    }

    //delete collection
    @DeleteMapping("/deleteCollection/{databaseName}/{collectionName}")
    public String deleteCollection (@PathVariable String databaseName, @PathVariable String collectionName)
    {
        String result = databaseService.deleteCollection (databaseName, collectionName);
        updateDatabase ();
        return result;
    }

    //delete document
    @DeleteMapping("/deleteDocument/{databaseName}/{collectionName}/{id}")
    public String deleteDocument (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id)
    {
        String result = databaseService.deleteDocument (databaseName, collectionName, id);
        updateDatabase ();
        return result;
    }

    //change name of database
    @PutMapping("/changeDatabaseName/{oldName}/{newName}")
    public String changeDatabaseName (@PathVariable String oldName, @PathVariable String newName)
    {
        String result = databaseService.changeDatabaseName (oldName, newName);
        updateDatabase ();
        return result;
    }

    //change name of collection
    @PutMapping("/changeCollectionName/{databaseName}/{oldName}/{newName}")
    public String changeCollectionName (@PathVariable String databaseName, @PathVariable String oldName, @PathVariable String newName)
    {
        String result = databaseService.changeCollectionName (databaseName, oldName, newName);
        updateDatabase ();
        return result;
    }

    //update document
    @PutMapping("/updateDocument/{databaseName}/{collectionName}/{id}/{document}")
    public String updateDocument (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id, @PathVariable JSONObject document)
    {
        String result = databaseService.updateDocument (databaseName, collectionName, id, document);
        updateDatabase ();
        return result;
    }

    //change index
    @PutMapping("/changeIndexCollection/{databaseName}/{collectionName}/{indexName}")
    public String changeIndexDocument (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String indexName)
    {
        String result = databaseService.changeIndexCollection (databaseName, collectionName, indexName);
        updateIndex (databaseName, collectionName, indexName);
        return result;
    }

    public void updateDatabase ()
    {
        ZipUtil.pack (new File (PATHS.DATABASE_PATH.getPath ()), new File (PATHS.DATABASE_FILE_ZIP.getPath ()));

        String fileName = PATHS.DATABASE_FILE_ZIP.getPath ();
        CloseableHttpClient client = HttpClientBuilder.create ().build ();
        String myFile_URL = "http://localhost:8082/slave/getDatabase";
        HttpPost post = new HttpPost (myFile_URL);
        File file = new File (fileName);
        FileBody fileBody = new FileBody (file, ContentType.DEFAULT_BINARY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create ();
        builder.setMode (HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart ("file", fileBody);
        HttpEntity entity = builder.build ();
        post.setEntity (entity);
        try
        {
            HttpResponse response = client.execute (post);
            ResponseEntity.status (HttpStatus.OK).body ("SUCCESS BS upload");
        } catch (Exception e)
        {
            ResponseEntity.status (HttpStatus.EXPECTATION_FAILED).body ("FAIL BS upload");
        }
        restTemplate.postForObject ("http://SLAVE-NODE/slave/updateDB", "Update The Database", String.class);
        restTemplate.postForObject ("http://SLAVE-NODE/slave/updateDB", "Update The Database", String.class);
//        restTemplate.postForObject ("http://localhost:8082/slave/updateDB","Update The Database", String.class);
//        restTemplate.postForObject ("http://localhost:8084/slave/updateDB","Update The Database", String.class);
    }

    public void updateIndex (String databaseName, String collectionName, String indexName)
    {
        ArrayList<String> index = new ArrayList<> ();
        index.add (databaseName);
        index.add (collectionName);
        index.add (indexName);
        restTemplate.postForObject ("http://SLAVE-NODE/slave/updateIndex", index, String.class);
    }


}

