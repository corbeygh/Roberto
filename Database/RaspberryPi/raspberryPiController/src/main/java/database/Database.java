package database;

import com.pi4j.io.gpio.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Calvin on 27/12/2016.
 */
public class Database {

    final static String requestsTableName = "requests";
    final static String logsTableName = "logs";

    private static GpioPinDigitalOutput led;
    private static GpioPinDigitalOutput frontLeft;
    private static GpioPinDigitalOutput frontRight;
    private static GpioPinDigitalOutput backLeft;
    private static GpioPinDigitalOutput backRight;

    public Database() throws Exception{

        if(getConnection() !=null){
            //createTableLogs();

            Timer timer = new Timer(60);
            timer.start();

            ArrayList<Request> requestHolder = new ArrayList<Request>();
            requestHolder = get();
            //For the next 60 seconds process requests, then continue to wait for anymore requests

            while (timer.hasFinished()) {

                if(requestHolder.size() != 0){
                    //Process the first request
                    final int id = requestHolder.get(0).getId();
                    final String typeOfRequest = requestHolder.get(0).getTypeOfRequest();
                    final boolean state = requestHolder.get(0).getState();
                    System.out.println("TypeOfRequest: "+typeOfRequest+" "+ "State: "+state+" "+ "ID: "+id);

                    //Turn pin On/Off
                    initPins();
                    if(state) led.high();
                    else led.low();

                    postLog(typeOfRequest, state);
                    //then remove the state at id 1
                    removeID(id);
                } else{
                    System.out.println("Awaiting Requests...");
                }
                //Get a new list of requests
                requestHolder = get();

                Thread.sleep(1000); //Wait 100 milliseconds (0.1seconds)
            }

        } else{
            System.out.println("Connection  Failed.");
        }
    }

    public static Connection getConnection() throws Exception{
        try{
            String driver = "com.mysql.jdbc.Driver";
            //When I change to a server hosting provider, I will need to change localhost to the ip address of the server I'm connecting to.
            String url = "jdbc:mysql://localhost:3308/raspberrypi";
            String username = "root";
            String password = "root";
            Class.forName(driver); Connection conn = DriverManager.getConnection(url,username,password);
            System.out.println("Connection established");
            return conn;
        } catch(Exception e){System.out.println(e);} return null;
    }

    public static void createTableLogs() throws Exception{
        try{
            Connection con = getConnection();
            PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+logsTableName+"(id int NOT NULL AUTO_INCREMENT, ledOn BOOLEAN, date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(id))"); //in quotes mySql code
            create.executeUpdate();

        }catch (Exception e){ System.out.println(e);}
        finally { System.out.println("Table Created.");}
    }

    public static void postLog(String variable, boolean state) throws Exception{

        try{
            Connection con = getConnection();
            PreparedStatement posted = con.prepareStatement("INSERT INTO "+ logsTableName +" (ledOn) VALUES ("+state+")"); // Single quotes to turn String to varchar

            posted.executeUpdate(); //query = receiving information. update = we're manipulating information

        }catch (Exception e){ System.out.println(e);}
        finally { System.out.println("Post complete.");}

    }

    public static ArrayList <Request> get() throws Exception{

        try{
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement("SELECT id, ledOn FROM "+requestsTableName);
            ResultSet result = statement.executeQuery();

            ArrayList <Request> requestHolder = new ArrayList<Request>();

            while(result.next()){
                String typeOfRequest = "ledOn";
                requestHolder.add(new Request(result.getInt("id"),typeOfRequest, result.getBoolean("ledOn")));
            }

            return requestHolder;

        }catch (Exception e){ System.out.println(e);}

        return null;
    }

    public static void removeID(int id){
        try{
            Connection con = getConnection();
            PreparedStatement statement = con.prepareStatement("DELETE FROM "+requestsTableName+" WHERE id="+id);
            statement.executeUpdate();

        }catch (Exception e){ System.out.println(e);}
        finally { System.out.println("Post complete.");}

    }

    private void initPins(){
        if(led == null){
            GpioController gpio = GpioFactory.getInstance();
            led = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "led", PinState.LOW);
        }
    }

    public static void main(String[] args) throws Exception{ new Database(); }
}
