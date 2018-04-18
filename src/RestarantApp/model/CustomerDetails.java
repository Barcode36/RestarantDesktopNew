package RestarantApp.model;

public class CustomerDetails {
    String Status_code,Status_message,Customer_id,Name,Email,Address,no_of_visit,Success;

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
}
