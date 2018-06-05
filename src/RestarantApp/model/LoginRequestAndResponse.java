package RestarantApp.model;

public class LoginRequestAndResponse {

    private static LoginRequestAndResponse ourInstance = new LoginRequestAndResponse();
    public static LoginRequestAndResponse getInstance() {
        return ourInstance;
    }

    public String status_code,status_message,no_of_visit,success,customer_id,name,mobile_num,customer_email,customer_address,Status_code;
    boolean fromWhere;

    public boolean isFromWhere() {
        return fromWhere;
    }

    public void setFromWhere(boolean fromWhere) {
        this.fromWhere = fromWhere;
    }

    public String getMobile_num() {
        return mobile_num;
    }

    public void setMobile_num(String mobile_num) {
        this.mobile_num = mobile_num;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatusCode() {
        return Status_code;
    }

    public void setStatusCode(String status_code) {
        this.Status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getNo_of_visit() {
        return no_of_visit;
    }

    public void setNo_of_visit(String no_of_visit) {
        this.no_of_visit = no_of_visit;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
}
