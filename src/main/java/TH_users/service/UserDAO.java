package TH_users.service;

import TH_users.config.connectionSingleton;
import TH_users.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {

    public UserDAO() {
    }
    private         Connection connection = connectionSingleton.getConnection();


    @Override
    public void insertUser(User user) {
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
        List<User> users = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("select users.id,users.name,users.email,users.country from users;");
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

    @Override
    public User getUserById(int id) {
        User user=null;
        try {
            CallableStatement callableStatement=connection.prepareCall("CALL get_user_by_id(?)");
            callableStatement.setInt(1,id);
            ResultSet rs=callableStatement.executeQuery();
            while (rs.next()){
                String name=rs.getString("name");
                String email=rs.getString("email");
                String country=rs.getString("country");
                user=new User(id,name,email,country);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void insertUserStore(User user) throws SQLException {
        CallableStatement callableStatement=connection.prepareCall("call insert_user(?,?,?);");
        callableStatement.setString(1,user.getName());
        callableStatement.setString(2,user.getEmail());
        callableStatement.setString(3,user.getCountry());
        System.out.println(callableStatement);
        callableStatement.executeUpdate();
    }

    @Override

    public void addUserTransaction(User user, int[] permisions) {

        // for insert a new user

        PreparedStatement pstmt = null;

        // for assign permision to user

        PreparedStatement pstmtAssignment = null;

        // for getting user id

        ResultSet rs = null;

        try {

            connection = connectionSingleton.getConnection();

            // set auto commit to false

            connection.setAutoCommit(false);

            //

            // Insert user

            //

            pstmt = connection.prepareStatement("insert into users(name,email,country) value (?,?,?);", Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, user.getName());

            pstmt.setString(2, user.getEmail());

            pstmt.setString(3, user.getCountry());

            int rowAffected = pstmt.executeUpdate();

            // get user id

            rs = pstmt.getGeneratedKeys();

            int userId = 0;

            if (rs.next())

                userId = rs.getInt(1);

            //

            // in case the insert operation successes, assign permision to user

            //

            if (rowAffected == 1) {

                // assign permision to user

                String sqlPivot = "INSERT INTO user_permision(user_id,permission_id) "

                        + "VALUES(?,?)";

                pstmtAssignment = connection.prepareStatement(sqlPivot);

                for (int permisionId : permisions) {

                    pstmtAssignment.setInt(1, userId);

                    pstmtAssignment.setInt(2, permisionId);

                    pstmtAssignment.executeUpdate();

                }

                connection.commit();

            } else {

                connection.rollback();

            }

        } catch (SQLException ex) {

            // roll back the transaction

            try {

                if (connection != null)

                    connection.rollback();

            } catch (SQLException e) {

                System.out.println(e.getMessage());

            }

            System.out.println(ex.getMessage());

        } finally {

            try {

                if (rs != null) rs.close();

                if (pstmt != null) pstmt.close();

                if (pstmtAssignment != null) pstmtAssignment.close();


            } catch (SQLException e) {

                System.out.println(e.getMessage());

            }

        }

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
