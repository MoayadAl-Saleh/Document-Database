package com.Moayad.slavenode.opration;


import com.Moayad.slavenode.Btree.BPlusTree;
import com.Moayad.slavenode.PATHS;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class ReadOperation
{
    //main function
    public static void main (String[] args)
    {

        ReadOperation operation = new ReadOperation ();
        System.out.println (operation.searchDocument ("MyDatabase1", "MyCollection1", "5"));
    }

    //constructor
    String path = PATHS.SLAVE_NODE_PATH.getPath ();

    public ReadOperation ()
    {
        findDirectories (path);
    }

    private HashMap<String, List<String>> databaseToCollection = new HashMap<> ();
    private HashMap<String, BPlusTree> collectionToBPlusTree = new HashMap<> ();

    public HashMap<String, String> indexes = new HashMap ();

    public String readFile (String databaseName, String collectionName)
    {
        StringBuilder sb = new StringBuilder ();
        try
        {
            if (isCollectionExist (databaseName, collectionName))
            {
                BufferedReader br = new BufferedReader (new FileReader (path + databaseName + "/" + collectionName + ".json"));
                String line = br.readLine ();
                while (line != null)
                {
                    sb.append (line);
                    line = br.readLine ();
                }
                br.close ();
                return sb.toString ();
            } else
            {
                System.out.println ("File not found");
                return "File not found";
            }

        } catch (IOException e)
        {
            e.printStackTrace ();
        }
        return "";
    }

    public boolean isDatabaseExist (String databaseName)
    {
        return databaseToCollection.containsKey (databaseName);
    }

    public boolean isCollectionExist (String databaseName, String collectionName)
    {
        if (databaseToCollection.containsKey (databaseName))
        {
            //System.out.println (databaseToCollection.get (databaseName).contains (collectionName));
            return databaseToCollection.get (databaseName).contains (collectionName);
        } else
        {
            return false;
        }
    }

    public void findDirectories (String path)
    {
        File root = new File (path);
        for (File file : Objects.requireNonNull (root.listFiles ()))
        {
            if (file.isDirectory ())
            {
                databaseToCollection.put (file.getName (), new ArrayList<> ());
                findAlNameFile (file.getName ());
            }
        }
    }

    private void findAlNameFile (String nameFolder)
    {
        File root = new File (path + nameFolder);
        for (File file : Objects.requireNonNull (root.listFiles ()))
        {
            databaseToCollection.get (nameFolder).add (file.getName ().split ("\\.")[0]);
            putAllDocumentToBPlusTree (nameFolder, file.getName ().split ("\\.")[0]);
        }
    }

    //put all document to B+Tree
    public void putAllDocumentToBPlusTree (String database, String collection)
    {
        String content = readFile (database, collection);
        JSONArray jsonArray = new JSONArray (content);
        collectionToBPlusTree.put (database + collection, new BPlusTree ());
        indexes.put (database + collection, "id");
        for (int i = 0; i < jsonArray.length (); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject (i);
            if (jsonObject.has ("id"))
            {
                collectionToBPlusTree.get (database + collection).insert (jsonObject.get ("id").toString (), jsonObject);
            }
        }
    }

    public void findDirectories (String path, HashMap<String, String> indexes)
    {
        File root = new File (path);
        for (File file : Objects.requireNonNull (root.listFiles ()))
        {
            if (file.isDirectory ())
            {
                databaseToCollection.put (file.getName (), new ArrayList<> ());
                findAlNameFile (file.getName (), indexes);
            }
        }
    }

    private void findAlNameFile (String nameFolder, HashMap<String, String> indexes)
    {
        File root = new File (path + nameFolder);
        for (File file : Objects.requireNonNull (root.listFiles ()))
        {
            databaseToCollection.get (nameFolder).add (file.getName ().split ("\\.")[0]);
            putAllDocumentToBPlusTree (nameFolder, file.getName ().split ("\\.")[0], indexes);
        }
    }

    //put all document to B+Tree
    public void putAllDocumentToBPlusTree (String database, String collection, HashMap<String, String> indexes)
    {
        String content = readFile (database, collection);
        String index = indexes.get (database + collection);
        JSONArray jsonArray = new JSONArray (content);
        collectionToBPlusTree.put (database + collection, new BPlusTree ());
        for (int i = 0; i < jsonArray.length (); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject (i);
            if (jsonObject.has (index))
            {
                collectionToBPlusTree.get (database + collection).insert (jsonObject.get (index).toString (), jsonObject);
            } else if (jsonObject.has ("id"))
            {
                collectionToBPlusTree.get (database + collection).insert (jsonObject.get ("id").toString (), jsonObject);
            }
        }
    }

    //search document by id in collectionToBPlusTree
    public ArrayList<JSONObject> searchDocument (String database, String collection, String key)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            ArrayList<JSONObject> list = collectionToBPlusTree.get (database + collection).search (key, key);
            if (list.size () == 0)
            {
                System.out.println ("Not found");
                list.add (new JSONObject ().put ("", "Not found"));
                return list;
            } else
            {
                return list;
            }
        } else
        {
            System.out.println ("Collection not found");
            ArrayList<JSONObject> list = new ArrayList<> ();
            list.add (new JSONObject ().put ("", "Collection not found"));
            return list;
        }
    }

    public ArrayList<JSONObject> searchDocument (String database, String collection, String key, String value)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            String content = readFile (database, collection);
            JSONArray jsonArray = new JSONArray (content);
            ArrayList<JSONObject> list = new ArrayList<> ();
            //search by id in jsonArray
            for (int i = 0; i < jsonArray.length (); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject (i);
                if (jsonObject.has (key))
                {
                    if (jsonObject.get (key).toString ().equals (value))
                    {
                        list.add (jsonObject);
                    }
                }
            }
            if (list.size () == 0)
            {
                System.out.println ("Not found");
                list.add (new JSONObject ().put ("", "Not found"));
                return list;
            } else
            {
                return list;
            }
        } else
        {
            System.out.println ("Collection not found");
            ArrayList<JSONObject> list = new ArrayList<> ();
            list.add (new JSONObject ().put ("", "Collection not found"));
            return list;
        }

    }

    //get all document in collectionPulseTree
    public JSONArray getAllDocument (String database, String collection)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            return collectionToBPlusTree.get (database + collection).getAllValues ();
        } else
        {
            System.out.println ("Collection not found");
            return new JSONArray ().put (new JSONObject ().put ("", "Collection not found"));
        }
    }

    //check if document is exist in collectionPlusTree
    public boolean isDocumentExistID (String databaseName, String collectionName, String id)
    {
        if (collectionToBPlusTree.containsKey (databaseName + collectionName))
        {
            return collectionToBPlusTree.get (databaseName + collectionName).search (id) != null;
        } else
        {
            return false;
        }
    }

    //get all collection in database
    public List<String> getAllCollection (String databaseName)
    {
        if (isDatabaseExist (databaseName))
        {
            return databaseToCollection.get (databaseName);
        } else
        {
            System.out.println ("Database not found");
            List<String> list = new ArrayList<> ();
            list.add ("Database not found");
            return list;
        }
    }

    //get all database from databaseToCollection
    public List<String> getAllDatabase ()
    {
        return new ArrayList<> (databaseToCollection.keySet ());
    }

    //get filed from document
    public String getFiled (String databaseName, String collectionName, String id, String filed)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            if (isDocumentExistID (databaseName, collectionName, id))
            {
                JSONObject jsonObject = collectionToBPlusTree.get (databaseName + collectionName).search (id);
                return jsonObject.get (filed).toString ();
            } else
            {
                return "Not found";
            }
        } else
        {
            return "Collection not found";
        }
    }

    //update DB
    public void changeIndexCollection (String databaseName, String collectionName, String index)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            JSONArray jsonArray = collectionToBPlusTree.get (databaseName + collectionName).getAllValues ();
            collectionToBPlusTree.remove (databaseName + collectionName);
            collectionToBPlusTree.put (databaseName + collectionName, new BPlusTree ());
            for (int i = 0; i < jsonArray.length (); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject (i);
                if (jsonObject.has (index))
                {
                    collectionToBPlusTree.get (databaseName + collectionName).insert (jsonObject.get (index).toString (), jsonObject);
                } else
                {
                    collectionToBPlusTree.get (databaseName + collectionName).insert (jsonObject.get ("id").toString (), jsonObject);
                }
            }
            setIndexForCollection (databaseName, collectionName, index);
            //for print all name of database and collection
            System.out.println (getIndexForCollection ("MyDatabase1", "MyCollection1"));
            System.out.println (collectionToBPlusTree.get ("MyDatabase1" + "MyCollection1").getAllValues ());
        } else
        {
            System.out.println ("Collection not found");
        }
    }

    public String getIndexForCollection (String databaseName, String collectionName)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            return collectionToBPlusTree.get (databaseName + collectionName).getIndex ();
        } else
        {
            return "this collection not exist";
        }
    }

    public void setIndexForCollection (String databaseName, String collectionName, String index)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            collectionToBPlusTree.get (databaseName + collectionName).setIndex (index);
            indexes.remove (databaseName + collectionName);
            indexes.put (databaseName + collectionName, index);
        } else
        {
            System.out.println ("Collection not found");
        }
    }

    public void updateDB (String massage)
    {
        System.out.println (massage);
        findDirectories (path, indexes);
        System.out.println (collectionToBPlusTree.get ("MyDatabase1" + "MyCollection1").getAllValues ());
    }

    public void updateIndex (ArrayList<String> list)
    {
        changeIndexCollection (list.get (0), list.get (1), list.get (2));
    }

}