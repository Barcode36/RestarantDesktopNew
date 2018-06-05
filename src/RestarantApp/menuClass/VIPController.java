package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VIPController implements Initializable {

    @FXML
    ProgressIndicator progressVip;
    @FXML
    TextArea txtDes;
    @FXML
    TextField txtTitle;
    @FXML
    Button btnUploadImg,btnUpload;
    @FXML
    StackPane catRootPane;
    BufferedImage bufferedImage;
    @FXML
    ImageView imgUploadImg;
    String outputImage;
    JFXSnackbar jfxSnackbar;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        progressVip.setVisible(false);
        String css = VIPController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);
    }

    public void uploadImg(ActionEvent actionEvent) {

        try {
            File file = UtilsClass.selectImage();
            if (file != null) {
                double bytes = file.length();
                double kilobytes = (bytes / 1024);
                System.out.println("file size-->" + String.valueOf(kilobytes));
                if (kilobytes < 250) {
                    bufferedImage = ImageIO.read(file);
                    if (bufferedImage != null) {
                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                        imgUploadImg.setImage(image);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void uploadImgtoServer(ActionEvent actionEvent) {

        progressVip.setVisible(true);
        sendVIPImageDetails();
    }

    private void sendVIPImageDetails() {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");

        try {
            jsonObject.put("title", txtTitle.getText());
            jsonObject.put("description", txtDes.getText());
            jsonObject.put("image", outputImage);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> sendVipGallery = retrofitClient.sendVIPImage(jsonObject);
        sendVipGallery.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    progressVip.setVisible(false);
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
                        txtDes.setText("");
                        txtTitle.setText("");
                        imgUploadImg.setImage(null);

                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        jfxSnackbar.show(throwable.getMessage(),5000);
                        progressVip.setVisible(false);
                    }
                });
            }
        });
    }
}
