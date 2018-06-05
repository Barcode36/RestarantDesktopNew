package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ResourceBundle;

public class VideoController implements Initializable {
    @FXML
    ProgressIndicator progressUploadVideo;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    @FXML
    TextField txtTitle,txtVideoLink;
    @FXML
    TextArea txtDescription;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        progressUploadVideo.setVisible(false);
        String css = VIPController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

    }

    public void uploadVideo(ActionEvent actionEvent) {
        progressUploadVideo.setVisible(true);
        sendVIPVideoDetails();
    }


    private void sendVIPVideoDetails() {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("title", txtTitle.getText());
            jsonObject.put("description", txtDescription.getText());
            jsonObject.put("video", txtVideoLink.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> sendVipGallery = retrofitClient.sendVideo(jsonObject);
        sendVipGallery.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    progressUploadVideo.setVisible(false);
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                        }
                    });

                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {
                        txtDescription.setText("");
                        txtTitle.setText("");
                        txtVideoLink.setText("");

                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        jfxSnackbar.show(throwable.getMessage(),5000);
                        progressUploadVideo.setVisible(false);
                    }
                });
            }
        });
    }
}
