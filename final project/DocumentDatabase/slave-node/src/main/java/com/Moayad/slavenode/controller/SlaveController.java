package com.Moayad.slavenode.controller;

import com.Moayad.slavenode.PATHS;
import com.Moayad.slavenode.configuration.SlaveConfiguration;
import com.Moayad.slavenode.service.SlaveService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;


@RestController
@RequestMapping("/slave")
public class SlaveController
{

    SlaveService databaseService;
    SlaveConfiguration configurationDatabase;

    //constructor
    @Autowired
    public SlaveController (SlaveService databaseService, SlaveConfiguration configurationDatabase)
    {
        this.databaseService = databaseService;
        this.configurationDatabase = configurationDatabase;
    }

    //search document
    @GetMapping("/searchDocument/{databaseName}/{collectionName}/{id}")
    public String searchDocument (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id)
    {
        ArrayList<JSONObject> jsonObjects = databaseService.searchDocument (databaseName, collectionName, id);
        System.out.println (jsonObjects);
        return jsonObjects.toString ();

    }

    @GetMapping("/searchDocument/{databaseName}/{collectionName}/{key}/{value}")
    public String searchDocument (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String key, @PathVariable String value)
    {
        ArrayList<JSONObject> jsonObjects = databaseService.searchDocument (databaseName, collectionName, key, value);
        System.out.println (jsonObjects);
        return jsonObjects.toString ();

    }

    //get all document from collection
    @GetMapping("/getAllDocument/{databaseName}/{collectionName}")
    public String getAllDocument (@PathVariable String databaseName, @PathVariable String collectionName)
    {
        return databaseService.getAllDocument (databaseName, collectionName).toString ();
    }

    //get all collection from database
    @GetMapping("/getAllCollection/{databaseName}")
    public String getAllCollection (@PathVariable String databaseName)
    {
        return databaseService.getAllCollection (databaseName).toString ();
    }

    //get all database
    @GetMapping("/getAllDatabase")
    public String getAllDatabase ()
    {
        return databaseService.getAllDatabase ().toString ();
    }

    //get filed from document
    @GetMapping("/getFiled/{databaseName}/{collectionName}/{id}/{filed}")
    public String getFiled (@PathVariable String databaseName, @PathVariable String collectionName, @PathVariable String id, @PathVariable String filed)
    {
        return databaseService.getFiled (databaseName, collectionName, id, filed);
    }

    @PostMapping("/getDatabase")
    public ResponseEntity<String> handleFileUpload2 (@RequestParam("file") MultipartFile file)
    {
        try
        {
            //delete src/main/java/com/example/slave/data.zip
            File fileZip = new File (PATHS.DATABASE_FILE_ZIP.getPath ());
            if (fileZip.exists ())
            {
                fileZip.delete ();
            }

            //delete all files in data/DatabaseFile
            File DatabaseFile = new File (PATHS.DATABASE_FILE.getPath ());
            if (DatabaseFile.exists ())
            {
                File[] listOfFolder = DatabaseFile.listFiles ();
                for (File folder : Objects.requireNonNull (listOfFolder))
                {
                    File[] ListOfFile = folder.listFiles ();
                    for (File file1 : Objects.requireNonNull (ListOfFile))
                    {
                        file1.delete ();
                    }
                    folder.delete ();
                }
            }

            Path uploadPath = Paths.get (PATHS.DATABASE_PATH.getPath ());
            Files.copy (file.getInputStream (), uploadPath.resolve (Objects.requireNonNull (file.getOriginalFilename ())));
        } catch (Exception e)
        {
            throw new RuntimeException ("FAIL!");
        }
        ZipUtil.unpack (new File (PATHS.DATABASE_FILE_ZIP.getPath ()), new File (PATHS.DATABASE_FILE.getPath ()));


        return ResponseEntity.status (HttpStatus.OK).body ("Uploaded on batchApp");
    }

    //update DB
    @PostMapping(value = "/updateDB")
    public void updateDB (@RequestBody String message)
    {
        System.out.println (message);
        databaseService.updateDB (message);
    }

    //updateIndex
    @PostMapping(value = "/updateIndex")
    public void updateIndex (@RequestBody ArrayList<String> index)
    {
        System.out.println (index);
        databaseService.updateIndex (index);
    }


}

