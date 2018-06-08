package RestarantApp.model;

import java.util.ArrayList;

public class HistoryDetails {
   private String Status_code,customerId,customerName,historyDate,listItems;
   private ArrayList<customer_list>customer_list = new ArrayList<>();

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(String historyDate) {
        this.historyDate = historyDate;
    }

    public String getItems() {
        return listItems;
    }

    public void setItems(String items) {
        this.listItems = items;
    }

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public ArrayList<HistoryDetails.customer_list> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(ArrayList<HistoryDetails.customer_list> customer_list) {
        this.customer_list = customer_list;
    }

    public class customer_list{
        String customer_id,customer_name,date,Status_code;
        ArrayList<items> items = new ArrayList<>();

        public String getStatus_code() {
            return Status_code;
        }

        public void setStatus_code(String status_code) {
            Status_code = status_code;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public ArrayList<HistoryDetails.items> getItems() {
            return items;
        }

        public void setItems(ArrayList<HistoryDetails.items> items) {
            this.items = items;
        }
    }
    public class items{
        String item_id;

        public String getItem_id() {
            return item_id;
        }

        public void setItem_id(String item_id) {
            this.item_id = item_id;
        }
    }

}
