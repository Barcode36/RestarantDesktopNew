package RestarantApp.database;

import RestarantApp.Billing.BillingSaveModel;

import java.sql.*;
import java.util.ArrayList;

public class SqliteConnection {

    PreparedStatement preparedStatement = null;
    public static Connection connector()
    {
        try {

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:prawnandcrab.db");
            return connection;
        }catch (Exception e)
        {
            System.out.println(e);
            return null;
        }
    }
    public void insertDataToOrderMaster(int Order_no,String customer_id,int count)
    {
        String query = "INSERT INTO ORDER_MASTER (ORDER_NO,CUSTOMER_ID) VALUES (?,?)";
        String query1 = "INSERT INTO ORDER_MASTER (CUSTOMER_ID) VALUES (?)";
        preparedStatement = null;

        try {

            if (count == 0) {
                preparedStatement = connector().prepareStatement(query);
                preparedStatement.setInt(1, Order_no);
                preparedStatement.setString(2, customer_id);
                preparedStatement.execute();
                preparedStatement.close();
            }else
            {
                preparedStatement = connector().prepareStatement(query1);
                preparedStatement.setString(1, customer_id);
                preparedStatement.execute();
                preparedStatement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void insertDataToOrderITEMMaster(int Order_no,String item_id,String qty)
    {
        String query = "INSERT INTO ORDER_ITEM_MASTER (ORDER_ID,ITEM_ID,QTY) VALUES (?,?,?)";
        preparedStatement = null;

        try {

                preparedStatement = connector().prepareStatement(query);
                preparedStatement.setInt(1, Order_no);
                preparedStatement.setString(2, item_id);
                preparedStatement.setString(3, qty);
                preparedStatement.execute();
                preparedStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertDataToOrderAmountMaster(int Order_no,String table_number,String net_amt,String tax_amt,String gross_amt,String discount_amt,String payment_method)
    {
        String query = "INSERT INTO ORDER_AMOUNT_MASTER (ORDER_ID,TABLE_NO,NET_AMT,TAX_AMT,GROSS_AMT,DISCOUNT_AMT,PAYMENT_METHOD) VALUES (?,?,?,?,?,?,?)";
        preparedStatement = null;

        try {

            preparedStatement = connector().prepareStatement(query);
            preparedStatement.setInt(1, Order_no);
            preparedStatement.setString(2, table_number);
            preparedStatement.setString(3, net_amt);
            preparedStatement.setString(4, tax_amt);
            preparedStatement.setString(5, gross_amt);
            preparedStatement.setString(6, discount_amt);
            preparedStatement.setString(7, payment_method);
            preparedStatement.execute();
            preparedStatement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }




    public int tableCount()
    {
        int count = 0;
        ResultSet rs = null;
        String query ="SELECT COUNT(*) FROM ORDER_MASTER";
        try {
            preparedStatement = connector().prepareStatement(query);
             rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int numberOfRows = rs.getInt(1);
                System.out.println("numberOfRows= " + numberOfRows);
                count = numberOfRows;
            } else {
                System.out.println("error: could not get the record counts");
            }
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public int getLastRow()
    {
        int lastOrder_id =0;
        ResultSet rs;
        String query ="SELECT * FROM    ORDER_MASTER WHERE   ORDER_NO = (SELECT MAX(ORDER_NO)  FROM ORDER_MASTER);";
        preparedStatement = null;
        try {
            preparedStatement = connector().prepareStatement(query);
            rs = preparedStatement.executeQuery();

            while (rs.next())
            {
                lastOrder_id =   rs.getInt(1);

                System.out.println("value-->"+lastOrder_id );
            }
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return lastOrder_id;
    }
    public ArrayList getAllData()
    {
        ArrayList<BillingSaveModel> getAllData = new ArrayList();

        ResultSet rs;
        String query ="SELECT  ORDER_MASTER.ORDER_NO,ORDER_ITEM_MASTER.ITEM_ID,ORDER_ITEM_MASTER.QTY,ORDER_AMOUNT_MASTER.DISCOUNT_AMT,ORDER_AMOUNT_MASTER.TABLE_NO\n" +
                ",ORDER_AMOUNT_MASTER.NET_AMT,ORDER_AMOUNT_MASTER.GROSS_AMT,ORDER_AMOUNT_MASTER.TAX_AMT,ORDER_AMOUNT_MASTER.PAYMENT_METHOD\n" +
                "FROM ORDER_MASTER INNER JOIN ORDER_ITEM_MASTER ON ORDER_MASTER.ORDER_NO = ORDER_ITEM_MASTER.ORDER_ID\n" +
                "INNER JOIN ORDER_AMOUNT_MASTER ON ORDER_MASTER.ORDER_NO = ORDER_AMOUNT_MASTER.ORDER_ID;";
        preparedStatement = null;
        try {
            preparedStatement = connector().prepareStatement(query);
            rs = preparedStatement.executeQuery();

            while (rs.next())
            {
                BillingSaveModel billingSaveModel = new BillingSaveModel();
               int order_id =   rs.getInt(1);
                String item_id =   rs.getString(2);
                String qty =   rs.getString(3);
                String dis_amt =   rs.getString(4);
                String table_no =   rs.getString(5);
                String net_amt =   rs.getString(6);
                String gross_amt =   rs.getString(7);
                String tax_amt =   rs.getString(8);
                String payment_mode =   rs.getString(9);
                billingSaveModel.setOreder_id(order_id);
                billingSaveModel.setItem_id(item_id);
                billingSaveModel.setQty(qty);
                billingSaveModel.setDiscount_amt(dis_amt);
                billingSaveModel.setTable_no(table_no);
                billingSaveModel.setNet_amt(net_amt);
                billingSaveModel.setGross_amt(gross_amt);
                billingSaveModel.setTax_amt(tax_amt);
                billingSaveModel.setPayment_method(payment_mode);
                getAllData.add(billingSaveModel);

            }
            preparedStatement.close();
            rs.close();
        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }

        return getAllData;
    }

}
