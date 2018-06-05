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

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class VideoListController implements Initializable {
    String outputImage = "null";
    @FXML
    TableColumn table_id, table_title, table_description, table_image;
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane catRootPane;
    @FXML
    TableView<GalleryModel.Video_Gallery_list> tableIndex;
    int totalcount = 0, remainCount = 1, dummyCount = 0, pageNum = 0;
    ArrayList pageNo = new ArrayList();
    ObservableList<GalleryModel.Video_Gallery_list> data;
    @FXML
    ImageView imgNext, imgPrevious;
    @FXML
    AnchorPane updateImage, viewImage;
    @FXML
    Label updateImageId, viewImageId, viewImageTitle, viewImageDescription,viewVideoLink;
    @FXML
    TextField updateImageTitle, updateImageDes,updateImageVideo;
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
                new PropertyValueFactory<GalleryModel.Video_Gallery_list, Long>("video_gal_id")
        );
        table_title.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.Video_Gallery_list, String>("title")
        );
        table_description.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.Video_Gallery_list, String>("description")
        );
        table_image.setCellValueFactory(
                new PropertyValueFactory<GalleryModel.Video_Gallery_list, String>("video")
        );

        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(45);
        tableIndex.prefHeightProperty().bind(Bindings.size(tableIndex.getItems()).multiply(tableIndex.getFixedCellSize()).add(30));

        TableColumn<GalleryModel.Video_Gallery_list, GalleryModel.Video_Gallery_list> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<GalleryModel.Video_Gallery_list, GalleryModel.Video_Gallery_list>() {

                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView editButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(GalleryModel.Video_Gallery_list person, boolean empty) {
                    super.updateItem(person, empty);

                    if (person == null) {
                        setGraphic(null);
                        return;
                    }
                    String TOOLTIP = "deleteTip", EDITTIP = "editTip";

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
                            updateImageId.setText(person.getVideo_gal_id());
                            updateImageDes.setText(person.getDescription());
                            updateImageTitle.setText(person.getTitle());
                            updateImageVideo.setText(person.getVideo());

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
                GalleryModel.Video_Gallery_list item = tableIndex.getSelectionModel().getSelectedItem();
                if (item != null) {
                    System.out.println("Call table");
                    updateImage.setVisible(false);
                    viewImage.setVisible(true);
                    setViewItem(item);
                }
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

        Call<GalleryModel> call = retrofitClient.getVideoGallery(jsonObject);
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

                    ArrayList<GalleryModel.Video_Gallery_list> getItemDetils = galleryModel.getVideo_Gallery_list();
                    totalcount = galleryModel.getTot_vip_gallery();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        GalleryModel.Video_Gallery_list list = getItemDetils.get(i);
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

    void showAlert(GalleryModel.Video_Gallery_list list) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this  Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            deleteItem(list);
        } else {
            alert.close();

        }
    }

    private void setViewItem(GalleryModel.Video_Gallery_list item) {

        viewImageId.setText(item.getVideo_gal_id());
        viewImageTitle.setText(item.getTitle());
        viewImageDescription.setText(item.getDescription());
        viewVideoLink.setText(item.getVideo());

    }

    private void deleteItem(GalleryModel.Video_Gallery_list list) {

        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("video_gal_id", list.getVideo_gal_id());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.deletVideo(jsonObject);
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
    public void sendEditCategoryDetails() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("description", updateImageDes.getText());
            jsonObject.put("title", updateImageTitle.getText());
            jsonObject.put("video", updateImageVideo.getText());
            jsonObject.put("video_gal_id",updateImageId.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<RequestAndResponseModel> call = retrofitClient.editVideo(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    RequestAndResponseModel requestAndResponseModel = response.body();
                    System.out.println(requestAndResponseModel.getStatus_message());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(requestAndResponseModel.getSuccessMessage(), 5000);
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
