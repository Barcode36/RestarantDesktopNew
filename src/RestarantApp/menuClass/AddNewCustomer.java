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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
    Button btnCancel;
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

    }

    public void btnNewOrder(ActionEvent actionEvent) {
        submitDetails();
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
