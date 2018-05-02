package RestarantApp.model;

import java.util.ArrayList;

public class SubCatagoryList {
    String Status_code,Success;
    ArrayList<sub_cat_list>sub_cat_list = new ArrayList<>();
    private static SubCatagoryList ourInstance = new SubCatagoryList();

    public static SubCatagoryList getInstance() {
        return ourInstance;
    }

    private SubCatagoryList() {
    }

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public ArrayList<SubCatagoryList.sub_cat_list> getSub_cat_list() {
        return sub_cat_list;
    }

    public void setSub_cat_list(ArrayList<SubCatagoryList.sub_cat_list> sub_cat_list) {
        this.sub_cat_list = sub_cat_list;
    }

    public class sub_cat_list{
        String sub_cat_id;

        public String getSub_cat_id() {
            return sub_cat_id;
        }

        public void setSub_cat_id(String sub_cat_id) {
            this.sub_cat_id = sub_cat_id;
        }
    }
}
