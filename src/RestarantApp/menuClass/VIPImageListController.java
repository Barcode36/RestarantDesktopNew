package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.aaditionalClass.UtilsClass;
import RestarantApp.model.Constants;
import RestarantApp.model.GalleryModel;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VIPImageListController implements Initializable {
    String outputImage = "null";
    @FXML
    TableColumn table_id,table_title,table_description,table_image,table_date;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    @FXML
    TableView<GalleryModel.VIP_Gallery_list> tableIndex;
    int totalcount = 0, remainCount = 1, dummyCount = 0, pageNum = 0;
    ObservableList<GalleryModel.VIP_Gallery_list> data;
    ArrayList pageNo = new ArrayList();
    @FXML
    ImageView imgNext,imgPrevious,imgSelectedImage,viewImagePic;
    @FXML
    AnchorPane updateImage,viewImage;
    @FXML
    Label updateImageId,viewImageId,viewImageTitle,viewImageDescription;
    @FXML
    TextField updateImageTitle,updateImageDes;
    BufferedImage bufferedImage;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        catRootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(catRootPane);

        viewImage.setVisible(false);
        updateImage.setVisible(false);

        // Set up the table data
        table_id.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.VIP_Gallery_list, Long>("vip_gal_id")
        );
        table_title.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.VIP_Gallery_list, String>("title")
        );
        table_description.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.VIP_Gallery_list, String>("description")
        );
        table_image.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.VIP_Gallery_list, String>("image")
        );
        table_date.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.VIP_Gallery_list, String>("date")
        );
        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(45);
        tableIndex.prefHeightProperty().bind(Bindings.size(tableIndex.getItems()).multiply(tableIndex.getFixedCellSize()).add(30));

        TableColumn<GalleryModel.VIP_Gallery_list, GalleryModel.VIP_Gallery_list> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<GalleryModel.VIP_Gallery_list,GalleryModel.VIP_Gallery_list>() {

                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView editButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(GalleryModel.VIP_Gallery_list person, boolean empty) {
                    super.updateItem(person, empty);

                    if (person == null) {
                        setGraphic(null);
                        return;
                    }
                    String TOOLTIP = "deleteTip",EDITTIP = "editTip";

                    Tooltip deleteTip = new Tooltip("Delete Item");
                    deleteButton.getProperties().put(TOOLTIP, deleteTip);
                    Tooltip.install(deleteButton, deleteTip);

                    Tooltip editTip = new Tooltip("Edit Item");
                    editButton.getProperties().put(EDITTIP, deleteTip);
                    Tooltip.install(editButton, editTip);

                    HBox pane = new HBox(editButton, button1);
                    pane.setAlignment(Pos.CENTER);
                    pane.setSpacing(5);
                    pane.setPadding(new Insets(10, 0, 0, 10));

                    button1.setStyle("-fx-background-color: transparent;");
                    setGraphic(pane);
                    button1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {

                            showAlert(person);

                        }
                    });

                    deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();


                        }
                    });
                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
                            viewImage.setVisible(false);
                            updateImage.setVisible(true);
                            updateImageId.setText(person.getVip_gal_id());
                            updateImageDes.setText(person.getDescription());
                            updateImageTitle.setText(person.getTitle());
                            Image image = new Image(Constants.VIP_IMAGE_BASE_URL + person.getImage());
                            imgSelectedImage.setImage(image);
                        }
                    });
                }
            };

        });
        tableIndex.getColumns().addAll(unfriendCol);
        getData();

        imgNext.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                remainCount = remainCount + 1;
                pageNum = pageNum + 1;
                imgPrevious.setVisible(true);
                getData();
            }
        });
        imgPrevious.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                pageNum = pageNum - 1;
                remainCount = (int) pageNo.get(pageNum);
                for (int j = 0; j < pageNo.size(); j++) {
                    System.out.println("get page Number from array----->" + String.valueOf(pageNo.get(j)));
                }

                getData();

            }
        });

        tableIndex.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                GalleryModel.VIP_Gallery_list item = tableIndex.getSelectionModel().getSelectedItem();
                if (item != null) {
                    System.out.println("Call table");
                    updateImage.setVisible(false);
                    viewImage.setVisible(true);
                    setViewItem(item);
                }
            }
        });
    }

    private void setViewItem(GalleryModel.VIP_Gallery_list item) {

        viewImageId.setText(item.getVip_gal_id());
        viewImageTitle.setText(item.getTitle());
        viewImageDescription.setText(item.getDescription());
        Image image = new Image(Constants.VIP_IMAGE_BASE_URL + item.getImage());
        viewImagePic.setImage(image);
    }



    public void btnUpdateAction(ActionEvent actionEvent) {

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
                        imgSelectedImage.setImage(image);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void showAlert(GalleryModel.VIP_Gallery_list list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this  Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            deleteItem(list);
        } else  {
            alert.close();

        }

    }

    private void deleteItem(GalleryModel.VIP_Gallery_list list) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("vip_gal_id", list.getVip_gal_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deletVIPImage(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());   Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(),5000);
                        }
                    });
                    if (requestAndResponseModel.getStatus_code().equals(Constants.Success))
                    {

                        data.remove(list);

                    }

                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });
    }
    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<GalleryModel> call = retrofitClient.getImageGallery(jsonObject);
        call.enqueue(new Callback<GalleryModel>() {
            @Override
            public void onResponse(Call<GalleryModel> call, Response<GalleryModel> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    } else {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    GalleryModel galleryModel = response.body();

                    ArrayList getItemDetils = galleryModel.getVIP_Gallery_list();
                    totalcount = galleryModel.getTot_vip_gallery();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        GalleryModel.VIP_Gallery_list list = ( GalleryModel.VIP_Gallery_list) getItemDetils.get(i);
                        data.add(list);

                        dummyCount = dummyCount + 1;
                    }
                    remainCount = dummyCount;
                    System.out.println("in response remain count--->" + String.valueOf(remainCount));
                    tableIndex.setItems(data);
                    if (totalcount >= remainCount) {
                        imgNext.setVisible(true);
                    }

                    if (remainCount == totalcount) {
                        imgNext.setVisible(false);

                    } else {
                        imgNext.setVisible(true);
                    }
                    if (pageNum == 0) {
                        imgPrevious.setVisible(false);
                    } else {
                        imgPrevious.setVisible(true);
                    }

                }
            }

            @Override
            public void onFailure(Call<GalleryModel> call, Throwable throwable) {

            }
        });
    }


    public void sendEditCategoryDetails() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        if (bufferedImage != null)
            outputImage = UtilsClass.encodeToString(bufferedImage, "png");
        else
            outputImage = " ";

        try {
            jsonObject.put("description", updateImageDes.getText());
            jsonObject.put("title", updateImageTitle.getText());
            jsonObject.put("image", outputImage);
            jsonObject.put("vip_gal_id",updateImageId.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.editVIPImage(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getStatus_message(), 5000);
                            updateImage.setVisible(false);

                        }
                    });

                    data.clear();
                    remainCount = (int) pageNo.get(pageNum);
                    getData();


                }
            }

            @Override
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });

    }

    public void btnUpdateImage(MouseEvent mouseEvent) {

        sendEditCategoryDetails();
    }
}
