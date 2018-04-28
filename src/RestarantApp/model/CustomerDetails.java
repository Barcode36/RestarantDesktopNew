package RestarantApp.model;

import java.util.ArrayList;

public class CustomerDetails {
    String Status_code,Status_message,Customer_id,Name,Email,Address,no_of_visit,Success,PhoneNum;
    int tot_customers;
    ArrayList<customer_list>customer_list = new ArrayList<customer_list>();
    ArrayList<customer_list>Customer_Details = new ArrayList<customer_list>();

    public ArrayList<CustomerDetails.customer_list> getCustomer_Details() {
        return Customer_Details;
    }

    public void setCustomer_Details(ArrayList<CustomerDetails.customer_list> customer_Details) {
        Customer_Details = customer_Details;
    }

    public String getPhoneNum() {
        return PhoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        PhoneNum = phoneNum;
    }

    public int getTot_customers() {
        return tot_customers;
    }

    public void setTot_customers(int tot_customers) {
        this.tot_customers = tot_customers;
    }



    public ArrayList<CustomerDetails.customer_list> getCustomer_list() {
        return customer_list;
    }

    public void setCustomer_list(ArrayList<CustomerDetails.customer_list> customer_list) {
        this.customer_list = customer_list;
    }

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public String getStatus_message() {
        return Status_message;
    }

    public void setStatus_message(String status_message) {
        Status_message = status_message;
    }

    public String getCustomer_id() {
        return Customer_id;
    }

    public void setCustomer_id(String customer_id) {
        Customer_id = customer_id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getNo_of_visit() {
        return no_of_visit;
    }

    public void setNo_of_visit(String no_of_visit) {
        this.no_of_visit = no_of_visit;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public class customer_list
    {
        String name,phone,email,address,no_of_visit;

        public String getNo_of_visit() {
            return no_of_visit;
        }

        public void setNo_of_visit(String no_of_visit) {
            this.no_of_visit = no_of_visit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
