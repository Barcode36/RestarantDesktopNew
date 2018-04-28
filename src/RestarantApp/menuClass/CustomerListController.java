package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.CustomerDetails;
import RestarantApp.model.ItemListRequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerListController implements Initializable {
    JFXSnackbar jfxSnackbar;
    @FXML
    StackPane rootPane;
    @FXML
    TableView<CustomerDetails> tableCustomer;
    @FXML
    TableColumn cusNameColm,cusNoVisColm,cusMobNmColm,cusEMailColm,cusAddressColm;
    int totalcount = 0,remainCount= 1,dummyCount = 0,pageNum = 0;
    @FXML
    ImageView imgNext,imgPrevious;
    ArrayList pageNo = new ArrayList();
    ObservableList<CustomerDetails> data;
    @FXML
    TextField txtSearchCustomer;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);


        cusNameColm.setCellValueFactory(
                new PropertyValueFactory<CustomerDetails,Long>("Name")
        );
        cusNoVisColm.setCellValueFactory(
                new PropertyValueFactory<CustomerDetails,Long>("tot_customers")
        );
        cusNoVisColm.setMinWidth(10);
        cusMobNmColm.setCellValueFactory(
                new PropertyValueFactory<CustomerDetails,Long>("PhoneNum")
        );
        cusEMailColm.setCellValueFactory(
                new PropertyValueFactory<CustomerDetails,Long>("Email")
        );
        cusAddressColm.setCellValueFactory(
                new PropertyValueFactory<CustomerDetails,Long>("Address")
        );
        tableCustomer.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableCustomer.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableCustomer.prefHeightProperty().bind(Bindings.size(tableCustomer.getItems()).multiply(tableCustomer.getFixedCellSize()).add(30));

        TableColumn<CustomerDetails, CustomerDetails> tableAction = new TableColumn<>("Action");
        tableAction.setMinWidth(30);
        tableAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableAction.setCellFactory(param -> {
            return new TableCell<CustomerDetails,CustomerDetails>()
            {
                Image image1 = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/edit.png"));
                Image image = new Image(ViewCategoryController.class.getResourceAsStream("/RestarantApp/images/delete.png"));
                ImageView editButton = new ImageView(image1);
                ImageView deleteButton = new ImageView(image);
                Button button1 = new Button("", deleteButton);
                @Override
                protected void updateItem(CustomerDetails item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
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

//                            showAlert(item);

                        }
                    });

                    editButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            event.consume();


                        }
                    });
                }
            };
        });

        tableCustomer.getColumns().addAll(tableAction);

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

        txtSearchCustomer.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchList(newValue);
                if (newValue.isEmpty())
                {
                    remainCount = 1;
                    getData();
                }
            }
        });
        getData();
    }

    public void btnSubmitEdit(ActionEvent actionEvent) {
    }



    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<CustomerDetails> call = retrofitClient.getCustomerList(jsonObject);
        call.enqueue(new Callback<CustomerDetails>() {
            @Override
            public void onResponse(Call<CustomerDetails> call, Response<CustomerDetails> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    } else {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    CustomerDetails customerDetails = response.body();

                    ArrayList getItemDetils = customerDetails.getCustomer_list();
                    totalcount = customerDetails.getTot_customers();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        CustomerDetails.customer_list list = (CustomerDetails.customer_list) getItemDetils.get(i);
                        CustomerDetails itemList = new CustomerDetails();
                        itemList.setName(list.getName());
                        itemList.setNo_of_visit(list.getNo_of_visit());
                        itemList.setAddress(list.getAddress());
                        itemList.setPhoneNum(list.getPhone());
                        itemList.setEmail(list.getEmail());


                        data.add(itemList);
                        dummyCount = dummyCount + 1;
                    }
                    remainCount = dummyCount;
                    System.out.println("in response remain count--->" + String.valueOf(remainCount));
                    tableCustomer.setItems(data);
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
            public void onFailure(Call<CustomerDetails> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }

    public void searchList(String keyword)
    {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("key_word", keyword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<CustomerDetails> call = retrofitClient.getSearchResult(jsonObject);
        call.enqueue(new Callback<CustomerDetails>() {
            @Override
            public void onResponse(Call<CustomerDetails> call, Response<CustomerDetails> response) {
                if (response.isSuccessful()) {

                    data = FXCollections.observableArrayList();
                    CustomerDetails customerDetails = response.body();

                    ArrayList getItemDetils = customerDetails.getCustomer_Details();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        CustomerDetails.customer_list list = (CustomerDetails.customer_list) getItemDetils.get(i);
                        CustomerDetails itemList = new CustomerDetails();
                        itemList.setName(list.getName());
                        itemList.setNo_of_visit(list.getNo_of_visit());
                        itemList.setAddress(list.getAddress());
                        itemList.setPhoneNum(list.getPhone());
                        itemList.setEmail(list.getEmail());


                        data.add(itemList);
                    }

                    imgNext.setVisible(false);
                    imgPrevious.setVisible(false);
                    System.out.println("data size   "+data.size());
                    tableCustomer.setItems(data);

                }
            }

            @Override
            public void onFailure(Call<CustomerDetails> call, Throwable throwable) {
                System.out.println(throwable);
            }
        });
    }
}
