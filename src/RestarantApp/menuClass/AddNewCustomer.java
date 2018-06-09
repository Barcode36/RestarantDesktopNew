package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.NetworkChangeListener;
import RestarantApp.Network.NetworkConnection;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.LoginRequestAndResponse;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ResourceBundle;

public class AddNewCustomer implements Initializable,NetworkChangeListener {
    boolean isConnectedNetwork;
    String strName, strMailId;
    JFXSnackbar jfxSnackbar;
    @FXML
    TextField txtMobileNumber,txtName,txtMailId;
    @FXML
    StackPane catRootPane;
    @FXML
    TextArea txtAddress;
    @FXML
    ProgressIndicator progressAddCustomer;
    @FXML
    Button btnCancel,btnNewCustomer;
    public Boolean isNew;
    LoginRequestAndResponse loginRequestAndResponse = LoginRequestAndResponse.getInstance();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = AddNewCustomer.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
        progressAddCustomer.setVisible(false);
        NetworkConnection networkConnection = new NetworkConnection(AddNewCustomer.this);
        isConnectedNetwork =  networkConnection.isInternetReachable();

        System.out.println("From value---->"+loginRequestAndResponse.isFromWhere());
        if (loginRequestAndResponse.isFromWhere())
        {
            btnCancel.setVisible(true);
            loginRequestAndResponse.setFromWhere(false);
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtMobileNumber.requestFocus();
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
                            btnNewCustomer.setStyle("-fx-background-color:    #008ccd; ");
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
                            btnNewCustomer.setStyle("-fx-background-color:    #008ccd; ");
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
                            btnNewCustomer.setStyle("-fx-background-color:    #008ccd; ");
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
                    btnNewCustomer.requestFocus();
                    btnNewCustomer.setStyle("-fx-background-color: #f58220; ");
//                    submitDetails();
                }
            }
        });

        btnNewCustomer.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.TAB))
                {

                    txtMobileNumber.requestFocus();
                    btnNewCustomer.setStyle("-fx-background-color:   #008ccd; ");
//                    submitDetails();
                }else if (event.getCode().equals(KeyCode.ENTER)) {
                    submitDetails();
                }
            }
        });

    }

    public void btnNewOrder(ActionEvent actionEvent) {
        if (!txtName.getText().isEmpty() && !txtMobileNumber.getText().isEmpty())
            submitDetails();
        else
            jfxSnackbar.show("Mobile Number and Name are mandatory", 5000);
    }

    public void submitDetails()
    {
        if (isConnectedNetwork) {
            progressAddCustomer.setVisible(true);
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
                        progressAddCustomer.setVisible(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                if (loginRequestAndResponse.getStatus_code().equals(Constants.Failure)) {
                                    LoginRequestAndResponse customer_id = LoginRequestAndResponse.getInstance();
                                    customer_id.setCustomer_id(loginRequestAndResponse.getCustomer_id());
                                    jfxSnackbar.show("Customer details added successfully", 5000);
                                    txtMailId.setText(" ");
                                    txtName.setText(" ");
                                    txtAddress.setText(" ");
                                    txtMobileNumber.setText(" ");

                                }else
                                {
                                    jfxSnackbar.show("Aleady Exists customer", 5000);
                                }
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<LoginRequestAndResponse> call, Throwable t) {

                }
            });

        }else
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    jfxSnackbar.show("Netwotk not connected",5000);
                }
            });
        }
    }
    public void isCancel()
    {
        isNew = true;
    }

    @Override
    public void Networkchanged(boolean isConnected) {
        isConnectedNetwork = isConnected;
    }


    public void btnCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
