package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.NetworkClient;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubCategoryController implements Initializable{

    @FXML
    ImageView imgCategoryIamge,imgUpload;
    String outputImage;
    BufferedImage bufferedImage;
    private NetworkClient networkClient;
    @FXML
    StackPane catRootPane;
    @FXML
    TextField txtCatagory,txtCatagoryName;
    JFXSnackbar jfxSnackbar;
    @FXML
    ProgressIndicator progressCategory;
    ArrayList<RequestAndResponseModel.cat_list> cat_listArrayList;
    ObservableList<String> getCatagoryList;
    @FXML
    ChoiceBox<String> comboCatList;
    HashMap< String,String> catHashMap;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        networkClient = new NetworkClient();

        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        progressCategory.setVisible(false);
        progressCategory.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        imgUpload.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent event) {

                    File file = UtilsClass.selectImage();
                    if (file != null) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                double bytes = file.length();
                                double kilobytes = (bytes / 1024);
                                System.out.println("file size-->" + String.valueOf(kilobytes));
                                if (kilobytes < 250) {
                                    bufferedImage = ImageIO.read(file);
                                    if (bufferedImage != null) {
                                        Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                                        imgCategoryIamge.setImage(image);
                                    }
                                }
                                } catch(IOException ex){
                                    Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        });

                    }


            }
        });

        getCatagoryList();

    }

    private void getCatagoryList() {
        progressCategory.setVisible(true);

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);

        Call<RequestAndResponseModel> call = retrofitClient.categoryList();
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success)) {
                        cat_listArrayList = requestAndResponseModel.getCat_list();
                        getCatagoryList = FXCollections.observableArrayList();
                        catHashMap = new HashMap<>();
                        getCatagoryList.add("Select Catagory");
                        for (int j= 0  ; j < cat_listArrayList.size();j++)
                        {
                            RequestAndResponseModel.cat_list cat_list = cat_listArrayList.get(j);
                            getCatagoryList.add(cat_list.getCat_name());
                            catHashMap.put(cat_list.getCat_id(),cat_list.getCat_name());
                        }
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                        comboCatList.setItems(getCatagoryList);
                        comboCatList.getSelectionModel().selectFirst();
                            }
                        });

                    }else
                    {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                            }
                        });
                    }
                    progressCategory.setVisible(false);
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }

    public void btnAddSubCategory(ActionEvent actionEvent) {


        if (txtCatagoryName.getText().isEmpty() || comboCatList.getSelectionModel().getSelectedIndex() == 0)
        {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    jfxSnackbar.show("Enter valid data...Please !!!",5000);
                }
            });
        }else
        {
            progressCategory.setVisible(true);
            sendCategoryDetails();
        }

    }


    public void sendCategoryDetails()
    {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        outputImage = UtilsClass.encodeToString(bufferedImage,"png");

        try {
            jsonObject.put("sub_cat_name", txtCatagory.getText());
            jsonObject.put("sub_cat_tag", txtCatagoryName.getText());
            jsonObject.put("sub_cat_image", outputImage);
            jsonObject.put("parent_cat_id",UtilsClass.getKeyFromValue(catHashMap,comboCatList.getSelectionModel().getSelectedItem()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.addSubCatagory(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    progressCategory.setVisible(false);
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
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {

                        txtCatagory.setText("");
                        txtCatagoryName.setText("");
                        imgCategoryIamge.setImage(null);
                        comboCatList.getSelectionModel().selectFirst();
                            }
                        });
                    }

                }else
                {

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            progressCategory.setVisible(false);
                            jfxSnackbar.show(response.raw().networkResponse().message(),5000);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });

    }


}
