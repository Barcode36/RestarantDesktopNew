package RestarantApp.model;

import java.util.ArrayList;

public class TestimonyDetails   {
    private String Status_code,Status_message,testimonyId,customerName,testimonyImage,testimonyMessage,testimonyDate,status_message,status_code
            ,testimonyApproved;
   private int tot_testimony;

    public String getTestimonyApproved() {
        return testimonyApproved;
    }

    public void setTestimonyApproved(String testimonyApproved) {
        this.testimonyApproved = testimonyApproved;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getStatusMessage() {
        return Status_message;
    }

    public void setStatusMessage(String status_message) {
        this.Status_message = status_message;
    }

    public String getStatuscode() {
        return status_code;
    }

    public void setStatuscode(String status_code) {
        this.status_code = status_code;
    }


    public int getTot_testimony() {
        return tot_testimony;
    }

    public void setTot_testimony(int tot_testimony) {
        this.tot_testimony = tot_testimony;
    }

    public String getTestimonyImage() {
        return testimonyImage;
    }

    public void setTestimonyImage(String testimonyImage) {
        this.testimonyImage = testimonyImage;
    }

    public String getTestimonyDate() {
        return testimonyDate;
    }

    public void setTestimonyDate(String testimonyDate) {
        this.testimonyDate = testimonyDate;
    }

    public String getTestimonyId() {
        return testimonyId;
    }

    public void setTestimonyId(String testimonyId) {
        this.testimonyId = testimonyId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerId) {
        this.customerName = customerId;
    }

    public String getTestimonyMessage() {
        return testimonyMessage;
    }

    public void setTestimonyMessage(String testimonyMessage) {
        this.testimonyMessage = testimonyMessage;
    }

    ArrayList<testimony_list>testimony_list = new ArrayList<>();

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public ArrayList<TestimonyDetails.testimony_list> getTestimony_list() {
        return testimony_list;
    }

    public void setTestimony_list(ArrayList<TestimonyDetails.testimony_list> testimony_list) {
        this.testimony_list = testimony_list;
    }

    public class testimony_list{
        String testimony_id,customer_name,message,image,date,approved;

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getApproved() {
            return approved;
        }

        public void setApproved(String approved) {
            this.approved = approved;
        }

        public String getTestimony_id() {
            return testimony_id;
        }

        public void setTestimony_id(String testimony_id) {
            this.testimony_id = testimony_id;
        }

        public String getCustomer_id() {
            return customer_name;
        }

        public void setCustomer_id(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
