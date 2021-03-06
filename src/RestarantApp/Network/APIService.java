package RestarantApp.Network;

import RestarantApp.model.*;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @FormUrlEncoded
    @POST("check_client_details.php")
    Call<LoginRequestAndResponse> getLoginResponse(@Field("x") JSONObject jsonObject);

    @FormUrlEncoded
    @POST("category_add.php")
    Call<RequestAndResponseModel> sendCategoryDetails(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("veraity_add.php")
    Call<RequestAndResponseModel> sendVarityDetails(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("veraity_view.php")
    Call<RequestAndResponseModel> viewVariety(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("veraity_delete.php")
    Call<RequestAndResponseModel> deleteVaiety(@Field("x") JSONObject object);

    @POST("veraity_list.php")
    Call<RequestAndResponseModel> listVaiety();

    @FormUrlEncoded
    @POST("category_view.php")
    Call<RequestAndResponseModel> viewCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("category_delete.php")
    Call<RequestAndResponseModel> deleteCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("category_edit.php")
    Call<RequestAndResponseModel> editCategory(@Field("x") JSONObject object);

    @POST("category_list.php")
    Call<RequestAndResponseModel> categoryList();

    @FormUrlEncoded
    @POST("cat_subcat_list.php")
    Call<SubCatagoryList> getSubCatagoryList(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_add.php")
    Call<RequestAndResponseModel> sendItemDetails(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_view.php")
    Call<ItemListRequestAndResponseModel> itemView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_edit.php")
    Call<RequestAndResponseModel> itemEdit(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("item_delete.php")
    Call<RequestAndResponseModel> deleteItem(@Field("x") JSONObject object);

    @POST("tax_list.php")
    Call<RequestAndResponseModel> getTaxList();

    @POST("tax_list.php")
    Call<TaxModel> getTaxList1();

    @FormUrlEncoded
    @POST("tax_add.php")
    Call<RequestAndResponseModel> addTax(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_view.php")
    Call<ItemListRequestAndResponseModel> taxView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_delete.php")
    Call<RequestAndResponseModel> deleteTax(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("tax_edit.php")
    Call<RequestAndResponseModel> editTax(@Field("x") JSONObject object);

    @POST("item_list.php")
    Call<ItemListRequestAndResponseModel> getItemList();

    @FormUrlEncoded
    @POST("combo_add.php")
    Call<RequestAndResponseModel> addComboItem(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_view.php")
    Call<ItemListRequestAndResponseModel> comboView(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_delete.php")
    Call<RequestAndResponseModel> deleteCombo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("combo_edit.php")
    Call<RequestAndResponseModel> editCombo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("table_add.php")
    Call<RequestAndResponseModel> addTable(@Field("x") JSONObject object);

    @POST("table_list.php")
    Call<ItemListRequestAndResponseModel> getTableList();

    @FormUrlEncoded
    @POST("table_delete.php")
    Call<RequestAndResponseModel> deleteTable(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("table_edit.php")
    Call<RequestAndResponseModel> updateTable(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("place_order.php")
    Call<RequestAndResponseModel> placeOrder(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("client_details_by_phone.php")
    Call<CustomerDetails> searchNumber(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("current_ip.php")
    Call<LoginRequestAndResponse> sendIp(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("customer_list.php")
    Call<CustomerDetails> getCustomerList(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("search_customer.php")
    Call<CustomerDetails> getSearchResult(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("sub_category_add.php")
    Call<RequestAndResponseModel> addSubCatagory(@Field("x") JSONObject object);


    @FormUrlEncoded
    @POST("sub_category_view.php")
    Call<RequestAndResponseModel> viewSubCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("sub_category_delete.php")
    Call<RequestAndResponseModel> deleteSubCategory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("sub_category_edit.php")
    Call<RequestAndResponseModel> editSubCategory(@Field("x") JSONObject object);


    @POST("sub_category_list.php")
    Call<RequestAndResponseModel> getSubCatagoryList();


    @FormUrlEncoded
    @POST("notification_add.php")
    Call<RequestAndResponseModel> sendNotification(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("vip_gallery_add.php")
    Call<RequestAndResponseModel> sendVIPImage(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("video_gallery_add.php")
    Call<RequestAndResponseModel> sendVideo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("vip_gallery_view.php")
    Call<GalleryModel> getImageGallery(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("vip_gallery_delete.php")
    Call<RequestAndResponseModel> deletVIPImage(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("vip_gallery_edit.php")
    Call<RequestAndResponseModel> editVIPImage(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("video_gallery_view.php")
    Call<GalleryModel> getVideoGallery(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("video_gallery_delete.php")
    Call<RequestAndResponseModel> deletVideo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("video_gallery_edit.php")
    Call<RequestAndResponseModel> editVideo(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("customer_history_details.php")
    Call<HistoryDetails> getHistory(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("testimony_view.php")
    Call<TestimonyDetails> getTestimony(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("testimony_delete.php")
    Call<TestimonyDetails> deleteTestimony(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("testimony_approve.php")
    Call<TestimonyDetails> approveTestimony(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("sales_report.php")
    Call<ReportDeatils> getReportBasedOnDaily(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("suggestions_view.php")
    Call<RequestAndResponseModel> getSuggestionList(@Field("x") JSONObject object);

    @FormUrlEncoded
    @POST("update_order.php")
    Call<RequestAndResponseModel> updateOrederId(@Field("x") JSONObject object);

}
