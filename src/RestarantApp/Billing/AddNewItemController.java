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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.commons.collections4.BagUtils;
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
    @FXML
    Button btnNewOrder;
    public void setAddItemListener(AddNewItemListener addItemListener)
    {
        this.addNewItemListener = addItemListener;
    }
    public void btnNewOrder(ActionEvent actionEvent) {
        submitDetails();

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

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtMobileNumber.requestFocus();
                btnNewOrder.setStyle("-fx-background-color:  #008ccd; ");
            }
        });

        txtMobileNumber.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER))
                {
                    System.out.print("Enter key");
                    searchMobileNumber();
                }else if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnNewOrder.setStyle("-fx-background-color:  #008ccd; ");
                            txtName.requestFocus();

                        }
                    });

                }
            }
        });
        txtName.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtMailId.requestFocus();
                            btnNewOrder.setStyle("-fx-background-color:  #008ccd; ");
                        }
                    });


                }
            }
        });
        txtMailId.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtAddress.requestFocus();
                            btnNewOrder.setStyle("-fx-background-color:  #008ccd; ");
                        }
                    });


                }
            }
        });
        txtAddress.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {
                    String s = txtAddress.getText().trim();
                    txtAddress.setText("");
                    txtAddress.setText(s);
                    txtAddress.positionCaret(txtAddress.getText().length());
                    btnNewOrder.requestFocus();
                    btnNewOrder.setStyle("-fx-background-color: #ff0000; ");
//                    submitDetails();
                }
            }
        });

        btnNewOrder.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {

                    txtMobileNumber.requestFocus();
                    btnNewOrder.setStyle("-fx-background-color:  #008ccd; ");
//                    submitDetails();
                }else if (event.getCode().equals(KeyCode.ENTER)) {
                    submitDetails();
                }
            }
        });
//        isConnectedNetwork =  networkConnection.isInternetReachable();
    }



    @Override
    public void Networkchanged(boolean isConnected) {
       isConnectedNetwork = isConnected;
    }

    public void btnSearchNumber(ActionEvent actionEvent) {

        searchMobileNumber();

    }

    public void searchMobileNumber()
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

                        isConnectedNetwork = true;
                    }
                }

                @Override
                public void onFailure(Call<CustomerDetails> call, Throwable throwable) {

                }
            });

    }

    public void submitDetails()
    {
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
                            if (txtName.getText().isEmpty())
                            {
                                customer_id.setName(String.valueOf(selectTable.getSelectionModel().getSelectedItem()));
                            } else {
                                customer_id.setName(txtName.getText());
                            }

//                            customer_id.setName(txtName.getText());
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
}
