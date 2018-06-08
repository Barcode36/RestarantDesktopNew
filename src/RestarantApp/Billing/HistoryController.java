package RestarantApp.Billing;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.EditingCell;
import RestarantApp.aaditionalClass.NotesEditingCell;
import RestarantApp.model.Constants;
import RestarantApp.model.HistoryDetails;
import RestarantApp.model.ItemListRequestAndResponseModel;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class HistoryController implements Initializable {
    @FXML
    ImageView imgClose;
    @FXML
    TextField txtName, txtMobileNumber;
    private APIService retrofitService;
    @FXML
    TableView<HistoryDetails> tableHistory;
    @FXML
    TableColumn tableColName,tableColCustId,tableColCustDate,tableColCustItem;
    ObservableList<HistoryDetails> historyDetailsLists = FXCollections.observableArrayList();
    HashMap<String,String> itemDetails ;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        retrofitService = RetrofitClient.getClient().create(APIService.class);
        txtMobileNumber.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(newValue);
                searchHistoryNumber(newValue,"");
            }
        });

        txtName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchHistoryNumber("",newValue);
            }
        });


        tableHistory.setEditable(true);
        tableColCustId.setResizable(false);
        tableColCustId.setCellValueFactory(new PropertyValueFactory<HistoryDetails,Integer>("customerId"));
        tableColName.setCellValueFactory(new PropertyValueFactory<HistoryDetails,Integer>("customerName"));
        tableColCustDate.setCellValueFactory(
                new PropertyValueFactory<HistoryDetails,String>("historyDate")
        );
        tableColCustItem.setCellValueFactory(
                new PropertyValueFactory<HistoryDetails,String>("items")
        );



        tableHistory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableHistory.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableHistory.setFixedCellSize(50);

        txtName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtMobileNumber.requestFocus();
                        }
                    });
                }
            }
        });

        txtMobileNumber.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {

                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtName.requestFocus();
                        }
                    });
                }
            }
        });

        getItemList();
    }

    private void getItemList() {

        Call<ItemListRequestAndResponseModel> getItemCall = retrofitService.getItemList();
        getItemCall.enqueue(new Callback<ItemListRequestAndResponseModel>() {
            @Override
            public void onResponse(Call<ItemListRequestAndResponseModel> call, Response<ItemListRequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    ItemListRequestAndResponseModel listRequestAndResponseModel = response.body();
                    itemDetails = new HashMap<>();
                    for (int j= 0 ; j < listRequestAndResponseModel.getItem_list().size() ; j++)
                    {
                        ItemListRequestAndResponseModel.item_list item_list = listRequestAndResponseModel.getItem_list().get(j);
                        itemDetails.put(item_list.getShort_code(),item_list.getItem_name());
                    }

                }
            }

            @Override
            public void onFailure(Call<ItemListRequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void viewCloseImageAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) imgClose.getScene().getWindow();
        stage.close();
    }

    public void imgSearch(MouseEvent mouseEvent) {
    }


    public void searchHistoryNumber(String phone,String  cust_name){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone_no",phone);
            jsonObject.put("cust_name",cust_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<HistoryDetails> placeOrder = retrofitService.getHistory(jsonObject);
        placeOrder.enqueue(new Callback<HistoryDetails>() {
            @Override
            public void onResponse(Call<HistoryDetails> call, Response<HistoryDetails> response) {

                if (response.isSuccessful())
                {
                    HistoryDetails historyDetails = response.body();
                    historyDetailsLists = FXCollections.observableArrayList();
                    if (historyDetails.getStatus_code().equals(Constants.Success)) {
                        ArrayList<HistoryDetails.customer_list> customer_lists = historyDetails.getCustomer_list();

                        for (int i = 0; i < customer_lists.size(); i++) {
                            HistoryDetails.customer_list customer_list = customer_lists.get(i);
                            System.out.println("customer id---->" + customer_list.getCustomer_id());
                            HistoryDetails historyDetailsList = new HistoryDetails();
                            historyDetailsList.setCustomerName(customer_list.getCustomer_name());
                            historyDetailsList.setCustomerId(customer_list.getCustomer_id());
                            historyDetailsList.setHistoryDate(customer_list.getDate());
                            ArrayList<HistoryDetails.items> items = customer_list.getItems();
                            String itemList = "";
                            if (items !=  null)
                            {

                                for (int j = 0 ; j < items.size() ; j ++){

                                    HistoryDetails.items  item = items.get(j);
                                    String itemIds = itemDetails.get(item.getItem_id());
                                    itemList = itemList + itemIds + ",";
                                }


                                itemList = itemList.substring(0,itemList.length()-1);
                                historyDetailsList.setItems(itemList);
                            }else if (customer_list.getStatus_code().equals(Constants.Failure))
                            {
                                historyDetailsList.setItems(itemList);
                            }
                            historyDetailsLists.add(historyDetailsList);

                        }
                    }
                    tableHistory.setItems(historyDetailsLists);
                }

            }

            @Override
            public void onFailure(Call<HistoryDetails> call, Throwable throwable) {

            }
        });
    }
}
