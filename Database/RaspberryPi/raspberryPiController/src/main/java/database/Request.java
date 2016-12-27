package database;

import java.sql.ResultSet;

/**
 * Created by Calvin on 27/12/2016.
 */
public class Request {

    final int id;
    final String typeOfRequest;
    final boolean state;

    public Request(int ID, String TypeOfRequest, boolean State){
        this.id = ID;
        this.typeOfRequest = TypeOfRequest;
        this.state = State;
    }

    public int getId() {
        return id;
    }

    public String getTypeOfRequest() {
        return typeOfRequest;
    }

    public boolean getState() {
        return state;
    }
}
