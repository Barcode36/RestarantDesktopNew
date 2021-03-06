package RestarantApp.Login;

import RestarantApp.Main;
import RestarantApp.Network.APIService;
import RestarantApp.Network.Api;
import RestarantApp.Network.NetworkClient;
import RestarantApp.Network.RetrofitClient;
import RestarantApp.chat.rabbitmq_server.RabbitmqServer;
import RestarantApp.chat.rabbitmq_stomp.Listener;
import RestarantApp.database.SqliteConnection;
import RestarantApp.menuClass.ViewCategoryController;
import RestarantApp.model.Constants;
import RestarantApp.model.LoginRequestAndResponse;
import com.jfoenix.controls.JFXSnackbar;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.util.*;

public class LoginController {
    @FXML
    ProgressIndicator progressLoginIndicator;
    @FXML
    ChoiceBox choiceBox;
    @FXML
    TextField txtFieldUsername,txtFieldPassword;

    @FXML
    Button btnLogin;

    @FXML
    BorderPane rootPane;
    private NetworkClient networkClient;
    Connection connection;
    JFXSnackbar jfxSnackbar;
    SqliteConnection sqliteConnection;
    InetAddress inetAddress = null;
    public void initialize() {
        String css = LoginController.class.getResource("/RestarantApp/cssFile/Login.css").toExternalForm();
        rootPane.getStylesheets().add(css);
        jfxSnackbar = new JFXSnackbar(rootPane);

        networkClient = new NetworkClient();
        progressLoginIndicator.setVisible(false);
        progressLoginIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                getBranchCode();
            }
        });


        try {
            inetAddress = InetAddress.getLocalHost();
            sendIP();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("IP Address:- " + inetAddress.getHostAddress());
        txtFieldPassword.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER))
                {
                    login();
                }
            }
        });


    }

    public void sendIP()
    {
        APIService apiInterface = RetrofitClient.getClient().create(APIService.class);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("act","add");
            jsonObject.put("current_ip", inetAddress.getHostAddress());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Call<LoginRequestAndResponse> customerDetailsCall = apiInterface.sendIp(jsonObject);
        customerDetailsCall.enqueue(new Callback<LoginRequestAndResponse>() {
            @Override
            public void onResponse(Call<LoginRequestAndResponse> call, Response<LoginRequestAndResponse> response) {
                if (response.isSuccessful())
                {
                    LoginRequestAndResponse loginRequestAndResponse = response.body();
                    System.out.println(loginRequestAndResponse.getStatusCode());
                    if (loginRequestAndResponse.getStatusCode().equals(Constants.Failure))
                    {
                        System.out.println(loginRequestAndResponse.getStatusCode());
                    }

                }
            }

            @Override
            public void onFailure(Call<LoginRequestAndResponse> call, Throwable throwable) {

            }
        });
    }
    public void login()
    {
        try {
        if (txtFieldUsername.getText().equals("admin") && txtFieldPassword.getText().equals("admin"))
        {
            Stage stage;
            stage=(Stage) btnLogin.getScene().getWindow();
            Parent   root = FXMLLoader.load(getClass().getResource("/RestarantApp/Dashboard/DashBoardScene.fxml"));
            stage.setTitle("Prawn And Crabs");
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root, visualBounds.getWidth(), visualBounds.getHeight()));
            stage.show();
        }else if (txtFieldUsername.getText().equals("test") && txtFieldPassword.getText().equals("test")) {


            Stage stage;
            Parent root;
            stage=(Stage) btnLogin.getScene().getWindow();
            root = FXMLLoader.load(getClass().getResource("/RestarantApp/Billing/billingscene.fxml"));
            stage.setTitle("Prawn And Crabs");
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
            stage.setScene(new Scene(root, visualBounds.getWidth(), visualBounds.getHeight()));
            stage.show();

        }else
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Login");
            alert.setHeaderText("Login Error");
            alert.setContentText("Username and Password Invalid !!!");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // ... user chose OK
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void onLoginButtonClick(ActionEvent actionEvent) throws IOException {


        login();

        /*FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/RestarantApp/Dashboard/DashBoardScene.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();*/

       /* progressLoginIndicator.setVisible(true);
        if (validateField()) {
            String comboBoxValue = choiceBox.getValue().toString();
            String[] branch = comboBoxValue.split(" ");
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", txtFieldUsername.getText());
                jsonObject.put("password", txtFieldPassword.getText());
                jsonObject.put("branch_id", branch[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String output = networkClient.makeHTTPPOSTRequest(jsonObject, Api.adminLogin);
            try {
                JSONObject outpurObject = new JSONObject(output);
                if (Constants.Success.equals(outpurObject.getString("status_code")))
                {
                    progressLoginIndicator.setVisible(false);
                    System.out.println("Login Success");

                }else if (outpurObject.getString("status_code").equals(Constants.Failure))
                {
                    progressLoginIndicator.setVisible(false);
                    Constants.showAlert(Alert.AlertType.INFORMATION,"Information Dialog",null,outpurObject.getString("status_message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("Output from Server controller .... " + output);
            progressLoginIndicator.setVisible(false);
        }else
        {
            progressLoginIndicator.setVisible(false);
            showAlert();
        }*/

       /* sendMessage();
        *//*  Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Look, a Confirmation Dialog");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
        } else {
            // ... user chose CANCEL or closed the dialog
        }*/
    }
    public void sendMessage()
    {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("message","hai");
            jsonObject.put("from","desktopUserUser");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMsg(jsonObject.toString());


    }

    public  void sendMsg(String msg) {

        HashMap headers = new HashMap();
        headers.put("content-type", "text/plain");
        if (RabbitmqServer.client!= null) {
            RabbitmqServer.client.send("/topic/resturantApp", msg, headers);
            RabbitmqServer.client.addErrorListener(new Listener() {

                @Override
                public void message(Map headers, String body) {

                    System.out.println(body);
                }
            });
        }else
        {
            System.out.println( "client not connected");
        }
    }

    public void getBranchCode()
    {

       String output =  networkClient.makeHTTPPOSTRequest(null,Api.getBranchCode);
        try {
            JSONObject jsonObject = new JSONObject(output);
            String statusCode = jsonObject.getString("status_code");
            JSONArray listArrary = jsonObject.getJSONArray("list");
            List branchList = new ArrayList();
            branchList.add("Select Branch Code");
            branchList.add(new Separator());
            for (int i=0; i <  listArrary.length();i++)
            {
                JSONObject branchCode = listArrary.getJSONObject(i);
                String brnchCode = branchCode.getString("Branch_id");
                String branchCodeName= brnchCode + "  " + branchCode.getString("Branch_name");
                System.out.println("Output from Server controller .... "+brnchCode);
                branchList.add(branchCodeName);

            }
            ObservableList<String> observableList = FXCollections.observableList(branchList);
            choiceBox.setItems(observableList);
            choiceBox.getSelectionModel().selectFirst();
//            System.out.println("Output from Server controller .... "+statusCode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public boolean validateField()
    {
        if (txtFieldUsername.getText().isEmpty() || txtFieldPassword.getText().isEmpty() || choiceBox.getSelectionModel().getSelectedIndex() == 0)
        {
            return false;
        }else
        {
            return true;
        }

    }

    public void showAlert()
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, a Warning Dialog");
        alert.setContentText("Please Enter all Fields");
        alert.showAndWait();
    }
}
