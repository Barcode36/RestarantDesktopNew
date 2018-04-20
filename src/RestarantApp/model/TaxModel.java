package RestarantApp.model;

import java.util.ArrayList;

public class TaxModel {

    String status_code, status_message, success,Status_code,Success,Status_message,Order_id;
    int tot_cats;
    ArrayList<list> list = new ArrayList<>();

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public ArrayList<TaxModel.list> getList() {
        return list;
    }

    public void setList(ArrayList<TaxModel.list> list) {
        this.list = list;
    }

    public String getStatus_message() {
        return status_message;
    }

    public void setStatus_message(String status_message) {
        this.status_message = status_message;
    }

    public int getTot_cats() {
        return tot_cats;
    }

    public void setTot_cats(int tot_cats) {
        this.tot_cats = tot_cats;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public static class list {
        String id, name;
        double active,  value;
        int comp1, comp2;

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

        public double getActive() {
            return active;
        }

        public void setActive(double active) {
            this.active = active;
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

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}
