package RestarantApp.Billing;

import java.util.HashMap;

public class BillingSaveModel {
    int oreder_id;
    String item_id,qty,discount_amt,table_no,net_amt,gross_amt,tax_amt,payment_method;
    HashMap<String,Boolean> getSetKot = new HashMap<>();
    private static BillingSaveModel ourInstance = new BillingSaveModel();
    public static BillingSaveModel getInstance() {
        return ourInstance;
    }

    public HashMap<String, Boolean> getGetSetKot() {
        return getSetKot;
    }

    public void setGetSetKot(HashMap<String, Boolean> getSetKot) {
        this.getSetKot = getSetKot;
    }

    public int getOreder_id() {
        return oreder_id;
    }

    public void setOreder_id(int oreder_id) {
        this.oreder_id = oreder_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getDiscount_amt() {
        return discount_amt;
    }

    public void setDiscount_amt(String discount_amt) {
        this.discount_amt = discount_amt;
    }

    public String getTable_no() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    public String getNet_amt() {
        return net_amt;
    }

    public void setNet_amt(String net_amt) {
        this.net_amt = net_amt;
    }

    public String getGross_amt() {
        return gross_amt;
    }

    public void setGross_amt(String gross_amt) {
        this.gross_amt = gross_amt;
    }

    public String getTax_amt() {
        return tax_amt;
    }

    public void setTax_amt(String tax_amt) {
        this.tax_amt = tax_amt;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }
}
