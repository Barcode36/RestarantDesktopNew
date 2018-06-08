package RestarantApp.model;

import java.util.ArrayList;

public class RequestAndResponseModel {

    String status_code, status_message, success,Status_code,Success,Status_message,Order_id;
    int tot_cats,tot_suggestion;
    ArrayList<cat_list> cat_list = new ArrayList<>();
    ArrayList<variety_id_list> Variety_id_list = new ArrayList<>();
    ArrayList<suggestion_list> suggestion_list = new ArrayList<>();

    public ArrayList<suggestion_list> getSuggestion_list() {
        return suggestion_list;
    }

    public int getTot_suggestion() {
        return tot_suggestion;
    }

    public void setTot_suggestion(int tot_suggestion) {
        this.tot_suggestion = tot_suggestion;
    }

    public void setSuggestion_list(ArrayList<suggestion_list> suggestion_list) {
        this.suggestion_list = suggestion_list;
    }

    public ArrayList<RequestAndResponseModel.variety_id_list> getVariety_id_list() {
        return Variety_id_list;
    }

    public void setVariety_id_list(ArrayList<RequestAndResponseModel.variety_id_list> variety_id_list) {
        Variety_id_list = variety_id_list;
    }

    public String getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(String order_id) {
        Order_id = order_id;
    }

    public ArrayList<RequestAndResponseModel.cat_list> getCat_list() {
        return cat_list;
    }

    public void setCat_list(ArrayList<RequestAndResponseModel.cat_list> cat_list) {
        this.cat_list = cat_list;
    }

    public int getTot_cats() {
        return tot_cats;
    }

    public void setTot_cats(int tot_cats) {
        this.tot_cats = tot_cats;
    }

    ArrayList<list> list = new ArrayList<>();

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public ArrayList<RequestAndResponseModel.list> getList() {
        return list;
    }

    public void setList(ArrayList<RequestAndResponseModel.list> list) {
        this.list = list;
    }

    public void setSuccessList(String Success)
    {
        this.Success = Success;
    }

    public String getSuccessList() {
        return this.Success;
    }
    public void setSuccessMessage(String Message)
    {
        this.Status_message = Message;
    }

    public String getSuccessMessage() {
        return Status_message;
    }

    public void setSuccessCode(String code)
    {
        this.Status_code = code;
    }

    public String getSuccessCode() {
        return Status_code;
    }
    public static class list {
        String id, name, tag_line, image;
        int active,comp1,comp2,value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getActive() {
            return active;
        }

        public int getComp1() {
            return comp1;
        }

        public void setComp1(int comp1) {
            this.comp1 = comp1;
        }

        public int getComp2() {
            return comp2;
        }

        public void setComp2(int comp2) {
            this.comp2 = comp2;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTag_line() {
            return tag_line;
        }

        public void setTag_line(String tag_line) {
            this.tag_line = tag_line;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public static class cat_list {

        String Cat_id, Cat_name,Sub_Cat_id,Sub_Cat_name,Image;

        public String getSub_Cat_id() {
            return Sub_Cat_id;
        }

        public void setSub_Cat_id(String sub_Cat_id) {
            Sub_Cat_id = sub_Cat_id;
        }

        public String getSub_Cat_name() {
            return Sub_Cat_name;
        }

        public void setSub_Cat_name(String sub_Cat_name) {
            Sub_Cat_name = sub_Cat_name;
        }

        public String getImage() {
            return Image;
        }

        public void setImage(String image) {
            Image = image;
        }

        public String getCat_id() {
            return Cat_id;
        }

        public void setCat_id(String cat_id) {
            Cat_id = cat_id;
        }

        public String getCat_name() {
            return Cat_name;
        }

        public void setCat_name(String cat_name) {
            Cat_name = cat_name;
        }
    }

    public class variety_id_list
    {
        String Variety_id, Variety_name;

        public String getVariety_id() {
            return Variety_id;
        }

        public void setVariety_id(String variety_id) {
            Variety_id = variety_id;
        }

        public String getVariety_name() {
            return Variety_name;
        }

        public void setVariety_name(String variety_name) {
            Variety_name = variety_name;
        }
    }

    public class suggestion_list{
        String id,customer_name,suggestion;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCustomer_name() {
            return customer_name;
        }

        public void setCustomer_name(String customer_name) {
            this.customer_name = customer_name;
        }

        public String getSuggestion() {
            return suggestion;
        }

        public void setSuggestion(String suggestion) {
            this.suggestion = suggestion;
        }
    }
}