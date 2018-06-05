package RestarantApp.notification;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.ItemListRequestAndResponseModel;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class NotificationController implements Initializable {

    @FXML
    ProgressIndicator progressSendNoti;
    @FXML
    TextField txtFiledTitle;
    @FXML
    TextArea txtFieldMessage;
    APIService retrofitClient;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressSendNoti.setVisible(false);
        String css = NotificationController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
    }

    public void imgSendNotification(MouseEvent mouseEvent) {

        System.out.println("Enter send notification");
        progressSendNoti.setVisible(true);
        if (!txtFieldMessage.getText().isEmpty() && !txtFiledTitle.getText().isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            System.out.println(formatter.format(date));
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("messageTitle",txtFiledTitle.getText());
                jsonObject.put("messageMessage",txtFieldMessage.getText());
                jsonObject.put("messageDate",formatter.format(date));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String notifiyMessage = jsonObject.toString();
            GcmSender gcmSender = new GcmSender(notifiyMessage);
//            sendMsgToServer();
            progressSendNoti.setVisible(true);
        }
    }


    public void sendMsgToServer()
    {
        retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("title",txtFiledTitle.getText());
            jsonObject.put("notification",txtFieldMessage.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> getRequestResponse = retrofitClient.sendNotification(jsonObject);
        getRequestResponse.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful())
                {
                    progressSendNoti.setVisible(false);
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getSuccessCode().equals(Constants.Success))
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                                txtFieldMessage.setText("");
                                txtFiledTitle.setText("");
                            }
                        });

                    }else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                                jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(),5000);
                            }
                        });

                    }
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }
}
