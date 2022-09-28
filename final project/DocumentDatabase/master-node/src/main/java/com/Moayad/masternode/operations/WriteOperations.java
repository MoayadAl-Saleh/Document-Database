package com.Moayad.masternode.operations;


import com.Moayad.masternode.Btree.BPlusTree;
import com.Moayad.masternode.PATHS;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class WriteOperations
{
    String path = PATHS.MASTER_NODE_PATH.getPath ();

    public WriteOperations ()
    {
        findDirectories (path);
    }

    private final HashMap<String, List<String>> databaseToCollection = new HashMap<> ();
    private final HashMap<String, BPlusTree> collectionToBPlusTree = new HashMap<> ();

    //write file using buffered writer
    public void writeFile (String databaseName, String collectionName, JSONArray jsonArray)
    {
        try
        {
            BufferedWriter bw = new BufferedWriter (new FileWriter (path + databaseName + "/" + collectionName + ".json"));
            bw.write (jsonArray.toString ());
            bw.close ();
        } catch (Exception e)
        {
            e.printStackTrace ();
        }
    }

    public void writeFile (String databaseName, String collectionName)
    {
        try
        {
            JSONArray jsonArray = new JSONArray ();
            BufferedWriter bw = new BufferedWriter (new FileWriter (path + databaseName + "/" + collectionName + ".json"));
            bw.write (jsonArray.toString ());
            bw.close ();
        } catch (Exception e)
        {
            e.printStackTrace ();
        }
    }

    //read file using BufferedReader
    public String readFile (String databasename, String collectionName)
    {
        StringBuilder sb = new StringBuilder ();
        try
        {
            if (isCollectionExist (databasename, collectionName))
            {
                BufferedReader br = new BufferedReader (new FileReader (path + databasename + "/" + collectionName + ".json"));
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
                return "File not found";
            }

        } catch (IOException e)
        {
            e.printStackTrace ();
        }
        return null;
    }

    public String createDatabase (String databaseName, String collectionName)
    {
        if (isCollectionExist (databaseName, collectionName))
        {

            return "Collection already exist";
        } else
        {
            new File (path + databaseName).mkdir ();
            databaseToCollection.put (databaseName, new ArrayList<> ());
            databaseToCollection.get (databaseName).add (collectionName);
            collectionToBPlusTree.put (databaseName + collectionName, new BPlusTree ());
            writeFile (databaseName, collectionName);
            return "Database created";
        }
    }

    public String createDatabase (String databaseName)
    {
        if (isDatabaseExist (databaseName))
        {
            return "Database already exist";
        } else
        {
            new File (path + databaseName).mkdir ();
            databaseToCollection.put (databaseName, new ArrayList<> ());
            return "Database created";
        }
    }

    //add collection to database
    public String createCollection (String databaseName, String collectionName)
    {
        if (isDatabaseExist (databaseName))
        {
            if (isCollectionExist (databaseName, collectionName))
            {
                return "Collection already exist";
            } else
            {
                databaseToCollection.get (databaseName).add (collectionName);
                collectionToBPlusTree.put (databaseName + collectionName, new BPlusTree ());
                writeFile (databaseName, collectionName);
                return "Collection created";
            }
        } else
        {
            return "Database not exist";
        }
    }

    public boolean isDatabaseExist (String databaseName)
    {
        return databaseToCollection.containsKey (databaseName);
    }

    public boolean isCollectionExist (String databaseName, String collectionName)
    {
        if (databaseToCollection.containsKey (databaseName))
        {
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
                findAllFilesNames (file.getName ());
            }
        }
    }

    private void findAllFilesNames (String nameFolder)
    {
        File root = new File (path + nameFolder);
        for (File file : Objects.requireNonNull (root.listFiles ()))
        {
            databaseToCollection.get (nameFolder).add (file.getName ().split ("\\.")[0]);
            putAllDocumentsInBPlusTree (nameFolder, file.getName ().split ("\\.")[0]);
        }
    }

    //put all document to B+Tree
    public void putAllDocumentsInBPlusTree (String database, String collection)
    {
        String content = readFile (database, collection);
        JSONArray jsonArray = new JSONArray (content);
        collectionToBPlusTree.put (database + collection, new BPlusTree ());
        for (int i = 0; i < jsonArray.length (); i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject (i);
            if (Objects.equals (database, "Authentication"))
            {
                if (jsonObject.has ("userName"))
                {
                    collectionToBPlusTree.get (database + collection).insert (jsonObject.getString ("userName"), jsonObject);
                }
            } else
            {
                if (jsonObject.has ("id"))
                {
                    collectionToBPlusTree.get (database + collection).insert (jsonObject.get ("id").toString (), jsonObject);
                }
            }

        }


    }

    //get all document in collectionPulseTree
    public JSONArray getAllDocuments (String database, String collection)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            return collectionToBPlusTree.get (database + collection).getAllValues ();
        } else
        {
            return null;
        }
    }

    //insert document to collectionToBPlusTree
    public String insertDocument (String database, String collection, JSONObject jsonObject)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            if (isContainID (jsonObject, "id"))
            {
                if (isDocumentExist (database, collection, jsonObject.get ("id").toString ()))
                {
                    return "Document already exist";
                }
            } else
            {
                addRandomID (jsonObject);
            }
            String index = getIndexForCollection (database, collection);
            if (jsonObject.has (index))
            {
                collectionToBPlusTree.get (database + collection).insert (jsonObject.get (index).toString (), jsonObject);
            } else
            {
                collectionToBPlusTree.get (database + collection).insert (jsonObject.get ("id").toString (), jsonObject);
            }
            writeFile (database, collection, getAllDocuments (database, collection));
            return "Document inserted";
        } else
        {
            return "Collection not exist";
        }
    }

    //change name of file
    public String changeCollectionName (String databaseName, String collectionName, String newName)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            File file = new File (path + databaseName + "/" + collectionName + ".json");
            file.renameTo (new File (path + databaseName + "/" + newName + ".json"));
            databaseToCollection.get (databaseName).remove (collectionName);
            databaseToCollection.get (databaseName).add (newName);
            BPlusTree bPlusTree = collectionToBPlusTree.get (databaseName + collectionName);
            collectionToBPlusTree.remove (databaseName + collectionName);
            collectionToBPlusTree.put (databaseName + newName, bPlusTree);
            return "Collection name changed";
        } else
        {
            return "Collection not exist";
        }
    }

    //chang name of database
    public String changeDatabaseName (String databaseName, String newName)
    {
        if (isDatabaseExist (databaseName))
        {
            File file = new File (path + databaseName);
            file.renameTo (new File (path + newName));
            List<String> ListOfCollection = databaseToCollection.get (databaseName);
            databaseToCollection.remove (databaseName);
            databaseToCollection.put (newName, ListOfCollection);
            for (String collection : ListOfCollection)
            {
                BPlusTree bPlusTree = collectionToBPlusTree.get (databaseName + collection);
                collectionToBPlusTree.remove (databaseName + collection);
                collectionToBPlusTree.put (newName + collection, bPlusTree);
            }
            return "Database changed";
        } else
        {
            return "Database not exist";
        }
    }

    //delete collection
    public String deleteCollection (String databaseName, String collectionName)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            File file = new File (path + databaseName + "/" + collectionName + ".json");
            file.delete ();
            databaseToCollection.get (databaseName).remove (collectionName);
            collectionToBPlusTree.remove (databaseName + collectionName);
            return "Collection deleted";
        } else
        {
            return "Collection not exist";
        }
    }

    //delete database
    public String deleteDatabase (String databaseName)
    {
        if (isDatabaseExist (databaseName))
        {
            deleteAllCollections (databaseName);
            File file = new File (path + databaseName);
            file.delete ();
            databaseToCollection.remove (databaseName);
            return "Database deleted";
        } else
        {
            return "Database not exist";
        }
    }

    //delete all collection in database
    public void deleteAllCollections (String databaseName)
    {
        if (isDatabaseExist (databaseName))
        {

            List<String> ListOfCollection = databaseToCollection.get (databaseName);
            if (ListOfCollection.size () == 0)
            {
                return;
            }
            for (String collectionName : ListOfCollection)
            {
                File file = new File (path + databaseName + "/" + collectionName + ".json");
                file.delete ();
            }
            databaseToCollection.get (databaseName).clear ();
        }
    }

    //delete document
    public String deleteDocument (String databaseName, String collectionName, String id)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            if (isDocumentExist (databaseName, collectionName, id))
            {
                collectionToBPlusTree.get (databaseName + collectionName).delete (id);
                return "Document deleted";
            } else
            {
                return "Document not exist";
            }
        } else
        {
            return "Collection not exist";
        }

    }

    //update document in collectionToBPlusTree
    public String updateDocument (String databaseName, String collectionName, String id, JSONObject jsonObject)
    {
        if (isCollectionExist (databaseName, collectionName))
        {
            if (isDocumentExist (databaseName, collectionName, id))
            {
                collectionToBPlusTree.get (databaseName + collectionName).delete (id);
                collectionToBPlusTree.get (databaseName + collectionName).insert (id, jsonObject);
                return "Document updated";
            } else
            {
                return "Document not exist";
            }
        } else
        {
            return "Collection not exist";
        }
    }

    //check if document is exist in collectionPlusTree
    public boolean isDocumentExist (String databaseName, String collectionName, String index)
    {
        if (collectionToBPlusTree.containsKey (databaseName + collectionName))
        {
            return collectionToBPlusTree.get (databaseName + collectionName).search (index) != null;
        } else
        {
            return false;
        }
    }

    //chech if Object Json is contain key
    private boolean isContainID (JSONObject obj, String key)
    {
        return obj.has (key);
    }

    // add random integer id unique to document
    private void addRandomID (JSONObject obj)
    {
        //20220603013045
        //generate random number from 0 to 10e18
        //get date and time in format yyyyMMddHHmmss
        //add to id
        long idRandom = (long) (Math.random () * 10e18);
        String date = new SimpleDateFormat ("yyyyMMddHHmmss").format (new Date ());
        //convert id to long
        String id = date + idRandom;
        obj.put ("id", id);
    }

    //change index of document
    public String changeIndexCollection (String databaseName, String collectionName, String index)
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
            writeFile (databaseName, collectionName, getAllDocuments (databaseName, collectionName));
            return "Index changed";
        } else
        {
            return "Collection not exist";
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
        }
    }

    public ArrayList<JSONObject> searchDocument (String database, String collection, String key)
    {
        if (collectionToBPlusTree.containsKey (database + collection))
        {
            ArrayList<JSONObject> list = collectionToBPlusTree.get (database + collection).search (key, key);
            if (list.size () == 0)
            {
                list.add (new JSONObject ().put ("", "Not found"));
                return list;
            } else
            {
                return list;
            }
        } else
        {
            ArrayList<JSONObject> list = new ArrayList<> ();
            list.add (new JSONObject ().put ("", "Collection not found"));
            return list;
        }
    }


}