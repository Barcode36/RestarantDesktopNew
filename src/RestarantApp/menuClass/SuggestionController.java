package RestarantApp.menuClass;

import RestarantApp.Network.APIService;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.model.RequestAndResponseModel;
import com.jfoenix.controls.JFXSnackbar;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SuggestionController implements Initializable {

    int totalcount = 0, remainCount = 1, dummyCount = 0, pageNum = 0;
    @FXML
    TableView<RequestAndResponseModel.suggestion_list> tableIndex;
    @FXML
    TableColumn tableSno, tableCustomerName, tableSuggestion;
    ArrayList pageNo = new ArrayList();
    @FXML
    ImageView imgNext, imgPrevious;
    @FXML
    StackPane rootPane;
    JFXSnackbar jfxSnackbar;
    ObservableList<RequestAndResponseModel.suggestion_list> data;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String css = ViewCategoryController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);

        // Set up the table data
        tableSno.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.suggestion_list, Long>("id")
        );
        tableCustomerName.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.suggestion_list, String>("customer_name")
        );
        tableSuggestion.setCellValueFactory(
                new PropertyValueFactory<RequestAndResponseModel.suggestion_list, String>("suggestion")
        );

        tableIndex.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableIndex.getStylesheets().add("/RestarantApp/cssFile/Login.css");
        tableIndex.setFixedCellSize(45);
        tableIndex.prefHeightProperty().bind(Bindings.size(tableIndex.getItems()).multiply(tableIndex.getFixedCellSize()).add(30));

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

        getData();
    }

    private void getData() {
        APIService retrofitClient = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("from", remainCount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<RequestAndResponseModel> call = retrofitClient.getSuggestionList(jsonObject);
        call.enqueue(new Callback<RequestAndResponseModel>() {
            @Override
            public void onResponse(Call<RequestAndResponseModel> call, Response<RequestAndResponseModel> response) {
                if (response.isSuccessful()) {
                    if (!pageNo.contains(remainCount)) {
                        pageNo.add(remainCount);
                    } else {
                        dummyCount = remainCount - 1;
                    }
                    data = FXCollections.observableArrayList();
                    RequestAndResponseModel requestAndResponseModel = response.body();

                    ArrayList getItemDetils = requestAndResponseModel.getSuggestion_list();
                    totalcount = requestAndResponseModel.getTot_suggestion();
                    for (int i = 0; i < getItemDetils.size(); i++) {
                        RequestAndResponseModel.suggestion_list list = (RequestAndResponseModel.suggestion_list) getItemDetils.get(i);
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
            public void onFailure(Call<RequestAndResponseModel> call, Throwable throwable) {

            }
        });


    }

}
