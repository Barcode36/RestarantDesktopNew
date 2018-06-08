package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.Constants;
import RestarantApp.model.TestimonyDetails;
import RestarantApp.popup.ViewTestimonyController;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class TestimonyController implements Initializable {

    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane rootPane;
    @FXML
    TableView<TestimonyDetails> tableIndex;
    @FXML
    TableColumn colTestimonyId,colCustomerId,colTextimonyImage,colTextimonyMessage,colTextimonyDate;
    ObservableList<TestimonyDetails> data;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    ArrayList pageNo = new ArrayList();
    APIService retrofitClient;
    @FXML
    ImageView imgNext,imgPrevious;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);

         retrofitClient = RetrofitClient.getClient().create(APIService.class);
        jfxSnackbar = new JFXSnackbar(rootPane);

        // Set up the table data
        colTestimonyId.setCellValueFactory(
                new PropertyValueFactory<TestimonyDetails,Long>("testimonyId")
        );
        colCustomerId.setCellValueFactory(
                new PropertyValueFactory<TestimonyDetails,String>("customerName")
        );
        colTextimonyImage.setCellValueFactory(
                new PropertyValueFactory<TestimonyDetails,String>("testimonyImage")
        );
        colTextimonyMessage.setCellValueFactory(
                new PropertyValueFactory<TestimonyDetails,String>("testimonyMessage")
        );
        colTextimonyDate.setCellValueFactory(
                new PropertyValueFactory<TestimonyDetails,String>("testimonyDate")
        );

        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(40);

        TableColumn<TestimonyDetails, TestimonyDetails> unfriendCol = new TableColumn<>("Action");
        unfriendCol.setMinWidth(40);
        unfriendCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        unfriendCol.setCellFactory(param -> {
            return new TableCell<TestimonyDetails,TestimonyDetails>() {

                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/tickicon.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/deleteIcon.png"));
                ImageView approvalButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);

                @Override
                protected void updateItem(TestimonyDetails person, boolean empty) {
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
                    approvalButton.getProperties().put(EDITTIP, deleteTip);
                    Tooltip.install(approvalButton, editTip);
                    if (person.getTestimonyApproved().equals(Constants.Success)){
                        approvalButton.setVisible(false);
                    }else {
                        approvalButton.setVisible(true);
                    }

                    HBox pane = new HBox(approvalButton, button1);
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

                    approvalButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();
                            getApproval(person);

                        }
                    });
                }
            };

        });
        imgNext.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                remainCount = remainCount + 1;
                pageNum = pageNum  + 1;
                imgPrevious.setVisible(true);
                getData();
            }
        });
        imgPrevious.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                pageNum = pageNum - 1;
                remainCount = (int)pageNo.get(pageNum);
                for(int j=0;j<pageNo.size();j++)
                {
                    System.out.println("get page Number from array----->"+String.valueOf(pageNo.get(j)));
                }

                getData();

            }
        });
        getData();
        tableIndex.getColumns().addAll(unfriendCol);


        tableIndex.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                TestimonyDetails item = tableIndex.getSelectionModel().getSelectedItem();
                Parent root;
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/popup/viewtestimonials.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    ViewTestimonyController viewTestimonyController =fxmlLoader.getController();
                    viewTestimonyController.setItemsDetails(item);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("ABC");
                    stage.setScene(new Scene(root1));
                    stage.show();
                    // Hide this current window (if this is what you want)
//                    ((Node)(event.getSource())).getScene().getWindow().hide();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

               /* if (item != null) {
                    System.out.println(item.getItemName() + item.getItemName());


                }*/
            }
        });
    }

    private void getData() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<TestimonyDetails> call = retrofitClient.getTestimony(jsonObject);
        call.enqueue(new Callback<TestimonyDetails>() {
            @Override
            public void onResponse(Call<TestimonyDetails> call, Response<TestimonyDetails> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    }else
                    {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    TestimonyDetails testimonyDetails = response.body();

                    ArrayList<TestimonyDetails.testimony_list>  testimonyList = testimonyDetails.getTestimony_list();
                    totalcount = testimonyDetails.getTot_testimony();
                    for (int i = 0; i < testimonyList.size() ;i ++)
                    {
                        TestimonyDetails.testimony_list testimony_list = testimonyList.get(i);
                        TestimonyDetails testimonyDetailList = new TestimonyDetails();
                        testimonyDetailList.setCustomerName(testimony_list.getCustomer_id());
                        testimonyDetailList.setTestimonyDate(testimony_list.getDate());
                        testimonyDetailList.setTestimonyImage(testimony_list.getImage());
                        testimonyDetailList.setTestimonyMessage(testimony_list.getMessage());
                        testimonyDetailList.setTestimonyId(testimony_list.getTestimony_id());
                        testimonyDetailList.setTestimonyApproved(testimony_list.getApproved());
                        data.add(testimonyDetailList);
                        dummyCount = dummyCount + 1;
                    }

                    remainCount = dummyCount;
                    System.out.println("in response remain count--->" + String.valueOf(remainCount));
                    tableIndex.setItems(data);
                    tableIndex.refresh();
                    if (totalcount >= remainCount) {
                        imgNext.setVisible(true);
                    }

                    if (remainCount == totalcount) {
                        imgNext.setVisible(false);

                    }else
                    {
                        imgNext.setVisible(true);
                    }
                    if (pageNum == 0) {
                        imgPrevious.setVisible(false);
                    } else
                    {
                        imgPrevious.setVisible(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<TestimonyDetails> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });


    }

    void showAlert(TestimonyDetails list)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText(null);
        alert.setContentText("Are you want to Delete this "+list.getTestimonyId() + " Item?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){

            deleteTestimony(list);
        } else  {
            alert.close();

        }

    }


    public void deleteTestimony(TestimonyDetails testimony){
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("testimony_id", testimony.getTestimonyId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<TestimonyDetails> deleteTestimony = retrofitClient.deleteTestimony(jsonObject);
        deleteTestimony.enqueue(new Callback<TestimonyDetails>() {
            @Override
            public void onResponse(Call<TestimonyDetails> call, Response<TestimonyDetails> response) {
                TestimonyDetails testimonyDetails = response.body();
                if (testimonyDetails.getStatuscode().equals(Constants.Success))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            jfxSnackbar.show(testimonyDetails.getStatus_message(),5000);
                        }
                    });
                    data.remove(testimony);

                }
            }

            @Override
            public void onFailure(Call<TestimonyDetails> call, Throwable throwable) {

            }
        });

    }

    public void getApproval(TestimonyDetails testimony){
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("testimony_id", testimony.getTestimonyId());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<TestimonyDetails> deleteTestimony = retrofitClient.approveTestimony(jsonObject);
        deleteTestimony.enqueue(new Callback<TestimonyDetails>() {
            @Override
            public void onResponse(Call<TestimonyDetails> call, Response<TestimonyDetails> response) {
                TestimonyDetails testimonyDetails = response.body();
                if (testimonyDetails.getStatus_code().equals(Constants.Success))
                {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            testimony.setTestimonyApproved("1");
                            tableIndex.refresh();
                            jfxSnackbar.show(testimonyDetails.getStatusMessage(),5000);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<TestimonyDetails> call, Throwable throwable) {

            }
        });
    }
}
