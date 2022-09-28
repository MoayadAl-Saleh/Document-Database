package com.Moayad.masternode.authentication;

import com.Moayad.masternode.operations.WriteOperations;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthenticationService
{
    WriteOperations operation = new WriteOperations ();

    //create user
    public void createUser (String userName, String password)
    {
        if (operation.isDocumentExist ("Authentication", "User", userName))
        {
            System.out.println ("User already exist");
        } else
        {
            JSONObject jsonObject = new JSONObject ();
            jsonObject.put ("id", userName);
            jsonObject.put ("userName", userName);
            jsonObject.put ("password", password);
            operation.insertDocument ("Authentication", "User", jsonObject);
            operation.writeFile ("Authentication", "User", operation.getAllDocuments ("Authentication", "User"));
        }
    }

    //check user and password
    public boolean isUserCorrect (String id, String password)
    {
        if (operation.isDocumentExist ("Authentication", "User", id))
        {
            ArrayList<JSONObject> jsonArray = operation.searchDocument ("Authentication", "User", id);
            return jsonArray.get (0).get ("password").equals (password);
        } else
        {
            System.out.println ("User not exist");
            return false;
        }
    }
}
