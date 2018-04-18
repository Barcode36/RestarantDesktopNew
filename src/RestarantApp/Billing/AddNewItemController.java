package RestarantApp.Billing;

import RestarantApp.Network.*;
import RestarantApp.database.SqliteConnection;
import RestarantApp.model.Constants;
import RestarantApp.model.CustomerDetails;
import RestarantApp.model.LoginRequestAndResponse;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddNewItemController  implements Initializable,NetworkChangeListener {

    String strName, strMailId;
    @FXML
    TextField txtMobileNumber,txtName,txtMailId;
    @FXML
    ImageView closeButton,minizeButton;
    public static AddNewItemListener addNewItemListener;
    boolean isConnectedNetwork;
    @FXML
    ChoiceBox selectTable;
    @FXML
    TextArea txtAddress;
    ObservableList listTable = FXCollections.observableArrayList();
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    public void setAddItemListener(AddNewItemListener addItemListener)
    {
        this.addNewItemListener = addItemListener;
    }
    public void btnNewOrder(ActionEvent actionEvent) {

        if (isConnectedNetwork) {
            APIService apiInterface = RetrofitClient.getClient().create(APIService.class);
            JSONObject jsonObject = new JSONObject();
            if (txtName.getText().isEmpty()) {
                strName = "";
            } else {
                strName = txtName.getText();
            }
            if (txtMailId.getText().isEmpty()) {
                strMailId = "";
            } else {
                strMailId = txtMailId.getText();
            }
            try {
                jsonObject.put("phone", txtMobileNumber.getText());
                jsonObject.put("name", strName);
                jsonObject.put("email", strMailId);
                jsonObject.put("address", txtAddress.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Call<LoginRequestAndResponse> loginRequestAndResponseCall = apiInterface.getLoginResponse(jsonObject);
            loginRequestAndResponseCall.enqueue(new Callback<LoginRequestAndResponse>() {
                @Override
                public void onResponse(Call<LoginRequestAndResponse> call, Response<LoginRequestAndResponse> response) {
                    if (response.isSuccessful()) {
                        LoginRequestAndResponse loginRequestAndResponse = response.body();
                        if (loginRequestAndResponse.getCustomer_id() != null) {
                            LoginRequestAndResponse customer_id = LoginRequestAndResponse.getInstance();
                            customer_id.setCustomer_id(loginRequestAndResponse.getCustomer_id());
                           /* if (txtName.getText().isEmpty()) {
                                customer_id.setName("Parcel");
                            } else {
                                customer_id.setName(txtName.getText());
                            }*/

                            customer_id.setName(String.valueOf(selectTable.getSelectionModel().getSelectedItem()));
                            customer_id.setMobile_num(txtMobileNumber.getText());
                            customer_id.setCustomer_address( " ");
                            customer_id.setCustomer_email(txtMailId.getText());
                            customer_id.setCustomer_address(txtAddress.getText());
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Stage stage = (Stage) closeButton.getScene().getWindow();
                                    stage.close();
                                    addNewItemListener.addNewItem();
                                }
                            });

                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginRequestAndResponse> call, Throwable t) {

                }
            });
        }else
        {
            LoginRequestAndResponse customer_id = LoginRequestAndResponse.getInstance();
            customer_id.setCustomer_id(txtMobileNumber.getText());
            if (txtName.getText().isEmpty()) {
                customer_id.setName("Parcel");
            } else {
                customer_id.setName(txtName.getText());
            }
            customer_id.setMobile_num(txtMobileNumber.getText());
            customer_id.setCustomer_address( " ");
            customer_id.setCustomer_email(txtMailId.getText());
            customer_id.setCustomer_address(txtAddress.getText().trim());
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Stage stage = (Stage) closeButton.getScene().getWindow();
                    stage.close();
                    addNewItemListener.addNewItem();
                }
            });
        }
    }


    public void closeImageClicked(MouseEvent mouseEvent) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void minimizemageClicked(MouseEvent event) {
        Stage stage = (Stage) minizeButton.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String css = AddNewItemController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        NetworkConnection networkConnection = new NetworkConnection(AddNewItemController.this);
        listTable.add("Parcel");
        SqliteConnection sqliteConnection = new SqliteConnection();
        listTable.addAll(sqliteConnection.getAllTableData());
        selectTable.setItems(listTable);
        selectTable.getSelectionModel().selectFirst();
//        isConnectedNetwork =  networkConnection.isInternetReachable();
    }



    @Override
    public void Networkchanged(boolean isConnected) {
       isConnectedNetwork = isConnected;
    }

    public void btnSearchNumber(ActionEvent actionEvent) {

        if (isConnectedNetwork)
        {
            APIService apiInterface = RetrofitClient.getClient().create(APIService.class);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("phone",txtMobileNumber.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Call<CustomerDetails> customerDetailsCall = apiInterface.searchNumber(jsonObject);
            customerDetailsCall.enqueue(new Callback<CustomerDetails>() {
                @Override
                public void onResponse(Call<CustomerDetails> call, Response<CustomerDetails> response) {
                    if (response.isSuccessful())
                    {
                        CustomerDetails customerDetails = response.body();
                        if (customerDetails.getStatus_code().equals(Constants.Success))
                        {
                            txtMailId.setText(customerDetails.getEmail());
                            txtAddress.setText(customerDetails.getAddress());
                            txtName.setText(customerDetails.getName());
                        }else
                        {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {

                                    jfxSnackbar.show(customerDetails.getStatus_message(),5000);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<CustomerDetails> call, Throwable throwable) {

                }
            });
        }
    }
}
