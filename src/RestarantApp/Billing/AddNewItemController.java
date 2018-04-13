package RestarantApp.Billing;

import RestarantApp.Network.*;
import RestarantApp.model.LoginRequestAndResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
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
    TextArea txtAddress;
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
                jsonObject.put("dob", "");
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
                            if (txtName.getText().isEmpty()) {
                                customer_id.setName("Parcel");
                            } else {
                                customer_id.setName(txtName.getText());
                            }
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
        NetworkConnection networkConnection = new NetworkConnection(AddNewItemController.this);

//        isConnectedNetwork =  networkConnection.isInternetReachable();
    }

    @Override
    public void Networkchanged(boolean isConnected) {
       isConnectedNetwork = isConnected;
    }
}
