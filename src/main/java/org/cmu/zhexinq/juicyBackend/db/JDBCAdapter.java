package org.cmu.zhexinq.juicyBackend.db;

import org.json.simple.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * JDBC adapter for operations on Juicy DB
 * Schema:
 * image -> (id, content)
 * user -> (email, name, passwd, imgId)
 * event -> (id, creatorEmail, name, lat, lon, eventDateTime, description, imgId)
 * eventUser -> (eventId, usrEmail)
 * Created by qiuzhexin on 11/25/15.
 */
public class JDBCAdapter {
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public JDBCAdapter(String url, String driverName, String user, String passwd) {
        try {
            Class.forName(driverName);
            System.out.println("Opening db connection");
            connection = DriverManager.getConnection(url, user, passwd);
            statement = connection.createStatement();
            statement.executeUpdate("USE juicy");
        } catch (ClassNotFoundException e) {
            System.err.println("Cannot find the database driver class");
            System.err.println(e);
        } catch (SQLException e) {
            System.err.println("Cannot connect to this DB");
            System.err.println(e);
        }
    }
    
    // close the adpater to DB
    public void close() throws SQLException {
    	System.out.println("closing juicy DB connection");
    	resultSet.close();
    	statement.close();
    	connection.close();
    }

    // create DB, DB tables, and insert values
    // using databaseCreation.sql file that contains sql commands
    public void initDB(String databaseFile) {
        if (connection == null || statement == null) {
            System.err.println("No connected to database");
            return;
        }
        try {
            StringBuffer command = new StringBuffer();
            BufferedReader in = new BufferedReader(new FileReader(databaseFile));
            String line;
            // read the whole table creation file
            while ((line = in.readLine()) != null) {
                command.append(line).append("\n");
                if (line.contains(";")) {
                    // execute when a statement finished
                    statement.executeUpdate(command.toString());
                    command = new StringBuffer();
                }
            }
            System.out.println("Finish creating tables");
        }
        catch (FileNotFoundException e) {
            System.err.println("File not found.");
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("reading file input error");
            e.printStackTrace();
        }
        catch (SQLException e) {
            System.err.println("sql error");
            e.printStackTrace();
        }
    }
    
    /* image table operations */
    // insert an image to the DB, and return its id, -1 if failed
    public int insertImage(String imageStr) {
    	try {
    		preparedStatement = connection.prepareStatement("INSERT INTO image (content) VALUES (?)");
    		preparedStatement.setString(1, imageStr);
    		int count = preparedStatement.executeUpdate();
    		preparedStatement = connection.prepareStatement("SELECT MAX(id) AS id FROM image");
    		resultSet = preparedStatement.executeQuery();
    		resultSet.next();
    		if (resultSet != null)
    			return resultSet.getInt("id");
            System.out.println("Insert count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot insert content into image table");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.out.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
    	return -1;
    }
    
    // read an image from DB as String
    public String readImage(int id) {
    	String result = null;
    	try {
    		preparedStatement = connection.prepareStatement("SELECT content FROM image WHERE id=?");
    		preparedStatement.setInt(1, id);
    		resultSet = preparedStatement.executeQuery();
    		resultSet.next();
    		if (resultSet != null) {
    			result = resultSet.getString("content");
    		}
            resultSet.close();
            System.out.println(preparedStatement.toString());
    	} catch (SQLException e) {
            System.err.println("Cannot read a image content");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
    	return result;
    }
    
    /* user table operations */
    // insert a user row
    public void insertUser(String email, String name, String passwd, int imgId) {
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO user VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, passwd);
            preparedStatement.setInt(4, imgId);
            int count = preparedStatement.executeUpdate();
            System.out.println("Insert count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot insert values into user table");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.out.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
    }

    // update a user row
    public void updateUser(String email, String name, String passwd, int imgId) {
        try {
            preparedStatement = connection.prepareStatement("UPDATE user SET email=?,name=?,passwd=?,imgId=? WHERE email=?");
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, passwd);
            preparedStatement.setInt(4, imgId);
            preparedStatement.setString(5, email);
            int count = preparedStatement.executeUpdate();
            System.out.println("update count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot update a row in user");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.out.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
    }

    // read a user row as a json object
    public JSONObject readUser(String email) {
        JSONObject result = new JSONObject();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM user WHERE email=?");
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (resultSet != null) {
                result.put("email", resultSet.getString("email"));
                result.put("passwd", resultSet.getString("passwd"));
                result.put("imgId", resultSet.getInt("imgId"));
                result.put("name", resultSet.getString("name"));
            }
            resultSet.close();
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot read a user row");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return result;
    }

    /* event table operations */
    // insert an event row and return its id if success, -1 if fail
    public int insertEvent(String creatorEmail, String name, double lat, double lon,
                            String eventDateTime, String description, int imgId) {
        try {
        	// insert event data to table
            preparedStatement = connection.prepareStatement("INSERT INTO event " +
                    "(creatorEmail, name, lat, lon, eventDateTime, description, imgId)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");
            preparedStatement.setString(1, creatorEmail);
            preparedStatement.setString(2, name);
            preparedStatement.setDouble(3, lat);
            preparedStatement.setDouble(4, lon);
            preparedStatement.setString(5, eventDateTime);
            preparedStatement.setString(6, description);
            preparedStatement.setInt(7, imgId);
            int count = preparedStatement.executeUpdate();
            // get the index of newly added event
    		preparedStatement = connection.prepareStatement("SELECT MAX(id) AS id FROM event");
    		resultSet = preparedStatement.executeQuery();
    		resultSet.next();
    		if (resultSet != null)
    			return resultSet.getInt("id");
            System.out.println("Insert count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot insert event in event");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return -1;
    }

    // update an event

    // read an event as a json object
    public JSONObject readEvent(int id) {
        JSONObject result = new JSONObject();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM event WHERE id=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (resultSet != null) {
                result.put("id", resultSet.getInt("id"));
                result.put("creatorEmail", resultSet.getString("creatorEmail"));
                result.put("name", resultSet.getString("name"));
                result.put("lat", resultSet.getDouble("lat"));
                result.put("lon", resultSet.getDouble("lon"));
                result.put("eventDateTime", resultSet.getString("eventDateTime"));
                result.put("description", resultSet.getString("description"));
                result.put("imgId", resultSet.getInt("imgId"));
            }
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot insert event in event");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return result;
    }

    // return a list of eventIds whose distance is within user preference
    public ArrayList<Integer> readEventListByGeoOrderByTime(double lat, double lon, double dist) {
        ArrayList<Integer> result = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM event ORDER BY eventDateTime");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                double eLat = resultSet.getDouble("lat");
                double eLon = resultSet.getDouble("lon");
                if (Utility.computeDistanceUsingGeoLoc(lat, lon, eLat, eLon) < dist)
                    result.add(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("error getting event list of geo location specification");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return result;
    }

    // delete an event

    /* eventUser table operations */
    // insert a event-user association if one decides to participate
    public void insertEventUserRelation(int eventId, String usrEmail) {
        try {
            preparedStatement = connection.prepareStatement("INSERT INTO eventUser VALUES (?, ?)");
            preparedStatement.setInt(1, eventId);
            preparedStatement.setString(2, usrEmail);
            int count = preparedStatement.executeUpdate();
            System.out.println("Insert count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot insert into table eventUser");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
    }

    // delete an event-user association
    public void deleteEventUserRelation(int eventId, String usrEmail) {
        try {
            preparedStatement = connection.prepareStatement("DELETE FROM eventUser WHERE eventId=? AND usrEmail=?");
            preparedStatement.setInt(1, eventId);
            preparedStatement.setString(2, usrEmail);
            int count = preparedStatement.executeUpdate();
            System.out.println("Delete count: " + count);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot delete event-user relation");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }

    }

    // given a email return an list of indexes of events ordered by time
    public ArrayList<Integer> readEventListByEmailOrderByTime(String usrEmail) {
        ArrayList<Integer> result = new ArrayList<>();
        try {
            preparedStatement = connection.prepareStatement("SELECT eventId FROM eventUser, event " +
                    "WHERE eventUser.eventId=event.id AND usrEmail=? " +
                    "ORDER BY eventDateTime");
            preparedStatement.setString(1, usrEmail);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getInt("eventId"));
            }
        } catch (SQLException e) {
            System.err.println("Cannot get ids of event for a user email");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return result;
    }

    // count the number of followers of an event
    public int readEventFollowers(int id) {
        int result = -1;
        try {
            preparedStatement = connection.prepareStatement("SELECT COUNT(usrEmail) FROM eventUser WHERE eventId=?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            if (resultSet != null)
                result = resultSet.getInt(1);
            System.out.println(preparedStatement.toString());
        } catch (SQLException e) {
            System.err.println("Cannot get ids of event for a user email");
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (Exception e) {
                System.err.println("prepared statement cannot be closed");
                e.printStackTrace();
            }
        }
        return result;
    }


}
