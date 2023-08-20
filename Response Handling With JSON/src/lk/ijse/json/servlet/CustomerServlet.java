package lk.ijse.json.servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/pages/customer"})
class wCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ass1", "root", "1234");

            PreparedStatement statement = connection.prepareStatement("select * from customer");

            ResultSet resultSet = statement.executeQuery();

            resp.addHeader("Content-Type","application/json");

            JsonArrayBuilder allCustomer = Json.createArrayBuilder();

            JsonObjectBuilder customerObject = Json.createObjectBuilder();

            while (resultSet.next()){

                String id = resultSet.getString(1);

                String name = resultSet.getString(2);

                String address = resultSet.getString(3);

                customerObject.add("id",id);
                customerObject.add("name",name);
                customerObject.add("address",address);

                allCustomer.add(customerObject.build());

            }

            resp.getWriter().print(customerObject.build());

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String cusID = req.getParameter("cusID");

        String cusName = req.getParameter("cusName");

        String address = req.getParameter("cusAddress");


        resp.addHeader("Content-Type","application/json");


        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ass1", "root", "1234");

            PreparedStatement statement = connection.prepareStatement("insert into customer values(?,?,?)");

            statement.setObject(1, cusID);
            statement.setObject(2, cusName);
            statement.setObject(3, address);


            if (statement.executeUpdate()>0){

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

                objectBuilder.add("state","Done!");
                objectBuilder.add("message","Successfully Added!");
                objectBuilder.add("data","");

                resp.getWriter().print(objectBuilder.build());
            }


        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customerObject = reader.readObject();

        String id = customerObject.getString("id");
        String name = customerObject.getString("name");
        String address = customerObject.getString("address");


        try {

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ass1", "root", "1234");

            PreparedStatement statement = connection.prepareStatement("update customer set name=?,address=? where id=?");

            statement.setObject(3, id);
            statement.setObject(1, name);
            statement.setObject(2, address);

            boolean b = statement.executeUpdate() > 0;

            JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

            objectBuilder.add("state","Done!");
            objectBuilder.add("message","Updated Successfully");
            objectBuilder.add("Data","");

            resp.getWriter().print(objectBuilder.build());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id");

        try {


            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ass1", "root", "1234");


            PreparedStatement statement = connection.prepareStatement("delete from Customer where id=?");

            statement.setObject(1, id);

            if (statement.executeUpdate() > 0) {

                JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
                objectBuilder.add("state", "OK");
                objectBuilder.add("message", "Successfully Deleted.....");
                objectBuilder.add("Data", " ");
                resp.getWriter().print(objectBuilder.build());
            }

        } catch (Exception e) {
            e.printStackTrace();
            JsonObjectBuilder response = Json.createObjectBuilder();
            response.add("state", "Error");
            response.add("message", e.getMessage());
            response.add("data", "");
            resp.setStatus(400);
            resp.getWriter().print(response.build());

        }
    }
}
