package TH_users.service;

import TH_users.config.connectionSingleton;
import TH_users.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    public UserDAO() {
    }

    @Override
    public void insertUser(User user) {
        Connection connection = connectionSingleton.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("insert into users(name,email,country) values (?,?,?)");
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            System.out.println(statement);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User selectUser(int id) {

        Connection connection = connectionSingleton.getConnection();
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement("select id,name,email,country from users where id =?");
            statement.setInt(1, id);
            System.out.println(statement);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(id, name, email, country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> selectAllUser() {
        Connection connection = connectionSingleton.getConnection();
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("select * from users;");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String country = resultSet.getString("country");

                User user = new User(id, name, email, country);
                users.add(user);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return users;
    }

    @Override
    public boolean deleteUser(int id) {
        boolean TFDelete = false;
        Connection connection = connectionSingleton.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("delete from users where id=?");
            statement.setInt(1, id);
            TFDelete = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return TFDelete;
    }

    @Override
    public boolean updateUser(User user) {
        boolean TFUpdate = false;
        Connection connection = connectionSingleton.getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("update users set name=?,email=?,country=? where id=?");
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getCountry());
            statement.setInt(4, user.getId());

            TFUpdate = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return TFUpdate;
    }

    public List<User> searchByCountry(String country){
        List<User> users=new ArrayList<>();
        Connection connection=connectionSingleton.getConnection();
        try {
            PreparedStatement statement=connection.prepareStatement("select * from users where country = ?;");
            statement.setString(1,country);
            ResultSet rs=statement.executeQuery();
            while (rs.next()){
                int id=rs.getInt("id");
                String name =rs.getString("name");
                String email=rs.getString("email");
                User user=new User(id,name,email,country);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public List<User> sort() {
        Connection connection=connectionSingleton.getConnection();
        List<User> users=new ArrayList<>();
        try {
            PreparedStatement statement=connection.prepareStatement("select * from users order by name ;");
            ResultSet rs=statement.executeQuery();
            while (rs.next()){
                int id=rs.getInt("id");
                String name= rs.getString("name");
                String email=rs.getString("email");
                String country=rs.getString("country");
                User user=new User(id,name,email,country);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
